
-- Insert data into admin_account
INSERT INTO admin_account (admin_id, email, full_name, password_hash) VALUES
(1, 'admin@example.com', 'Trần Nguyễn Phúc Khang', 'hashed_password_1');

-- Insert data into users
INSERT INTO users (username, email, address, password_hash, full_name, status) VALUES
('user1', 'user1@example.com', '123 Main St', 'hashed_password_2', 'User One', 'online'),
('user2', 'user2@example.com', '456 Elm St', 'hashed_password_3', 'User Two', 'offline'),
('phuckhang', 'khang@example.com', '123 Đường ABC, Hà Nội', 'f9wihc93w8bveiu9u', 'Phúc Khang', 'online'),
('letriman', 'man@example.com', '456 Đường DEF, TP.HCM', 'hashed_password_4', 'Lê Trí Mẩn', 'offline'),
('nguyenvana', 'vana@example.com', '789 Đường GHI, Đà Nẵng', 'hashed_password_5', 'Nguyễn Văn A', 'online'),
('tranthib', 'thib@example.com', '101 Đường JKL, Cần Thơ', 'hashed_password_6', 'Trần Thị B', 'offline'),
('levanc', 'vanc@example.com', '202 Đường MNO, Hải Phòng', 'hashed_password_7', 'Lê Văn C', 'online'),
('phamthid', 'thid@example.com', '303 Đường PQR, Huế', 'hashed_password_8', 'Phạm Thị D', 'offline'),
('nguyenvanf', 'vanf@example.com', '404 Đường STU, Nha Trang', 'hashed_password_9', 'Nguyễn Văn F', 'online'),
('tranthig', 'thig@example.com', '505 Đường VWX, Vũng Tàu', 'hashed_password_10', 'Trần Thị G', 'offline'),
('levanh', 'vanh@example.com', '606 Đường YZ, Quy Nhơn', 'hashed_password_11', 'Lê Văn H', 'online'),
('phamthii', 'thii@example.com', '707 Đường ABC, Biên Hòa', 'hashed_password_12', 'Phạm Thị I', 'offline'),
('hoangvane', 'vane@example.com', '808 Đường DEF, Buôn Ma Thuột', 'hashed_password_13', 'Hoàng Văn E', 'online');
-- Insert data into log_history
INSERT INTO log_history (log_id, user_id, section_start, section_end) VALUES
(1, 1, '2023-01-01 10:00:00', '2023-01-01 11:00:00'),
(2, 2, '2023-01-02 12:00:00', '2023-01-02 13:00:00');

-- Insert data into friend_request
INSERT INTO friend_request (user_id, friend_id, status) VALUES
(1, 2, 'pending'),
(2, 1, 'accepted');

-- Insert data into friendships
INSERT INTO friendships (user1_id, user2_id) VALUES
(1, 2);

-- Insert data into Blocked_List
INSERT INTO Blocked_List (blocker_id, blocked_id) VALUES
(2, 1);

-- Insert data into Report
INSERT INTO Report (reporter_id, reported_id, reason) VALUES
(1, 2, 'Inappropriate behavior');
-- (1, "Phuc Khang", "SpamLover", "Spamming", "22/11/2024", "Pending"),
-- (2, "Man the Man", "RuleBreaker", "Inappropriate Content", "23/11/2024", "Resolved"),
-- (3, "Van A", "Cheater", "Hacking", "24/11/2024", "Pending"),
-- (4, "Thi B", "Spammer", "Spamming", "25/11/2024", "Pending"),
-- (5, "Van C", "Hacker", "Hacking", "26/11/2024", "Resolved"),
-- (6, "Thi D", "Abuser", "Abuse", "27/11/2024", "Pending"),
-- (7, "Van F", "Spammer", "Spamming", "28/11/2024", "Resolved"),
-- (8, "Thi G", "Cheater", "Hacking", "29/11/2024", "Pending"),
-- (9, "Van H", "RuleBreaker", "Inappropriate Content", "30/11/2024", "Resolved"),
-- (10, "Thi I", "Abuser", "Abuse", "01/12/2024", "Pending")

-- Insert data into chats
INSERT INTO chats (chat_id, group_name, chat_type, admin_id) VALUES
(1, 'General Chat', 'group', 1),
(2, "The amazing group of football", 'private', NULL);

-- Insert data into chat_members
INSERT INTO chat_members (chat_id, user_id) VALUES
(1, 1),
(1, 2),
(2, 1),
(2, 2);

-- Insert data into messages
INSERT INTO messages (chat_id, sender_id, content) VALUES
(1, 1, 'Hello everyone!'),
(1, 2, 'Hi there!'),
(2, 1, 'Private message content');