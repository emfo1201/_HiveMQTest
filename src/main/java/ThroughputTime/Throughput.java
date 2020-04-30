package ThroughputTime;

import java.time.Duration;
import java.time.LocalTime;
import java.util.Scanner;

public class Throughput {
    public static void main(String[] args){

        System.out.println("Start time: ");
        Scanner startTimeIn = new Scanner(System.in);
        LocalTime startTime = LocalTime.parse(startTimeIn.nextLine());

        System.out.println("End time: ");
        Scanner endTimeIn = new Scanner(System.in);
        LocalTime endTime = LocalTime.parse(endTimeIn.nextLine());

        System.out.println("Total latency: ");
        Scanner latency = new Scanner(System.in);
        long totLatency = latency.nextLong();

        System.out.println("Number of received messages: ");
        Scanner messages = new Scanner(System.in);
        int totMessages = messages.nextInt();

        Duration d = Duration.between(startTime, endTime);
        long minutes = d.toMinutes();
        long seconds = d.minusMinutes(minutes).getSeconds();

        long milliseconds = d.toMillis();
        double secmilli = (double) milliseconds / 1000;
        long mintosec = minutes * 60;
        long secomintomillis = (seconds + mintosec) * 1000;
        long millisec =  milliseconds - secomintomillis;

        System.out.println("Test total time is: " + minutes + " minutes, " + seconds + " seconds and " + millisec + " milliseconds, or just simply " + milliseconds + " milliseconds, if you prefer it like that.");
        System.out.println("Throughput: " + (double)totMessages/secmilli);
        System.out.println("Average latency per packet: " + totLatency/totMessages + " milliseconds.");
    }
}
