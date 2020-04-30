package ThroughputTime;

import java.time.Duration;
import java.time.LocalTime;
import java.util.Scanner;

public class Throughput {
    public static void main(String[] args){

        System.out.println("Publisher start time: ");
        Scanner pubStartTimeIn = new Scanner(System.in);
        LocalTime startTime = LocalTime.parse(pubStartTimeIn.nextLine());

        System.out.println("Publisher end time: ");
        Scanner pubEndTimeIn = new Scanner(System.in);
        LocalTime pubEndTime = LocalTime.parse(pubEndTimeIn.nextLine());

        System.out.println("Subscriber end time: ");
        Scanner subEndTimeIn = new Scanner(System.in);
        LocalTime endTime = LocalTime.parse(subEndTimeIn.nextLine());

        System.out.println("Total latency: ");
        Scanner latency = new Scanner(System.in);
        long totLatency = latency.nextLong();

        System.out.println("Number of sent messages: ");
        Scanner sentMessages = new Scanner(System.in);
        int totsentMessages = sentMessages.nextInt();

        System.out.println("Number of received messages: ");
        Scanner messages = new Scanner(System.in);
        int totReceivedMessages = messages.nextInt();

        int dropped = totsentMessages - totReceivedMessages;

        Duration d = Duration.between(startTime, endTime);
        long minutes = d.toMinutes();
        long seconds = d.minusMinutes(minutes).getSeconds();

        long milliseconds = d.toMillis();
        double secmilli = (double) milliseconds / 1000;
        long mintosec = minutes * 60;
        long secomintomillis = (seconds + mintosec) * 1000;
        long millisec =  milliseconds - secomintomillis;

        System.out.println("Test total time is: " + minutes + " minutes, " + seconds + " seconds and " + millisec + " milliseconds, or just simply " + milliseconds + " milliseconds, if you prefer it like that.");
        System.out.println("Throughput: " + (double)totReceivedMessages/secmilli);
        System.out.println("Average latency per packet: " + totLatency/totReceivedMessages + " milliseconds.");
        System.out.println("Number of dropped messages: " + dropped);
        System.out.println("Publish rate: " + (double)totsentMessages/((double)Duration.between(startTime,pubEndTime).toMillis()/1000));
    }
}
