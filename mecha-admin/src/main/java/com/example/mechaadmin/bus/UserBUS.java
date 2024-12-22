package com.example.mechaadmin.bus;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.RandomStringUtils;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import com.example.mechaadmin.dao.UserDAO;
import com.example.mechaadmin.dao.ChatDAO;
import com.example.mechaadmin.dto.AccountDTO;
import com.example.mechaadmin.dto.GroupChatDTO;
import com.example.mechaadmin.dto.RecentLoginDTO;

public class UserBUS {
    // Configuration configuration = null;
    // SessionFactory sessionFactory = null;
    // Session session = null;

    public UserBUS() {
        // configuration = new Configuration();
        // configuration.configure("hibernate.cfg.xml");
        // sessionFactory = configuration.buildSessionFactory();

        // session = sessionFactory.openSession();
    }

    public List<AccountDTO> getAllUsers() {
        Configuration configuration = new Configuration();
        configuration.configure("hibernate.cfg.xml");
        SessionFactory sessionFactory = configuration.buildSessionFactory();
        Session session = sessionFactory.openSession();
        session.beginTransaction();

        List<Object[]> users = session
                .createQuery("select u from UserDAO u", Object[].class)
                .getResultList();

        session.getTransaction().commit();

        List<AccountDTO> accounts = new ArrayList<AccountDTO>();
        for (Object[] user : users) {
            AccountDTO account = new AccountDTO();
            account.setFullName(((UserDAO) user[0]).getFullName());
            account.setUsername(((UserDAO) user[0]).getUsername());
            account.setStatus(((UserDAO) user[0]).getStatus());
            account.setCreatedAt(((UserDAO) user[0]).getCreatedAt());
            account.setRecentLogin(LocalDateTime.now());
            account.setAddress(((UserDAO) user[0]).getAddress());
            account.setEmail(((UserDAO) user[0]).getEmail());
            account.setGender(((UserDAO) user[0]).getGender());
            account.setDob(((UserDAO) user[0]).getDob());
            account.setAccountId(((UserDAO) user[0]).getUserId());
            account.setAdminAction(((UserDAO) user[0]).getAdminAction());
            accounts.add(account);
        }

        return accounts;
    }

    static public List<AccountDTO> getAllUsersWithFriendCount() {
        System.out.println("GETTING ALL USERS WITH FRIEND COUNT");
        Configuration configuration = new Configuration();
        configuration.configure("hibernate.cfg.xml");
        SessionFactory sessionFactory = configuration.buildSessionFactory();
        Session session = sessionFactory.openSession();
        session.beginTransaction();

        List<Object[]> users = session
                .createQuery("select u from UserDAO u", Object[].class)
                .getResultList();

        List<AccountDTO> accounts = new ArrayList<AccountDTO>();
        for (Object[] user : users) {
            AccountDTO account = new AccountDTO();
            account.setFullName(((UserDAO) user[0]).getFullName());
            account.setUsername(((UserDAO) user[0]).getUsername());
            account.setStatus(((UserDAO) user[0]).getStatus());
            account.setCreatedAt(((UserDAO) user[0]).getCreatedAt());
            account.setRecentLogin(LocalDateTime.now());
            account.setAddress(((UserDAO) user[0]).getAddress());
            account.setEmail(((UserDAO) user[0]).getEmail());
            account.setGender(((UserDAO) user[0]).getGender());
            account.setDob(((UserDAO) user[0]).getDob());
            account.setAccountId(((UserDAO) user[0]).getUserId());
            account.setAdminAction(((UserDAO) user[0]).getAdminAction());
            accounts.add(account);
        }
        
        System.out.println("ACCOUNTS: " + accounts.size());

        for (AccountDTO account : accounts) {
            List<Object[]> friends = session
                    .createQuery("select f from FriendshipDAO f where f.user1 = :id or f.user2 = :id", Object[].class)
                    .setParameter("id", account.getUserId())
                    .getResultList();
            account.setDirectFriends(friends.size());
        }

        for (AccountDTO account : accounts) {
            List<Object[]> friends = session
                    .createQuery("WITH DirectFriends AS (select f.user1 as direct_ids\r\n" + //
                            "                       from FriendshipDAO f\r\n" + //
                            "                       where f.user2 = :id\r\n" + //
                            "                       union\r\n" + //
                            "                       select f.user2\r\n" + //
                            "                       from FriendshipDAO f\r\n" + //
                            "                       where f.user1 = :id)\r\n" + //
                            "select f.user1\r\n" + //
                            "from FriendshipDAO f\r\n" + //
                            "         join DirectFriends d on f.user2 = d.direct_ids\r\n" + //
                            "where f.user1 != :id\r\n" + //
                            "  and f.user1 not IN (select direct_ids from DirectFriends)\r\n" + //
                            "\r\n" + //
                            "union\r\n" + //
                            "select f.user2\r\n" + //
                            "from FriendshipDAO f\r\n" + //
                            "         join DirectFriends d on f.user1 = d.direct_ids\r\n" + //
                            "where f.user2 != :id\r\n" + //
                            "  and f.user2 not IN (select direct_ids from DirectFriends)\r\n" + //
                            "", Object[].class)
                    .setParameter("id", account.getUserId())
                    .getResultList();
            account.setIndirectFriends(friends.size());
        }

        session.getTransaction().commit();
        return accounts;
    }

