package com.alfie.logger.restlogger;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.AppenderBase;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

public class AsyncRestApiAppender extends AppenderBase<ILoggingEvent> {

    private String apiUrl;
    private BlockingQueue<String> logQueue;
    private RestTemplate restTemplate;
    private Thread workerThread;
    private boolean running = true;
    private CustomLogEncoder encoder;  // Custom encoder for formatting

    public AsyncRestApiAppender() {
        this.restTemplate = new RestTemplate();
        this.logQueue = new LinkedBlockingQueue<>();
        this.encoder = new CustomLogEncoder();

        // Start the worker thread
        this.workerThread = new Thread(new LogWorker());
        this.workerThread.start();
    }

    @Override
    protected void append(ILoggingEvent eventObject) {
        try {
            // Encode the event and add it to the queue
            String logMessage = new String(encoder.encode(eventObject), StandardCharsets.UTF_8);
            logQueue.offer(logMessage);
        } catch (Exception e) {
            e.printStackTrace(); // Handle exception
        }
    }

    @Override
    public void stop() {
        super.stop();
        running = false;
        workerThread.interrupt();  // Stop the worker thread when the appender is stopped
    }

    public void setApiUrl(String apiUrl) {
        this.apiUrl = apiUrl;
    }

    public void setEncoder(CustomLogEncoder encoder) {
        this.encoder = encoder;
    }

    private class LogWorker implements Runnable {

        @Override
        public void run() {
            while (running) {
                try {
                    // Collect logs from the queue, send in batches every 5 seconds
                    StringBuilder batchLogs = new StringBuilder();

                    while (true) {
                        String log = logQueue.poll(5, TimeUnit.SECONDS);
                        if (log == null) {
                            break; // Exit inner loop if no logs in the queue for 5 seconds
                        }
                        batchLogs.append(log);
                    }

                    if (batchLogs.length() > 0) {
                        sendLogsToApi(batchLogs.toString());
                    }

                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        }

        private void sendLogsToApi(String logs) {
            try {
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON);
                HttpEntity<String> request = new HttpEntity<>(logs, headers);
                restTemplate.postForEntity(apiUrl, request, String.class);
            } catch (Exception e) {
                e.printStackTrace(); // Handle exception
            }
        }
    }
}

