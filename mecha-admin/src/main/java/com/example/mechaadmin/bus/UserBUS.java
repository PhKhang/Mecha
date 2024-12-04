package com.example.mechaadmin.bus;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import com.example.mechaadmin.dao.UserDAO;
import com.example.mechaadmin.dao.GroupDAO;
import com.example.mechaadmin.dto.AccountDTO;

public class UserBUS {
    Configuration configuration = null;
    SessionFactory sessionFactory = null;
    Session session = null;

    public UserBUS() {
        configuration = new Configuration();
        configuration.configure("hibernate.cfg.xml");
        sessionFactory = configuration.buildSessionFactory();

        session = sessionFactory.openSession();
    }

    public List<AccountDTO> getAllUsers() {
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
            account.setRecentLogin(LocalDate.now());
            account.setAddress(((UserDAO) user[0]).getAddress());
            account.setEmail(((UserDAO) user[0]).getEmail());
            accounts.add(account);
        }

        return accounts;
    }

    public List<Object[]> getAllGroups() {
        session.beginTransaction();

        List<Object[]> groups = session
                // .createQuery("select g from GroupDAO g", Object[].class)
                .createQuery("select c.*, group_concat(u.username) from chats c \r\n" + //
                        "join chat_members cm on c.chat_id = cm.chat_id\r\n" + //
                        "join users u on cm.user_id = u.user_id \r\n" + //
                        "group by c.chat_id ", Object[].class)
                .getResultList();

        session.getTransaction().commit();

        return groups;
    }
}
