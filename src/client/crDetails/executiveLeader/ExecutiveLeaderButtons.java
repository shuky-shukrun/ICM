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
	private Button requestPhaseTimeButton;
	@FXML
	private Button confirmExecutionButton;
	@FXML
	private Button requestPhaseTimeInfoButton;
	@FXML
	private Button confirmExecutionInfoButton;

	private ClientController clientController;
	private String info;
	private static Phase currPhase;
	private int flag = 0;

	/**
	 * initialize the executive leader buttons
	 */
    public void initialize() {

		try {
			clientController = ClientController.getInstance(this);
			System.out.println(CrDetails.getCurrRequest().getPhases().get(0).getPhaseStatus());
			if (flag == 0) {
				currPhase = CrDetails.getCurrRequest().getPhases().get(0);
				flag = 1;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		ChangeRequest currRequest = CrDetails.getCurrRequest();

		confirmExecutionInfoButton.setVisible(false);

		
        switch (currPhase.getPhaseStatus()) {
			case PHASE_LEADER_ASSIGNED: case PHASE_EXEC_LEADER_ASSIGNED:
				info = "time of phase yet not approved";
				confirmExecutionButton.setDisable(true);
				requestPhaseTimeInfoButton.setDisable(true);
				confirmExecutionInfoButton.setVisible(true);
				break;

			case TIME_REQUESTED:
				info = "Phase time has been submitted";
				confirmExecutionButton.setDisable(true);
				requestPhaseTimeButton.setDisable(true);
				requestPhaseTimeInfoButton.setDisable(false);
				confirmExecutionInfoButton.setVisible(true);
				confirmExecutionInfoButton.setDisable(false);
				break;

			case IN_PROCESS: case EXTENSION_TIME_REQUESTED: case EXTENSION_TIME_APPROVED:
				confirmExecutionButton.setVisible(true);
				requestPhaseTimeButton.setDisable(true);
				requestPhaseTimeInfoButton.setVisible(true);
				break;
				
            default:
                IcmUtils.displayErrorMsg(
                		"Request status error",
						"Request status error",
						"Request status can't be " +
                        currPhase.getPhaseStatus() + ".\n" +
								"Please contact ICM support team.");
                try {
                    IcmUtils.loadScene(this, IcmUtils.Scenes.Main_Window);
                } catch (IOException e) {
                    e.printStackTrace();
                }
		}

	}

	/**
	 * shows "Request Phase Time" dialog.
	 * @throws IOException if have problem to load the the scene from fxml file.
	 */
	@FXML
	void showRequestTimeDialog() throws IOException {

		IcmUtils.popUpScene(this, "Request Phase Time", "/client/crDetails/evaluator/TimeRequest.fxml", 588, 688);
		initialize();
	}

	/**
	 * shows "Execution Confirmation" dialog.
	 * if confirm - change the request's phase to Validation.
	 */
	@FXML
	void confirmExecution() {
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

	/**
	 * shows info popup message: why the requestPhaseTimeButton is disable.
	 */
	@FXML
	public void requestPhaseTimeInfoMsg() {
		 entities.Phase.PhaseStatus phaseStatus = CrDetails.getCurrRequest().getPhases().get(0).getPhaseStatus();
		 switch(phaseStatus) {
		 case IN_PROCESS:
			 IcmUtils.displayInformationMsg(
					 "Request phase time Help",
					 "Phase time already approved",
					 "Phase time for this phase already approved.\n" +
							 "Contact the phase leader if you want to request extension time.");
		 break;
		 case TIME_REQUESTED:
			 IcmUtils.displayInformationMsg(
					 "Request phase time Help",
					 "Phase time already submitted",
					 "Phase time for this phase already submitted.\n" +
							 "Please wait for supervisor approval.");
				break;
		 }

	}

	/**
	 * shows info popup message: why the confirmExecutionButton is disable.
	 */
	@FXML
	public void confirmExecutionInfoMsg() {
		entities.Phase.PhaseStatus phaseStatus = CrDetails.getCurrRequest().getPhases().get(0).getPhaseStatus();
		switch (phaseStatus) {
			case PHASE_LEADER_ASSIGNED:
			case TIME_DECLINED:
				IcmUtils.displayInformationMsg(
						"Create evaluation report Help",
						"Phase time not submitted",
						"You need to submit phase time and get supervisor approval " +
								"before you create evaluation report.");
				break;
			case TIME_REQUESTED:
			case EXTENSION_TIME_REQUESTED:
			case EXTENSION_TIME_APPROVED:
				IcmUtils.displayInformationMsg(
						"Create evaluation report Help",
						"Phase time is waiting for approval",
						"You need to get supervisor approval for the requested phase time" +
								"before you create evaluation report.");
				break;
		}

	}

	/**
	 * @return the current request's phase
	 */
	public static Phase getPhase1() {
		return currPhase;
	}


	/**
	 * handle the returned value from server.
	 * shows an error popup message in case of server error
	 *
	 * @param serverService contains the exception object from server
	 */
	@Override
    public void handleMessageFromClientController(ServerService serverService) {
        switch (serverService.getDatabaseService()) {
            case Error:
                List<Exception> errorList = serverService.getParams();
                IcmUtils.displayErrorMsg(
                		"Server error",
						"Server error",
						"Can not confirm execution.\n" +
								"Server returned error: " +	errorList.get(0).getMessage());
                break;
        }
    }
}
