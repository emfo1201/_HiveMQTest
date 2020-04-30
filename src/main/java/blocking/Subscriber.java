package blocking;

import com.hivemq.client.mqtt.MqttGlobalPublishFilter;
import com.hivemq.client.mqtt.datatypes.MqttQos;
import com.hivemq.client.mqtt.mqtt5.Mqtt5BlockingClient;
import com.hivemq.client.mqtt.mqtt5.Mqtt5Client;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

import static java.lang.Thread.sleep;
import static java.time.temporal.ChronoUnit.MILLIS;

public class Subscriber {
    public static void main(String[] args) throws InterruptedException {
        ArrayList<Long> listOfMessageLatencyTimes = new ArrayList<>();
        AtomicLong sumOfMessageLatency = new AtomicLong();

        final Mqtt5BlockingClient client = Mqtt5Client.builder()
                .identifier(UUID.randomUUID().toString())
                .buildBlocking();

        client.connect();

        try (final Mqtt5BlockingClient.Mqtt5Publishes publishes = client.publishes(MqttGlobalPublishFilter.ALL)) {

            client.subscribeWith().addSubscription().topicFilter("a/b/c").qos(MqttQos.AT_LEAST_ONCE).applySubscription()
            .addSubscription().topicFilter("a/b/d").qos(MqttQos.AT_LEAST_ONCE).applySubscription().send();

            while (true){
                String message = new String(publishes.receive().getPayloadAsBytes());
                String[] split = message.split(" ");
                long messageLatency = calculateMessageLatency(split[1]);
                sumOfMessageLatency.addAndGet(messageLatency);
                listOfMessageLatencyTimes.add(messageLatency);
                printCurrentResult(sumOfMessageLatency.get(),listOfMessageLatencyTimes.size(), split[0]);
                sleep(100);
            }

        } finally {
            client.disconnect();
        }
    }

    private static long calculateMessageLatency(String message) {
        LocalTime sentTime = LocalTime.parse(message);
        LocalTime receivedTime = java.time.LocalTime.now();
        return MILLIS.between(sentTime, receivedTime);
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
