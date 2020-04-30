package reactive;

import com.hivemq.client.mqtt.datatypes.MqttQos;
import com.hivemq.client.mqtt.mqtt5.Mqtt5Client;
import com.hivemq.client.mqtt.mqtt5.Mqtt5RxClient;
import com.hivemq.client.mqtt.mqtt5.message.connect.Mqtt5Connect;
import com.hivemq.client.mqtt.mqtt5.message.connect.connack.Mqtt5ConnAck;
import com.hivemq.client.mqtt.mqtt5.message.publish.Mqtt5Publish;
import com.hivemq.client.mqtt.mqtt5.message.subscribe.suback.Mqtt5SubAck;
import com.hivemq.client.rx.FlowableWithSingle;
import io.reactivex.Completable;
import io.reactivex.Single;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

import static java.time.temporal.ChronoUnit.*;

public class Subscriber {
    public static void main(String[] args) {
        ArrayList<Long> listOfMessageLatencyTimes = new ArrayList<>();

        Mqtt5RxClient client = Mqtt5Client.builder()
                .identifier(UUID.randomUUID().toString())
                .buildRx();

        Mqtt5Connect connectMessage = Mqtt5Connect.builder().restrictions().receiveMaximum(20).applyRestrictions().build();

// As we use the reactive API, the following line does not connect yet, but returns a reactive type.
// e.g. Single is something like a lazy and reusable future. Think of it as a source for the ConnAck message.
        Single<Mqtt5ConnAck> connAckSingle = client.connect(connectMessage);

// Same here: the following line does not subscribe yet, but returns a reactive type.
// FlowableWithSingle is a combination of the single SubAck message and a Flowable of Publish messages.
// A Flowable is an asynchronous stream that enables backpressure from the application over the client to the broker.
        FlowableWithSingle<Mqtt5Publish, Mqtt5SubAck> subAckAndMatchingPublishes = client.subscribeStreamWith()
                .topicFilter("a/b/c").qos(MqttQos.AT_LEAST_ONCE)
                .addSubscription().topicFilter("a/b/c/d").qos(MqttQos.AT_LEAST_ONCE).applySubscription()
                .applySubscribe();

// The reactive types offer many operators that will not be covered here.
// Here we register callbacks to print messages when we received the CONNACK, SUBACK and matching PUBLISH messages.
        Completable connectScenario = connAckSingle
                .doOnSuccess(connAck -> System.out.println("Connected, " + connAck.getRestrictions()))
                .doOnError(throwable -> System.out.println("Connection failed, " + throwable.getMessage()))
                .ignoreElement();

        int i = 0;

        AtomicLong sumOfMessageLatency = new AtomicLong();
        Completable subscribeScenario = subAckAndMatchingPublishes
                .doOnSingle(subAck -> System.out.println("Subscribed, " + subAck.getReasonCodes()))
                .doOnNext(publish -> {
                    String message = new String(publish.getPayloadAsBytes());
                    String[] split = message.split(" ");
                    long messageLatency = calculateMessageLatency(split[1]);
                    sumOfMessageLatency.addAndGet(messageLatency);
                    listOfMessageLatencyTimes.add(messageLatency);
                    printCurrentResult(sumOfMessageLatency.get(),listOfMessageLatencyTimes.size(), split[0]);

                    Thread.sleep(100);
                })
                .ignoreElements();

// Reactive types can be easily and flexibly combined
        connectScenario.andThen(subscribeScenario).blockingAwait();
    }

    private static long calculateMessageLatency(String publish){
        LocalTime sentTime = LocalTime.parse(publish);
        LocalTime receivedTime = java.time.LocalTime.now();
        return MILLIS.between(sentTime,receivedTime);
    }

    private static void printCurrentResult(long sumOfMessageLatency, int numberOfMessagesReceived, String messageNumber){
        System.out.println("======================");
        System.out.println("Current time: " + java.time.LocalTime.now());
        System.out.println("Message number: " + messageNumber);
        System.out.println("Number Of Messages Received : " + numberOfMessagesReceived);
        System.out.println("Total latency : " + sumOfMessageLatency);
        System.out.println("======================\n");
    }
}