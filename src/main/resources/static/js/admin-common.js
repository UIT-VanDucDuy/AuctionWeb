/**
 * Admin Panel Common JavaScript Functions
 */

// Live Clock Update
function updateClock() {
    const now = new Date();
    const options = { 
        day: '2-digit', 
        month: '2-digit', 
        year: 'numeric', 
        hour: '2-digit', 
        minute: '2-digit', 
        second: '2-digit' 
    };
    const clockElement = document.getElementById('live-clock');
    if (clockElement) {
        clockElement.textContent = now.toLocaleDateString('vi-VN', options);
    }
}

// Initialize clock on page load
document.addEventListener('DOMContentLoaded', function() {
    // Start live clock
    setInterval(updateClock, 1000);
    updateClock(); // Call immediately
    
    // Initialize tooltips if Bootstrap is available
    if (typeof bootstrap !== 'undefined') {
        const tooltipTriggerList = [].slice.call(document.querySelectorAll('[data-bs-toggle="tooltip"]'));
        tooltipTriggerList.map(function (tooltipTriggerEl) {
            return new bootstrap.Tooltip(tooltipTriggerEl);
        });
    }
});

// Search functionality for user/product lists
function initializeSearch(inputId, itemSelector, searchFields) {
    const searchInput = document.getElementById(inputId);
    if (!searchInput) return;
    
    searchInput.addEventListener('input', function() {
        const searchTerm = this.value.toLowerCase();
        const items = document.querySelectorAll(itemSelector);
        
        items.forEach(item => {
            let shouldShow = false;
            
            searchFields.forEach(field => {
                const fieldValue = item.dataset[field];
                if (fieldValue && fieldValue.toLowerCase().includes(searchTerm)) {
                    shouldShow = true;
                }
            });
            
            item.style.display = shouldShow ? '' : 'none';
        });
    });
}

// Confirmation dialog for actions
function confirmAction(message) {
    return confirm(message || 'Bạn có chắc chắn muốn thực hiện hành động này?');
}

// Show success message
function showSuccessMessage(message) {
    const alertDiv = document.createElement('div');
    alertDiv.className = 'alert alert-success alert-dismissible fade show position-fixed top-0 end-0 m-3';
    alertDiv.style.zIndex = '9999';
    alertDiv.innerHTML = `
        <i class="bi bi-check-circle"></i> ${message}
        <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
    `;
    document.body.appendChild(alertDiv);
    
    setTimeout(() => {
        alertDiv.remove();
    }, 3000);
}

// Show error message
function showErrorMessage(message) {
    const alertDiv = document.createElement('div');
    alertDiv.className = 'alert alert-danger alert-dismissible fade show position-fixed top-0 end-0 m-3';
    alertDiv.style.zIndex = '9999';
    alertDiv.innerHTML = `
        <i class="bi bi-exclamation-triangle"></i> ${message}
        <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
    `;
    document.body.appendChild(alertDiv);
    
    setTimeout(() => {
        alertDiv.remove();
    }, 5000);
}

// Format number with thousand separators
function formatNumber(num) {
    return num.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ",");
}

// Format currency (VND)
function formatCurrency(amount) {
    return formatNumber(amount) + ' ₫';
}

// Format date to Vietnamese format
function formatDate(date) {
    const d = new Date(date);
    const options = { 
        day: '2-digit', 
        month: '2-digit', 
        year: 'numeric',
        hour: '2-digit',
        minute: '2-digit'
    };
    return d.toLocaleDateString('vi-VN', options);
}

// Get status badge HTML
function getStatusBadge(status) {
    const statusMap = {
        'PENDING': { class: 'status-pending', text: 'Chờ duyệt' },
        'APPROVED': { class: 'status-approved', text: 'Đã duyệt' },
        'REJECTED': { class: 'status-rejected', text: 'Đã từ chối' },
        'SOLD': { class: 'status-sold', text: 'Đã bán' },
        'ACTIVE': { class: 'status-active', text: 'Hoạt động' },
        'INACTIVE': { class: 'status-inactive', text: 'Bị khóa' }
    };
    
    const statusInfo = statusMap[status] || { class: 'bg-secondary', text: status };
    return `<span class="status-badge ${statusInfo.class}">${statusInfo.text}</span>`;
}

// Export functions for use in other scripts
window.AdminCommon = {
    updateClock,
    initializeSearch,
    confirmAction,
    showSuccessMessage,
    showErrorMessage,
    formatNumber,
    formatCurrency,
    formatDate,
    getStatusBadge
};


