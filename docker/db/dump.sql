-- MySQL dump 10.13  Distrib 9.1.0, for Linux (x86_64)
--
-- Host: localhost    Database: chat_application
-- ------------------------------------------------------
-- Server version	9.1.0

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--

-- Dumping data for table `Blocked_List`
--

LOCK TABLES `Blocked_List` WRITE;
/*!40000 ALTER TABLE `Blocked_List` DISABLE KEYS */;
INSERT INTO `Blocked_List` VALUES (1,2,1,'2024-12-05 11:58:21');
/*!40000 ALTER TABLE `Blocked_List` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `Report`
--


--
-- Dumping data for table `Report`
--

LOCK TABLES `Report` WRITE;
/*!40000 ALTER TABLE `Report` DISABLE KEYS */;
INSERT INTO `Report` VALUES (1,3,2,'Spam','pending','2024-12-05 11:58:21'),(2,4,1,'Quảng cáo','pending','2024-12-05 15:19:45'),(3,5,7,'Khác','pending','2024-12-05 15:19:45'),(4,10,9,'Hành vi không phù hợp','resolved','2024-12-05 15:20:34');
/*!40000 ALTER TABLE `Report` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `admin_account`
--

--
-- Dumping data for table `admin_account`
--

LOCK TABLES `admin_account` WRITE;
/*!40000 ALTER TABLE `admin_account` DISABLE KEYS */;
INSERT INTO `admin_account` VALUES (1,'admin@example.com','Tráº§n Nguyá»…n PhÃºc Khang','hashed_password_1','2024-12-05 11:58:21');
/*!40000 ALTER TABLE `admin_account` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `chat_members`
--


--
-- Dumping data for table `chat_members`
--

LOCK TABLES `chat_members` WRITE;
/*!40000 ALTER TABLE `chat_members` DISABLE KEYS */;
INSERT INTO `chat_members` VALUES (1,1,'2024-12-05 11:58:21'),(1,2,'2024-12-05 11:58:21'),(2,1,'2024-12-05 11:58:21'),(2,2,'2024-12-05 11:58:21');
/*!40000 ALTER TABLE `chat_members` ENABLE KEYS */;
UNLOCK TABLES;


--
-- Dumping data for table `chats`
--

