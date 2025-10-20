package com.example.auctionweb.controller;

import com.example.auctionweb.websocket.BidWebSocketHandler;
import com.example.auctionweb.websocket.NotificationWebSocketHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/websocket")
@CrossOrigin(origins = "*")
public class WebSocketController {

    @Autowired
    private BidWebSocketHandler bidWebSocketHandler;

    @Autowired
    private NotificationWebSocketHandler notificationWebSocketHandler;

    /**
     * Lấy thông tin trạng thái WebSocket
     */
    @GetMapping("/status")
    public ResponseEntity<Map<String, Object>> getWebSocketStatus() {
        Map<String, Object> status = new HashMap<>();
        status.put("bidConnections", bidWebSocketHandler.getConnectedClientsCount());
        status.put("notificationConnections", notificationWebSocketHandler.getConnectedClientsCount());
        status.put("timestamp", System.currentTimeMillis());
        
        return ResponseEntity.ok(status);
    }

    /**
     * Gửi thông báo test qua WebSocket
     */
    @PostMapping("/test-notification")
    public ResponseEntity<Map<String, String>> sendTestNotification(@RequestBody Map<String, String> notification) {
        try {
            // Tạo notification object
            Map<String, Object> notificationData = new HashMap<>();
            notificationData.put("title", notification.getOrDefault("title", "Test Notification"));
            notificationData.put("content", notification.getOrDefault("content", "This is a test notification"));
            notificationData.put("createdAt", System.currentTimeMillis());

            // Broadcast notification
            notificationWebSocketHandler.broadcastMessage(
                new com.example.auctionweb.dto.WebSocketMessage("NOTIFICATION", notificationData)
            );

            Map<String, String> response = new HashMap<>();
            response.put("status", "success");
            response.put("message", "Test notification sent successfully");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("status", "error");
            response.put("message", "Failed to send test notification: " + e.getMessage());
            
            return ResponseEntity.badRequest().body(response);
        }
    }
}
