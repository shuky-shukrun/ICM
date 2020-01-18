package client.crDetails.supervisor.edit;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import client.ClientController;
import client.ClientUI;
import client.crDetails.CrDetails;
import common.IcmUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.DateCell;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import server.ServerService;
import server.ServerService.DatabaseService;

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
	
	private ClientController clientController;
	DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/YYYY");
	
	/**
	 * Initialize the edit dialog.
	 */
	public void initialize() {
		try {
			clientController = ClientController.getInstance(this);
			LocalDate deadLine = CrDetails.getCurrRequest().getPhases().get(0).getDeadLine();
			currDeadlineTextField.textProperty().setValue(deadLine.format(formatter));
			currDeadlineTextField.setEditable(false);
			
			newDeadlineDatePicker.setDayCellFactory(picker -> new DateCell() {
				public void updateItem(LocalDate date, boolean empty) {
					super.updateItem(date, empty);					
					setDisable(empty || date.compareTo(deadLine) == 0 ||
							date.compareTo(LocalDate.now()) < 0);
				}
			});
			newDeadlineDatePicker.setDisable(false);
			reasonTextArea.setDisable(false);
			submitButton.setDisable(true);
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}

	@FXML
	/**
	 *Set the submit button according to the validation of the fields.(if not valid- disable the button).
	 */
	public void keyReleaseProperty() {
		boolean fieldsEmpty = true;
		String reason = reasonTextArea.getText();
		fieldsEmpty = (newDeadlineDatePicker.getValue() == null || (reason.isEmpty() && reason.trim().equals("")));
		submitButton.setDisable(fieldsEmpty);
	}
	
	@FXML
	/**
	 * Submit the requested edit, if possible when submit button pressed.
	 * @param event-submit button pressed event
	 */
	void submitEdit(ActionEvent event) {
		List<String> params = new ArrayList<String>();
		String crId = new String();
		crId = CrDetails.getCurrRequest().getId().toString();
		params.add(crId);
		params.add(newDeadlineDatePicker.getValue().toString());
		params.add(reasonTextArea.getText());
		params.add(CrDetails.getCurrRequest().getCurrPhaseName().toString());
		
		ServerService serverService = new ServerService(DatabaseService.Edit_Request, params);
		clientController.handleMessageFromClientUI(serverService);
		
	}

	@FXML
	/**
	 * close the edit request dialog.
	 * @param event-cancel button pressed event
	 */
	void closeEdit(ActionEvent event) {
		IcmUtils.getPopUp().close();
	}
	
	
	@Override
	/**
	 * Show pop-up with the information if the edit action was successful 
	 * 
	 * @param serverService-ServerService object that the client controller send
	 */
	public void handleMessageFromClientController(ServerService serverService) {
		List<Boolean> list = serverService.getParams();
		if (list.get(0) == true && list.get(1)== true && list.get(2) == true) {
			IcmUtils.displayInformationMsg("Decision updated", "Decision updated", "Your decision was update successfully.");
		} else {
			IcmUtils.displayErrorMsg("Error", "Error in tester decision", "Please contact system administrator.");
		}
		IcmUtils.getPopUp().close();
		
	}

}
