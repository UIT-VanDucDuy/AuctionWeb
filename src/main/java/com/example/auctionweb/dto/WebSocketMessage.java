package com.example.auctionweb.dto;

import com.example.auctionweb.entity.Auction;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class WebSocketMessage {
    private String type; // "BID", "NOTIFICATION", "ERROR", "SUCCESS"
    private Object data;
    private String message;
    private Auction auction;
    private String timestamp;

    public WebSocketMessage(String type, Object data) {
        this.type = type;
        this.data = data;
        this.timestamp = java.time.Instant.now().toString();
    }

    public WebSocketMessage(String type, String message) {
        this.type = type;
        this.message = message;
        this.timestamp = java.time.Instant.now().toString();
    }
}
