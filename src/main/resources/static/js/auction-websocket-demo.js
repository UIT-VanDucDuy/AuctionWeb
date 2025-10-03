/**
 * Demo sử dụng WebSocket cho Auction
 * Ví dụ implementation của AuctionWebSocketClient
 */

class AuctionWebSocketDemo extends AuctionWebSocketClient {
    constructor() {
        super();
        this.currentAuctionId = null;
        this.bidHistory = [];
        this.notifications = [];
    }

    // Khởi tạo demo
    init(auctionId) {
        this.currentAuctionId = auctionId;
        this.setupUI();
        this.connectWebSockets();
    }

    // Thiết lập UI
    setupUI() {
        // Tạo container cho bid history
        const bidContainer = document.getElementById('bid-history-container') || this.createBidContainer();
        
        // Tạo container cho notifications
        const notificationContainer = document.getElementById('notification-container') || this.createNotificationContainer();
        
        // Tạo form đấu giá
        const bidForm = document.getElementById('bid-form') || this.createBidForm();
        
        // Tạo status indicator
        const statusIndicator = document.getElementById('websocket-status') || this.createStatusIndicator();
    }

    // Tạo container cho bid history
    createBidContainer() {
        const container = document.createElement('div');
        container.id = 'bid-history-container';
        container.innerHTML = `
            <div class="card">
                <div class="card-header">
                    <h5>Lịch sử đấu giá</h5>
                </div>
                <div class="card-body">
                    <div id="bid-list" class="list-group"></div>
                </div>
            </div>
        `;
        document.body.appendChild(container);
        return container;
    }

    // Tạo container cho notifications
    createNotificationContainer() {
        const container = document.createElement('div');
        container.id = 'notification-container';
        container.innerHTML = `
            <div class="card">
                <div class="card-header">
                    <h5>Thông báo</h5>
                </div>
                <div class="card-body">
                    <div id="notification-list" class="list-group"></div>
                </div>
            </div>
        `;
        document.body.appendChild(container);
        return container;
    }

    // Tạo form đấu giá
    createBidForm() {
        const form = document.createElement('form');
        form.id = 'bid-form';
        form.innerHTML = `
            <div class="card">
                <div class="card-header">
                    <h5>Đấu giá</h5>
                </div>
                <div class="card-body">
                    <div class="form-group">
                        <label for="bid-amount">Số tiền đấu giá:</label>
                        <input type="number" id="bid-amount" class="form-control" min="0" step="1000" required>
                    </div>
                    <div class="form-group">
                        <label for="bidder-name">Tên người đấu giá:</label>
                        <input type="text" id="bidder-name" class="form-control" required>
                    </div>
                    <button type="submit" class="btn btn-primary">Đấu giá</button>
                </div>
            </div>
        `;
        
        form.addEventListener('submit', (e) => {
            e.preventDefault();
            this.submitBid();
        });
        
        document.body.appendChild(form);
        return form;
    }

    // Tạo status indicator
    createStatusIndicator() {
        const indicator = document.createElement('div');
        indicator.id = 'websocket-status';
        indicator.className = 'alert alert-info';
        indicator.innerHTML = `
            <strong>Trạng thái WebSocket:</strong> 
            <span id="connection-status">Đang kết nối...</span>
        `;
        document.body.insertBefore(indicator, document.body.firstChild);
        return indicator;
    }

    // Kết nối WebSockets
    connectWebSockets() {
        this.connectBidWebSocket();
        this.connectNotificationWebSocket();
    }

    // Submit bid
    submitBid() {
        const amount = document.getElementById('bid-amount').value;
        const bidderName = document.getElementById('bidder-name').value;
        
        if (!amount || !bidderName) {
            this.showError('Vui lòng nhập đầy đủ thông tin');
            return;
        }

        const bidData = {
            amount: parseFloat(amount),
            user: {
                id: 1, // Mock user ID
                username: bidderName
            },
            auction: {
                id: this.currentAuctionId
            },
            time: new Date().toISOString()
        };

        this.sendBid(bidData);
        
        // Clear form
        document.getElementById('bid-amount').value = '';
        document.getElementById('bidder-name').value = '';
    }

