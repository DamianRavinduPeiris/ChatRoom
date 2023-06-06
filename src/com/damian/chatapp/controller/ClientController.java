package com.damian.chatapp.controller;

import animatefx.animation.LightSpeedIn;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.Socket;
import java.net.URL;
import java.util.ResourceBundle;

public class ClientController implements Initializable {

    public JFXTextField msgField;
    public JFXButton sendButton;
    public Label l1;

    public Socket socket;

    public DataInputStream dis;
    public DataOutputStream dos;
    public JFXButton imageButton;

    public String msg;


    public JFXButton cc;
    public VBox otherTextBox;
    public VBox myTextBox;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        new LightSpeedIn(msgField).play();
        new LightSpeedIn(sendButton).play();
        new LightSpeedIn(l1).play();
        new LightSpeedIn(imageButton).play();
        new LightSpeedIn(cc).play();


        l1.setText("WELCOME " + HomeScreenController.clientName + " !");
        setLabelWidth(l1, l1.getText());

        new Thread(() -> {

            try {
                socket = new Socket("localhost", 3000);
                System.out.println("Client started at port : " + 3000);
                while (true) {
                    dis = new DataInputStream(socket.getInputStream());
                    msg = dis.readUTF();
//                   /*If the message was an image.👇*/
                    if (msg.equals("<Image>")) {
                        handleReceivedImage(dis);
                    } else {
                        /*If it was a normal msg.👇*/
                        HBox hBox = new HBox();
                        Text text = new Text(msg);
                        text.setFill(Color.color(0.934, 0.945, 0.996));
                        text.setStyle("-fx-font-size: 20px;");
                        text.setText(msg);

                        TextFlow textFlow = new TextFlow(text);
                        textFlow.setStyle("-fx-color : rgb(239, 242, 255);" +
                                "-fx-background-color: rgb(15, 125, 242);" +
                                "-fx-background-radius: 20px");
                        textFlow.setPadding(new Insets(5, 10, 8, 10));

                        hBox.setAlignment(Pos.CENTER_LEFT);
                        hBox.setPadding(new Insets(5, 5, 5, 10));
                        hBox.getChildren().add(textFlow);

                        Platform.runLater(new Runnable() {
                            @Override
                            public void run() {
                                otherTextBox.getChildren().add(hBox);
                                new LightSpeedIn(hBox).play();
                            }
                        });


                    }
                }

            } catch (IOException e) {
                Platform.runLater(() -> {
                    new Alert(Alert.AlertType.ERROR, "Error while connecting to the server ! : " + e.getLocalizedMessage()).show();
                });
            }

        }).start();

    }

    public void setLabelWidth(Label label, String text) {
        Text textNode = new Text("Welcome : " + HomeScreenController.clientName + " !");
        textNode.setFont(label.getFont());
        double textWidth = textNode.getLayoutBounds().getWidth();
        label.setPrefWidth(textWidth);
    }

    public void sendButtonOnAction(ActionEvent actionEvent) {
        if (msgField.getText().isEmpty()) {
            new Alert(Alert.AlertType.ERROR, "Cannot send empty messages !  ").show();

        } else {
            new Thread(() -> {

                HBox hBox = new HBox();

                Text text = new Text(msgField.getText());
                text.setFill(Color.color(0.934, 0.945, 0.996));
                text.setStyle("-fx-font-size: 20px;");
                text.setText("Me : " + msgField.getText());

                TextFlow textFlow = new TextFlow(text);
                textFlow.setStyle("-fx-color : rgb(239, 242, 255);" +
                        "-fx-background-color: rgb(15, 125, 242);" +
                        "-fx-background-radius: 20px");
                textFlow.setPadding(new Insets(5, 10, 8, 10));

                hBox.setAlignment(Pos.CENTER_RIGHT);
                hBox.setPadding(new Insets(5, 5, 5, 10));
                hBox.getChildren().add(textFlow);

                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        myTextBox.getChildren().add(hBox);
                        new LightSpeedIn(hBox).play();

                    }
                });
                try {
                    dos = new DataOutputStream(socket.getOutputStream());
                    dos.writeUTF(msgField.getText());
                    dos.flush();
                    msgField.clear();
                } catch (IOException e) {
                    new Alert(Alert.AlertType.ERROR, "Error while sending the message ! : " + e.getLocalizedMessage()).show();
                }


            }).start();

        }

    }


    private void handleReceivedImage(DataInputStream dis) {
        try {
            /*The dis.read() method reads the length of the image data.👇 */
            int imageDataLength = dis.readInt();
            /*Creating a byte array using the length of the image data.👇*/
            byte[] imageData = new byte[imageDataLength];
            dis.readFully(imageData);

            /*Converting the byte array to a buffered image object.👇*/
            ByteArrayInputStream bais = new ByteArrayInputStream(imageData);
            BufferedImage bufferedImage = ImageIO.read(bais);

            /* Convert BufferedImage to JavaFX Image.👇*/
            Image image = SwingFXUtils.toFXImage(bufferedImage, null);

            /* Create an ImageView with the Image.👇*/
            ImageView imageView = new ImageView(image);
            imageView.setPreserveRatio(true);
            imageView.setFitWidth(500);
            imageView.setFitHeight(500);

            //ADD A scroll pane to the image container.👇
            ScrollPane scrollPane = new ScrollPane();
            scrollPane.setContent(imageView);

            /* Append the ImageView to the imageContainer.👇*/
            Platform.runLater(() -> otherTextBox.getChildren().add(imageView));
        } catch (IOException e) {
            Platform.runLater(() -> {
                new Alert(Alert.AlertType.ERROR, "Error while handling the received image: " + e.getLocalizedMessage()).show();
            });
        }
    }


    public void imageHandler(ActionEvent actionEvent) {
        FileChooser fileChooser = new FileChooser();
        Stage stage = (Stage) imageButton.getScene().getWindow();
        File image = fileChooser.showOpenDialog(stage);
        if (image != null) {
            try {
                /*Reading the image!.👇*/
                /* Reads the contents of the image file and creates a BufferedImage object called bufferedImage.👇*/
                BufferedImage bufferedImage = ImageIO.read(image);

                /*This line creates a ByteArrayOutputStream object called byteArrayOutputStream. This stream is used to write the image data as bytes.👇*/
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

                /*This line writes the image data from the bufferedImage to the byteArrayOutputStream. The ImageIO.write() method takes three parameters: the image to be written (bufferedImage), the format of the image ("jpg" in this case), and the output stream to which the image data will be written (byteArrayOutputStream).👇*/
                ImageIO.write(bufferedImage, "jpg", byteArrayOutputStream);

                /*Below line can get the byte image data from the byteArrayOutPutStream and convert it to a byte array.👇 */
                byte[] imageData = byteArrayOutputStream.toByteArray();

                /*Sending the image through output stream!👇*/
                DataOutputStream dos2 = new DataOutputStream(socket.getOutputStream());
                /*Letting the server know an image is being sent.👇*/
                dos2.writeUTF("<Image>");
                /*Writing the length of the image data.👇*/
                dos2.writeInt(imageData.length);
                dos2.write(imageData);
                dos2.flush();

                /*Appending the image to the text area.👇*/
                /* Convert the image data to an Image.👇*/
                ByteArrayInputStream bais = new ByteArrayInputStream(imageData);
                BufferedImage bufferredImage = ImageIO.read(bais);
                Image image1 = new Image(bais);
                /* Convert BufferedImage to JavaFX Image.👇*/
                Image image2 = SwingFXUtils.toFXImage(bufferedImage, null);

                /* Create an ImageView with the Image.👇*/
                ImageView imageView = new ImageView(image2);
                imageView.setPreserveRatio(true);
                imageView.setFitWidth(500);
                imageView.setFitHeight(500);
                //ADD A scroll pane to the image container.👇
                ScrollPane scrollPane = new ScrollPane();
                scrollPane.setContent(imageView);

                /* Append the ImageView to the imageContainer.👇*/
                Platform.runLater(() -> myTextBox.getChildren().add(imageView));


            } catch (IOException e) {
                new Alert(Alert.AlertType.ERROR, "Error while reading the image data !").show();
            }

        } else {
            new Alert(Alert.AlertType.ERROR, "Error while selecting the image !").show();
        }
    }

    public void ccOnAction(ActionEvent actionEvent) {
        myTextBox.getChildren().clear();
        otherTextBox.getChildren().clear();
    }


}
