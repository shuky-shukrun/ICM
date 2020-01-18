package client.login;

import javafx.beans.binding.BooleanBinding;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;

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
import javafx.scene.input.KeyEvent;
import server.ServerService;
import server.ServerService.DatabaseService;

public class ForgotPassword implements ClientUI {
	private ClientController clientController;
	@FXML
	private TextField loginIDTextField;
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
			loginIDTextField.addEventFilter(KeyEvent.KEY_TYPED, numeric_Validation(8));
		} catch (IOException e) {
			e.printStackTrace();
		}
		// disable request time button any field is invalid
				BooleanBinding bb = new BooleanBinding() {
					{
						super.bind(loginIDTextField.textProperty());
					}

					@Override
					// disable, if one selection is missing or evaluated time is later than the
					// deadline of the phase
					protected boolean computeValue() {
					
						if( loginIDTextField.getText().isEmpty())
							info="no Id";
						return ( loginIDTextField.getText().isEmpty());
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
		List<Integer> idList=new ArrayList<Integer>();
		int id;
		if(!loginIDTextField.getText().equals(""))
		{
		id=Integer.parseInt(loginIDTextField.getText());
		idList.add(id);
		clientController.handleMessageFromClientUI(new ServerService(DatabaseService.Forgot_Password, idList));
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

	@Override
	public void handleMessageFromClientController(ServerService serverService) {
	
	}

}
