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

@Service
public class WebSocketMessageService{
    @Value("src/main/resources/templates/RandomTextFile")
    public File file;
    public String currentLogs;
    public long lastModified=0L;
    public long prevLength=0L;
    @Scheduled(fixedRate = 1000)
    public void run() {
            for (WebSocketSession session : WebSocketHandler.getSessions()) {
                if (session.isOpen()) {
                    long currLastModified = file.lastModified();
                    long currLength = file.length();
                    if (lastModified!=currLastModified) {
                        try {
                            ReverseInputStream reverseInputStream = new ReverseInputStream(file, prevLength);
                            lastModified = currLastModified;
                            prevLength = currLength;
                            currentLogs = reverseInputStream.pushLastLines();
                            session.sendMessage(new TextMessage(currentLogs));
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }
            }
    }
}
