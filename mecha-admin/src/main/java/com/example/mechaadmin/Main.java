package com.example.mechaadmin;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

import com.example.mechaadmin.bus.ReportBUS;
import com.example.mechaadmin.bus.UserBUS;
import com.example.mechaadmin.dto.GroupChatDTO;
import com.example.mechaadmin.dto.ReportInfoDTO;

public class Main extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        ReportBUS reportBUS = new ReportBUS();
        UserBUS userBUS = new UserBUS();
        // List<ReportInfoDTO> list = reportBUS.getAll();
        List<GroupChatDTO> list = userBUS.getAllGroups();
        
        System.out.println(list.size());
        
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("views/login.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 800, 600);
        scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("styles/app.css")).toExternalForm());
        stage.setTitle("Mecha Admin Dashboard");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}