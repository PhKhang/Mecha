package com.example.mechaadmin.bus;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import com.example.mechaadmin.dao.ReportDAO;
import com.example.mechaadmin.dao.UserDAO;
import com.example.mechaadmin.dto.ReportInfoDTO;

public class ReportBUS {
    List<ReportInfoDTO> list = null;
    public List<ReportInfoDTO> getAll() {
        list = new ArrayList<ReportInfoDTO>();

        Configuration configuration = new Configuration();
        configuration.configure("hibernate.cfg.xml");

        SessionFactory sessionFactory = configuration.buildSessionFactory();

        Session session = sessionFactory.openSession();

        session.beginTransaction();

        List<Object[]> reports = session
                .createQuery("select r, u.username, u2.username" +
                        " from ReportDAO r join UserDAO u on r.reporterId = u.userId" +
                        " join UserDAO u2 on r.reportedId = u2.userId",
                        Object[].class)
                .getResultList();

        for (Object[] report : reports) {
            ReportInfoDTO reportInfoDTO = new ReportInfoDTO();
            reportInfoDTO.setReportId(((ReportDAO) report[0]).getReportId());
            reportInfoDTO.setReporterId(((ReportDAO) report[0]).getReporterId());
            reportInfoDTO.setRepoter((String) report[1]);
            reportInfoDTO.setReportedId(((ReportDAO) report[0]).getReportedId());
            reportInfoDTO.setReported((String) report[2]);
            reportInfoDTO.setReason(((ReportDAO) report[0]).getReason());
            reportInfoDTO.setStatus(((ReportDAO) report[0]).getStatus());
            reportInfoDTO.setCreatedAt(((ReportDAO) report[0]).getCreatedAt());
            list.add(reportInfoDTO);
        }

        session.getTransaction().commit();

        return list;
    }
    
    public String[][] getData(){
        if (list == null) {
            getAll();
        }
        return list.stream().map((report) -> {
            List<String> row = new ArrayList<>();
            row.add(report.getReportId() + "");
            row.add(report.getReporter() + "");
            row.add(report.getReported() + "");
            row.add(report.getReason());
            row.add(report.getCreatedAt() + "");
            row.add(report.getStatus());

            return row.toArray(String[]::new);
        }).toArray(String[][]::new);
    }

    public boolean setStatus(int reportId, String status) {
        if (reportId < 0)
            return false;

        Configuration configuration = new Configuration();
        configuration.configure("hibernate.cfg.xml");

        SessionFactory sessionFactory = configuration.buildSessionFactory();

        Session session = sessionFactory.openSession();

        session.beginTransaction();

        ReportDAO report = session.get(ReportDAO.class, reportId);
        report.setStatus(status);

        session.merge(report);

        session.getTransaction().commit();

        // TODO: add boolean query statuss
        return true;
    }

    public boolean setUser(int userId, String status) {
        if (userId < 0)
            return false;

        Configuration configuration = new Configuration();
        configuration.configure("hibernate.cfg.xml");

        SessionFactory sessionFactory = configuration.buildSessionFactory();

        Session session = sessionFactory.openSession();

        session.beginTransaction();

        UserDAO user = session.get(UserDAO.class, userId);
        user.setStatus(status);

        session.merge(user);

        session.getTransaction().commit();

        return true;
    }
}