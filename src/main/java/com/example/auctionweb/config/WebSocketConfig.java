package com.example.auctionweb.config;

import com.example.auctionweb.websocket.BidWebSocketHandler;
import com.example.auctionweb.websocket.NotificationWebSocketHandler;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    private final BidWebSocketHandler bidWebSocketHandler;
    private final NotificationWebSocketHandler notificationWebSocketHandler;

    public WebSocketConfig(BidWebSocketHandler bidWebSocketHandler, 
                          NotificationWebSocketHandler notificationWebSocketHandler) {
        this.bidWebSocketHandler = bidWebSocketHandler;
        this.notificationWebSocketHandler = notificationWebSocketHandler;
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        // WebSocket endpoint cho đấu giá
        registry.addHandler(bidWebSocketHandler, "/ws/bid")
                .setAllowedOrigins("*"); // Cho phép mọi client connect
        
        // WebSocket endpoint cho thông báo
        registry.addHandler(notificationWebSocketHandler, "/ws/notification")
                .setAllowedOrigins("*"); // Cho phép mọi client connect
    }
}
