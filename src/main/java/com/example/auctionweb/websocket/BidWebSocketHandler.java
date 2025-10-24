package com.example.auctionweb.websocket;

import com.example.auctionweb.dto.WebSocketMessage;
import com.example.auctionweb.entity.BidHistory;
import com.example.auctionweb.service.IBidHistoryService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.*;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.math.BigDecimal;

@Component
public class BidWebSocketHandler extends TextWebSocketHandler {

    private static final Logger logger = LoggerFactory.getLogger(BidWebSocketHandler.class);
    private final List<WebSocketSession> sessions = new CopyOnWriteArrayList<>();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private IBidHistoryService bidHistoryService;

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        sessions.add(session);
        logger.info("Client connected: {} - Total sessions: {}", session.getId(), sessions.size());
        
        // Gửi welcome message
        WebSocketMessage welcomeMsg = new WebSocketMessage("SUCCESS", "Kết nối WebSocket thành công!");
        sendMessageToSession(session, welcomeMsg);
    }

    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        logger.info("Received message from {}: {}", session.getId(), message.getPayload());
        
        try {
            // Parse JSON từ client thành BidHistory
            BidHistory bid = objectMapper.readValue(message.getPayload(), BidHistory.class);
            
            // Validate bid
            if (bid.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
                WebSocketMessage errorMsg = new WebSocketMessage("ERROR", "Số tiền đấu giá không hợp lệ!");
                sendMessageToSession(session, errorMsg);
                return;
            }


            // Lưu vào DB
            boolean saved = bidHistoryService.add(bid);
            if (saved) {
                // Nếu lưu thành công thì broadcast cho tất cả client
                broadcastNewBid(bid);
                logger.info("Bid saved and broadcasted: Auction ID {}, Amount {}", 
                           bid.getAuction().getId(), bid.getAmount());
            } else {
                WebSocketMessage errorMsg = new WebSocketMessage("ERROR", "Không thể lưu đấu giá. Vui lòng thử lại!");
                sendMessageToSession(session, errorMsg);
            }
        } catch (Exception e) {
            logger.error("Error processing bid message: {}", e.getMessage());
            WebSocketMessage errorMsg = new WebSocketMessage("ERROR", "Lỗi xử lý dữ liệu: " + e.getMessage());
            sendMessageToSession(session, errorMsg);
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        sessions.remove(session);
        logger.info("Client disconnected: {} - Reason: {} - Remaining sessions: {}", 
                   session.getId(), status.getReason(), sessions.size());
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        logger.error("Transport error for session {}: {}", session.getId(), exception.getMessage());
        sessions.remove(session);
    }


    public void broadcastNewBid(BidHistory bid) throws Exception {
        WebSocketMessage message = new WebSocketMessage("BID", bid);
        message.setAuctionId(bid.getAuction().getId());
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
                    logger.error("Error sending message to session {}: {}", session.getId(), e.getMessage());
                    sessions.remove(session);
                }
            }
        }
        
        logger.info("Broadcasted message to {} clients", sentCount);
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
