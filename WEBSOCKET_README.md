# WebSocket Implementation cho Auction Web

## Tổng quan

Dự án đã được tích hợp WebSocket để hỗ trợ real-time communication cho:
- **Bid WebSocket**: Xử lý đấu giá real-time
- **Notification WebSocket**: Gửi thông báo real-time

## Cấu trúc WebSocket

### 1. Backend Components

#### WebSocket Handlers
- `BidWebSocketHandler`: Xử lý đấu giá real-time
- `NotificationWebSocketHandler`: Xử lý thông báo real-time

#### Configuration
- `WebSocketConfig`: Cấu hình WebSocket endpoints
- `WebSocketMessage`: DTO cho WebSocket messages

#### Controller
- `WebSocketController`: REST API để quản lý WebSocket

### 2. Frontend Components

#### JavaScript Classes
- `AuctionWebSocketClient`: Base class cho WebSocket client
- `AuctionWebSocketDemo`: Demo implementation

#### Demo Page
- `websocket-demo.html`: Trang demo WebSocket

## WebSocket Endpoints

### Bid WebSocket
- **URL**: `ws://localhost:8080/ws/bid`
- **Chức năng**: Xử lý đấu giá real-time
- **Message Format**:
```json
{
  "type": "BID",
  "data": {
    "amount": 1000000,
    "user": {
      "id": 1,
      "username": "bidder_name"
    },
    "auction": {
      "id": 1
    },
    "time": "2024-01-01T10:00:00"
  },
  "auctionId": 1,
  "timestamp": "2024-01-01T10:00:00"
}
```

### Notification WebSocket
- **URL**: `ws://localhost:8080/ws/notification`
- **Chức năng**: Gửi thông báo real-time
- **Message Format**:
```json
{
  "type": "NOTIFICATION",
  "data": {
    "title": "Thông báo",
    "content": "Nội dung thông báo",
    "createdAt": 1640995200000
  },
  "timestamp": "2024-01-01T10:00:00"
}
```

## Cách sử dụng

### 1. Chạy ứng dụng
```bash
./gradlew bootRun
```

### 2. Truy cập demo
Mở trình duyệt và truy cập: `http://localhost:8080/websocket-demo.html`

### 3. Test WebSocket
1. Mở nhiều tab để test real-time updates
2. Nhập thông tin đấu giá và click "Đấu giá"
3. Quan sát real-time updates trên tất cả các tab

### 4. Sử dụng trong code

#### JavaScript Client
```javascript
// Tạo WebSocket client
const client = new AuctionWebSocketClient();

// Kết nối
client.connectBidWebSocket();
client.connectNotificationWebSocket();

// Gửi đấu giá
client.sendBid({
    amount: 1000000,
    user: { id: 1, username: "bidder" },
    auction: { id: 1 },
    time: new Date().toISOString()
});

// Override callbacks
client.onNewBidReceived = (bidData) => {
    console.log('New bid:', bidData);
};
```

#### REST API
```bash
# Lấy trạng thái WebSocket
GET /api/websocket/status

# Gửi thông báo test
POST /api/websocket/test-notification
Content-Type: application/json

{
  "title": "Test Notification",
  "content": "This is a test notification"
}
```

## Tính năng

### ✅ Đã implement
- [x] Bid WebSocket với real-time updates
- [x] Notification WebSocket
- [x] Error handling và logging
- [x] Auto-reconnection
- [x] Message validation
- [x] Demo page với UI
- [x] REST API để quản lý WebSocket

### 🔄 Có thể mở rộng
- [ ] Authentication cho WebSocket
- [ ] Room-based messaging (theo auction ID)
- [x] User-specific notifications
- [ ] Message persistence
- [ ] Rate limiting
- [ ] WebSocket metrics

## Dependencies

Đã thêm vào `build.gradle`:
```gradle
implementation 'org.springframework.boot:spring-boot-starter-websocket'
implementation 'com.fasterxml.jackson.core:jackson-databind'
```

## Troubleshooting

### Lỗi thường gặp

1. **WebSocket không kết nối được**
   - Kiểm tra server đã chạy chưa
   - Kiểm tra firewall/proxy settings
   - Xem console log để debug

2. **Message không được gửi**
   - Kiểm tra WebSocket connection status
   - Kiểm tra message format
   - Xem server logs

3. **Real-time updates không hoạt động**
   - Kiểm tra WebSocket handlers
   - Kiểm tra database connection
   - Xem browser console

### Debug
```javascript
// Trong browser console
console.log(window.auctionDemo); // Xem demo instance
console.log(window.auctionDemo.getConnectedClientsCount()); // Số clients
```

## Tài liệu tham khảo

- [Spring WebSocket Documentation](https://docs.spring.io/spring-framework/docs/current/reference/html/web.html#websocket)
- [WebSocket API](https://developer.mozilla.org/en-US/docs/Web/API/WebSocket)
- [Bootstrap 5](https://getbootstrap.com/docs/5.3/)
