package client.hostIpSelector;

import client.ClientMain;
import client.ClientUI;
import common.IcmUtils;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import server.ServerService;

public class HostIpSelector implements ClientUI {

    @FXML
    private TextField ipTextField;
    @FXML
    private TextField portTextField;


    /**
     * Initialize the forgot password screen
     */
    public void initialize() {
        portTextField.addEventFilter(KeyEvent.KEY_TYPED, numeric_Validation(4));
    }
    /**
     * gets the server ip and port from UI and connect to server
     */
    @FXML
    void login() {
        ClientMain.setHost(ipTextField.getText());
        ClientMain.setPort(Integer.parseInt(portTextField.getText()));

        // load the gui and starting it
        try {
            IcmUtils.loadScene(this, "ICM - Login", "/client/login/Login.fxml", 800, 500);
        } catch (Exception e) {
            e.printStackTrace();
            IcmUtils.displayErrorMsg(
                    "Load scene error",
                    "Load scene error",
                    e.getMessage() + "\nPlease contact ICM support team");
        }
    }

    /**
     * helper function to validate that search TextField contain only digits.
     *
     * @param max_Length the max length of change request id
     * @return EventHandler that can be assign to TextField
     */
    public EventHandler<KeyEvent> numeric_Validation(final Integer max_Length) {
        return new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent e) {
                TextField txt_TextField = (TextField) e.getSource();
                if (txt_TextField.getText().length() >= max_Length) {
                    e.consume();
                }
                if(!e.getCharacter().matches("[0-9]")){
                    e.consume();
                }
            }
        };
    }

    /**
     * handle the returned value from server. Does nothing in this case.
     */
    @Override
    public void handleMessageFromClientController(ServerService serverService) {
    }
}