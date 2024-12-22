package com.example.mechaadmin.bus;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import com.example.mechaadmin.dao.AdminDAO;

public class AdminBUS {
    public static boolean checkLogin(String email, String password) {
        Configuration configuration = new Configuration();
        configuration.configure("hibernate.cfg.xml");

        SessionFactory sessionFactory = configuration.buildSessionFactory();

        Session session = sessionFactory.openSession();

        session.beginTransaction();

        List<Object[]> users = session.createQuery("SELECT u.adminId FROM AdminDAO u WHERE u.email = :email AND u.password_hash = :password_hash", Object[].class)
                .setParameter("email", email)
                .setParameter("password_hash", password)
                .getResultList();

        session.getTransaction().commit();

        return users.size() > 0;
    }
}