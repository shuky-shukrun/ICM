package client.login;

import javafx.event.ActionEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.jfoenix.controls.JFXButton;

import client.ClientController;
import client.ClientUI;
import common.IcmUtils;
import common.IcmUtils.Scenes;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import server.ServerService;
import server.ServerService.DatabaseService;

public class ForgotPassword implements ClientUI {
	private ClientController clientController;
	@FXML
	private TextField loginEmailTextField;
	@FXML
	private Button SubmitLoginEmail;
	@FXML
	private JFXButton backToLogin;

	public void initialize() {
		try {
			clientController = ClientController.getInstance(this);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void forgotPasswordAction(ActionEvent e) {
		IcmUtils.displayInformationMsg("restore password sent to you", "please check your entered email");
		try {
			IcmUtils.loadScene(this, IcmUtils.Scenes.Login);
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		List<String> email = new ArrayList<String>();
		if(!loginEmailTextField.getText().equals(""))
		{
		email.add(loginEmailTextField.getText());
		clientController.handleMessageFromClientUI(new ServerService(DatabaseService.Forgot_Password, email));
		}
		else
			IcmUtils.displayErrorMsg("you did not enter a mail");
	}
	@FXML
	public void backToLogin(ActionEvent e) {
		try {
			IcmUtils.loadScene(this, Scenes.Login);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}
	@Override
	public void handleMessageFromClientController(ServerService serverService) {
		//try {
		//	IcmUtils.loadScene(this, IcmUtils.Scenes.Login);
		//} catch (IOException e) {
		//	e.printStackTrace();
	//	}

	}

}
