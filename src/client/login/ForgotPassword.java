package client.login;

import javafx.beans.binding.BooleanBinding;
import javafx.event.ActionEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.jfoenix.controls.JFXButton;

import client.ClientController;
import client.ClientUI;
import common.IcmUtils;
import common.JavaEmail;
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
	private Button backToLogin;
	@FXML
	private JFXButton submitInfoButton;
	private String info;
	/**
	 * Initialize the forgot password screen
	 */
	public void initialize() {
	
		try {
			clientController = ClientController.getInstance(this);
		} catch (IOException e) {
			e.printStackTrace();
		}
		// disable request time button any field is invalid
				BooleanBinding bb = new BooleanBinding() {
					{
						super.bind(loginEmailTextField.textProperty());
					}

					@Override
					// disable, if one selection is missing or evaluated time is later than the
					// deadline of the phase
					protected boolean computeValue() {
					
						if( loginEmailTextField.getText().isEmpty())
							info="no mail";
						else if(!JavaEmail.isValidEmailAddress(loginEmailTextField.getText()))
							info="not legal email";
						return ( loginEmailTextField.getText().isEmpty()||!JavaEmail.isValidEmailAddress(loginEmailTextField.getText()));
					}
				};

				SubmitLoginEmail.disableProperty().bind(bb);
				submitInfoButton.visibleProperty().bind(bb);
	}
	/**
	 * send email to restore password when appropriate button pressed
	 * @param e-"submit" button pressed
	 */
	public void forgotPasswordAction(ActionEvent e) {
		IcmUtils.displayInformationMsg(
				"Password restored",
				"Password restored",
				"We sent you an email with your login details.\n" +
						"Please check your email box.");
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
		IcmUtils.getPopUp().close();
		}
		
	}
	@FXML
	public void submitInfoMsg(ActionEvent e) {
		switch(info) {
		case "no mail":
			IcmUtils.displayInformationMsg(
					"Forgot password help",
					"No email entered",
					"You did not enter a mail.");
			break;
		case "not legal email":
			IcmUtils.displayInformationMsg(
					"Forgot password help",
					"Illegal email address",
					"Format of legal mail: \nmail_box_name@domain_name.xyz"+"\n"+"for example:ploni@gmail.com");
		}
	}
 
	@FXML
	/**
	 * back to the login screen when appropriate button pressed
	 * @param e-"back" button pressed
	 */
	public void backToLogin(ActionEvent e) {
		IcmUtils.getPopUp().close();
	
	}
	@Override
	public void handleMessageFromClientController(ServerService serverService) {
	
	}

}
