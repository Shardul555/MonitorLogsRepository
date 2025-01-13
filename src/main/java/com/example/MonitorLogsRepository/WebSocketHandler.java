package com.example.MonitorLogsRepository;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import org.w3c.dom.Text;

import java.io.IOException;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.concurrent.CopyOnWriteArrayList;

@Component
public class WebSocketHandler extends TextWebSocketHandler {

    private static final CopyOnWriteArrayList<WebSocketSession> sessions = new CopyOnWriteArrayList<>();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws IOException {
        sessions.add(session);
        if(!WebSocketMessageService.lastLogs.isEmpty()) {
            Deque<String> lastLogs = new ArrayDeque<>(WebSocketMessageService.lastLogs);
            StringBuilder stringBuilder = new StringBuilder();
            while(!lastLogs.isEmpty()) {
                stringBuilder.insert(stringBuilder.length(), lastLogs.pop());
                stringBuilder.append(System.lineSeparator());
            }
            session.sendMessage(new TextMessage(stringBuilder.toString()));
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, org.springframework.web.socket.CloseStatus status) {
        sessions.remove(session);
    }

    public static CopyOnWriteArrayList<WebSocketSession> getSessions() {
        return sessions;
    }
}
