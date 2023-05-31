package com.damian.chatapp.controller;

import animatefx.animation.LightSpeedIn;
import com.damian.chatapp.server.Server;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class HomeScreenController implements Initializable {
    public static String clientName = "";
    public ImageView i1;
    public Label l1;
    public JFXTextField t1;
    public JFXButton b1;
    public static ArrayList<String> clientsNames = new ArrayList<>();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        new LightSpeedIn(i1).play();
        new LightSpeedIn(l1).play();
        new LightSpeedIn(t1).play();
        new LightSpeedIn(b1).play();

        /*Starting the server👇*/
        Server.startServer();



    }

    public void loginOnAction(ActionEvent actionEvent) {
        clientName = t1.getText();
        clientsNames.add(clientName);
        t1.clear();
        Stage primaryStage = new Stage();
        try {
            primaryStage.setScene(new Scene(FXMLLoader.load(getClass().getResource("../view/Client.fxml"))));
        } catch (IOException e) {
            new Alert(Alert.AlertType.ERROR, "Error while loading the client UI : " + e.getLocalizedMessage()).show();
        }
        primaryStage.getIcons().add(new Image("com/damian/chatapp/assets/client.png"));
        primaryStage.setTitle(t1.getText());
        primaryStage.show();
    }
}
