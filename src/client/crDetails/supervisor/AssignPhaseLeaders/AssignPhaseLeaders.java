package client.crDetails.supervisor.AssignPhaseLeaders;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import client.ClientController;
import client.ClientUI;
import client.crDetails.CrDetails;
import entities.ChangeInitiator;
import entities.Phase;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import server.ServerService;

public class AssignPhaseLeaders implements ClientUI {

	@FXML
	private ChoiceBox<String> evaluationPhaseLeaderChoiceBox;
	@FXML
	private ChoiceBox<String> examinationPhaseLeaderChoiceBox;
	@FXML
	private ChoiceBox<String> executionPhaseLeaderChoiceBox;
	@FXML
	private ChoiceBox<String> validationPhaseLeaderChoiceBox;
	@FXML
	private Button submitButton;
	@FXML
	private Button cancelButton;
	@FXML
	private Label helpLabel;
	
	
	
	private ChangeInitiator currInitiator;
	private ClientController clientController;
	private List<ChangeInitiator> phaseLeadersList = new ArrayList<>();
	private ObservableList<String> phaseLeaderDetailsList = FXCollections.observableArrayList();

	public void initialize() {
	
		try {
			clientController = ClientController.getInstance(this);
		
			currInitiator= CrDetails.getCurrRequest().getInitiator();
			helpLabel.setText("please assign phase leaders for change request " +CrDetails.getCurrRequest().getId().toString());
			List<ChangeInitiator> ChangeInitiatorList = new ArrayList<>();
			ChangeInitiatorList.add(currInitiator);
			System.out.printf("%s\n",ChangeInitiatorList);
			ServerService getPhaseLeaders = new ServerService(ServerService.DatabaseService.Get_Phase_Leaders, ChangeInitiatorList);
			//System.out.println(updatePhaseExtension);
			clientController.handleMessageFromClientUI(getPhaseLeaders);
			
			} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void handleMessageFromClientController(ServerService serverService) {
		
		phaseLeadersList= serverService.getParams();
		for(ChangeInitiator phaseLeader:phaseLeadersList ) {
			 String details =phaseLeader.toString();
			 phaseLeaderDetailsList.add(details);
		}
		
		evaluationPhaseLeaderChoiceBox.getItems().addAll(phaseLeaderDetailsList);
		examinationPhaseLeaderChoiceBox.getItems().addAll(phaseLeaderDetailsList);
		executionPhaseLeaderChoiceBox.getItems().addAll(phaseLeaderDetailsList);
		validationPhaseLeaderChoiceBox.getItems().addAll(phaseLeaderDetailsList);
	}

}
