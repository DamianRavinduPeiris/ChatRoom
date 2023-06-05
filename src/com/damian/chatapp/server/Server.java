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
    public static int index = 0;
    public static ArrayList<Thread> threadList = new ArrayList<>();


    public static void startServer() {
        System.out.println("Server started at port : " + 3000);
        new Thread(() -> {

            try {
                ServerSocket ss = new ServerSocket(3000);
                while (true) {

                    Socket socket = ss.accept();
                    socketArrayList.add(socket);
                    /*Notifying the other clients who joined the chat.👇*/
                    for (Socket s : socketArrayList) {
                        if (s.getPort() == socket.getPort()) {
                            /*Avoid sending the message to the sender.👇*/
                            continue;
                        }
                        DataOutputStream dos = new DataOutputStream(s.getOutputStream());
                        int index = socketArrayList.indexOf(socket);
                        dos.writeUTF(HomeScreenController.clientsNames.get(index) + " " + "has joined the chat.");
                        dos.flush();
                    }
                    System.out.println("Client connected to the server from port : " + socket.getPort());

                    /*Handling each client from a separate thread.👇*/
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
            /*Adding the current thread to an arrayList.*/
            threadList.add(Thread.currentThread());

            String clientMsg = "";
            try {
                while (true) {
                    DataInputStream dis = new DataInputStream(socket.getInputStream());
                    clientMsg = dis.readUTF();
                    /*Checking if an image has received.👇*/
                    /*Checking if the client sent an image.👇*/
                    if (clientMsg.equals("<Image>")) {
                        handleReceivedImage(dis, socket);
                    } else {
                        sendMsgToOthers(clientMsg, socket);
                    }


                }


            } catch (IOException e) {
                throw new RuntimeException(e);


            }


        }).start();

    }


    public static void handleReceivedImage(DataInputStream dis, Socket senderSocket) {
        try {
            /*Reading the image length.👇*/
            int imageDataLength = dis.readInt();
            byte[] imageData = new byte[imageDataLength];
            dis.readFully(imageData);
            sendImageToOthers(imageData, senderSocket);
        } catch (IOException e) {
            Platform.runLater(() -> {
                new Alert(Alert.AlertType.ERROR, "Error while handling the received image: " + e.getLocalizedMessage()).show();
            });
        }
    }

    public static void sendImageToOthers(byte[] imageData, Socket senderSocket) {
        for (Socket s : socketArrayList) {
            try {
                if (s.getPort() == senderSocket.getPort()) {
                     /*Avoid sending the image to the sender.👇*/
                    continue;
                }
                DataOutputStream dos = new DataOutputStream(s.getOutputStream());

                /* Sending a special message indicating the start of an image transmission.👇*/
                dos.writeUTF("<Image>");

                /* Letting the server know the size of the image.(This is useful to allocate resources properly.)👇*/
                dos.writeInt(imageData.length);
                /* Sending the image data.👇*/
                dos.write(imageData);
                dos.flush();
            } catch (IOException e) {
                Platform.runLater(() -> {
                    new Alert(Alert.AlertType.ERROR, "Error while getting the output stream: " + e.getLocalizedMessage()).show();
                });
            }
        }
    }


    public static void sendMsgToOthers(String msg, Socket socket) {

        for (Socket s : socketArrayList) {
            try {
                if (s.getPort() == socket.getPort()) {
                    //Avoid sending the message to the sender.
                    continue;

                }
                DataOutputStream dos = new DataOutputStream(s.getOutputStream());
                /*Since socketArray index == clientsNames array client name index.*/
                index = socketArrayList.indexOf(socket);
                dos.writeUTF(HomeScreenController.clientsNames.get(index) + " : " + msg);
                dos.flush();
            } catch (IOException e) {
                Platform.runLater(() -> {
                    new Alert(Alert.AlertType.ERROR, "Error while getting the output stream : " + e.getLocalizedMessage()).show();
                });
            }

        }

    }

    /*public static void handleExits(Socket socket) {
        int index = socketArrayList.indexOf(socket);
        if (index != -1) {
            for (Socket s : socketArrayList) {
                if (s.getPort() == socket.getPort()) {
                    //Avoid sending the message to the exited client.
                    continue;
                }
                try {
                    DataOutputStream dos = new DataOutputStream(s.getOutputStream());
                    dos.writeUTF(HomeScreenController.clientsNames.get(index) + " has left the chat!");
                    dos.flush();
                } catch (IOException e) {
                    new Alert(Alert.AlertType.ERROR, "Error while handling exit. : " + e.getLocalizedMessage()).show();
                }

            }
            try {
                socketArrayList.remove(socket);
                HomeScreenController.clientsNames.remove(index);
                socket.close();
            } catch (IOException e) {
                new Alert(Alert.AlertType.ERROR, "Error while closing the socket : " + e.getLocalizedMessage()).show();
            }


        }
    }*/


}