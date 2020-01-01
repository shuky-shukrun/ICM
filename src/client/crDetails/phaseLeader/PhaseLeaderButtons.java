package client.crDetails.phaseLeader;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

import client.ClientController;
import client.ClientUI;
import common.IcmUtils;
import entities.ChangeInitiator;
import entities.ChangeRequest;
import entities.Phase;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import server.ServerService;
import client.crDetails.CrDetails;


public class PhaseLeaderButtons implements ClientUI {

	@FXML
	private Button requestPhaseTimeButton2;
	@FXML
	private Label halpLabel;
	@FXML
	private Button moreInformation;
	
	private ChangeRequest currRequest;
	private static Phase currPhase;
	private ClientController clientController;
	private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
	private long  days;

	
	public void initialize() {
		halpLabel.setVisible(false);
		requestPhaseTimeButton2.setDisable(true);
		moreInformation.setVisible(false);
		
		 try {
	            clientController = ClientController.getInstance(this);
	        } catch (IOException e) {
	            e.printStackTrace();
	        }

	        List<ChangeRequest> params = new ArrayList<>();
	        currRequest= CrDetails.getCurrRequest();
	        params.add(currRequest);
	        System.out.println(params);

	        ServerService loadPhaseData = new ServerService(ServerService.DatabaseService.Get_Phase_Details, params);
	        System.out.println(loadPhaseData);
	        clientController.handleMessageFromClientUI(loadPhaseData);
	       
	}
 
    @FXML
    void showExtensionTimeDialog(ActionEvent event) {
    
    	try {
    		IcmUtils.popUpScene(this, "Extention Time Request", "/client/crDetails/phaseLeader/RequestExtensionTime/RequestExtensionTime.fxml",407 ,381 );
    			initialize();
    	} catch (IOException e) {
             e.printStackTrace(); }
    }
    	 
    
    @Override
    public void handleMessageFromClientController(ServerService serverService) {

    	switch (serverService.getDatabaseService()) {

        case Get_Phase_Details:
        	List<Phase> phaseList= serverService.getParams();
        	currPhase= phaseList.get(0);
        	
        	LocalDate currDate = LocalDate.now();     // Create a date object
        	LocalDate deadLine = currPhase.getDeadLine();
        	days = (ChronoUnit.DAYS.between(currDate, deadLine))+1;
            System.out.println("Days between: " + days);
        	
        if(!(days< 4 && days > 0)|| days>3) {
        	halpLabel.setText("Time Exception");
        	halpLabel.setVisible(true);
        	moreInformation.setVisible(true);       		
        	 //IcmUtils.getPopUp().close();
        }
       
        else if(currPhase.isExtensionRequest()== true) {
        	halpLabel.setText("Time extension already" + "\n" +"exists!");
        	halpLabel.setVisible(true);
        	moreInformation.setVisible(true);
        	 
        	   //IcmUtils.getPopUp().close();
        	   }
        else {
        	requestPhaseTimeButton2.setDisable(false);       	
        }
            break;
}
    }
    
    @FXML
    void moreInformationEvent(ActionEvent event) {
    	String label=halpLabel.getText();
    	
    	switch (label) {
		case "Time Exception":
			 IcmUtils.displayErrorMsg("ERROR","Current stage: " + currPhase.getName().toString() + "\n" +
       	    	"Deadline: " + currPhase.getDeadLine().format(formatter) + "\n" + "Time left: " + days +" days" + "\n","ERROR :This request can only be submitted if 3 or less days are left to complete this phase!" );
			break;

		case "Time extension already" + "\n" +"exists!":
			IcmUtils.displayErrorMsg("ERROR","ERROR","A time extension request for this phase has already been submitted." +"\n" + "extension request can only be submitted once!" );
			break;
		}

    }
    
    
public static void setPhase (Phase NewPhase) {
	currPhase=NewPhase;
}

public static Phase getPhase () {
	return currPhase;
}

}