package com.example.mechaadmin.bus;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import com.example.mechaadmin.dao.LogDAO;
import com.example.mechaadmin.dto.UsageDTO;

public class UsageBUS {
    public List<UsageDTO> getAppOpened(int year) {
        Configuration configuration = new Configuration();
        configuration.configure("hibernate.cfg.xml");

        SessionFactory sessionFactory = configuration.buildSessionFactory();

        Session session = sessionFactory.openSession();

        session.beginTransaction();

        List<Object[]> reports = session
                .createQuery("select month(l.sectionStart) as month, count(distinct l.userId) as opened, year(l.sectionStart) as year " +
                        "from LogDAO l " +
                        "where year(l.sectionStart) = :year " +
                        "group by month(l.sectionStart), year(l.sectionStart)",
                        Object[].class)
                .setParameter("year", year)
                .getResultList();
                
        session.getTransaction().commit();
        
        List<UsageDTO> list = new ArrayList<UsageDTO>();
        
        for (Object[] report : reports) {
            UsageDTO usageDTO = new UsageDTO();
            usageDTO.setMonth((Integer) report[0]);
            usageDTO.setOpened(((Long) report[1]).intValue());
            usageDTO.setYear((Integer) report[2]);
            list.add(usageDTO);
        }
        
        return list;
    }
}