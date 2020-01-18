package client.crDetails.supervisor;

import java.awt.color.ICC_ColorSpace;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import client.ClientController;
import client.ClientUI;
import client.crDetails.CrDetails;
import common.IcmUtils;
import entities.Phase;
import entities.Phase.PhaseStatus;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.stage.StageStyle;
import server.ServerService;
import server.ServerService.DatabaseService;

public class SupervisorButtons implements ClientUI {

	@FXML
	private Button phaseTimeDecisionButton;

	@FXML
	private Button assignPhaseLeadersButton;

	@FXML
	private Button freezeRequestButton;

	@FXML
	private Button closeChangeRequestButton;
	@FXML
	private Button phaseTimeRequestInfo;
	@FXML
	private Button closeChangeRequestInfoButton;
	@FXML
	private Button freezeRequestInfoButton;
	@FXML
	private Button editButton;
	@FXML
	private Button editButtonInfo;

	private static Phase.PhaseStatus CurrStatus;
    private String info;
    private ClientController clientController;
    private static Phase currPhase;
    private static Phase.PhaseName currPhaseName;
    private boolean reloadScreen = false;

    private Alert pleaseWaitMessage;

	/**
	 * Initialize the supervisor buttons according to the phase and status of the request.
	 */
    public void initialize() {
    	try {
			clientController=ClientController.getInstance(this);
			if(reloadScreen == false) {
				currPhase = CrDetails.getCurrRequest().getPhases().get(0);
				CurrStatus = CrDetails.getCurrRequest().getPhases().get(0).getPhaseStatus();
 			    reloadScreen = true;
			}
			currPhaseName = currPhase.getName();
			switch(currPhaseName) {
			case SUBMITTED:
			case EVALUATION:
			case EXAMINATION:
			case EXECUTION:
			case VALIDATION:
				info = "not in closing";
				phaseTimeDecisionButton.setDisable(true);
				assignPhaseLeadersButton.setDisable(false);
				freezeRequestButton.setDisable(false);
				closeChangeRequestButton.setDisable(true);
				phaseTimeRequestInfo.setVisible(true);
				closeChangeRequestInfoButton.setVisible(true);
				freezeRequestInfoButton.setVisible(false);
				editButton.setDisable(false);
				editButtonInfo.setVisible(false);
				switch(CurrStatus)
				{
				case SUBMITTED:
					phaseTimeDecisionButton.setDisable(true);
	    			phaseTimeRequestInfo.setVisible(true);
	    			break;
				case PHASE_LEADER_ASSIGNED:
				case PHASE_EXEC_LEADER_ASSIGNED:
				case EXTENSION_TIME_APPROVED:
				case IN_PROCESS:
				case DECLINED:
				case DONE:
				case TIME_DECLINED:
					assignPhaseLeadersButton.setText("View phase leaders");
					phaseTimeDecisionButton.setDisable(true);
	    			phaseTimeRequestInfo.setVisible(true);
	    			break;
				case TIME_REQUESTED:
				case EXTENSION_TIME_REQUESTED:
					assignPhaseLeadersButton.setText("View phase leaders");
					phaseTimeDecisionButton.setDisable(false);
	    			phaseTimeRequestInfo.setVisible(false);
	    			break;
				}
				break;
				
			case CLOSING:
				assignPhaseLeadersButton.setText("View phase leaders");
				switch(CurrStatus)
				{
				case DONE:
					info = "finished";
					phaseTimeDecisionButton.setDisable(true);
					assignPhaseLeadersButton.setDisable(false);
					freezeRequestButton.setDisable(true);
					closeChangeRequestButton.setDisable(true);
					phaseTimeRequestInfo.setVisible(false);
					closeChangeRequestInfoButton.setVisible(true);
					freezeRequestInfoButton.setVisible(false);
					editButton.setDisable(true);
					editButtonInfo.setVisible(true);
					break;
					
				default:
					phaseTimeDecisionButton.setDisable(true);
					assignPhaseLeadersButton.setDisable(false);
					freezeRequestButton.setDisable(false);
					closeChangeRequestButton.setDisable(false);
					phaseTimeRequestInfo.setVisible(false);
					closeChangeRequestInfoButton.setVisible(false);
					freezeRequestInfoButton.setVisible(false);
					editButton.setDisable(true);
					break;
					
				}
				break;
			}
			if(CrDetails.getCurrRequest().isSuspended())
			{//if the change request is suspended, the supervisor can only see the freezeRequestInfoButton.
				phaseTimeDecisionButton.setDisable(true);
				assignPhaseLeadersButton.setDisable(false);
				freezeRequestButton.setDisable(true);
				closeChangeRequestButton.setDisable(true);
				phaseTimeRequestInfo.setVisible(false);
				closeChangeRequestInfoButton.setVisible(false);
				freezeRequestInfoButton.setVisible(true);
				editButton.setDisable(true);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

    /**
     * Closes change request
     * @param event-close request button pressed
     */
	@FXML
	void closeChangeRequest(ActionEvent event) {
		Optional<ButtonType> result = IcmUtils.displayConfirmationMsg("Close request confirmation", "Close request confirmation", "Are you sure you want to close this request?");
		if (result.get() == ButtonType.OK) {
			List<String> params = new ArrayList<String>();
			params.add(CrDetails.getCurrRequest().getId().toString());
			params.add(CrDetails.getCurrRequest().getPhases().get(0).getPhaseStatus().toString());
			params.add(CrDetails.getCurrRequest().getInitiator().getFirstName() + " "
					+ CrDetails.getCurrRequest().getInitiator().getLastName());
			params.add(CrDetails.getCurrRequest().getInitiator().getEmail());
			clientController.handleMessageFromClientUI(new ServerService(DatabaseService.Close_Request, params));

			pleaseWaitMessage = new Alert(Alert.AlertType.INFORMATION);
			pleaseWaitMessage.setTitle("Closing request");
			pleaseWaitMessage.setHeaderText("Closing request");
			pleaseWaitMessage.setContentText("Please wait...");
			pleaseWaitMessage.getDialogPane().lookupButton(ButtonType.OK).setVisible(false);
			pleaseWaitMessage.initStyle(StageStyle.TRANSPARENT);
			pleaseWaitMessage.showAndWait();

		}
	}
	/**
	 * Freezes specific request
	 * @param event-freeze button pressed
	 */
	@FXML
	void freezeRequest(ActionEvent event) {
		Optional<ButtonType> result = IcmUtils.displayConfirmationMsg("Freeze request confirmation", "Freeze request confirmation", "Are you sure you want to freeze this request?");
		if (result.get() == ButtonType.OK) {
			List<Integer> list = new ArrayList<Integer>();
			list.add(CrDetails.getCurrRequest().getId());
			clientController.handleMessageFromClientUI(new ServerService(DatabaseService.Freeze_Request, list));
			try {
				IcmUtils.loadScene(this, IcmUtils.Scenes.Change_Request_Summary);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
	}

    @FXML
    /**
	 * Set decision on a time request 
	 * (Show the dialog according to the time request- phase time or phase time extension).
	 * @param event-setTimeDecision button pressed
	 */
    void setTimeDecision(ActionEvent event) {

    		CurrStatus = CrDetails.getCurrRequest().getPhases().get(0).getPhaseStatus();
    		System.out.println(CrDetails.getCurrRequest().getPhases().get(0).getPhaseStatus());
    		System.out.println(CurrStatus);
    		switch (CurrStatus) {
    		case TIME_REQUESTED:
    			try {
    				IcmUtils.popUpScene(this, "Time Request Decision","/client/crDetails/supervisor/timeDecision/TimeRequestDecision.fxml", 588, 688);
					IcmUtils.loadScene(this, IcmUtils.Scenes.Change_Request_Summary);
    			} catch (IOException e) {
    				e.printStackTrace();
    			}
    			break;

    		case EXTENSION_TIME_REQUESTED:
    			try {
    				IcmUtils.popUpScene(this, "Time Request Decision","/client/crDetails/supervisor/timeDecision/ExtensionTimeDecision.fxml", 588, 688);
					IcmUtils.loadScene(this, IcmUtils.Scenes.Change_Request_Summary);
    			} catch (IOException e) {
    				e.printStackTrace();
    			}
    			break;

		default:
			IcmUtils.displayErrorMsg("Phase time error", "Phase time error", "There are no time requests.\n" +
					"The button should be disable.\nPlease contact ICM support team.");

		}
	}

	@FXML
	/**
	 * Information massage that appears when the time decision button is disabled. 
	 */
	private void phaseTimeDecisionInfo() {
		IcmUtils.displayInformationMsg(
				"Phase time help",
				"Phase time help",
				"There is no time request waiting for approval.");
	}

	@FXML
	/**
	 * Information massage that appears when the freeze button is disabled. 
	 */
	private void freezeRequestInfo() {
		IcmUtils.displayInformationMsg(
				"Frozen request",
				"Frozen request",
				"This request is already frozen.");
	}

	@FXML
	/**
	 * Assign phase leaders and workers dialog when pressed.
	 * @param event- assignPhaseLeaders button pressed.
	 */
	void showAssignPhaseLeadersDialog(ActionEvent event) {
		try {
			IcmUtils.popUpScene(this, "Assign Phase Leaders",
					"/client/crDetails/supervisor/AssignPhaseLeaders.fxml", 588, 768);
			IcmUtils.loadScene(this, IcmUtils.Scenes.Change_Request_Summary);
		} catch (IOException e) {
			e.printStackTrace(); }
    }
	/**
	 * Displays information why close button disabled
	 */
	@FXML
	public void closeRequestInfoMsg() {
		switch (info) {

			case "finished":
				IcmUtils.displayInformationMsg(
						"Closed request",
						"Closed request",
						"This request is already closed.");
				break;
			case "not in closing":
				IcmUtils.displayInformationMsg(
						"Close request help",
						"Request is still in process",
						"This request is still in process.\n" +
								"Only requests in CLOSING phase can be closed.");
				break;
		}
	}
    
    @Override
    /**
	 * Show pop-up with the information if the freeze or close action was successful 
	 * 
	 * @param serverService-ServerService object that the client controller send
	 */
    public void handleMessageFromClientController(ServerService serverService) {
    	switch(serverService.getDatabaseService()) {
    		case Freeze_Request:
    			if((Boolean)serverService.getParams().get(0)==true){
					IcmUtils.displayInformationMsg(
							"Freeze request successfully",
							"Freeze request successfully",
							"The request froze successfully.\n" +
									"To thaw it, contact ITD manager.");
					freezeRequestButton.setDisable(true);
				}

    			else
					IcmUtils.displayErrorMsg(
							"Error",
							"freeze Request Failed",
							"Please contact ICM support team.");
    			break;
    		case Close_Request:
				if ((Boolean) serverService.getParams().get(0) == true) {
					pleaseWaitMessage.close();
					IcmUtils.displayInformationMsg(
							"Close request",
							"Close request",
							"Request successfully closed.");
					closeChangeRequestButton.setDisable(true);
					closeChangeRequestInfoButton.setVisible(true);
					info="finished";
				}
				break;
    	}
    }

    @FXML
    /**
     * Edit the change request.
     * @param event- edit button was pressed.
     */
	private void editRequest(ActionEvent event){
    	try {
			IcmUtils.popUpScene(this, "Edit Request","/client/crDetails/supervisor/edit/Edit.fxml", 588, 688);
			IcmUtils.loadScene(this, IcmUtils.Scenes.Change_Request_Summary);
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	@FXML
	/**
	 * Information massage in case the edit button is disable.
	 */
	private void editButtonInfoMsg() {
		IcmUtils.displayInformationMsg(
				"Edit request help",
				"Edit is not allowed in CLOSING phase",
				"You can not edit requests in CLOSING phase.");
	}
    
	/**
	 * Set currPhase
	 * @param phase- the new current phase.
	 */
    public static void setCurrPhase (Phase phase) {
    	SupervisorButtons.currPhase=phase;
    }
    
    /**
     * get the current phase.
     * @return currPhase (the current phase).
     */
    public static Phase getPhase() {
    	return currPhase;
    }
    
    /**
     * set the current phase status.
     * @param status- the current phase status.
     */
    public static void setCurrPhaseStatus (PhaseStatus status) {
    	SupervisorButtons.CurrStatus = status;
    }
    
    /**
     * get the current phase status.
     * @return CurrStatus- the currant status.
     */
    public static PhaseStatus getPhaseStatus() {
    	return CurrStatus;
    }
}
