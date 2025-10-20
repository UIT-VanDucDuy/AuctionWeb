package com.example.auctionweb.websocket;

import com.example.auctionweb.dto.WebSocketMessage;
import com.example.auctionweb.entity.Notification;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.*;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Component
public class NotificationWebSocketHandler extends TextWebSocketHandler {

    private static final Logger logger = LoggerFactory.getLogger(NotificationWebSocketHandler.class);
    private final List<WebSocketSession> sessions = new CopyOnWriteArrayList<>();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        sessions.add(session);
        logger.info("Notification client connected: {} - Total sessions: {}", session.getId(), sessions.size());
        
        // Gửi welcome message
        WebSocketMessage welcomeMsg = new WebSocketMessage("SUCCESS", "Kết nối Notification WebSocket thành công!");
        sendMessageToSession(session, welcomeMsg);
    }

    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        logger.info("Received notification message from {}: {}", session.getId(), message.getPayload());
        
        try {
            // Parse message để lấy user ID hoặc auction ID để subscribe
            // Có thể mở rộng logic này để filter notifications theo user
            WebSocketMessage response = new WebSocketMessage("SUCCESS", "Đã đăng ký nhận thông báo!");
            sendMessageToSession(session, response);
        } catch (Exception e) {
            logger.error("Error processing notification message: {}", e.getMessage());
            WebSocketMessage errorMsg = new WebSocketMessage("ERROR", "Lỗi xử lý thông báo: " + e.getMessage());
            sendMessageToSession(session, errorMsg);
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        sessions.remove(session);
        logger.info("Notification client disconnected: {} - Reason: {} - Remaining sessions: {}", 
                   session.getId(), status.getReason(), sessions.size());
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        logger.error("Notification transport error for session {}: {}", session.getId(), exception.getMessage());
        sessions.remove(session);
    }

    // 🟢 Method để broadcast notification cho tất cả clients
    public void broadcastNotification(Notification notification) throws Exception {
        WebSocketMessage message = new WebSocketMessage("NOTIFICATION", notification);
        broadcastMessage(message);
    }

    // 🟢 Method để broadcast message cho tất cả clients
    public void broadcastMessage(WebSocketMessage message) throws Exception {
        String json = objectMapper.writeValueAsString(message);
        int sentCount = 0;
        
        for (WebSocketSession session : sessions) {
            if (session.isOpen()) {
                try {
                    session.sendMessage(new TextMessage(json));
                    sentCount++;
                } catch (Exception e) {
                    logger.error("Error sending notification to session {}: {}", session.getId(), e.getMessage());
                    sessions.remove(session);
                }
            }
        }
        
        logger.info("Broadcasted notification to {} clients", sentCount);
    }

    // 🟢 Method để gửi message cho một session cụ thể
    public void sendMessageToSession(WebSocketSession session, WebSocketMessage message) throws Exception {
        if (session.isOpen()) {
            String json = objectMapper.writeValueAsString(message);
            session.sendMessage(new TextMessage(json));
        }
    }

    // 🟢 Method để lấy số lượng clients đang kết nối
    public int getConnectedClientsCount() {
        return sessions.size();
    }
}