    // Override callback methods
    onBidConnectionOpen(event) {
        this.updateConnectionStatus('Đã kết nối Bid WebSocket', 'success');
    }

    onBidConnectionClose(event) {
        this.updateConnectionStatus('Mất kết nối Bid WebSocket', 'danger');
    }

    onBidConnectionError(error) {
        this.updateConnectionStatus('Lỗi Bid WebSocket', 'danger');
    }

    onNotificationConnectionOpen(event) {
        this.updateConnectionStatus('Đã kết nối Notification WebSocket', 'success');
    }

    onNotificationConnectionClose(event) {
        this.updateConnectionStatus('Mất kết nối Notification WebSocket', 'warning');
    }

    onNotificationConnectionError(error) {
        this.updateConnectionStatus('Lỗi Notification WebSocket', 'danger');
    }

    onNewBidReceived(bidData) {
        this.bidHistory.unshift(bidData);
        this.updateBidHistory();
        const bidderName = bidData.user ? bidData.user.username : 'Unknown';
        const amount = bidData.amount || 0;
        this.showSuccess(`Đấu giá mới: ${bidderName} - ${amount.toLocaleString()} VND`);
    }

    onNotificationReceived(notificationData) {
        this.notifications.unshift(notificationData);
        this.updateNotifications();
        this.showSuccess(`Thông báo mới: ${notificationData.title}`);
    }

    showSuccess(message) {
        this.showAlert(message, 'success');
    }

    showError(message) {
        this.showAlert(message, 'danger');
    }

    // Cập nhật trạng thái kết nối
    updateConnectionStatus(message, type) {
        const statusElement = document.getElementById('connection-status');
        const statusContainer = document.getElementById('websocket-status');
        
        if (statusElement) {
            statusElement.textContent = message;
        }
        
        if (statusContainer) {
            statusContainer.className = `alert alert-${type}`;
        }
    }

    // Cập nhật lịch sử đấu giá
    updateBidHistory() {
        const bidList = document.getElementById('bid-list');
        if (!bidList) return;

        bidList.innerHTML = this.bidHistory.map(bid => `
            <div class="list-group-item">
                <div class="d-flex w-100 justify-content-between">
                    <h6 class="mb-1">${bid.user ? bid.user.username : 'Unknown'}</h6>
                    <small>${new Date(bid.time).toLocaleString()}</small>
                </div>
                <p class="mb-1"><strong>${(bid.amount || 0).toLocaleString()} VND</strong></p>
            </div>
        `).join('');
    }

    // Cập nhật thông báo
    updateNotifications() {
        const notificationList = document.getElementById('notification-list');
        if (!notificationList) return;

        notificationList.innerHTML = this.notifications.map(notification => `
            <div class="list-group-item">
                <div class="d-flex w-100 justify-content-between">
                    <h6 class="mb-1">${notification.title}</h6>
                    <small>${new Date(notification.createdAt).toLocaleString()}</small>
                </div>
                <p class="mb-1">${notification.content}</p>
            </div>
        `).join('');
    }

    // Hiển thị alert
    showAlert(message, type) {
        const alert = document.createElement('div');
        alert.className = `alert alert-${type} alert-dismissible fade show`;
        alert.innerHTML = `
            ${message}
            <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
        `;
        
        document.body.insertBefore(alert, document.body.firstChild);
        
        // Auto remove after 5 seconds
        setTimeout(() => {
            if (alert.parentNode) {
                alert.parentNode.removeChild(alert);
            }
        }, 5000);
    }
}

// Khởi tạo demo khi trang load
document.addEventListener('DOMContentLoaded', function() {
    const demo = new AuctionWebSocketDemo();
    
    // Lấy auction ID từ URL hoặc set mặc định
    const urlParams = new URLSearchParams(window.location.search);
    const auctionId = urlParams.get('auctionId') || 1;
    
    demo.init(auctionId);
    
    // Export để có thể sử dụng từ console
    window.auctionDemo = demo;
});

// Export class
window.AuctionWebSocketDemo = AuctionWebSocketDemo;
