package client.crDetails.phaseLeader.RequestExtensionTime;

import client.ClientController;
import client.ClientUI;
import client.crDetails.phaseLeader.PhaseLeaderButtons;
import common.IcmUtils;
import entities.Phase;
import entities.Phase.PhaseStatus;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.DateCell;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextArea;
import server.ServerService;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class RequestExtensionTime implements ClientUI{

	@FXML
	private DatePicker RequestedtimeDatePicker;
	@FXML
	private TextArea DescriptionTextArea;
	@FXML
	private Button submitButton;
	@FXML
	private Button CancelButton;


	private Phase newCurrPhase;
	private LocalDate datePickerChoice;
	private String description;
	private ClientController clientController;
	final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

	public void initialize() {
		try {
		newCurrPhase=PhaseLeaderButtons.getPhase();
		
		RequestedtimeDatePicker.setDayCellFactory(picker -> new DateCell() {
			public void updateItem(LocalDate date, boolean empty) {
				super.updateItem(date, empty);
				LocalDate deadLine = newCurrPhase.getDeadLine();
				
				setDisable(empty || date.compareTo(deadLine) <=0 );
			}
		});

		BooleanBinding test = Bindings.createBooleanBinding(() -> {
    		datePickerChoice = RequestedtimeDatePicker.getValue();
    		description = DescriptionTextArea.getText();
          System.out.println(DescriptionTextArea.getText()+RequestedtimeDatePicker.getValue());
            return (datePickerChoice == null || description.isEmpty()|| description.trim().equals(""));
        }, 	RequestedtimeDatePicker.valueProperty(),
				DescriptionTextArea.textProperty()
        );
    	submitButton.disableProperty().bind(test);
		
		
		clientController = ClientController.getInstance(this);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@FXML
	void submitRequestTime(ActionEvent event) {
		datePickerChoice = RequestedtimeDatePicker.getValue();
		description = DescriptionTextArea.getText();

		newCurrPhase.setTimeExtensionRequest(datePickerChoice);
		newCurrPhase.setPhaseStatus(PhaseStatus.EXTENSION_TIME_REQUESTED);
		newCurrPhase.setDescription(description);
		PhaseLeaderButtons.setPhase(newCurrPhase);

		List<Phase> phaseList = new ArrayList<>();
		phaseList.add(newCurrPhase);
		ServerService updatePhaseExtension = new ServerService(ServerService.DatabaseService.Update_Phase_Extension, phaseList);
		clientController.handleMessageFromClientUI(updatePhaseExtension);
	}


	@FXML
	void cancelRequestTime(ActionEvent event) {
		IcmUtils.getPopUp().close();
	}

	@Override
	public void handleMessageFromClientController(ServerService serverService) {
		
				List<Boolean> update=serverService.getParams();
				boolean checkUpdate= update.get(0);
				if(checkUpdate == true) {
					IcmUtils.displayInformationMsg("Time Extension Request Submited", "Time extension request has been successfully submited","Current deadline: " + newCurrPhase.getDeadLine().format(formatter) + "\n"+ "Time extension request: " + newCurrPhase.getTimeExtensionRequest().format(formatter));
					IcmUtils.getPopUp().close();
				}	
	}
}