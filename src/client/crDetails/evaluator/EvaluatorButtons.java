package client.crDetails.evaluator;

import java.io.IOException;

import client.ClientController;
import client.ClientUI;
import client.crDetails.CrDetails;
import common.IcmUtils;
import common.IcmUtils.Scenes;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import server.ServerService;

public class EvaluatorButtons implements ClientUI {

	@FXML
	private Button requestPhaseTimeButton;

	@FXML
	private Button createEvaluationReportButton;

	private ClientController clientController;

	/**
	 * Initialize the evaluator change request summary dialog
	 */
	public void initialize() {
		try {
			clientController = ClientController.getInstance(this);
			System.out.println(CrDetails.getCurrRequest().getPhases().get(0).getPhaseStatus());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@FXML
	/**
	 * load the create evaluation report dialog when the appropriate button pressed
	 * @param event-"show create evaluation report" button pressed event
	 */
	public void showCreateEvaluationReportDialog(ActionEvent event) {
		//check if the request is suspended
		if (CrDetails.getCurrRequest().isSuspended())
			IcmUtils.displayErrorMsg("request is frozen");
		else if (CrDetails.getCurrRequest().getPhases().get(0)
				.getPhaseStatus() == entities.Phase.PhaseStatus.TIME_APPROVED) {
			try {
				IcmUtils.loadScene(this, IcmUtils.Scenes.Create_Evaluation_Report);
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			IcmUtils.displayErrorMsg("time of phase yet not approved");
		}
	}

	@FXML
	/**
	 * load the request time dialog when the appropriate button pressed
	 * @param event-"showRequestTime" button pressed event
	 */
	public void showRequestTimeDialog(ActionEvent event) {
		if (CrDetails.getCurrRequest().isSuspended())
			IcmUtils.displayErrorMsg("request is frozen");
		else {
			try {
				IcmUtils.loadScene(this, IcmUtils.Scenes.show_Request_Time_Dialog);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void handleMessageFromClientController(ServerService serverService) {

	}
}
