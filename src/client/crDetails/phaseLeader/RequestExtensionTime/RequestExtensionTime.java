package client.crDetails.phaseLeader.RequestExtensionTime;

import java.io.IOException;
import java.security.cert.Extension;
import java.sql.Time;
import java.util.ArrayList;
import java.util.List;

import client.ClientController;
import client.ClientMain;
import client.ClientUI;
import client.crDetails.CrDetails;
import client.crDetails.phaseLeader.PhaseLeaderButtons;
import common.IcmUtils;
import entities.ChangeRequest;
import entities.Phase;
import entities.Phase.PhaseStatus;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;
import server.ServerService;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.time.format.DateTimeFormatter;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;

public class RequestExtensionTime implements ClientUI{
	
	@FXML
    private DatePicker RequestedtimeDatePicker;
	@FXML
	private TextArea DescriptionTextAreaDescriptionTextArea;
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
	            clientController = ClientController.getInstance(this);
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
	}
	
    @FXML
    void submitRequestTime(ActionEvent event) {
    	
    	    newCurrPhase=PhaseLeaderButtons.getPhase();
    		datePickerChoice = RequestedtimeDatePicker.getValue();
    		description = DescriptionTextAreaDescriptionTextArea.getText();
    		System.out.println(description+datePickerChoice.format(formatter));
    		
    		newCurrPhase.setTimeExtensionRequest(datePickerChoice);
    		newCurrPhase.setExtensionRequest(true);
    		newCurrPhase.setPhaseStatus(PhaseStatus.EXTENSION_TIME_REQUESTED);
    		newCurrPhase.setDescription(description);
    		
            List<Phase> phaseList = new ArrayList<>();
            phaseList.add(newCurrPhase);
            System.out.printf("%s\n",phaseList);
            ServerService updatePhaseExtension = new ServerService(ServerService.DatabaseService.Update_Phase_Extension, phaseList);
            //System.out.println(updatePhaseExtension);
            clientController.handleMessageFromClientUI(updatePhaseExtension);  
    }
    
    
    @FXML
    void cancelRequestTime(ActionEvent event) {
    	IcmUtils.getPopUp().close();
    }

    @Override
    public void handleMessageFromClientController(ServerService serverService) {
     
    
    	switch (serverService.getDatabaseService()) {

        case Update_Phase_Extension:
        	List<Boolean> update=serverService.getParams();
        	boolean checkUpdate= update.get(0);
        	if(checkUpdate== true) {
        		IcmUtils.displayInformationMsg("Time Extension Request Submited", "Time extension request has been successfully submited", "Time extension request: " + newCurrPhase.getTimeExtensionRequest().format(formatter));
        	    IcmUtils.getPopUp().close();    	  
        	}
        	
        	break; }
    }
}