package client.crDetails.supervisor.AssignPhaseLeaders;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import client.ClientController;
import client.ClientUI;
import client.crDetails.CrDetails;
import common.IcmUtils;
import entities.ChangeInitiator;
import entities.IEPhasePosition;
import entities.IEPhasePosition.PhasePosition;
import entities.Phase;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import server.ServerService;

public class AssignPhaseLeaders implements ClientUI {

	@FXML
	private ChoiceBox<ChangeInitiator> evaluationPhaseLeaderChoiceBox;
	@FXML
	private ChoiceBox<ChangeInitiator> examinationPhaseLeaderChoiceBox;
	@FXML
	private ChoiceBox<ChangeInitiator> executionPhaseLeaderChoiceBox;
	@FXML
	private ChoiceBox<ChangeInitiator> validationPhaseLeaderChoiceBox;
	@FXML
	private Button submitButton;
	@FXML
	private Button cancelButton;
	@FXML
	private Label helpLabel;
	
	
	
	private ChangeInitiator currInitiator;
	private ClientController clientController;
	private List<ChangeInitiator> optionalPhaseLeadersList = new ArrayList<>();
	private ObservableList<ChangeInitiator> phaseLeaderDetailsList = FXCollections.observableArrayList();
	private List<IEPhasePosition> newPhaseLeadersList = new ArrayList<>();
    private IEPhasePosition evaluationPhaseLeader= new IEPhasePosition(); 
    private IEPhasePosition examinationPhaseLeader= new IEPhasePosition(); 
    private IEPhasePosition executionPhaseLeader= new IEPhasePosition(); 
    private IEPhasePosition validationPhaseLeader= new IEPhasePosition(); 
    private int crId;
    
	public void initialize() {
		try {
			clientController = ClientController.getInstance(this);
			crId=CrDetails.getCurrRequest().getId();
			
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
	
	
	@FXML
	void submitAssignPhaseLeaders(ActionEvent event) {
		evaluationPhaseLeader.setCrID(crId);
		evaluationPhaseLeader.setInformationEngineer(evaluationPhaseLeaderChoiceBox.getSelectionModel().getSelectedItem());
		evaluationPhaseLeader.setPhaseName(Phase.PhaseName.EVALUATION);
		evaluationPhaseLeader.setPhasePosition(PhasePosition.PHASE_LEADER);
		
		examinationPhaseLeader.setCrID(crId);
		examinationPhaseLeader.setInformationEngineer(examinationPhaseLeaderChoiceBox.getSelectionModel().getSelectedItem());
		examinationPhaseLeader.setPhaseName(Phase.PhaseName.EXAMINATION);
		examinationPhaseLeader.setPhasePosition(PhasePosition.PHASE_LEADER);
		
		executionPhaseLeader.setCrID(crId);
		executionPhaseLeader.setInformationEngineer(executionPhaseLeaderChoiceBox.getSelectionModel().getSelectedItem());
		executionPhaseLeader.setPhaseName(Phase.PhaseName.EXECUTION);
		executionPhaseLeader.setPhasePosition(PhasePosition.PHASE_LEADER);
		
		validationPhaseLeader.setCrID(crId);
		validationPhaseLeader.setInformationEngineer(validationPhaseLeaderChoiceBox.getSelectionModel().getSelectedItem());
		validationPhaseLeader.setPhaseName(Phase.PhaseName.VALIDATION);
		validationPhaseLeader.setPhasePosition(PhasePosition.PHASE_LEADER);
		
		newPhaseLeadersList.add(evaluationPhaseLeader);
		newPhaseLeadersList.add(examinationPhaseLeader);
		newPhaseLeadersList.add(executionPhaseLeader);
		newPhaseLeadersList.add(validationPhaseLeader);
		
		ServerService updatePhaseLeaders = new ServerService(ServerService.DatabaseService.Supervisor_Update_Phase_Leaders, newPhaseLeadersList);
		clientController.handleMessageFromClientUI(updatePhaseLeaders);
	}


	@FXML
	void cancelAssignPhaseLeaders(ActionEvent event) {
		IcmUtils.getPopUp().close();
	}
	
	@Override
	public void handleMessageFromClientController(ServerService serverService) {
		switch (serverService.getDatabaseService()) {
		case Get_Phase_Leaders:
		optionalPhaseLeadersList= serverService.getParams();
		phaseLeaderDetailsList.setAll(optionalPhaseLeadersList);
	
		evaluationPhaseLeaderChoiceBox.setItems(phaseLeaderDetailsList);
		examinationPhaseLeaderChoiceBox.setItems(phaseLeaderDetailsList);
		executionPhaseLeaderChoiceBox.setItems(phaseLeaderDetailsList);
		validationPhaseLeaderChoiceBox.setItems(phaseLeaderDetailsList);
			break;

		case Supervisor_Update_Phase_Leaders:
			List<Boolean> update=serverService.getParams();
			boolean checkUpdate= update.get(0);
			if(checkUpdate== true) {
				IcmUtils.displayInformationMsg("Time Extension Request Submited", "Phase Leaders has been successfully submited","Current deadline: ");
				IcmUtils.getPopUp().close();
			break;
		}
	}
	}	
		
	
}
