package com.damian.chatapp.server;

import com.damian.chatapp.controller.HomeScreenController;
import javafx.application.Platform;
import javafx.scene.control.Alert;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class Server {

    public static ArrayList<Socket> socketArrayList = new ArrayList<>();


    public static void startServer() {
        System.out.println("Server started at port : " + 3000);
        new Thread(() -> {

            try {
                ServerSocket ss = new ServerSocket(3000);
                while (true) {
                    Socket socket = ss.accept();
                    socketArrayList.add(socket);
                    System.out.println("Client connected to the server from port : " + socket.getPort());

                    handleClient(socket);


                }
            } catch (IOException e) {
                Platform.runLater(() -> {
                    new Alert(Alert.AlertType.ERROR, "Error while starting the server : " + e.getLocalizedMessage()).show();
                });
            }

        }).start();


    }

    public static void handleClient(Socket socket) {
        new Thread(() -> {
            System.out.println("handleClient method called.");
            String clientMsg = "";
            try {
                DataInputStream dis = new DataInputStream(socket.getInputStream());
                while (!clientMsg.equalsIgnoreCase("Exit")) {
                    clientMsg = dis.readUTF();
                    System.out.println("CLIENT: " + clientMsg);
                    sendMsgToOthers(clientMsg, socket);
                }
                socket.close();
                dis.close();
                socketArrayList.remove(socket);
            } catch (IOException e) {
                Platform.runLater(() -> {
                    new Alert(Alert.AlertType.ERROR, "Error while getting the input stream: " + e.getLocalizedMessage()).show();
                });
            }

        }).start();

    }


    public static void sendMsgToOthers(String msg, Socket socket) {
        int index = 0;
        for (Socket s : socketArrayList) {
            try {
                if (s.getPort() == socket.getPort()) {

                    //Avoid sending the message to the sender.
                    continue;

                }
                DataOutputStream dos = new DataOutputStream(s.getOutputStream());
                index = socketArrayList.indexOf(socket);
                dos.writeUTF(HomeScreenController.clientsNames.get(index)+" : "+msg);
                dos.flush();
            } catch (IOException e) {
                Platform.runLater(() -> {
                    new Alert(Alert.AlertType.ERROR, "Error while getting the output stream : " + e.getLocalizedMessage()).show();
                });
            }

        }

    }

}