package client.crDetails.executiveLeader;

import java.io.IOException;

import client.ClientController;
import client.ClientUI;
import client.crDetails.CrDetails;
import common.IcmUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import server.ServerService;

public class ExecutiveLeaderButtons implements ClientUI {

	@FXML
	private Button requestPhaseTimeButton1;

	@FXML
	private Button confirmExecutionButton;
	@FXML
	private Button moreInformationButton;

	private ClientController clientController;
	private String info;

	@FXML
	void confirmExecution(ActionEvent event) {

	}

	@FXML
	void showRequestTimeDialog(ActionEvent event) throws IOException {
		
   	 IcmUtils.popUpScene(this, "Request Phase Time","/client/crDetails/evaluator/TimeRequest.fxml", 400, 300);

	}

	

	@Override
	public void handleMessageFromClientController(ServerService serverService) {

	}

	public void initialize() {
		try {
			clientController = ClientController.getInstance(this);
			System.out.println(CrDetails.getCurrRequest().getPhases().get(0).getPhaseStatus());
		} catch (IOException e) {
			e.printStackTrace();
		}
		moreInformationButton.setVisible(false);
		if (CrDetails.getCurrRequest().getPhases().get(0)
				.getPhaseStatus() != entities.Phase.PhaseStatus.TIME_APPROVED) {
			info = "time of phase yet not approved";
			confirmExecutionButton.setDisable(true);
			moreInformationButton.setVisible(true);
		}
	}

	@FXML
	public void moreInformationEvent(ActionEvent e) {

		IcmUtils.displayInformationMsg("Information message",
				"Phase Details-" + "\n" + "Change request ID: " + +CrDetails.getCurrRequest().getId() + "\n"
						+ "Current phase: " + CrDetails.getCurrRequest().getCurrPhaseName().toString(),
				"Change request " + CrDetails.getCurrRequest().getId() + " -time request not approved yet." + "\n\n"
						+ "Execution can't be confirmed when the phase time not yet approved!");

	}
}
