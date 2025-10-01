package com.example.auctionweb.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.web.socket.WebSocketHandler;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    private final WebSocketHandler bidWebSocketHandler;

    public WebSocketConfig(WebSocketHandler bidWebSocketHandler) {
        this.bidWebSocketHandler = bidWebSocketHandler;
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(bidWebSocketHandler, "/ws/bid")
                .setAllowedOrigins("*"); // Cho phép mọi client connect
    }
}
