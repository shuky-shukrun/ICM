package client.crDetails.tester.setDecision;

import java.util.ArrayList;
import java.util.List;

import client.ClientController;
import client.ClientUI;
import common.IcmUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextArea;
import server.ServerService;
import server.ServerService.DatabaseService;

public class SetDecision implements ClientUI {

    @FXML
    private ChoiceBox<String> decisionChoiceBox;

    @FXML
    private TextArea descriptionTextArea;

    @FXML
    private Button okButton;

    @FXML
    private Button cancelButton;
    
    
    private ClientController clientController;
	
	public void initialize() {
		 decisionChoiceBox.getItems().addAll("Approve The Change","Report test failure");
	}

    @FXML
    void submitDecision(ActionEvent event) {
    	String decision = decisionChoiceBox.getValue();
    	boolean flag = true;
    	
    	if(decision.equals("Report test failure")&& descriptionTextArea.getText().trim().contentEquals("")) {
    		IcmUtils.displayErrorMsg("Please enter a description to your choice");
    		flag = false;
    	}
    		
    	if(flag == true) {
    		List<String> list = new ArrayList<String>();
    		list.add(decision);
    		list.add(descriptionTextArea.getText());
    		ServerService serverService = new ServerService(DatabaseService.Tester_Decision, list);
    		clientController.handleMessageFromClientUI(serverService);
    	}
    		
    	System.out.println(decision);
    }
    
    @Override
    public void handleMessageFromClientController(ServerService serverService) {
    }
}
