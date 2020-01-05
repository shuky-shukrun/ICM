package client.crDetails.tester.setDecision;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import client.ClientController;
import client.ClientUI;
import client.crDetails.CrDetails;
import common.IcmUtils;
import entities.Phase;
import entities.Phase.PhaseName;
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
	private String currPhase = new String();

	public void initialize() {
		currPhase = CrDetails.getCurrRequest().getCurrPhaseName().toString();
		okButton.setDisable(true);

		try {
			clientController = ClientController.getInstance(this);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		switch (currPhase) {
		case "VALIDATION":
			decisionChoiceBox.getItems().addAll("Approve The Change", "Report Test Failure");
			break;

		case "EXAMINATION":
			decisionChoiceBox.getItems().addAll("Commite The Change", "Ask For Additional Data", "Decline The Change");
			break;
		}
	}

	@FXML
	public void keyReleaseProperty() {// go back to here?????????????????????????????
		String decision = decisionChoiceBox.getValue();
		boolean isDisabled = true;
		if (decision.equals("Report Test Failure") || decision.equals("Ask For Additional Data")
				|| decision.equals("Decline The Change")) {
			if (!descriptionTextArea.getText().isEmpty() && !descriptionTextArea.getText().trim().isEmpty())
				isDisabled = false;
		} else if (decision.equals("Approve The Change") || decision.equals("Commite The Change"))
			isDisabled = false;

		// boolean isDisabled =(decision.equals("Report test
		// failure")||decision.equals("Ask For Additional
		// Data")||decision.equals("Decline The Change"))&&
		// (descriptionTextArea.getText().isEmpty()&&descriptionTextArea.getText().trim().isEmpty());
		okButton.setDisable(isDisabled);
	}

	@FXML
	void submitDecision(ActionEvent event) {
		String decision = decisionChoiceBox.getValue();

		List<String> list = new ArrayList<String>();
		list.add(decision);
		list.add(descriptionTextArea.getText());
		
		String crId = new String();
		crId = CrDetails.getCurrRequest().getId().toString();
		list.add(crId);
		list.add(currPhase);

		ServerService serverService = new ServerService(DatabaseService.Set_Decision, list);
		clientController.handleMessageFromClientUI(serverService);

		System.out.println(decision);
	}
	
	@FXML
	void cancelSetDecision(ActionEvent event) {
		IcmUtils.getPopUp().close();
	}

	@Override
	public void handleMessageFromClientController(ServerService serverService) {
		List<Boolean> list = serverService.getParams();
		if (list.get(0) == true && list.get(2) == true) {
			IcmUtils.displayConfirmationMsg("update the tester decision- success");
		} else {
			IcmUtils.displayErrorMsg("update the tester decision- failed");
		}

	}
}
