package client.crDetails.initiator;

import client.ClientUI;
import client.crDetails.CrDetails;
import common.IcmUtils;
import entities.Phase;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import server.ServerService;

public class InitiatorButtons implements ClientUI {

	@FXML
	private Button closedRequestInfoButton;

	/**
	 * Initialize the Initiator change Request summary dialog
	 */
	public void initialize() {
		if (! (CrDetails.getCurrRequest().getPhases().get(0).getName() == Phase.PhaseName.CLOSING
				&& CrDetails.getCurrRequest().getPhases().get(0).getPhaseStatus() == Phase.PhaseStatus.DONE)) {
			closedRequestInfoButton.setVisible(false);
		}
	}

	@FXML
	/**
	 * Show specific message when request is closed
	 */
	public void closedRequestInfoMsg() {
		IcmUtils.displayInformationMsg(
				"Closed Request",
				"Closed Request",
				"This request is closed. " +
				"Check your email for more details.");
	}

	@Override
	public void handleMessageFromClientController(ServerService serverService) {
	}
}
