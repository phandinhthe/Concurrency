package com.the.udemy.concurrency.performance;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class Throughput {
    private static final String WAR_AND_PEACE = "war_and_peace.txt";
    private static final int NUMBER_OF_THREADS = 8;
    public static void main(String[] args) throws IOException {
        String txt = readInputFile(WAR_AND_PEACE);
        startServer(txt);
    }

    private static String readInputFile(String url) {
        String rs = null;
        try(InputStream stream = Throughput.class.getClassLoader().getResourceAsStream(url)) {
//            rs = new String(stream.readAllBytes());
            rs = new String();
        } catch (IOException ioException) {
            ioException.printStackTrace();
        } finally {
            return rs;
        }
    }

    private static void startServer(String txt) throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(8000), 0);
        server.createContext("/search", new WordCountHandler(txt));
        Executor executor = Executors.newFixedThreadPool(NUMBER_OF_THREADS);
        server.setExecutor(executor);
        server.start();
    }

    private static class WordCountHandler implements HttpHandler {
        private String txt;

        public WordCountHandler(String txt) {
            this.txt = txt;
        }


        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String query = exchange.getRequestURI().getQuery();
            String[] keyVal = query.split("=");
            String key = keyVal[0];
            String val = keyVal[1];

            boolean isInvalidKey = !"word".equalsIgnoreCase(key);
            if (isInvalidKey) {
                exchange.sendResponseHeaders(400, 0);
                return;
            }

            long count = countWord(val);
            byte[] response = Long.toString(count).getBytes();
            exchange.sendResponseHeaders(200, response.length);
            OutputStream responseStream = exchange.getResponseBody();
            responseStream.write(response);
            responseStream.close();
        }

        private long countWord(String word) {int index = 0;
            long count = 0;
            while (index >= 0) {
                index = txt.indexOf(word, index);
                boolean exist = index != -1;
                if (exist) {
                    index ++;
                    count ++;
                }
            }
            return count;
        }
    }
}
