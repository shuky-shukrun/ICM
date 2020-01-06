package client.crDetails.evaluator;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import client.ClientController;
import client.ClientUI;
import client.crDetails.CrDetails;
import common.IcmUtils;
import common.IcmUtils.Scenes;
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
	private boolean flag;
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
		if (CrDetails.getCurrRequest().isSuspended()) {
			info = "request is frozen";
			requestPhaseTimeButton.setDisable(true);
			moreInformation1.setVisible(true);
			createEvaluationReportButton.setDisable(true);
			moreInformation1.setVisible(true);
		} else if (CrDetails.getCurrRequest().getPhases().get(0)
				.getPhaseStatus() != entities.Phase.PhaseStatus.TIME_APPROVED) {
			info = "time of phase yet not approved";
			createEvaluationReportButton.setDisable(true);
			moreInformation2.setVisible(true);
		} else if (CrDetails.getCurrRequest().getPhases().get(0)
				.getPhaseStatus() == entities.Phase.PhaseStatus.TIME_APPROVED) {
			info = "time of phase approved";
			requestPhaseTimeButton.setDisable(true);
			moreInformation1.setVisible(true);

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
			IcmUtils.loadScene(this, IcmUtils.Scenes.Create_Evaluation_Report);
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
	@FXML
	public void importantInfoEvent() {
		if(info.equals("return request")) {
			IcmUtils.displayInformationMsg("information message","pay attention!!this request returns from examination for more details");
		}
	}
	public void checkIsReturnRequest(int id) {
		List<Integer>l=new ArrayList<Integer>();
		l.add(id);
		clientController.handleMessageFromClientUI(new ServerService(DatabaseService.Return_Request,l));
	}
	@FXML
	/**
	 * load the request time dialog when the appropriate button pressed
	 * 
	 * @param event-"showRequestTime" button pressed event
	 */
	public void showRequestTimeDialog(ActionEvent event) {

		try {
			IcmUtils.loadScene(this, IcmUtils.Scenes.show_Request_Time_Dialog);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@FXML
	public void moreInformation1Event(ActionEvent e) {
		switch (info) {
		case "request is frozen":
			IcmUtils.displayInformationMsg("Information message",
					"Phase Details-" + "\n" + "Change request ID: " + +CrDetails.getCurrRequest().getId() + "\n"
							+ "Current phase: " + CrDetails.getCurrRequest().getCurrPhaseName().toString(),
					"Change request " + CrDetails.getCurrRequest().getId() + " is frozen." + "\n\n"
							+ "A time  request can't be submited when the change request is frozen!");
			break;
		}

	}

	@FXML
	public void moreInformation2Event(ActionEvent e) {
		switch (info) {
		case "request is frozen":
			IcmUtils.displayInformationMsg("Information message",
					"Phase Details-" + "\n" + "Change request ID: " + +CrDetails.getCurrRequest().getId() + "\n"
							+ "Current phase: " + CrDetails.getCurrRequest().getCurrPhaseName().toString(),
					"Change request " + CrDetails.getCurrRequest().getId() + " is frozen." + "\n\n"
							+ "evaluation report can't be submited when the change request is frozen!");
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
		switch(serverService.getDatabaseService()) {
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
			System.out.println("was here!!!");
			if ((Boolean) serverService.getParams().get(0) == true) {
				info="return request";
				importantInfo.setVisible(true);
				break;
			}
	}
	}
}
