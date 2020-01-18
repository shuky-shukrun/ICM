package client.crDetails.supervisor.timeDecision;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import client.ClientController;
import client.ClientUI;
import client.crDetails.CrDetails;
import client.crDetails.phaseLeader.PhaseLeaderButtons;
import client.crDetails.supervisor.SupervisorButtons;
import client.crDetails.tester.TesterButtons;
import common.IcmUtils;
import entities.Phase;
import entities.Phase.PhaseStatus;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import server.ServerService;
import server.ServerService.DatabaseService;

public class TimeRequestDecision implements ClientUI  {
    @FXML
    private DatePicker phaseTimeDatePicker;

    @FXML
    private Button applyButton;
    
    @FXML
    private Button refuseButton;

	private ClientController clientController;
	private LocalDate requestedTime;
	private String CurrStatus = new String();
	private PhaseStatus newCurrPhase;
	
	/**
	 * Initialize the time request decision dialog
	 */
	public void initialize() {
		newCurrPhase = SupervisorButtons.getPhaseStatus();
		CurrStatus = CrDetails.getCurrRequest().getPhases().get(0).getPhaseStatus().toString();
		try {
			clientController = ClientController.getInstance(this);
		} catch (IOException e) {
			e.printStackTrace();
		}
		/*initialize the fields according to the time request.*/
		requestedTime = CrDetails.getCurrRequest().getPhases().get(0).getDeadLine();
		phaseTimeDatePicker.setValue(requestedTime);
		phaseTimeDatePicker.setDisable(false);
		phaseTimeDatePicker.setEditable(false);
		phaseTimeDatePicker.setOnMouseClicked(e -> {
	        if(!phaseTimeDatePicker.isEditable())
	        	phaseTimeDatePicker.hide(); 
	        });
	}

    @FXML
    /**
	 * Submit the decision, if possible when submit button pressed
	 * 
	 * @param event-submit button pressed event
	 */
    void submitRequestTime(ActionEvent event) {
		String crId = new String();
		crId = CrDetails.getCurrRequest().getId().toString();
		List<String> list = new ArrayList<String>();
		list.add(crId);
		list.add(CurrStatus);
		list.add(CrDetails.getCurrRequest().getCurrPhaseName().toString());
		ServerService serverService = new ServerService(DatabaseService.Approve_Phase_Time, list);
		clientController.handleMessageFromClientUI(serverService);
		newCurrPhase= Phase.PhaseStatus.IN_PROCESS;
		SupervisorButtons.setCurrPhaseStatus(newCurrPhase);
    }

    @FXML
    /**
	 * Submit the decision, if possible when reject button pressed
	 * 
	 * @param event-reject button pressed event
	 */
    void rejectRequestTime (ActionEvent event) {
    	String crId = new String();
		crId = CrDetails.getCurrRequest().getId().toString();
		List<String> list = new ArrayList<String>();
		list.add(crId);
		list.add(CurrStatus);
		list.add(CrDetails.getCurrRequest().getCurrPhaseName().toString());
		ServerService serverService = new ServerService(DatabaseService.Reject_Phase_Time, list);
		clientController.handleMessageFromClientUI(serverService);
		newCurrPhase= Phase.PhaseStatus.IN_PROCESS;
		SupervisorButtons.setCurrPhaseStatus(newCurrPhase);
    }
    
	@Override
	/**
	 * Show pop-up with the information if the decision (approve/reject) action was successful 
	 * 
	 * @param serverService-ServerService object that the client controller send
	 */
	public void handleMessageFromClientController(ServerService serverService) {
		List<Boolean> list = serverService.getParams();
		if (list.get(0) == true) {
			IcmUtils.displayInformationMsg(
					"Time request decision",
					"Time request decision updated",
					"Your decision was update successfully.");
		} else {
			IcmUtils.displayErrorMsg(
					"Time request decision error",
					"Error in time request decision",
					"Please contact ICM support team.");
		}
		IcmUtils.getPopUp().close();
		
	}
}
