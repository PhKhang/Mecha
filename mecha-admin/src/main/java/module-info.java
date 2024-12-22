module com.example.mechaadmin {
    requires java.naming;
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires javafx.base;
    requires jakarta.persistence;
    requires org.hibernate.orm.core;
    requires commons.lang3;


    opens com.example.mechaadmin to javafx.fxml;
    exports com.example.mechaadmin;
    opens com.example.mechaadmin.dao to org.hibernate.orm.core;
    exports com.example.mechaadmin.dao;
    opens com.example.mechaadmin.dto to javafx.base;
    exports com.example.mechaadmin.dto;
}