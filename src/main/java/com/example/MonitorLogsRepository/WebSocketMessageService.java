package com.example.MonitorLogsRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.File;
import java.io.IOException;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Stack;

@Service
public class WebSocketMessageService{

    @Value("src/main/resources/templates/RandomTextFile")
    public File file;
    public String currentLogs;
    public long lastModified=0L;
    public long prevLength=0L;
    public static Deque<String> lastLogs = new ArrayDeque<>();

    @Scheduled(fixedDelay = 1000)
    public void run() throws IOException {
            long currLastModified = file.lastModified();
            long currLength = file.length();
            if (lastModified!=currLastModified && !WebSocketHandler.getSessions().isEmpty()) {
                try {
                    ReverseInputStream reverseInputStream = new ReverseInputStream(file, prevLength);
                    lastModified = currLastModified;
                    currentLogs = reverseInputStream.pushLastLines();
                    prevLength = currLength;
                    for (WebSocketSession session : WebSocketHandler.getSessions()) {
                        if (session.isOpen()) {
                            session.sendMessage(new TextMessage(currentLogs));
                        }
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
    }
}
