package loipt.example.autionweb1.websocket;

import com.example.auctionweb.entity.BidHistory;
import com.example.auctionweb.service.IBidHistoryService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.*;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Component
public class BidWebSocketHandler extends TextWebSocketHandler {

    private final List<WebSocketSession> sessions = new CopyOnWriteArrayList<>();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private IBidHistoryService bidHistoryService;

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        sessions.add(session);
        System.out.println("Client connected: " + session.getId());
    }

    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        System.out.println("Received: " + message.getPayload());

        // Parse JSON từ client thành BidHistory
        BidHistory bid = objectMapper.readValue(message.getPayload(), BidHistory.class);

        // Lưu vào DB
        boolean saved = bidHistoryService.add(bid);
        if (saved) {
            // Nếu lưu thành công thì broadcast cho tất cả client
            broadcastNewBid(bid);
        } else {
            session.sendMessage(new TextMessage("Error: Không lưu được bid"));
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        sessions.remove(session);
        System.out.println("Client disconnected: " + session.getId());
    }

    // 🟢 Method để broadcast BidHistory sau khi lưu
    public void broadcastNewBid(BidHistory bid) throws Exception {
        String json = objectMapper.writeValueAsString(bid); // convert entity -> JSON
        for (WebSocketSession session : sessions) {
            if (session.isOpen()) {
                session.sendMessage(new TextMessage(json));
            }
        }
    }
}