    public List<AccountDTO> getByIdList(List<Integer> ids) {
        Configuration configuration = new Configuration();
        configuration.configure("hibernate.cfg.xml");
        SessionFactory sessionFactory = configuration.buildSessionFactory();
        Session session = sessionFactory.openSession();
        session.beginTransaction();

        List<Object[]> users = session
                .createQuery(
                        "select u, l.sectionStart from UserDAO u left join LogDAO l on u.userId = l.userId where u.userId in :ids",
                        Object[].class)
                .setParameter("ids", ids)
                .getResultList();

        session.getTransaction().commit();

        List<AccountDTO> accounts = new ArrayList<AccountDTO>();
        for (Object[] user : users) {
            AccountDTO account = new AccountDTO();
            account.setAccountId(((UserDAO) user[0]).getUserId());
            account.setFullName(((UserDAO) user[0]).getFullName());
            account.setUsername(((UserDAO) user[0]).getUsername());
            account.setStatus(((UserDAO) user[0]).getStatus());
            account.setCreatedAt(((UserDAO) user[0]).getCreatedAt());
            account.setRecentLogin((LocalDateTime) user[1]);
            account.setAddress(((UserDAO) user[0]).getAddress());
            account.setEmail(((UserDAO) user[0]).getEmail());
            accounts.add(account);
        }

        return accounts;
    }

    static public List<Object[]> getFriends(int id) {
        Configuration configuration = new Configuration();
        configuration.configure("hibernate.cfg.xml");
        SessionFactory sessionFactory = configuration.buildSessionFactory();
        Session session = sessionFactory.openSession();
        session.beginTransaction();

        List<Object[]> users = session
                .createQuery("select u.username, u.fullName from UserDAO u\r\n" + //
                        "join FriendshipDAO f on u.userId = f.user1\r\n" + //
                        "where f.user2 = 1\r\n" + //
                        "union\r\n" + //
                        "select u.username, u.fullName from UserDAO u\r\n" + //
                        "join FriendshipDAO f on u.userId = f.user2\r\n" + //
                        "where f.user1 = 1", Object[].class)
                .getResultList();

        session.getTransaction().commit();
        return users;
    }

    static public List<RecentLoginDTO> getRecentLogin(Integer id) {
        Configuration configuration = new Configuration();
        configuration.configure("hibernate.cfg.xml");

        SessionFactory sessionFactory = configuration.buildSessionFactory();

        Session session = sessionFactory.openSession();

        session.beginTransaction();

        List<RecentLoginDTO> logs = session
                .createQuery("select l.sectionStart, u.username, u.fullName from LogDAO l " +
                        "join UserDAO u on l.userId = u.userId " +
                        "where u.userId = :id " +
                        "order by l.sectionStart desc", RecentLoginDTO.class)
                .setParameter("id", id)
                .getResultList();

        session.getTransaction().commit();

        return logs;
    }

