-- =============================================
-- Auction Web Application Database Setup
-- =============================================

-- Create database
CREATE DATABASE IF NOT EXISTS demoweb;
USE demoweb;

-- =============================================
-- Drop existing tables (if any)
-- =============================================
DROP TABLE IF EXISTS bidhistory;
DROP TABLE IF EXISTS auctionregistration;
DROP TABLE IF EXISTS auction;
DROP TABLE IF EXISTS notification;
DROP TABLE IF EXISTS product;
DROP TABLE IF EXISTS category;
DROP TABLE IF EXISTS user;
DROP TABLE IF EXISTS account;

-- =============================================
-- Create Tables
-- =============================================

-- Account table
CREATE TABLE account (
    id INT(11) NOT NULL AUTO_INCREMENT,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role ENUM('ADMIN', 'USER', 'SELLER') DEFAULT 'USER',
    active BOOLEAN DEFAULT TRUE,
    PRIMARY KEY (id)
);

-- User table
CREATE TABLE user (
    id INT(11) NOT NULL AUTO_INCREMENT,
    account_id INT(11) NOT NULL,
    name VARCHAR(100),
    email VARCHAR(100),
    phone VARCHAR(20),
    address VARCHAR(255),
    PRIMARY KEY (id),
    FOREIGN KEY (account_id) REFERENCES account(id) ON DELETE CASCADE
);

-- Category table
CREATE TABLE category (
    id INT(11) NOT NULL AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL UNIQUE,
    PRIMARY KEY (id)
);

-- Product table
CREATE TABLE product (
    id INT(11) NOT NULL AUTO_INCREMENT,
    seller_id INT(11) NOT NULL,
    owner_id INT(11),
    name VARCHAR(200) NOT NULL,
    description TEXT,
    starting_price DECIMAL(15,2),
    image_url VARCHAR(255),
    category_id INT(11),
    requested_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    status ENUM('PENDING', 'APPROVED', 'REJECTED', 'SOLD') DEFAULT 'PENDING',
    PRIMARY KEY (id),
    FOREIGN KEY (seller_id) REFERENCES user(id) ON DELETE CASCADE,
    FOREIGN KEY (owner_id) REFERENCES user(id) ON DELETE SET NULL,
    FOREIGN KEY (category_id) REFERENCES category(id) ON DELETE SET NULL
);

-- Auction table
CREATE TABLE auction (
    id INT(11) NOT NULL AUTO_INCREMENT,
    product_id INT(11) NOT NULL,
    start_time DATETIME,
    end_time DATETIME,
    starting_price DECIMAL(15,2),
    PRIMARY KEY (id),
    FOREIGN KEY (product_id) REFERENCES product(id) ON DELETE CASCADE
);