LOCK TABLES `chats` WRITE;
/*!40000 ALTER TABLE `chats` DISABLE KEYS */;
INSERT INTO `chats` VALUES (1,'General Chat','group',1,'2024-12-05 11:58:21'),(2,'The amazing group of football','private',NULL,'2024-12-05 11:58:21');
/*!40000 ALTER TABLE `chats` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `friend_request`
--


--
-- Dumping data for table `friend_request`
--

LOCK TABLES `friend_request` WRITE;
/*!40000 ALTER TABLE `friend_request` DISABLE KEYS */;
INSERT INTO `friend_request` VALUES (1,2,'pending','2024-12-05 11:58:21'),(2,1,'accepted','2024-12-05 11:58:21');
/*!40000 ALTER TABLE `friend_request` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `friendships`
--


--
-- Dumping data for table `friendships`
--

LOCK TABLES `friendships` WRITE;
/*!40000 ALTER TABLE `friendships` DISABLE KEYS */;
INSERT INTO `friendships` VALUES (1,1,2,'2024-12-05 11:58:21');
/*!40000 ALTER TABLE `friendships` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `log_history`
--


--
-- Dumping data for table `log_history`
--

LOCK TABLES `log_history` WRITE;
/*!40000 ALTER TABLE `log_history` DISABLE KEYS */;
INSERT INTO `log_history` VALUES (1,1,'2023-01-01 10:00:00','2023-01-01 11:00:00'),(2,2,'2023-01-02 12:00:00','2023-01-02 13:00:00'),(3,3,'2024-12-11 04:15:27','2024-12-11 04:15:27'),(4,4,'2024-12-11 04:15:27','2024-12-11 04:15:27'),(5,5,'2024-11-20 04:15:27','2024-12-20 04:23:27'),(6,6,'2024-07-14 04:15:27','2024-07-14 05:15:27'),(7,7,'2024-06-15 01:10:27','2024-06-15 02:15:27');
/*!40000 ALTER TABLE `log_history` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `messages`
--


--
-- Dumping data for table `messages`
--

LOCK TABLES `messages` WRITE;
/*!40000 ALTER TABLE `messages` DISABLE KEYS */;
INSERT INTO `messages` VALUES (1,1,1,'Hello everyone!','2024-12-05 11:58:21',0),(2,1,2,'Hi there!','2024-12-05 11:58:21',0),(3,2,1,'Private message content','2024-12-05 11:58:21',0),(4,1,1,'Testing','2024-12-13 08:11:51',0);
/*!40000 ALTER TABLE `messages` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `users`
--


--
-- Dumping data for table `users`
--

LOCK TABLES `users` WRITE;
/*!40000 ALTER TABLE `users` DISABLE KEYS */;
INSERT INTO `users`(user_id, username, email, address, password_hash, full_name, status, created_at, date_of_birth, gender) VALUES (1,'User Wan','user1@example.com','123 Main St','hashed_password_2','User One','online','2024-12-05 11:58:21',NULL, "Male"),(2,'Secand Use','user2@example.com','456 Elm St','hashed_password_3','User Two','offline','2024-12-05 11:58:21',NULL, "Male"),(3,'Jiji','khang@example.com','123 Đường ABC, Hà Nội','f9wihc93w8bveiu9u','Phúc Khang','online','2024-12-05 11:58:21',NULL, "Male"),(4,'Man the man','man@example.com','456 Đường DEF, TP.HCM','hashed_password_4','Lê Trí Mẩn','offline','2024-12-05 11:58:21',NULL, "Male"),(5,'nguyen vana','vana@example.com','789 Đường GHI, Đà Nẵng','hashed_password_5','Nguyễn Văn A','online','2024-12-05 11:58:21',NULL, "Male"),(6,'tranthib','thib@example.com','101 Đường JKL, Cần Thơ','hashed_password_6','Trần Thị B','offline','2024-12-05 11:58:21',NULL, "Male"),(7,'levanc','vanc@example.com','202 Đường MNO, Hải Phòng','hashed_password_7','Lê Văn C','online','2024-12-05 11:58:21',NULL, "Male"),(8,'pham thid','thid@example.com','303 Đường PQR, Huế','hashed_password_8','Phạm Thị D','offline','2024-12-05 11:58:21',NULL, "Male"),(9,'nguyen vanf','vanf@example.com','404 Đường STU, Nha Trang','hashed_password_9','Nguyễn Văn F','online','2024-12-05 11:58:21',NULL, "Male"),(10,'tranthig','thig@example.com','505 Đường VWX, Vũng Tàu','hashed_password_10','Trần Thị G','offline','2024-12-05 11:58:21',NULL, "Male"),(11,'le vanh','vanh@example.com','606 Đường YZ, Quy Nhơn','hashed_password_11','Lê Văn H','online','2024-12-05 11:58:21',NULL, "Male"),(12,'pham thii','thii@example.com','707 Đường ABC, Biên Hòa','hashed_password_12','Phạm Thị I','offline','2024-12-05 11:58:21',NULL, "Male"),(13,'hoangvane','vane@example.com','808 Đường DEF, Buôn Ma Thuột','hashed_password_13','Hoàng Văn E','online','2024-12-05 11:58:21',NULL, "Male"),(14,'The og','me@mail.com','321 Đường 11, phường 11, quận Gò Vấp, TP.HCM','938nkdoanc98','Nguyễn Thị A','offline','2024-07-19 15:28:14',NULL, "Male"),(15,'Me','t@mail.com','Mars','onq983x08qejd','Văn Trỗi','offline','2024-12-12 16:55:28',NULL, "Male"),(16,'123','trannguyenphuckhang12@gmail.com','321 đường số 8, phường 11, quận Gò Vấp, TPHCM','123','123','offline','2024-12-13 10:39:46',NULL, "Male");
/*!40000 ALTER TABLE `users` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2024-12-16 18:22:09
