package client.crDetails.tester.setDecision;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import client.ClientController;
import client.ClientUI;
import client.crDetails.CrDetails;
import client.crDetails.ccc.CCCButtons;
import client.crDetails.phaseLeader.PhaseLeaderButtons;
import client.crDetails.tester.TesterButtons;
import common.IcmUtils;
import entities.Phase;
import entities.Phase.PhaseName;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextArea;
import server.ServerService;
import server.ServerService.DatabaseService;

public class SetDecision implements ClientUI {

	@FXML
	private ChoiceBox<String> decisionChoiceBox;

	@FXML
	private TextArea descriptionTextArea;

	@FXML
	private Button okButton;

	@FXML
	private Button cancelButton;

	private ClientController clientController;
	private PhaseName currPhase;
	private Phase newCurrPhase;
	private Phase oldCurrPhase;
	
	/**
	 * Initialize the set decision report dialog
	 */
	public void initialize() {
		currPhase = CrDetails.getCurrRequest().getCurrPhaseName();
		switch(currPhase) {
		case VALIDATION:
			newCurrPhase = TesterButtons.getPhase();
			break;
		case EXAMINATION:
			newCurrPhase = CCCButtons.getPhase();
		}
		okButton.setDisable(true);

		try {
			clientController = ClientController.getInstance(this);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		switch (currPhase) {
		//initialize the choice box according to the phase.
		case VALIDATION:
			decisionChoiceBox.getItems().addAll("Approve The Change", "Report Test Failure");
			break;

		case EXAMINATION:
			decisionChoiceBox.getItems().addAll("Commit The Change", "Ask For Additional Data", "Decline The Change");
			break;
		}
	}

	@FXML
	public void keyReleaseProperty() {
		//set the submit button according to the validation of the fields.(if not valid- disable the button).
		String decision = decisionChoiceBox.getValue();
		boolean isDisabled = true;
		if (decision.equals("Report Test Failure") || decision.equals("Ask For Additional Data")
				|| decision.equals("Decline The Change")) {
			if (!descriptionTextArea.getText().isEmpty() && !descriptionTextArea.getText().trim().isEmpty())
				isDisabled = false;
		} else if (decision.equals("Approve The Change") || decision.equals("Commit The Change"))
			isDisabled = false;

		okButton.setDisable(isDisabled);
	}

	@FXML
	/**
	 * Submit the decision, if possible when submit button pressed
	 * 
	 * @param event-submit button pressed event
	 */
	void submitDecision(ActionEvent event) {
		String decision = decisionChoiceBox.getValue();

		List<String> list = new ArrayList<String>();
		list.add(decision);
		list.add(descriptionTextArea.getText());
		
		String crId = new String();
		crId = CrDetails.getCurrRequest().getId().toString();
		list.add(crId);
		list.add(currPhase.toString());

		ServerService serverService = new ServerService(DatabaseService.Set_Decision, list);
		clientController.handleMessageFromClientUI(serverService);
		newCurrPhase.setName(Phase.PhaseName.CLOSING);
		switch(currPhase) {
		case VALIDATION:
			TesterButtons.setPhase(newCurrPhase);
			break;
			
		case EXAMINATION:
			CCCButtons.setCurrPhase(newCurrPhase);
			break;
		}
		
		System.out.println(decision);
	}
	
	@FXML
	/**
	 * Back to change request summary dialog when cancel button pressed
	 * 
	 * @param event-cancel button pressed event
	 */
	void cancelSetDecision(ActionEvent event) {
		IcmUtils.getPopUp().close();
	}

	@Override
	/**
	 * Show pop-up with the information if the set decision action was successful 
	 * 
	 * @param serverService-ServerService object that the client controller send
	 */
	public void handleMessageFromClientController(ServerService serverService) {
		List<Boolean> list = serverService.getParams();
		if (list.get(0) == true && list.get(2) == true) {
			IcmUtils.displayInformationMsg("Decision updated", "Decision updated", "Your decision was update successfully.");
		} else {
			IcmUtils.displayErrorMsg("Error", "Error in tester decision", "Please contact system administrator.");
		}
		//Check if there was an exception in the phase time.
		oldCurrPhase = CrDetails.getCurrRequest().getPhases().get(0);
		oldCurrPhase.setName(currPhase);
		List<Phase> phList = new ArrayList<>();
		phList.add(oldCurrPhase);
		ServerService updateExceptionTime = new ServerService(ServerService.DatabaseService.Update_Exception_Time, phList);
		clientController.handleMessageFromClientUI(updateExceptionTime);
		IcmUtils.getPopUp().close();
	}
}
