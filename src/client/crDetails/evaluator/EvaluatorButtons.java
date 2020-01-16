package client.crDetails.evaluator;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import client.ClientController;
import client.ClientUI;
import client.crDetails.CrDetails;
import com.jfoenix.controls.JFXButton;
import common.IcmUtils;
import entities.Phase;
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
	private Button phaseTimeRequestInfo;
	@FXML
	private Button createReportInfo;
	@FXML
	private JFXButton returnRequestInfo;
	private String info;
	private static Phase currPhase;
	private int flag = 0;
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
		phaseTimeRequestInfo.setVisible(false);
		createReportInfo.setVisible(false);
		returnRequestInfo.setVisible(false);
		if (flag == 0) {
			currPhase = CrDetails.getCurrRequest().getPhases().get(0);
			flag = 1;
		}
		checkIsReturnRequest(CrDetails.getCurrRequest().getId());
	
		switch (currPhase.getPhaseStatus()) {
		// time request not submitted yet
			case SUBMITTED:
			case PHASE_LEADER_ASSIGNED:
				info = "time of phase yet not submitted";
				createEvaluationReportButton.setDisable(true);
				createReportInfo.setVisible(true);
				break;
			// time request submitted but not approved yet
			case TIME_REQUESTED:
				info = "time of phase yet not approved";
				requestPhaseTimeButton.setDisable(true);
				createEvaluationReportButton.setDisable(true);
				phaseTimeRequestInfo.setVisible(true);
				createReportInfo.setVisible(true);
				break;
			//time of phase approved or extension time requested or approved
			case IN_PROCESS:
			case EXTENSION_TIME_REQUESTED:
			case EXTENSION_TIME_APPROVED:
				info = "time of phase approved";
				requestPhaseTimeButton.setDisable(true);
				createEvaluationReportButton.setDisable(false);
				phaseTimeRequestInfo.setVisible(true);
				returnRequestInfo.setVisible(true);
				break;
			//time of phase declined
			case TIME_DECLINED:
				info = "time declined";
				createEvaluationReportButton.setDisable(true);
				requestPhaseTimeButton.setDisable(false);
				createReportInfo.setVisible(true);
				returnRequestInfo.setVisible(true);
				break;
			//phase done
			case DONE:
				info="have Report";
				requestPhaseTimeButton.setDisable(true);
				createEvaluationReportButton.setDisable(true);
				phaseTimeRequestInfo.setVisible(true);
				createReportInfo.setVisible(true);
				
		}
		List<Integer> l = new ArrayList<>();
		l.add(CrDetails.getCurrRequest().getId());
		clientController.handleMessageFromClientUI(new ServerService(DatabaseService.Is_Exists_Eva_Report, l));
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
					"/client/crDetails/evaluator/createEvaluationReport.fxml", 588, 788);
			initialize();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	/**
	 * function that check if specific request return from examination phase
	 *
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
			IcmUtils.popUpScene(this, "ICM request time dialog", "/client/crDetails/evaluator/TimeRequest.fxml", 588,
					688);
			initialize();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Show specific message when this button appeared on the screen
	 */
	@FXML
	public void importantInfoEvent() {
		switch (info) {
			case "time of phase approved":
				IcmUtils.displayInformationMsg("Information message", "Attention",
						"This request phase time approved.\nPlease create evaluation report.");
				break;
			case "time declined":
				IcmUtils.displayInformationMsg("Information message", "Attention",
						"The requested phase duration was rejected.\nPlease submit new phase duration.");
				break;
			case "return request":
				IcmUtils.displayInformationMsg("Information message", "Attention",
						"This request returns from examination for more details.\nPlease create new evaluation report.");
				break;
		}
	}

	@FXML
	/**
	 * Show specific message when this button appeared on the screen
	 *
	 * @param e
	 */
	public void moreInformation1Event(ActionEvent e) {
		switch (info) {
			case "time of phase yet not approved":
				IcmUtils.displayInformationMsg("Information message",
						"Phase Details-" + "\n" + "Change request ID: " + +CrDetails.getCurrRequest().getId() + "\n"
								+ "Current phase: " + CrDetails.getCurrRequest().getCurrPhaseName().toString(),
						"Change request " + CrDetails.getCurrRequest().getId() + " -time request not approved yet." + "\n\n"
								+ "request phase time can't be submitted when there is already phase time request!");
				break;
			case "time of phase approved":
				IcmUtils.displayInformationMsg("Information message",
						"Phase Details-" + "\n" + "Change request ID: " + +CrDetails.getCurrRequest().getId() + "\n"
								+ "Current phase: " + CrDetails.getCurrRequest().getCurrPhaseName().toString(),
						"Change request " + CrDetails.getCurrRequest().getId() + " -time request approved." + "\n\n"
								+ "request phase time can't be submitted when there is already phase time!");
				break;
			case "have Report":
				IcmUtils.displayInformationMsg("Information message", "there is already evaluation report");
				break;
		}

	}

	/**
	 * Show specific message when this button appeared on the screen
	 *
	 * @param e
	 */
	@FXML
	public void moreInformation2Event(ActionEvent e) {
		switch (info) {
			case "time of phase yet not submitted":
			case "return request":
				IcmUtils.displayInformationMsg("Information message",
						"Phase Details-" + "\n" + "Change request ID: " + +CrDetails.getCurrRequest().getId() + "\n"
								+ "Current phase: " + CrDetails.getCurrRequest().getCurrPhaseName().toString(),
						"Change request " + CrDetails.getCurrRequest().getId() + " -time request not submitted yet."
								+ "\n\n" + "evaluation report can't be submited when the phase time not yet submitted!");
				break;
			case "time of phase yet not approved":
			case "time declined":
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
	/**
	 * Sets the attribute of current phase with phase object
	 * @param NewPhase-the new phase object
	 */
	public static void setPhase1(Phase NewPhase) {
		currPhase = NewPhase;
	}
	/**
	 * gets the attribute of current phase
	 */

	public static Phase getPhase1() {
		return currPhase;
	}
	/**
	 * handle message that came from the client controller
	 */
	@Override
	public void handleMessageFromClientController(ServerService serverService) {
		switch (serverService.getDatabaseService()) {
			case Is_Exists_Eva_Report:
				if ((Boolean) serverService.getParams().get(0) == true) {
					info = "have Report";
					createEvaluationReportButton.setDisable(true);
					createReportInfo.setVisible(true);
					// moreInformation2.setDisable(false);
				}
					break;
		
			case Return_Request:
				if ((Boolean) serverService.getParams().get(0) == true) {
					switch (CrDetails.getCurrRequest().getPhases().get(0).getPhaseStatus()) {
						case PHASE_LEADER_ASSIGNED: case PHASE_EXEC_LEADER_ASSIGNED: case TIME_DECLINED:
							requestPhaseTimeButton.setDisable(false);
							createEvaluationReportButton.setDisable(true);
							createReportInfo.setVisible(true);
							createReportInfo.setDisable(false);
							break;
						case EXTENSION_TIME_APPROVED: case EXTENSION_TIME_REQUESTED: case IN_PROCESS:
							requestPhaseTimeButton.setDisable(true);
							createEvaluationReportButton.setDisable(false);
							phaseTimeRequestInfo.setVisible(true);
							phaseTimeRequestInfo.setDisable(false);
							break;
					}
					info = "return request";
					returnRequestInfo.setVisible(true);
					break;
				}
		}
	}


}
