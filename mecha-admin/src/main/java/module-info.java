module com.example.mechaadmin {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires javafx.base;
    requires jakarta.persistence;
    requires org.hibernate.orm.core;


    opens com.example.mechaadmin to javafx.fxml;
    exports com.example.mechaadmin;
    opens com.example.mechaadmin.dao to org.hibernate.orm.core;
    exports com.example.mechaadmin.dao;
}