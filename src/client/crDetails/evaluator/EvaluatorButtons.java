package client.crDetails.evaluator;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import client.ClientController;
import client.ClientUI;
import client.crDetails.CrDetails;
import common.IcmUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import server.ServerService;
import server.ServerService.DatabaseService;

public class EvaluatorButtons implements ClientUI {

	@FXML
	private Button requestPhaseTimeButton;

	@FXML
	private Button createEvaluationReportButton;
	@FXML
	private Button moreInformation1;
	@FXML
	private Button moreInformation2;
	@FXML
	private Button importantInfo;
	private String info;

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
		moreInformation1.setVisible(false);
		moreInformation2.setVisible(false);
		importantInfo.setVisible(false);
		checkIsReturnRequest(CrDetails.getCurrRequest().getId());
		// time request not submitted yet
		if (CrDetails.getCurrRequest().getPhases().get(0).getPhaseStatus()
				.compareTo(entities.Phase.PhaseStatus.TIME_REQUESTED) < 0) {
			info = "time of phase yet not submitted";
			createEvaluationReportButton.setDisable(true);
			moreInformation2.setVisible(true);
		}
		// time request submitted but not approved yet
		else if (CrDetails.getCurrRequest().getPhases().get(0)
				.getPhaseStatus() == entities.Phase.PhaseStatus.TIME_REQUESTED) {
			info = "time of phase yet not approved";
			requestPhaseTimeButton.setDisable(true);
			createEvaluationReportButton.setDisable(true);
			moreInformation1.setVisible(true);
			moreInformation2.setVisible(true);
		} else if (CrDetails.getCurrRequest().getPhases().get(0)
				.getPhaseStatus() == entities.Phase.PhaseStatus.EXTENSION_TIME_APPROVED) {
			info = "time of phase approved";
			requestPhaseTimeButton.setDisable(true);
			IcmUtils.displayInformationMsg("time of phase approved,please create report");

		} else {
			List<Integer> l = new ArrayList<>();
			l.add(CrDetails.getCurrRequest().getId());
			clientController.handleMessageFromClientUI(new ServerService(DatabaseService.Is_Exists_Eva_Report, l));
		}
	}

	@FXML
	/**
	 * load the create evaluation report dialog when the appropriate button pressed
	 * 
	 * @param event-"show create evaluation report" button pressed event
	 */
	public void showCreateEvaluationReportDialog(ActionEvent event) {

		try {
			IcmUtils.popUpScene(this, "ICM Create Evaluation Report",
					"/client/crDetails/evaluator/createEvaluationReport.fxml", 600, 632);
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
	/**
	 * Show specific message when this button appeared on the screen
	 */
	@FXML
	public void importantInfoEvent() {
		if (info.equals("return request")) {
			IcmUtils.displayInformationMsg("information message",
					"pay attention!!this request returns from examination for more details");
		}
	}
	/**
	 * function that check if specific request return from examination phase
	 * @param id-the id of the wanted request
	 */
	public void checkIsReturnRequest(int id) {
		List<Integer> l = new ArrayList<Integer>();
		l.add(id);
		clientController.handleMessageFromClientUI(new ServerService(DatabaseService.Return_Request, l));
	}

	@FXML
	/**
	 * load the request time dialog when the appropriate button pressed
	 * 
	 * @param event-"showRequestTime" button pressed event
	 */
	public void showRequestTimeDialog(ActionEvent event) {

		try {
			IcmUtils.popUpScene(this, "ICM request time dialog", "/client/crDetails/evaluator/TimeRequest.fxml", 600,
					632);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@FXML
	/**
	 * Show specific message when this button appeared on the screen
	 * @param e
	 */
	public void moreInformation1Event(ActionEvent e) {
		switch (info) {
		case "time of phase yet not approved":
			IcmUtils.displayInformationMsg("Information message",
					"Phase Details-" + "\n" + "Change request ID: " + +CrDetails.getCurrRequest().getId() + "\n"
							+ "Current phase: " + CrDetails.getCurrRequest().getCurrPhaseName().toString(),
					"Change request " + CrDetails.getCurrRequest().getId() + " -time request not approved yet." + "\n\n"
							+ "evaluation report can't be submited when the phase time not yet approved!");
			break;
		}

	}
	/**
	 * Show specific message when this button appeared on the screen
	 * @param e
	 */
	@FXML
	public void moreInformation2Event(ActionEvent e) {
		switch (info) {
		case "time of phase yet not submitted":
			IcmUtils.displayInformationMsg("Information message",
					"Phase Details-" + "\n" + "Change request ID: " + +CrDetails.getCurrRequest().getId() + "\n"
							+ "Current phase: " + CrDetails.getCurrRequest().getCurrPhaseName().toString(),
					"Change request " + CrDetails.getCurrRequest().getId() + " -time request not submitted yet."
							+ "\n\n" + "evaluation report can't be submited when the phase time not yet submitted!");
			break;
		case "time of phase yet not approved":
			IcmUtils.displayInformationMsg("Information message",
					"Phase Details-" + "\n" + "Change request ID: " + +CrDetails.getCurrRequest().getId() + "\n"
							+ "Current phase: " + CrDetails.getCurrRequest().getCurrPhaseName().toString(),
					"Change request " + CrDetails.getCurrRequest().getId() + " -time request not approved yet." + "\n\n"
							+ "evaluation report can't be submited when the phase time not yet approved!");
			break;
		case "have Report":
			IcmUtils.displayInformationMsg("Information message", "there is already evaluation report");
			break;
		case "time of phase approved":
			IcmUtils.displayInformationMsg("Information message", "there is already deadline");
			break;
		}

	}

	@Override
	public void handleMessageFromClientController(ServerService serverService) {
		switch (serverService.getDatabaseService()) {
		case Is_Exists_Eva_Report:
			if ((Boolean) serverService.getParams().get(0) == true) {
				info = "have Report";
				createEvaluationReportButton.setDisable(true);
				moreInformation2.setVisible(true);
				// moreInformation2.setDisable(false);
			} else
				moreInformation2.setVisible(false);
			break;

		case Return_Request:
			if ((Boolean) serverService.getParams().get(0) == true) {
				info = "return request";
				importantInfo.setVisible(true);
				break;
			}
		}
	}
}
