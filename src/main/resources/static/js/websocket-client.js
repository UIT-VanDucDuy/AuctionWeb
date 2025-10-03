/**
 * WebSocket Client cho Auction Web
 * Hỗ trợ kết nối đến Bid WebSocket và Notification WebSocket
 */

class AuctionWebSocketClient {
    constructor() {
        this.bidSocket = null;
        this.notificationSocket = null;
        this.isConnected = false;
        this.reconnectAttempts = 0;
        this.maxReconnectAttempts = 5;
        this.reconnectInterval = 3000; // 3 seconds
    }

    // Kết nối đến Bid WebSocket
    connectBidWebSocket() {
        const protocol = window.location.protocol === 'https:' ? 'wss:' : 'ws:';
        const wsUrl = `${protocol}//${window.location.host}/ws/bid`;
        
        try {
            this.bidSocket = new WebSocket(wsUrl);
            
            this.bidSocket.onopen = (event) => {
                console.log('✅ Bid WebSocket connected');
                this.isConnected = true;
                this.reconnectAttempts = 0;
                this.onBidConnectionOpen(event);
            };

            this.bidSocket.onmessage = (event) => {
                console.log('📨 Bid message received:', event.data);
                this.handleBidMessage(event.data);
            };

            this.bidSocket.onclose = (event) => {
                console.log('❌ Bid WebSocket disconnected:', event.code, event.reason);
                this.isConnected = false;
                this.onBidConnectionClose(event);
                this.attemptReconnect('bid');
            };

            this.bidSocket.onerror = (error) => {
                console.error('🚨 Bid WebSocket error:', error);
                this.onBidConnectionError(error);
            };

        } catch (error) {
            console.error('Failed to create Bid WebSocket connection:', error);
        }
    }

    // Kết nối đến Notification WebSocket
    connectNotificationWebSocket() {
        const protocol = window.location.protocol === 'https:' ? 'wss:' : 'ws:';
        const wsUrl = `${protocol}//${window.location.host}/ws/notification`;
        
        try {
            this.notificationSocket = new WebSocket(wsUrl);
            
            this.notificationSocket.onopen = (event) => {
                console.log('✅ Notification WebSocket connected');
                this.onNotificationConnectionOpen(event);
            };

            this.notificationSocket.onmessage = (event) => {
                console.log('📨 Notification message received:', event.data);
                this.handleNotificationMessage(event.data);
            };

            this.notificationSocket.onclose = (event) => {
                console.log('❌ Notification WebSocket disconnected:', event.code, event.reason);
                this.onNotificationConnectionClose(event);
                this.attemptReconnect('notification');
            };

            this.notificationSocket.onerror = (error) => {
                console.error('🚨 Notification WebSocket error:', error);
                this.onNotificationConnectionError(error);
            };

        } catch (error) {
            console.error('Failed to create Notification WebSocket connection:', error);
        }
    }

    // Gửi bid message
    sendBid(bidData) {
        if (this.bidSocket && this.bidSocket.readyState === WebSocket.OPEN) {
            const message = JSON.stringify(bidData);
            this.bidSocket.send(message);
            console.log('📤 Bid sent:', message);
        } else {
            console.error('❌ Bid WebSocket is not connected');
            this.showError('Không thể gửi đấu giá. Vui lòng kiểm tra kết nối.');
        }
    }

    // Xử lý message từ Bid WebSocket
    handleBidMessage(data) {
        try {
            const message = JSON.parse(data);
            
            switch (message.type) {
                case 'BID':
                    this.onNewBidReceived(message.data);
                    break;
                case 'SUCCESS':
                    this.showSuccess(message.message);
                    break;
                case 'ERROR':
                    this.showError(message.message);
                    break;
                default:
                    console.log('Unknown bid message type:', message.type);
            }
        } catch (error) {
            console.error('Error parsing bid message:', error);
        }
    }

    // Xử lý message từ Notification WebSocket
    handleNotificationMessage(data) {
        try {
            const message = JSON.parse(data);
            
            switch (message.type) {
                case 'NOTIFICATION':
                    this.onNotificationReceived(message.data);
                    break;
                case 'SUCCESS':
                    this.showSuccess(message.message);
                    break;
                case 'ERROR':
                    this.showError(message.message);
                    break;
                default:
                    console.log('Unknown notification message type:', message.type);
            }
        } catch (error) {
            console.error('Error parsing notification message:', error);
        }
    }

    // Thử kết nối lại
    attemptReconnect(type) {
        if (this.reconnectAttempts < this.maxReconnectAttempts) {
            this.reconnectAttempts++;
            console.log(`🔄 Attempting to reconnect ${type} WebSocket (${this.reconnectAttempts}/${this.maxReconnectAttempts})...`);
            
            setTimeout(() => {
                if (type === 'bid') {
                    this.connectBidWebSocket();
                } else if (type === 'notification') {
                    this.connectNotificationWebSocket();
                }
            }, this.reconnectInterval);
        } else {
            console.error(`❌ Max reconnection attempts reached for ${type} WebSocket`);
            this.showError('Mất kết nối. Vui lòng tải lại trang.');
        }
    }

    // Ngắt kết nối
    disconnect() {
        if (this.bidSocket) {
            this.bidSocket.close();
            this.bidSocket = null;
        }
        if (this.notificationSocket) {
            this.notificationSocket.close();
            this.notificationSocket = null;
        }
        this.isConnected = false;
        console.log('🔌 WebSocket connections closed');
    }

    // Callback methods - có thể override trong implementation
    onBidConnectionOpen(event) {
        // Override this method
    }

    onBidConnectionClose(event) {
        // Override this method
    }

    onBidConnectionError(error) {
        // Override this method
    }

    onNotificationConnectionOpen(event) {
        // Override this method
    }

    onNotificationConnectionClose(event) {
        // Override this method
    }

    onNotificationConnectionError(error) {
        // Override this method
    }

    onNewBidReceived(bidData) {
        // Override this method để xử lý bid mới
        console.log('New bid received:', bidData);
    }

    onNotificationReceived(notificationData) {
        // Override this method để xử lý notification mới
        console.log('New notification received:', notificationData);
    }

    showSuccess(message) {
        // Override this method để hiển thị thông báo thành công
        console.log('✅ Success:', message);
    }

    showError(message) {
        // Override this method để hiển thị thông báo lỗi
        console.error('❌ Error:', message);
    }
}

// Export cho sử dụng
window.AuctionWebSocketClient = AuctionWebSocketClient;
