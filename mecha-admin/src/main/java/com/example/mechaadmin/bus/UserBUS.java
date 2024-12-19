package com.example.mechaadmin.bus;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import com.example.mechaadmin.dao.UserDAO;
import com.example.mechaadmin.dao.ChatDAO;
import com.example.mechaadmin.dto.AccountDTO;
import com.example.mechaadmin.dto.GroupChatDTO;

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
            accounts.add(account);
        }

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

    public void lockAccount(int userId) {
        Configuration configuration = new Configuration();
        configuration.configure("hibernate.cfg.xml");
        SessionFactory sessionFactory = configuration.buildSessionFactory();
        Session session = sessionFactory.openSession();
        session.beginTransaction();

        UserDAO user = session.get(UserDAO.class, userId);
        user.setStatus("locked");

        session.getTransaction().commit();
    }

    public void warnAccount(int userId) {
        Configuration configuration = new Configuration();
        configuration.configure("hibernate.cfg.xml");
        SessionFactory sessionFactory = configuration.buildSessionFactory();
        Session session = sessionFactory.openSession();
        session.beginTransaction();

        UserDAO user = session.get(UserDAO.class, userId);
        user.setStatus("warned");

        session.getTransaction().commit();
    }

    public void unlockAccount(int userId) {
        Configuration configuration = new Configuration();
        configuration.configure("hibernate.cfg.xml");
        SessionFactory sessionFactory = configuration.buildSessionFactory();
        Session session = sessionFactory.openSession();
        session.beginTransaction();

        UserDAO user = session.get(UserDAO.class, userId);
        user.setStatus("active");

        session.getTransaction().commit();
    }

    public void deleteAccount(int userId) {
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
