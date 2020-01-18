package client.crDetails.supervisor.edit;

import client.ClientUI;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import server.ServerService;

public class Edit implements ClientUI{

	@FXML
	private Button submitButton;
	@FXML
	private Button CloseButton;
	@FXML
	private TextField currDeadlineTextField;
	@FXML
	private DatePicker newDeadlineDatePicker;
	@FXML
	private TextArea reasonTextArea;
	
	
	public void initialize() {
		
	}

	@FXML
	void submitEdit(ActionEvent event) {
		
	}

	@FXML
	void closeEdit(ActionEvent event) {
		
	}
	
	
	@Override
	public void handleMessageFromClientController(ServerService serverService) {
		
		
	}

}
