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
	
	public void initialize() {
		newCurrPhase = SupervisorButtons.getPhaseStatus();
		CurrStatus = CrDetails.getCurrRequest().getPhases().get(0).getPhaseStatus().toString();
		try {
			clientController = ClientController.getInstance(this);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
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
	public void handleMessageFromClientController(ServerService serverService) {
		List<Boolean> list = serverService.getParams();
		if (list.get(0) == true) {
			IcmUtils.displayInformationMsg("update time decision- success");
		} else {
			IcmUtils.displayErrorMsg("update time decision- failed");
		}
		IcmUtils.getPopUp().close();
		
	}
}