    static public void saveAccount(AccountDTO account) {
        Configuration configuration = new Configuration();
        configuration.configure("hibernate.cfg.xml");
        SessionFactory sessionFactory = configuration.buildSessionFactory();
        Session session = sessionFactory.openSession();
        session.beginTransaction();

        UserDAO user = new UserDAO();
        user.setFullName(account.getFullName());
        user.setUsername(account.getUsername());
        user.setAdminAction(account.getAdminAction());
        user.setCreatedAt(account.getCreatedAt());
        user.setAddress(account.getAddress());
        user.setEmail(account.getEmail());
        user.setGender(account.getGender());
        user.setDob(account.getDob());
        user.setStatus("offline");
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789~`!@#$%^&*()-_=+[{]}\\|;:\'\",<.>/?";
        String newPassword = RandomStringUtils.random(15, characters);
        user.setPasswordHash(newPassword);
        session.persist(user);

        session.getTransaction().commit();
    }

    static public void updateAccount(AccountDTO account) {
        Configuration configuration = new Configuration();
        configuration.configure("hibernate.cfg.xml");
        SessionFactory sessionFactory = configuration.buildSessionFactory();
        Session session = sessionFactory.openSession();
        session.beginTransaction();

        UserDAO user = session.get(UserDAO.class, account.getUserId());
        user.setFullName(account.getFullName());
        user.setUsername(account.getUsername());
        user.setStatus(account.getStatus());
        user.setCreatedAt(account.getCreatedAt());
        user.setAddress(account.getAddress());
        user.setEmail(account.getEmail());
        user.setGender(account.getGender());
        user.setDob(account.getDob());
        session.merge(user);

        session.getTransaction().commit();
    }

    static public void lockAccount(int userId) {
        Configuration configuration = new Configuration();
        configuration.configure("hibernate.cfg.xml");
        SessionFactory sessionFactory = configuration.buildSessionFactory();
        Session session = sessionFactory.openSession();
        session.beginTransaction();

        UserDAO user = session.get(UserDAO.class, userId);
        user.setAdminAction("locked");

        session.getTransaction().commit();
    }

    static public void warnAccount(int userId) {
        Configuration configuration = new Configuration();
        configuration.configure("hibernate.cfg.xml");
        SessionFactory sessionFactory = configuration.buildSessionFactory();
        Session session = sessionFactory.openSession();
        session.beginTransaction();

        UserDAO user = session.get(UserDAO.class, userId);
        user.setAdminAction("warned");

        session.getTransaction().commit();
    }

    static public void unlockAccount(int userId) {
        Configuration configuration = new Configuration();
        configuration.configure("hibernate.cfg.xml");
        SessionFactory sessionFactory = configuration.buildSessionFactory();
        Session session = sessionFactory.openSession();
        session.beginTransaction();

        UserDAO user = session.get(UserDAO.class, userId);
        user.setAdminAction(null);

        session.getTransaction().commit();
    }

    static public void deleteAccount(int userId) {
        Configuration configuration = new Configuration();
        configuration.configure("hibernate.cfg.xml");
        SessionFactory sessionFactory = configuration.buildSessionFactory();
        Session session = sessionFactory.openSession();
        session.beginTransaction();

        UserDAO user = session.get(UserDAO.class, userId);
        session.remove(user);

        session.getTransaction().commit();
    }

    public List<GroupChatDTO> getAllGroups() {
        Configuration configuration = new Configuration();
        configuration.configure("hibernate.cfg.xml");
        SessionFactory sessionFactory = configuration.buildSessionFactory();
        Session session = sessionFactory.openSession();
        session.beginTransaction();

        List<Object[]> groups = session
                // .createQuery("select g from GroupDAO g", Object[].class)
                // .createQuery("select c from ChatDAO c ", Object[].class)
                .createQuery(
                        "select c, group_concat(u.username) as memebers, count(u.username) as amount, group_concat(u.userId) as ids, u2.username as admin from ChatDAO c \r\n"
                                + //
                                "join MemberDAO cm on c.chatId = cm.chatId\r\n" + //
                                "join UserDAO u on cm.userId = u.userId \r\n" + //
                                "left join UserDAO u2 on c.adminId = u2.userId \r\n" + //
                                "group by c.chatId ",
                        Object[].class)
                .getResultList();

        session.getTransaction().commit();

        List<GroupChatDTO> groupChats = new ArrayList<GroupChatDTO>();
        for (Object[] group : groups) {
            GroupChatDTO groupChat = new GroupChatDTO();
            groupChat.setChatId(((ChatDAO) group[0]).getChatId());
            groupChat.setGroupName(((ChatDAO) group[0]).getGroupName());
            groupChat.setChatType(((ChatDAO) group[0]).getChatType());
            groupChat.setAdminId(((ChatDAO) group[0]).getAdminId());
            groupChat.setCreatedAt(((ChatDAO) group[0]).getCreatedAt());
            groupChat.setMembers(Arrays.asList(((String) group[1]).split(",")));
            groupChat.setTotalMembers(((Long) group[2]).intValue());
            groupChats.add(groupChat);
        }

        return groupChats;
    }
}
