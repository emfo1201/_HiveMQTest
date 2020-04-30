package reactive;

import com.hivemq.client.mqtt.datatypes.MqttQos;
import com.hivemq.client.mqtt.mqtt5.Mqtt5Client;
import com.hivemq.client.mqtt.mqtt5.Mqtt5RxClient;
import com.hivemq.client.mqtt.mqtt5.message.publish.Mqtt5Publish;
import io.reactivex.Completable;
import io.reactivex.Flowable;

import java.time.LocalTime;
import java.util.UUID;

import static java.time.temporal.ChronoUnit.*;

public class Publisher {

    public static void main(String[] args) {
        LocalTime start = java.time.LocalTime.now();
        System.out.println("Test started: " + start);
        Mqtt5RxClient client = Mqtt5Client.builder()
                .identifier(UUID.randomUUID().toString())
                .buildRx();

// As we use the reactive API, the following line does not connect yet, but returns a reactive type.
        Completable connectScenario = client.connect()
                .doOnSuccess(connAck -> System.out.println("Connected, " + connAck.getReasonCode()))
                .doOnError(throwable -> System.out.println("Connection failed, " + throwable.getMessage()))
                .ignoreElement();


// Fake a stream of Publish messages with an incrementing number in the payload
        Flowable<Mqtt5Publish> messagesToPublish = Flowable.range(0, 200)
                .map(i -> Mqtt5Publish.builder()
                        .topic("a/b/c")
                        .qos(MqttQos.AT_LEAST_ONCE)
                        .payload((i + " " + java.time.LocalTime.now()).getBytes())
                        .build());

// As we use the reactive API, the following line does not publish yet, but returns a reactive type.
        Completable publishScenario = client.publish(messagesToPublish)
                .ignoreElements();

// As we use the reactive API, the following line does not disconnect yet, but returns a reactive type.
        Completable disconnectScenario = client.disconnect().doOnComplete(() -> System.out.println("Disconnected"));

// Reactive types can be easily and flexibly combined
        connectScenario.andThen(publishScenario).andThen(disconnectScenario).blockingAwait();
        LocalTime end = java.time.LocalTime.now();
        System.out.println("Test finished: " + end);
        System.out.println("Test took: " + MILLIS.between(start,end));
    }
}