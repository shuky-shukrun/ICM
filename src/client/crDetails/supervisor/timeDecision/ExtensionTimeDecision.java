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
import entities.EvaluationReport;
import entities.Phase;
import entities.Phase.PhaseStatus;
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
	
	public void initialize() {
		newCurrPhase = SupervisorButtons.getPhaseStatus();
		CurrStatus = CrDetails.getCurrRequest().getPhases().get(0).getPhaseStatus().toString();
		try {
			clientController = ClientController.getInstance(this);
		} catch (IOException e) {
			e.printStackTrace();
		}
		List<String> params = new ArrayList<String>();
		params.add(CrDetails.getCurrRequest().getId().toString());
		params.add(CrDetails.getCurrRequest().getCurrPhaseName().toString());
		ServerService request = new ServerService(ServerService.DatabaseService.Load_Extension_Time, params);
		clientController.handleMessageFromClientUI(request);
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

		ServerService serverService = new ServerService(DatabaseService.Reject_Phase_Time, list);
		clientController.handleMessageFromClientUI(serverService);
		newCurrPhase= Phase.PhaseStatus.IN_PROCESS;
		SupervisorButtons.setCurrPhaseStatus(newCurrPhase);
    }

	@Override
	public void handleMessageFromClientController(ServerService serverService) {
		System.out.println("adding extension time request details to screen");

        List<String> extensionDetails = serverService.getParams();
        descriptionTextArea.textProperty().setValue(extensionDetails.get(1));
        descriptionTextArea.setEditable(false);
        descriptionTextArea.setDisable(false);
        
        String date = extensionDetails.get(0);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate localDate = LocalDate.parse(date, formatter);
        extensionTimeDatePicker.setValue(localDate);
        extensionTimeDatePicker.setDisable(false);
        extensionTimeDatePicker.setEditable(false);
        extensionTimeDatePicker.setOnMouseClicked(e -> {
	        if(!extensionTimeDatePicker.isEditable())
	        	extensionTimeDatePicker.hide(); 
	        });
        
	}
}
