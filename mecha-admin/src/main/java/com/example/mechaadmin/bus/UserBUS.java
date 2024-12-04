package com.example.mechaadmin.bus;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import com.example.mechaadmin.dao.UserDAO;
import com.example.mechaadmin.dao.ChatDAO;
import com.example.mechaadmin.dao.MemberDAO;
import com.example.mechaadmin.dto.AccountDTO;
import com.example.mechaadmin.dto.GroupChatDTO;

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

    public List<GroupChatDTO> getAllGroups() {
        session.beginTransaction();

        List<Object[]> groups = session
                // .createQuery("select g from GroupDAO g", Object[].class)
                // .createQuery("select c from ChatDAO c ", Object[].class)
                .createQuery("select c, group_concat(u.username), count(u.username) from ChatDAO c \r\n" + //
                        "join MemberDAO cm on c.chatId = cm.chatId\r\n" + //
                        "join UserDAO u on cm.userId = u.userId \r\n" + //
                        "group by c.chatId ", Object[].class)
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
