package client.crDetails.executiveLeader;

import client.ClientController;
import client.ClientUI;
import client.crDetails.CrDetails;
import common.IcmUtils;
import entities.ChangeRequest;
import entities.Phase;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import server.ServerService;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ExecutiveLeaderButtons implements ClientUI {

	@FXML
	private Button requestPhaseTimeButton1;
	@FXML
	private Button confirmExecutionButton;
	@FXML
	private Button moreInformationButton;
	@FXML
	private Button moreInformationButton2;

	private ClientController clientController;
	private String info;

	public void initialize() {

		try {
			clientController = ClientController.getInstance(this);
			System.out.println(CrDetails.getCurrRequest().getPhases().get(0).getPhaseStatus());
		} catch (IOException e) {
			e.printStackTrace();
		}

		ChangeRequest currRequest = CrDetails.getCurrRequest();

		moreInformationButton2.setVisible(false);

		Phase.PhaseStatus currPhaseStatus = currRequest.getPhases().get(0).getPhaseStatus();
		switch (currPhaseStatus) {
		case PHASE_LEADER_ASSIGNED:
		case PHASE_EXEC_LEADER_ASSIGNED:
			info = "time of phase yet not approved";
			confirmExecutionButton.setDisable(true);
			moreInformationButton.setDisable(true);
			moreInformationButton2.setVisible(true);
			break;

		case TIME_REQUESTED:
			info = "Phase time has been submitted";
			confirmExecutionButton.setDisable(true);
			requestPhaseTimeButton1.setDisable(true);
			moreInformationButton2.setVisible(true);
			break;

		case IN_PROCESS:
		case EXTENSION_TIME_REQUESTED:
		case EXTENSION_TIME_APPROVED:
			confirmExecutionButton.setVisible(true);
			requestPhaseTimeButton1.setDisable(true);
			moreInformationButton.setVisible(true);
			break;

		default:
			IcmUtils.displayErrorMsg(
					"Request status can't be " + currPhaseStatus + ". Please contact system administrator.");
			try {
				IcmUtils.loadScene(this, IcmUtils.Scenes.Main_Window_New);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		// if (CrDetails.getCurrRequest().getPhases().get(0)
		// .getPhaseStatus() == entities.Phase.PhaseStatus.SUBMITTED) {
		// info = "time of phase yet not approved";
		// confirmExecutionButton.setDisable(true);
		// moreInformationButton.setDisable(true);
		// moreInformationButton2.setVisible(true);
		// }
		// if (CrDetails.getCurrRequest().getPhases().get(0)
		// .getPhaseStatus() == entities.Phase.PhaseStatus.TIME_REQUESTED) {
		// info = "Phase time has been submitted";
		// confirmExecutionButton.setDisable(true);
		// requestPhaseTimeButton1.setDisable(true);
		// moreInformationButton2.setVisible(true);

		// }

		// if (CrDetails.getCurrRequest().getPhases().get(0)
		// .getPhaseStatus() == entities.Phase.PhaseStatus.TIME_APPROVED) {
		// confirmExecutionButton.setVisible(true);
		// requestPhaseTimeButton1.setDisable(true);
		// moreInformationButton.setVisible(true);

		// }
	}

	@FXML
	void showRequestTimeDialog(ActionEvent event) throws IOException {

		IcmUtils.popUpScene(this, "Request Phase Time", "/client/crDetails/evaluator/TimeRequest.fxml", 400, 300);

	}

	@FXML
	void confirmExecution(ActionEvent event) {
		Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
		confirmAlert.setTitle("Execution Confirmation");
		confirmAlert.setHeaderText("Execution Confirmation");
		confirmAlert.setContentText("Press 'Confirm' to confirm that all required changes are done."
				+ "The request will then move to Validation phase.");

		ButtonType confirmButton = new ButtonType("Confirm", ButtonBar.ButtonData.OK_DONE);
		confirmAlert.getButtonTypes().setAll(confirmButton, ButtonType.CANCEL);

		Optional<ButtonType> result = confirmAlert.showAndWait();

		if (result.isPresent() && result.get() == confirmButton) {
			List<ChangeRequest> requestList = new ArrayList<>();
			requestList.add(CrDetails.getCurrRequest());
			ServerService serverService = new ServerService(ServerService.DatabaseService.Execution_Confirmation,
					requestList);

			clientController.handleMessageFromClientUI(serverService);
			confirmExecutionButton.setDisable(true);
		}
	}

	@FXML
	public void moreInformationEvent(ActionEvent e) {
		 entities.Phase.PhaseStatus temp=CrDetails.getCurrRequest().getPhases().get(0).getPhaseStatus();
		 switch(temp) {
		 case IN_PROCESS:
		 
				IcmUtils.displayInformationMsg("Information message",
						"Phase Details-" + "\n" + "Change request ID: " + +CrDetails.getCurrRequest().getId() + "\n"
								+ "Current phase: " + CrDetails.getCurrRequest().getCurrPhaseName().toString(),
						"Change request " + CrDetails.getCurrRequest().getId()
								+ " -time request approved allready." );
			
		 break;
		 case TIME_REQUESTED:
			 
				IcmUtils.displayInformationMsg("Information message",
						"Phase Details-" + "\n" + "Change request ID: " + +CrDetails.getCurrRequest().getId() + "\n"
								+ "Current phase: " + CrDetails.getCurrRequest().getCurrPhaseName().toString(),
						"Change request " + CrDetails.getCurrRequest().getId()
								+ " -time request is waiting for supervisor's approval." );
				break;
		 }

	}

	@FXML
	public void moreInformationEvent1(ActionEvent e) {

		IcmUtils.displayInformationMsg("Information message",
				"Phase Details-" + "\n" + "Change request ID: " + +CrDetails.getCurrRequest().getId() + "\n"
						+ "Current phase: " + CrDetails.getCurrRequest().getCurrPhaseName().toString(),
				"Change request " + CrDetails.getCurrRequest().getId() + " -time request not approved yet." + "\n\n"
						+ "Execution can't be confirmed when the phase time not yet approved!");

	}

	@Override
    public void handleMessageFromClientController(ServerService serverService) {
        switch (serverService.getDatabaseService()) {
            case Execution_Confirmation:

                break;
            case Error:
                List<Exception> errorList = serverService.getParams();
                IcmUtils.displayErrorMsg(errorList.get(0).getMessage());
                break;
        }
    }
}
