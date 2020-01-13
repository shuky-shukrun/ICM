package client.crDetails.supervisor;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import client.ClientController;
import client.ClientUI;
import client.crDetails.CrDetails;
import client.crDetails.supervisor.AssignPhaseLeaders.AssignPhaseLeaders;
import common.IcmUtils;
import entities.ChangeInitiator;
import entities.ChangeRequest;
import entities.Phase;
import entities.Phase.PhaseName;
import entities.Phase.PhaseStatus;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import server.ServerService;
import server.ServerService.DatabaseService;

public class SupervisorButtons implements ClientUI {

    @FXML
    private Button phaseTimeDecisionButton;

    @FXML
    private Button assignPhaseLeadersButton;

    @FXML
    private Button assignPhaseWorkersButton;

    @FXML
    private Button freezeRequestButton;

    @FXML
	private Button closeChangeRequestButton;	
	
	@FXML
	private Button moreInformation2;
    @FXML
    private Button moreInformation3;

	private static Phase.PhaseStatus CurrStatus;
    private String info;
    private ClientController clientController;
    private static Phase currPhase;
    private boolean flag=false;
    
    public void initialize() {
    	try {
			clientController=ClientController.getInstance(this);
			moreInformation2.setVisible(false);	
			moreInformation3.setVisible(true);
			
			if(CrDetails.getCurrRequest().isSuspended())
			{
				info="frozen";
				moreInformation2.setVisible(true);
				closeChangeRequestButton.setDisable(true);
				phaseTimeDecisionButton.setDisable(true);
			}
			if(CrDetails.getCurrRequest().getPhases().get(0).getPhaseStatus().equals("DONE"))
			{
				info="finished";
				moreInformation2.setVisible(true);
				closeChangeRequestButton.setDisable(true);
			}
			
			assignPhaseLeadersButton.setDisable(true);
			if(flag==false) {
				System.out.printf("1");
				currPhase=CrDetails.getCurrRequest().getPhases().get(0);
				CurrStatus = CrDetails.getCurrRequest().getPhases().get(0).getPhaseStatus();
 			    flag=true; 
			}
    		if(currPhase.getName()==PhaseName.SUBMITTED) {
    		    assignPhaseLeadersButton.setDisable(false);
    		    moreInformation3.setVisible(false);
    		    System.out.printf("2");
    		}
    		if(!(CurrStatus.equals(Phase.PhaseStatus.TIME_REQUESTED)||CurrStatus.equals(Phase.PhaseStatus.EXTENSION_TIME_REQUESTED))){
    			phaseTimeDecisionButton.setDisable(true);
    		}
		} catch (IOException e) {
			e.printStackTrace();
		}
    }

    
    @FXML
    void closeChangeRequest(ActionEvent event) {
      List<String>params=new ArrayList<String>();
      params.add(CrDetails.getCurrRequest().getId().toString());
      params.add(CrDetails.getCurrRequest().getPhases().get(0).getPhaseStatus().toString());
      params.add(CrDetails.getCurrRequest().getInitiator().getFirstName()+" "+CrDetails.getCurrRequest().getInitiator().getLastName());
      params.add(CrDetails.getCurrRequest().getInitiator().getEmail());
      clientController.handleMessageFromClientUI(new ServerService(DatabaseService.Close_Request, params));
    }

    @FXML
    void freezeRequest(ActionEvent event) {
    	List<Integer>list=new ArrayList<Integer>();
    	list.add(CrDetails.getCurrRequest().getId());
    	clientController.handleMessageFromClientUI(new ServerService(DatabaseService.Freeze_Request, list));
    }

    @FXML
    void setTimeDecision(ActionEvent event) {

    		CurrStatus = CrDetails.getCurrRequest().getPhases().get(0).getPhaseStatus();
    		System.out.println(CrDetails.getCurrRequest().getPhases().get(0).getPhaseStatus());
    		System.out.println(CurrStatus);
    		switch (CurrStatus) {
    		case TIME_REQUESTED:
    			try {
    				IcmUtils.popUpScene(this, "Time Request Decision","/client/crDetails/supervisor/timeDecision/TimeRequestDecision.fxml", 420, 350);
    				initialize();
    			} catch (IOException e) {
    				e.printStackTrace();
    			}
    			break;

    		case EXTENSION_TIME_REQUESTED:
    			try {
    				IcmUtils.popUpScene(this, "Time Request Decision","/client/crDetails/supervisor/timeDecision/ExtensionTimeDecision.fxml", 420, 350);
    				initialize();
    			} catch (IOException e) {
    				e.printStackTrace();
    			}
    			break;

    		default:
    			IcmUtils.displayInformationMsg("There are no time requests.");

    		}
    	}

    @FXML
    void showAssignPhaseLeadersDialog(ActionEvent event) {
		try {
			IcmUtils.popUpScene(this, "Assign Phase Leaders", "/client/crDetails/supervisor/AssignPhaseLeaders/AssignPhaseLeaders.fxml",600 ,680 );
			initialize();
		} catch (IOException e) {
			e.printStackTrace(); }
    }

    @FXML
    public void moreInformation2Event() {
    	switch(info) {
    		case "frozen":
    			IcmUtils.displayInformationMsg("Information message",
    					"Phase Details-" + "\n" + "Change request ID: " + +CrDetails.getCurrRequest().getId() + "\n"
    							+ "Current phase: " + CrDetails.getCurrRequest().getCurrPhaseName().toString(),
    					"Change request " + CrDetails.getCurrRequest().getId() + " is frozen." + "\n\n"
    							+ "closing request can't be done when the change request is frozen!");
    			break;
    		case "finished":
    			IcmUtils.displayInformationMsg("Information message","This request closed");
    			break;
    	}
    }
    
    @FXML
    public void moreInformation3Event() {
	  System.out.printf("4");
	  IcmUtils.displayInformationMsg("Information message", "Phase leaders for this change request have already been assigned.");
  
  }

    @Override
    public void handleMessageFromClientController(ServerService serverService) {
    	switch(serverService.getDatabaseService()) {
    		case Freeze_Request:
    			if((Boolean)serverService.getParams().get(0)==true)
    				IcmUtils.displayConfirmationMsg("Success", "Freeze Request Successfully");
    			else
    				IcmUtils.displayErrorMsg("Error", "Freeze Request Failed");
    			break;
    		case Close_Request:
    			if((Boolean)serverService.getParams().get(0)==true)
    				IcmUtils.displayConfirmationMsg("Success", "Close Request Successfully");
    			else
    				IcmUtils.displayErrorMsg("Error", "Close Request Failed");   		
    	}
    }
    
    
    public static void setCurrPhase (Phase phase) {
    	SupervisorButtons.currPhase=phase;
    }
    
    public static Phase getPhase() {
    	return currPhase;
    }
    
    public static void setCurrPhaseStatus (PhaseStatus status) {
    	SupervisorButtons.CurrStatus = status;
    }
    
    public static PhaseStatus getPhaseStatus() {
    	return CurrStatus;
    }
}
