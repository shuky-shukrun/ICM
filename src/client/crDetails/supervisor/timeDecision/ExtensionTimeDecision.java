package client.crDetails.supervisor.timeDecision;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import client.ClientController;
import client.ClientUI;
import client.crDetails.CrDetails;
import client.crDetails.supervisor.SupervisorButtons;
import client.crDetails.tester.TesterButtons;
import common.IcmUtils;
import entities.EvaluationReport;
import entities.Phase;
import entities.Phase.PhaseStatus;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextArea;
import server.ServerService;
import server.ServerService.DatabaseService;

public class ExtensionTimeDecision implements ClientUI  {

    @FXML
    private Button submitButton;
    
    @FXML
    private DatePicker extensionTimeDatePicker;
    
    @FXML
    private Button refuseButton;
    
    @FXML
    private TextArea descriptionTextArea;
    
    private ClientController clientController;
	private LocalDate requestedTime;
	private String CurrStatus = new String();
	private PhaseStatus newCurrPhase;
	private LocalDate localDate;
	
	/**
	 * Initialize the extension time request decision dialog
	 */
	public void initialize() {
		newCurrPhase = SupervisorButtons.getPhaseStatus();
		CurrStatus = CrDetails.getCurrRequest().getPhases().get(0).getPhaseStatus().toString();
		try {
			clientController = ClientController.getInstance(this);
		} catch (IOException e) {
			e.printStackTrace();
		}
		//Get the extension time details to initialize accordingly.
		List<String> params = new ArrayList<String>();
		params.add(CrDetails.getCurrRequest().getId().toString());
		params.add(CrDetails.getCurrRequest().getCurrPhaseName().toString());
		ServerService request = new ServerService(ServerService.DatabaseService.Load_Extension_Time, params);
		clientController.handleMessageFromClientUI(request);
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
		list.add(localDate.toString());
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
	 * Initialize the extension time request decision dialog according to the request and
	 * show pop-up with the information if the decision (approve/reject) action was successful 
	 * 
	 * @param serverService-ServerService object that the client controller send
	 */
	public void handleMessageFromClientController(ServerService serverService) {
		switch(serverService.getDatabaseService()) {
		//Initialize the extension time request decision dialog according to the request
		case Load_Extension_Time:
		System.out.println("adding extension time request details to screen");

        List<String> extensionDetails = serverService.getParams();
        descriptionTextArea.textProperty().setValue(extensionDetails.get(1));
        descriptionTextArea.setEditable(false);
        descriptionTextArea.setDisable(false);
        
        String date = extensionDetails.get(0);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        localDate = LocalDate.parse(date, formatter);
        extensionTimeDatePicker.setValue(localDate);
        extensionTimeDatePicker.setDisable(false);
        extensionTimeDatePicker.setEditable(false);
        extensionTimeDatePicker.setOnMouseClicked(e -> {
	        if(!extensionTimeDatePicker.isEditable())
	        	extensionTimeDatePicker.hide(); 
	        });
        break;
        //Show pop-up with the information if the approve action was successful and send an email to ITD manager
		case Approve_Phase_Time:
			if(CurrStatus.equals("EXTENSION_TIME_REQUESTED")) {
				List<Boolean> list = serverService.getParams();
				if (list.get(0) == true &&list.get(1) == true) {
					IcmUtils.displayInformationMsg("Update time extension decision- success");
					
					List<String> dtls = new ArrayList<String>();
					dtls.add(CrDetails.getCurrRequest().getId().toString());
					dtls.add(localDate.toString());
					ServerService service = new ServerService(DatabaseService.Email_ITD_Extension_Time_Approved, dtls);
					Platform.runLater(new Runnable() {
						@Override
						public void run() {
							clientController.handleMessageFromClientUI(service);
						}
					});
				} else {
					IcmUtils.displayErrorMsg("Update time extension decision- failed");
				}
			}
			IcmUtils.getPopUp().close();
			break;
		//Show pop-up with the information if the reject action was successful 
		default:
			List<Boolean> list = serverService.getParams();
			if (list.get(0) == true) {
				IcmUtils.displayInformationMsg("Update time extension decision- success");
			} else {
				IcmUtils.displayErrorMsg("Update time extension decision- failed");
			}
			IcmUtils.getPopUp().close();
			break;
        
        
		}
		  
	}
}
