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

    ClientController clientController;

    public void initialize() {
        ChangeRequest currRequest = CrDetails.getCurrRequest();

        Phase.PhaseStatus currPhaseStatus = currRequest.getPhases().get(0).getPhaseStatus();
        switch (currPhaseStatus) {
            case PHASE_EXEC_LEADER_ASSIGNED:
                confirmExecutionButton.setDisable(true);
                break;
            case IN_PROCESS:
                requestPhaseTimeButton.setDisable(true);
                break;
            default:
                IcmUtils.displayErrorMsg("Request status can't be " +
                        currPhaseStatus + ". Please contact system administrator.");
                try {
                    IcmUtils.loadScene(this, IcmUtils.Scenes.Main_Window_New);
                } catch (IOException e) {
                    e.printStackTrace();
                }
        }
    }

        @FXML
    void confirmExecution(ActionEvent event) {
        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Execution Confirmation");
        confirmAlert.setHeaderText("Execution Confirmation");
        confirmAlert.setContentText("Press 'Confirm' to confirm that all required changes are done." +
                "The request will then move to Validation phase.");

        ButtonType confirmButton = new ButtonType("Confirm", ButtonBar.ButtonData.OK_DONE);
        confirmAlert.getButtonTypes().setAll(confirmButton, ButtonType.CANCEL);

        Optional<ButtonType> result = confirmAlert.showAndWait();

        if(result.isPresent() && result.get() == confirmButton) {
            List<ChangeRequest> requestList = new ArrayList<>();
            requestList.add(CrDetails.getCurrRequest());
            ServerService serverService = new ServerService(ServerService.DatabaseService.Execution_Confirmation, requestList);

            try {
                clientController = ClientController.getInstance(this);
                clientController.handleMessageFromClientUI(serverService);
                confirmExecutionButton.setDisable(true);
            } catch (IOException e) {
                List<Exception> errorMessageList = new ArrayList<>();
                errorMessageList.add(e);
                ServerService error = new ServerService(ServerService.DatabaseService.Error, errorMessageList);
                handleMessageFromClientController(error);
                e.printStackTrace();
            }
        }
    }

    @FXML
    void showRequestTimeDialog(ActionEvent event) {

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
