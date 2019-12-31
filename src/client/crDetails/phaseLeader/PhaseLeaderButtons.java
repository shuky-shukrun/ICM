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
import entities.Phase.PhaseStatus;
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
	private Button moreInformation;
	
	private ChangeRequest currRequest;
	private static Phase currPhase;
	private ClientController clientController;
	private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
	private long  days;
	private String helpType;
	private int flag=0;

	
	public void initialize() {
		requestPhaseTimeButton2.setDisable(true);
		moreInformation.setVisible(false);
		
		 try {
	            clientController = ClientController.getInstance(this);
	        } catch (IOException e) {
	            e.printStackTrace();
	        }

	    if(flag==0) {
	        currPhase= CrDetails.getCurrRequest().getPhases().get(0);
	        System.out.println(currPhase.getName().toString() + currPhase.getDeadLine().format(formatter));
	        flag=1;
	    }
	             
	        LocalDate currDate = LocalDate.now();     // Create a date object
        	LocalDate deadLine = currPhase.getDeadLine();
        	days = (ChronoUnit.DAYS.between(currDate, deadLine))+1;
            System.out.println("Days between: " + days);
            
            if(CrDetails.getCurrRequest().isSuspended()!=true) {
         
        if(currPhase.isExtensionRequest()== true) {
        	helpType="Time extension already exists";
        	moreInformation.setVisible(true);
        }
        else  if (!(days< 4 && days > 0)|| days>3) { 
        	helpType="Time Exception";
        	moreInformation.setVisible(true); 
        	   }    
        else if(currPhase.getPhaseStatus()==PhaseStatus.EXTENSION_TIME_REQUESTED) {
        	helpType="time extension requested";
        	moreInformation.setVisible(true);
        }   
        else 
        	requestPhaseTimeButton2.setDisable(false);       	          
	}
            else {
            	helpType="isSuspended";
            	moreInformation.setVisible(true);
            }
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
    	
    }
    
    
    @FXML
    void moreInformationEvent(ActionEvent event) {
    	
    	switch (helpType) {
		case "Time Exception":
			 IcmUtils.displayInformationMsg("Information message","Phase Details-" + "\n" +"Change request ID: " + currPhase.getChangeRequestId()+ "\n" + "Current phase: " + currPhase.getName().toString() + "\n" +
       	    	"Deadline: " + currPhase.getDeadLine().format(formatter) + "\n" + "Time left: " + days +" days" + "\n","This request can only be submitted if 3 or less days are left to complete this phase!" );
			break;

		case "Time extension already exists":
			IcmUtils.displayInformationMsg("Information message","Phase Details-" + "\n" +"Change request ID: " + currPhase.getChangeRequestId()+ "\n"+ "Current phase: " + currPhase.getName().toString() 
	       	    	,"A time extension request for this phase has already been submitted and aproved." +"\n" + "Time extension request can only be submitted once!" );
			break;
		case "isSuspended":
			IcmUtils.displayInformationMsg("Information message","Phase Details-" + "\n" +"Change request ID: " + currPhase.getChangeRequestId()+ "\n" + "Current phase: " + currPhase.getName().toString()
					,"Change request " +currPhase.getChangeRequestId()+ " is frozen." +"\n\n" + "A time extension request can't be submited when the change request is frozen!" );
			break;
		case "time extension requested":
			IcmUtils.displayInformationMsg("Information message","Phase Details-" + "\n" +"Change request ID: " +currPhase.getChangeRequestId() + "\n" + "Current phase: " + currPhase.getName().toString() + "\n" +
	       	    	"Phase status: " + currPhase.getPhaseStatus(),"Time extension request for this phase has been forwarded to the supervisor's approval.");
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