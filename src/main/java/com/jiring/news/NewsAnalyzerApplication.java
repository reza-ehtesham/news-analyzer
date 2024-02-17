package com.jiring.news;

import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class NewsAnalyzerApplication {
    public static void main(String[] args) {
        NewsAnalyzer newsAnalyzer = new NewsAnalyzer();
        int port = Integer.parseInt(System.getProperty("analyzerPort", "8686"));
        newsAnalyzer.start(port);
    }
}
