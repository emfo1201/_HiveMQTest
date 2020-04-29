package blocking;

import com.hivemq.client.mqtt.datatypes.MqttQos;
import com.hivemq.client.mqtt.mqtt5.Mqtt5BlockingClient;
import com.hivemq.client.mqtt.mqtt5.Mqtt5Client;
import java.time.LocalTime;
import java.util.UUID;

import static java.time.temporal.ChronoUnit.MILLIS;

public class Publisher {
    public static void main(String[] args) {
        LocalTime start = java.time.LocalTime.now();
        System.out.println("Test started: " + start);

        Mqtt5BlockingClient client = Mqtt5Client.builder()
            .identifier(UUID.randomUUID().toString())
            .buildBlocking();

        client.connect();

        for(int i = 0; i < 2000; i++) {
            client.publishWith().topic("a/b/c").qos(MqttQos.AT_LEAST_ONCE).payload(java.time.LocalTime.now().toString().getBytes()).send();
        }
        client.disconnect();

        LocalTime end = java.time.LocalTime.now();
        System.out.println("Test finished: " + end);
        System.out.println("Test took: " + MILLIS.between(start,end));
    }
}
