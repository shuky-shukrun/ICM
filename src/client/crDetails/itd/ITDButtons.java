package client.crDetails.itd;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import client.ClientController;
import client.ClientUI;
import client.crDetails.CrDetails;
import common.IcmUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import server.ServerService;
import server.ServerService.DatabaseService;

public class ITDButtons implements ClientUI {

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
    }
    @FXML
    void thawChangeRequest(ActionEvent event) {
    	Optional<ButtonType>result=IcmUtils.displayConfirmationMsg("are you sure you want to thaw this request?");
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
    			if((Boolean)serverService.getParams().get(0)==true)
    				IcmUtils.displayConfirmationMsg("Success", "Request Thawed");
    			else
    				IcmUtils.displayErrorMsg("Error", "Thaw Request Failed");
    	}
    }
    

}
