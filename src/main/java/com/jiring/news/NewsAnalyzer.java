package com.jiring.news;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class NewsAnalyzer {
    private final Map<String, Integer> positiveWords = new HashMap<>() {{
        put("up", 1);
        put("rise", 1);
        put("good", 1);
        put("success", 1);
        put("high", 1);
    }};

    private int positiveNewsCount = 0;
    private final Map<String, Integer> positiveHeadlines = new HashMap<>();

    public void start(int port) {
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        scheduler.scheduleWithFixedDelay(new Analyzer(), 10, 10, TimeUnit.SECONDS);
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("NewsAnalyzer listening on port " + port);
            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Client connected: " + clientSocket.getInetAddress());
                new Thread(() -> processNewsFeed(clientSocket)).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void processNewsFeed(Socket clientSocket) {
        try (ObjectInputStream inputStream = new ObjectInputStream(clientSocket.getInputStream())) {
            while (true) {
                NewsItem newsItem = (NewsItem) inputStream.readObject();
                processNewsItem(newsItem);
            }
        } catch (IOException | ClassNotFoundException e) {
            //e.printStackTrace();
            // for hide Stacktrace in EOFException
        }
    }

    private void processNewsItem(NewsItem newsItem) {
        String headline = newsItem.getHeadline();
        int priority = newsItem.getPriority();
        if (isPositive(headline)) {
            synchronized (this) {
                positiveNewsCount++;
                positiveHeadlines.put(headline, priority);
            }
        }
    }

    private boolean isPositive(String headline) {
        List<String> words = Arrays.asList(headline.split("\\s+"));
        long positiveCount = words.stream().filter(positiveWords::containsKey).count();
        return positiveCount > words.size() / 2;
    }

    private class Analyzer implements Runnable {
        @Override
        public void run() {
            synchronized (NewsAnalyzer.this) {
                System.out.println("Positive News Count every 10 Second : " + positiveNewsCount);
                positiveHeadlines.entrySet().stream()
                        .sorted((entry1, entry2) -> Integer.compare(entry2.getValue(), entry1.getValue()))
                        .limit(3)
                        .map(Map.Entry::getKey)
                        .forEach(headline -> System.out.println("High-Priority Positive Headline: " + headline));

                positiveNewsCount = 0;
                positiveHeadlines.clear();
            }
        }
    }
}