-- Auction Registration table
CREATE TABLE auctionregistration (
    id INT(11) NOT NULL AUTO_INCREMENT,
    auction_id INT(11) NOT NULL,
    user_id INT(11) NOT NULL,
    status ENUM('PENDING', 'APPROVED', 'REJECTED') DEFAULT 'PENDING',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    FOREIGN KEY (auction_id) REFERENCES auction(id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES user(id) ON DELETE CASCADE
);

-- Bid History table
CREATE TABLE bidhistory (
    id INT(11) NOT NULL AUTO_INCREMENT,
    auction_id INT(11) NOT NULL,
    user_id INT(11) NOT NULL,
    amount DECIMAL(15,2) NOT NULL,
    time DATETIME DEFAULT CURRENT_TIMESTAMP,
    winner_flag BOOLEAN DEFAULT FALSE,
    PRIMARY KEY (id),
    FOREIGN KEY (auction_id) REFERENCES auction(id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES user(id) ON DELETE CASCADE
);

-- Notification table
CREATE TABLE notification (
    id INT(11) NOT NULL AUTO_INCREMENT,
    user_id INT(11) NOT NULL,
    notification TEXT,
    time DATETIME DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    FOREIGN KEY (user_id) REFERENCES user(id) ON DELETE CASCADE
);

-- =============================================
-- Insert Sample Data
-- =============================================

-- Insert Categories
INSERT INTO category (name) VALUES 
('Điện tử'),
('Thời trang'),
('Gia dụng'),
('Sách'),
('Thể thao'),
('Nghệ thuật'),
('Đồ cổ'),
('Xe cộ');

-- Insert Accounts
INSERT INTO account (username, password, role, active) VALUES 
('admin', '$2a$10$2bi2Y1CSp.wiY.YSDU31JeGZOAS3wwFuiFv0ji.MxS4iKSJjE67TO', 'ADMIN', TRUE), -- password: 123
('seller1', '$2a$10$2bi2Y1CSp.wiY.YSDU31JeGZOAS3wwFuiFv0ji.MxS4iKSJjE67TO', 'SELLER', TRUE), -- password: 123
('seller2', '$2a$10$2bi2Y1CSp.wiY.YSDU31JeGZOAS3wwFuiFv0ji.MxS4iKSJjE67TO', 'SELLER', TRUE), -- password: 123
('user1', '$2a$10$2bi2Y1CSp.wiY.YSDU31JeGZOAS3wwFuiFv0ji.MxS4iKSJjE67TO', 'USER', TRUE), -- password: 123
('user2', '$2a$10$2bi2Y1CSp.wiY.YSDU31JeGZOAS3wwFuiFv0ji.MxS4iKSJjE67TO', 'USER', TRUE), -- password: 123
('user3', '$2a$10$2bi2Y1CSp.wiY.YSDU31JeGZOAS3wwFuiFv0ji.MxS4iKSJjE67T', 'USER', TRUE), -- password: 123
('user4', '$2a$10$2bi2Y1CSp.wiY.YSDU31JeGZOAS3wwFuiFv0ji.MxS4iKSJjE67TO', 'USER', FALSE); -- password: 123 (inactive)

-- Insert Users
INSERT INTO user (account_id, name, email, phone, address) VALUES 
(1, 'Administrator', 'admin@auction.com', '0123456789', 'Hà Nội'),
(2, 'Nguyễn Văn Bán', 'seller1@email.com', '0987654321', 'TP.HCM'),
(3, 'Trần Thị Bán', 'seller2@email.com', '0912345678', 'Đà Nẵng'),
(4, 'Lê Văn Mua', 'user1@email.com', '0923456789', 'Hải Phòng'),
(5, 'Phạm Thị Mua', 'user2@email.com', '0934567890', 'Cần Thơ'),
(6, 'Hoàng Văn Đấu', 'user3@email.com', '0945678901', 'Nha Trang'),
(7, 'Vũ Thị Tạm', 'user4@email.com', '0956789012', 'Huế');

-- Insert Products
INSERT INTO product (seller_id, owner_id, name, description, starting_price, image_url, category_id, requested_at, status) VALUES 
(2, 2, 'iPhone 15 Pro Max', 'Điện thoại iPhone 15 Pro Max 256GB, màu Titan tự nhiên, còn bảo hành Apple', 25000000, '/images/iphone15.jpg', 1, '2024-10-15 10:00:00', 'APPROVED'),
(2, 2, 'MacBook Air M2', 'Laptop MacBook Air 13 inch M2 chip, 8GB RAM, 256GB SSD, màu Space Gray', 28000000, '/images/macbook.jpg', 1, '2024-10-16 14:30:00', 'APPROVED'),
(3, 3, 'Áo khoác da', 'Áo khoác da bò thật, size L, màu đen, chất lượng cao', 1500000, '/images/jacket.jpg', 2, '2024-10-17 09:15:00', 'PENDING'),
(3, 3, 'Túi xách Gucci', 'Túi xách Gucci chính hãng, màu đen, tình trạng tốt', 12000000, '/images/gucci.jpg', 2, '2024-10-18 16:45:00', 'APPROVED'),
(2, 2, 'Bộ bàn ghế gỗ', 'Bộ bàn ghế gỗ lim, 6 ghế, tình trạng tốt', 8000000, '/images/table.jpg', 3, '2024-10-19 11:20:00', 'REJECTED'),
(3, 3, 'Sách "Đắc Nhân Tâm"', 'Sách Đắc Nhân Tâm bản gốc, tình trạng mới', 150000, '/images/book.jpg', 4, '2024-10-20 08:30:00', 'APPROVED');

-- Insert Auctions
INSERT INTO auction (product_id, start_time, end_time, starting_price) VALUES
(1, 1, '2024-10-25 09:00:00', '2025-10-25 18:00:00', 25000000.00, 'FINISHED', 4),
(2, 2, '2024-10-26 10:00:00', '2024-10-26 19:00:00', 28000000.00, 'FINISHED', 4),
(3, 4, '2024-10-27 14:00:00', '2024-10-27 20:00:00', 12000000.00, 'FINISHED', 4),
(4, 6, '2024-10-28 15:00:00', '2025-10-28 23:49:00', 150000.00, 'FINISHED', 4),
(5, 5, '2024-10-29 15:00:00', '2025-10-30 21:00:00', 150000.00, 'FINISHED', NULL),
(6, 3, '2025-11-04 15:00:00', '2025-11-05 15:00:00', 150000.00, 'PENDING', NULL),
(7, 7, '2025-11-04 15:00:00', '2025-11-05 15:00:00', 800000000.00, 'PENDING', NULL),
(8, 8, '2025-11-03 15:00:00', '2025-11-04 15:00:00', 70000000.00, 'ONGOING', NULL),
(9, 9, '2025-11-04 15:00:00', '2025-11-05 15:00:00', 15000000.00, 'PENDING', NULL),
(10, 10, '2025-11-05 15:00:00', '2025-11-07 15:00:00', 4000000.00, 'PENDING', NULL),
(11, 11, '2025-11-02 15:00:00', '2025-11-07 15:00:00', 5000000.00, 'ONGOING', NULL),
(12, 12, '2025-11-03 15:00:00', '2025-11-05 15:00:00', 6000000.00, 'ONGOING', NULL),
(13, 13, '2025-11-03 15:00:00', '2025-11-04 15:00:00', 7000000.00, 'ONGOING', NULL),
(14, 14, '2025-11-05 15:00:00', '2025-11-06 15:00:00', 20000000.00, 'PENDING', NULL),
(15, 15, '2025-11-03 15:00:00', '2025-11-06 15:00:00', 600000000.00, 'ONGOING', NULL),
(16, 16, '2025-11-03 15:00:00', '2025-11-05 15:00:00', 8000000.00, 'ONGOING', NULL),
(17, 17, '2025-11-03 15:00:00', '2025-11-05 15:00:00', 200000000.00, 'ONGOING', NULL);

-- Insert Auction Registrations
INSERT INTO auctionregistration (auction_id, user_id, status, created_at) VALUES
(1, 1, 4, 'APPROVED', '2024-10-20 10:00:00'),
(2, 1, 5, 'APPROVED', '2024-10-20 10:30:00'),
(3, 1, 6, 'PENDING', '2024-10-20 11:00:00'),
(4, 2, 4, 'APPROVED', '2024-10-21 09:00:00'),
(5, 2, 5, 'APPROVED', '2024-10-21 09:30:00'),
(6, 3, 4, 'APPROVED', '2024-10-22 08:00:00'),
(7, 3, 5, 'APPROVED', '2024-10-22 08:30:00'),
(8, 3, 6, 'REJECTED', '2024-10-22 09:00:00'),
(9, 4, 4, 'APPROVED', '2024-10-23 07:00:00'),
(10, 4, 5, 'PENDING', '2024-10-23 07:30:00'),
(11, 16, 4, 'APPROVED', '2025-11-03 18:12:47'),
(12, 7, 3, 'APPROVED', '2025-11-03 18:13:02'),
(13, 8, 5, 'APPROVED', '2025-11-03 18:13:44'),
(14, 12, 4, 'APPROVED', '2025-11-03 18:13:44'),
(15, 11, 3, 'APPROVED', '2025-11-03 18:13:44'),
(16, 15, 4, 'APPROVED', '2025-11-03 18:13:44'),
(17, 13, 6, 'APPROVED', '2025-11-03 18:13:44');

-- Insert Bid History
INSERT INTO bidhistory (auction_id, user_id, amount, time, winner_flag) VALUES 
(1, 4, 25000000, '2024-10-25 09:05:00', FALSE),
(1, 5, 25500000, '2024-10-25 09:10:00', FALSE),
(1, 4, 26000000, '2024-10-25 09:15:00', FALSE),
(1, 5, 26500000, '2024-10-25 09:20:00', TRUE),
(2, 4, 28000000, '2024-10-26 10:05:00', FALSE),
(2, 5, 28500000, '2024-10-26 10:10:00', FALSE),
(2, 4, 29000000, '2024-10-26 10:15:00', TRUE),
(3, 4, 12000000, '2024-10-27 14:05:00', FALSE),
(3, 5, 12500000, '2024-10-27 14:10:00', FALSE),
(3, 4, 13000000, '2024-10-27 14:15:00', TRUE),
(4, 4, 150000, '2024-10-28 15:05:00', FALSE),
(4, 5, 160000, '2024-10-28 15:10:00', FALSE),
(4, 4, 170000, '2024-10-28 15:15:00', TRUE);

-- Insert Notifications
INSERT INTO notification (user_id, notification, time) VALUES 
(2, 'Sản phẩm "iPhone 15 Pro Max" của bạn đã được duyệt và sẽ được đấu giá vào 25/10/2024', '2024-10-20 10:00:00'),
(2, 'Sản phẩm "MacBook Air M2" của bạn đã được duyệt và sẽ được đấu giá vào 26/10/2024', '2024-10-21 10:00:00'),
(3, 'Sản phẩm "Áo khoác da" của bạn đang chờ duyệt', '2024-10-17 09:15:00'),
(3, 'Sản phẩm "Túi xách Gucci" của bạn đã được duyệt và sẽ được đấu giá vào 27/10/2024', '2024-10-22 10:00:00'),
(2, 'Sản phẩm "Bộ bàn ghế gỗ" của bạn đã bị từ chối', '2024-10-19 11:20:00'),
(3, 'Sản phẩm "Sách Đắc Nhân Tâm" của bạn đã được duyệt và sẽ được đấu giá vào 28/10/2024', '2024-10-23 10:00:00'),
(4, 'Bạn đã thắng đấu giá sản phẩm "iPhone 15 Pro Max" với giá 26,500,000 VNĐ', '2024-10-25 18:00:00'),
(4, 'Bạn đã thắng đấu giá sản phẩm "MacBook Air M2" với giá 29,000,000 VNĐ', '2024-10-26 19:00:00'),
(4, 'Bạn đã thắng đấu giá sản phẩm "Túi xách Gucci" với giá 13,000,000 VNĐ', '2024-10-27 20:00:00'),
(4, 'Bạn đã thắng đấu giá sản phẩm "Sách Đắc Nhân Tâm" với giá 170,000 VNĐ', '2024-10-28 21:00:00'),
(5, 'Bạn đã thua đấu giá sản phẩm "iPhone 15 Pro Max"', '2024-10-25 18:00:00'),
(5, 'Bạn đã thua đấu giá sản phẩm "MacBook Air M2"', '2024-10-26 19:00:00'),
(5, 'Bạn đã thua đấu giá sản phẩm "Túi xách Gucci"', '2024-10-27 20:00:00'),
(5, 'Bạn đã thua đấu giá sản phẩm "Sách Đắc Nhân Tâm"', '2024-10-28 21:00:00');

-- =============================================
-- Create Indexes for Performance
-- =============================================
CREATE INDEX idx_user_account_id ON user(account_id);
CREATE INDEX idx_product_seller_id ON product(seller_id);
CREATE INDEX idx_product_category_id ON product(category_id);
CREATE INDEX idx_product_status ON product(status);
CREATE INDEX idx_auction_product_id ON auction(product_id);
CREATE INDEX idx_auctionregistration_auction_id ON auctionregistration(auction_id);
CREATE INDEX idx_auctionregistration_user_id ON auctionregistration(user_id);
CREATE INDEX idx_auctionregistration_status ON auctionregistration(status);
CREATE INDEX idx_bidhistory_auction_id ON bidhistory(auction_id);
CREATE INDEX idx_bidhistory_user_id ON bidhistory(user_id);
CREATE INDEX idx_bidhistory_time ON bidhistory(time);
CREATE INDEX idx_notification_user_id ON notification(user_id);
CREATE INDEX idx_notification_time ON notification(time);

-- =============================================
-- Sample Queries for Testing
-- =============================================

-- View all users with their account information
-- SELECT u.id, u.name, u.email, a.username, a.role, a.active 
-- FROM user u 
-- JOIN account a ON u.account_id = a.id;

-- View all products with seller and category information
-- SELECT p.id, p.name, p.starting_price, p.status, 
--        u.name as seller_name, c.name as category_name
-- FROM product p 
-- JOIN user u ON p.seller_id = u.id 
-- LEFT JOIN category c ON p.category_id = c.id;

-- View auction registrations with user and auction details
-- SELECT ar.id, ar.status, ar.created_at,
--        u.name as user_name, p.name as product_name, a.start_time, a.end_time
-- FROM auctionregistration ar
-- JOIN user u ON ar.user_id = u.id
-- JOIN auction a ON ar.auction_id = a.id
-- JOIN product p ON a.product_id = p.id;

-- View bid history with user and product information
-- SELECT bh.id, bh.amount, bh.time, bh.winner_flag,
--        u.name as bidder_name, p.name as product_name
-- FROM bidhistory bh
-- JOIN user u ON bh.user_id = u.id
-- JOIN auction a ON bh.auction_id = a.id
-- JOIN product p ON a.product_id = p.id
-- ORDER BY bh.time DESC;

-- =============================================
-- Database Setup Complete
-- =============================================
SELECT 'Database setup completed successfully!' as status;
