package server.serverUI;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import server.EchoServer;

import java.io.IOException;

public class ServerUI {


    @FXML
    private Label serverStatusLabel;
    @FXML
    private Button serverStatusBtn;
    @FXML
    private TextField portTextFiled;
    @FXML
    private Button exitBtn;
    @FXML
    private Label listenLabel;
    @FXML
    private TextField databaseUsernameTextFiled;
    @FXML
    private TextField databaseUrlTextFiled;
    @FXML
    private PasswordField databasePasswordField;

    private EchoServer echoServer = null;

    /**
     * gets the server params from UI and start the server
     */
    @FXML
    void startServer() {
        // for the first time, create new server object
        if (echoServer == null) {
            int port = Integer.parseInt(portTextFiled.getText());
            String databaseUrl = databaseUrlTextFiled.getText();
            String databaseUsername = databaseUsernameTextFiled.getText();
            String databasePassword = databasePasswordField.getText();

            echoServer = new EchoServer(port, databaseUrl, databaseUsername, databasePassword, true);
            try {
                echoServer.listen();
                serverStatusLabel.setText("On");
                serverStatusBtn.setText("Stop");
                listenLabel.setText("Server is listening on port " + portTextFiled.getText());
            } catch (IOException e) {
                e.printStackTrace();
                Alert error = new Alert(Alert.AlertType.ERROR);
                error.setContentText(e.getMessage());
                error.showAndWait();
                System.exit(1);
            }
        }
        // if the server is running, shut it down
        else if (echoServer.isListening()) {
            echoServer.stopListening();
            serverStatusLabel.setText("off");
            serverStatusBtn.setText("Start");
            listenLabel.setText("");
        }
        // if it exist but not listening, start listen
        else if (!echoServer.isListening()) {
            try {
                echoServer.listen();
                serverStatusLabel.setText("On");
                serverStatusBtn.setText("Stop");
                listenLabel.setText("Server is listening on port " + portTextFiled.getText());
            } catch (IOException e) {
                e.printStackTrace();
                Alert error = new Alert(Alert.AlertType.ERROR);
                error.setContentText(e.getMessage());
                error.showAndWait();
                System.exit(1);
            }
        }
    }

    /**
     * exit the program.
     */
    public void exitProgram(ActionEvent event) {
        System.exit(0);
    }

}