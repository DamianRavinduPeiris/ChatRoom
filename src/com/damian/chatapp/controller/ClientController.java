package com.damian.chatapp.controller;

import animatefx.animation.LightSpeedIn;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextArea;
import com.jfoenix.controls.JFXTextField;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;

import java.net.URL;
import java.util.ResourceBundle;

public class ClientController implements Initializable {
    public JFXTextArea textArea;
    public JFXTextField msgField;
    public JFXButton sendButton;
    public Label l1;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        new LightSpeedIn(textArea).play();
        new LightSpeedIn(msgField).play();
        new LightSpeedIn(sendButton).play();
        new LightSpeedIn(l1).play();

        l1.setText("WELCOME " + HomeScreenController.clientName + " !");

    }

    public void sendButtonOnAction(ActionEvent actionEvent) {
    }


}
