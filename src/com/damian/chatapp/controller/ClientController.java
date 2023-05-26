package com.damian.chatapp.controller;

import animatefx.animation.LightSpeedIn;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextArea;
import com.jfoenix.controls.JFXTextField;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.URL;
import java.util.ResourceBundle;

public class ClientController implements Initializable {
    public JFXTextArea textArea;
    public JFXTextField msgField;
    public JFXButton sendButton;
    public Label l1;

    public Socket socket;

    public DataInputStream dis;
    public DataOutputStream dos;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        new LightSpeedIn(textArea).play();
        new LightSpeedIn(msgField).play();
        new LightSpeedIn(sendButton).play();
        new LightSpeedIn(l1).play();

        l1.setText("WELCOME " + HomeScreenController.clientName + " !");

        new Thread(() -> {

            try {
                socket = new Socket("localhost", 3000);
                System.out.println("Client started at port : " + 3000);
                while (true) {
                    dis = new DataInputStream(socket.getInputStream());
                    textArea.appendText(dis.readUTF() + "\n");

                }
            } catch (IOException e) {
                Platform.runLater(() -> {
                    new Alert(Alert.AlertType.ERROR, "Error while connecting to the server ! : " + e.getLocalizedMessage()).show();
                });

            }

        }).start();


    }

    public void sendButtonOnAction(ActionEvent actionEvent) {
        new Thread(() -> {
            if (!msgField.getText().equalsIgnoreCase("exit")) {
                textArea.appendText("ME : " + msgField.getText() + "\n");
                try {
                    dos = new DataOutputStream(socket.getOutputStream());
                    dos.writeUTF(msgField.getText());
                    dos.flush();
                    msgField.clear();
                } catch (IOException e) {
                    new Alert(Alert.AlertType.ERROR, "Error while sending the message ! : " + e.getLocalizedMessage()).show();
                }

            } else {
                try {
                    socket.close();
                    dis.close();
                    dos.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

            }


        }).start();

    }


}
