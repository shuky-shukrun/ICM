package client.login;

import client.ClientController;
import client.ClientUI;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXPasswordField;

import common.IcmUtils;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import entities.ChangeInitiator;
import javafx.animation.TranslateTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Circle;
import javafx.util.Duration;
import server.ServerService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Login implements ClientUI {

    @FXML
    private AnchorPane layer2;
    @FXML
    private JFXButton signIn;
    @FXML
    private Label l1;
    @FXML
    private Label l2;
    @FXML
    private Label l3;
    @FXML
    private Label s1;
    @FXML
    private Label s2;
    @FXML
    private JFXButton aboutUs;
    @FXML
    private Label a1;
    @FXML
    private Label a2;
    @FXML
    private Label a3;
    @FXML
    private Label a4;
    @FXML
    private Label b2;
    @FXML
    private JFXButton loginButton; 
    @FXML
    private TextField userName;
    @FXML
    private JFXPasswordField password;
    @FXML
    private JFXButton forgotPass;
    @FXML
    private AnchorPane layer1;
    @FXML
    private Pane pane;
    @FXML
    private FontAwesomeIconView icon;
    @FXML
    private Circle circle;
    @FXML
    private Circle circle1;
    @FXML
    private ImageView logo;
    @FXML
    private Circle circle11;
    @FXML
    private ImageView logo1;

    private ClientController clientController = null;

    /**
     * Initialize the login window
     */
    public void initialize() {
    	 s1.setVisible(false);
         s2.setVisible(false);
         aboutUs.setVisible(false);
         b2.setVisible(false);
         loginButton.setVisible(false);
         userName.setVisible(false);
         password.setVisible(false);
         forgotPass.setVisible(false);
    	
        try {
            clientController = ClientController.getInstance(this);
            System.out.println("login UI initialized");
        } catch (IOException e) {
            e.printStackTrace();
            IcmUtils.displayErrorMsg(e.getMessage());
        }
    }

    /**
     * slide the pane to show the login screen
     */
    @FXML
    private void showLoginPane() {
        TranslateTransition slide = new TranslateTransition();
        slide.setDuration(Duration.seconds(0.7));
        slide.setNode(layer2);
        
        slide.setToX(491);
        slide.play();
        
        layer1.setTranslateX(-309);
        loginButton.setVisible(true);
        b2.setVisible(true);
        
        s1.setVisible(true);
        s2.setVisible(true);
        aboutUs.setVisible(true);
        circle1.setVisible(false);
        logo.setVisible(false);
        l1.setVisible(false);
        l2.setVisible(false);
        l3.setVisible(false);
        signIn.setVisible(false);
        a1.setVisible(false);
        a2.setVisible(false);
        a3.setVisible(false);
        a4.setVisible(false);
        userName.setVisible(true);
        password.setVisible(true);
        forgotPass.setVisible(true);
        pane.setVisible(true);
        circle.setVisible(true);
        icon.setVisible(true);
        circle11.setVisible(true);
        logo1.setVisible(true);
    }

    /**
     * slide the pane to show the about us screen
     */
    @FXML
    private void showAboutUsPane() {
        TranslateTransition slide = new TranslateTransition();
        slide.setDuration(Duration.seconds(0.7));
        slide.setNode(layer2);
        
        slide.setToX(0);
        slide.play();
        
        layer1.setTranslateX(0);
        loginButton.setVisible(false);
        b2.setVisible(false);
        
        s1.setVisible(false);
        s2.setVisible(false);
        aboutUs.setVisible(false);
        circle1.setVisible(true);
        logo.setVisible(true);
        l1.setVisible(true);
        l2.setVisible(true);
        l3.setVisible(true);
        signIn.setVisible(true);
        a1.setVisible(true);
        a2.setVisible(true);
        a3.setVisible(true);
        a4.setVisible(true);
        userName.setVisible(false);
        password.setVisible(false);
        forgotPass.setVisible(false);
        pane.setVisible(false);
        circle.setVisible(false);
        icon.setVisible(false);
        circle11.setVisible(false);
        logo1.setVisible(false);
    }

    /**
     * get the user ID and password from UI and login the user into the system.
     */
    @FXML
    void login() {
        try {
            clientController = ClientController.getInstance(this);
        } catch (IOException e) {
            e.printStackTrace();
        }
        List<String> params = new ArrayList<>();
        params.add(userName.getText());
        params.add(password.getText());
        ServerService loginService = new ServerService(ServerService.DatabaseService.Login, params);

        clientController.handleMessageFromClientUI(loginService);


    }

    /**
     * shows "ICM - Forgot password" dialog.
     */
    @FXML
    void forgotPass() {
    	try {
			IcmUtils.popUpScene(this,  "ICM - Forgot password", "/client/login/ForgotPassword.fxml", 588, 515);
		} catch (IOException e) {
			e.printStackTrace();
		}
    }

    /**
     * handle the returned value from server.
     * if the details are correct, sets the current user on ClientController and load the main window.
     * else, shows an error message.
     *
     * @param serverService contains the exception object from server
     */
    @Override
    public void handleMessageFromClientController(ServerService serverService) {
            System.out.println("login return from server");
            if(serverService.getParams() == null) {
                IcmUtils.displayErrorMsg("Wrong user name or password");
            }
            else {
                List<ChangeInitiator> userList = serverService.getParams();
                ClientController.setUser(userList.get(0));
                try {
                    IcmUtils.loadScene(this, IcmUtils.Scenes.Main_Window);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
    }
}
