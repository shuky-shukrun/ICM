package client.crDetails.itd;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import client.ClientController;
import client.ClientUI;
import client.crDetails.CrDetails;
import com.jfoenix.controls.JFXButton;
import common.IcmUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import server.ServerService;
import server.ServerService.DatabaseService;

public class ITDButtons implements ClientUI {

    @FXML
    private Button thawMessage;
    @FXML
    private Button thawButton;

    private ClientController clientController;
    public void initialize() {
    	try {
    		clientController=ClientController.getInstance(this);
    	}
    	catch(Exception e) {
    		e.printStackTrace();
    	}

    	if(CrDetails.getCurrRequest().isSuspended()) {
    		thawButton.setDisable(false);
			thawMessage.setVisible(true);
		}
    	else {
			thawButton.setDisable(true);
			thawMessage.setVisible(false);
		}

    }
    /**
     * Thaw change request
     * @param event-thaw button pressed
     */
    @FXML
    void thawChangeRequest(ActionEvent event) {
    	Optional<ButtonType>result=IcmUtils.displayConfirmationMsg("Thaw request confirmation", "Thaw request confirmation", "Are you sure you want to thaw this request?");
    	if(result.get()==ButtonType.OK) {
    	int id=CrDetails.getCurrRequest().getId();
    	List<Integer>l=new ArrayList<>();
    	l.add(id);
    	clientController.handleMessageFromClientUI(new ServerService(DatabaseService.Thaw_Request, l));
    	}
    }

    @Override
    public void handleMessageFromClientController(ServerService serverService) {
    	switch(serverService.getDatabaseService()) {
    		case Thaw_Request:
    			if((Boolean)serverService.getParams().get(0)==true) {
    				IcmUtils.displayInformationMsg(
    						"Request thawed",
							"Request thawed",
							"Request Thawed successfully.");
    				CrDetails.getCurrRequest().setSuspended(false);
    				thawButton.setDisable(true);
    			}
    			else
    				IcmUtils.displayErrorMsg(
    						"Error",
							"Thaw Request Failed",
							"Please contact ICM support team.");
    			break;
    	}
    }
    /**
     * Displays information why thaw button disabled
     */
	@FXML
	public void thawRequestInfoMsg() {
		String frozenRequest = "This request is suspended.\nTo thaw it, Click 'Thaw Change Request' button.";
		IcmUtils.displayInformationMsg(
				"Frozen request",
				"Frozen request",
				frozenRequest);
	}
}
