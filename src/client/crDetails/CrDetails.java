package client.crDetails;


import client.ClientController;
import client.ClientUI;
import client.crDetails.ccc.CCCButtons;
import com.jfoenix.controls.JFXTabPane;
import common.IcmUtils;
import entities.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.stage.DirectoryChooser;
import server.ServerService;
import server.ServerService.DatabaseService;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CrDetails implements ClientUI {

    @FXML
    private TextField changeRequestIDTextField;
    @FXML
    private TextField openingDateTextField;
    @FXML
    private TextField initiatorTextField;
    @FXML
    private TextField infoSystemTextField;
    @FXML
    private TextArea currentStateTextArea;
    @FXML
    private TextArea requestedChangeTextField;
    @FXML
    private TextArea reasonForChangeTextArea;
    @FXML
    private TextArea commentsTextArea;
    @FXML
    private TextField currentPhaseTextField;
    @FXML
    private TextField phaseDeadLineTextField;
    @FXML
    private TextField currPhaseStatus;
    @FXML
    private TextField phaseLeaderTextField;
    @FXML
    private Pane buttonsPane;
    @FXML
    private Label userNameLabel;
    @FXML
    private ProgressBar processBar;

    @FXML
    private Button downloadFilesButton;

    private static ChangeRequest currRequest;
    private ClientController clientController;

    public static ChangeRequest getCurrRequest() {
        return currRequest;
    }

    public static void setCurrRequest(ChangeRequest currRequest) {
        CrDetails.currRequest = currRequest;
    }

    public void initialize() {
        try {
            clientController = ClientController.getInstance(this);
        } catch (IOException e) {
            e.printStackTrace();
        }

        ChangeInitiator currUser = ClientController.getUser();
        userNameLabel.setText(currUser.getFirstName() + " " + currUser.getLastName());

        List<Integer> params = new ArrayList<>();
        params.add(ClientController.getUser().getId());
        params.add(currRequest.getId());


        ServerService loadRequestData = new ServerService(ServerService.DatabaseService.Get_Request_Details, params);

        clientController.handleMessageFromClientUI(loadRequestData);

    }

    @FXML
    public void downloadFiles(ActionEvent event) {
    	DirectoryChooser dirChooser = new DirectoryChooser();
    	File chosenDir = dirChooser.showDialog(client.ClientMain.getPrimaryStage());
    	if(chosenDir==null)
    		return;
    	List<Object>param=new ArrayList<Object>();
    	param.add(Integer.parseInt(changeRequestIDTextField.getText()));
    	param.add(chosenDir);
    	clientController.handleMessageFromClientUI(new ServerService(DatabaseService.download_files, param));
    }


    @FXML
    void backToHome() {
        try {
            IcmUtils.loadScene(this, IcmUtils.Scenes.Main_Window);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void handleMessageFromClientController(ServerService serverService) {
        switch (serverService.getDatabaseService()){
            case Get_Request_Details:
                System.out.println("adding details to screen");
                List<ChangeRequest> crList = serverService.getParams();
                setCurrRequest(crList.get(0));

                changeRequestIDTextField.setText(String.valueOf(currRequest.getId()));
                openingDateTextField.setText(currRequest.getDate().toString());
                initiatorTextField.setText(currRequest.getInitiator().getFirstName() + " " + currRequest.getInitiator().getLastName());
                infoSystemTextField.setText(currRequest.getInfoSystem().toString());
                currentPhaseTextField.setText(currRequest.getCurrPhaseName().toString());
                currentStateTextArea.textProperty().setValue(currRequest.getCurrState());
                reasonForChangeTextArea.textProperty().setValue(currRequest.getReasonForChange());
                requestedChangeTextField.textProperty().setValue(currRequest.getRequestedChange());
                commentsTextArea.textProperty().setValue(currRequest.getComment());
                currPhaseStatus.setText(currRequest.getPhases().get(0).getPhaseStatus().toString());

                IEPhasePosition phaseLeader = currRequest.getPhases().get(0).getIePhasePosition().get(IEPhasePosition.PhasePosition.PHASE_LEADER);
                String phaseLeaderFn = phaseLeader.getInformationEngineer().getFirstName();
                String phaseLeaderLn = phaseLeader.getInformationEngineer().getLastName();
                phaseLeaderTextField.setText(phaseLeaderFn + " " + phaseLeaderLn);

                LocalDate deadLine = currRequest.getPhases().get(0).getDeadLine();
                if(deadLine != null)
                    phaseDeadLineTextField.setText(deadLine.toString());

                try {
                    initButtons();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                switch (currRequest.getCurrPhaseName()) {
                    case SUBMITTED:
                        processBar.setProgress(0.0);
                        break;
                    case EVALUATION:
                        processBar.setProgress(0.2);
                        break;
                    case EXAMINATION:
                        processBar.setProgress(0.4);
                        break;
                    case EXECUTION:
                        processBar.setProgress(0.6);
                        break;
                    case VALIDATION:
                        processBar.setProgress(0.8);
                        break;
                    case CLOSING:
                        processBar.setProgress(1.0);
                        break;
                }
                break;
            case download_files:
            	switch((String)serverService.getParams().get(0)) {
            case "success":
            		IcmUtils.displayInformationMsg("Information message","check your chosen folder");
            		break;
            case "noFiles":
            	IcmUtils.displayInformationMsg("Information message","no files to download");
            	break;
            case "exception":	
            	IcmUtils.displayInformationMsg("Information message", "Error in process", ((Exception)serverService.getParams().get(1)).getMessage());
            	break;
            	}
        }
    }

    private void initButtons() throws Exception{
        Map<IEPhasePosition.PhasePosition, IEPhasePosition> iePhasePositionMap;
        iePhasePositionMap = currRequest.getPhases().get(0).getIePhasePosition();
        ChangeInitiator currUser = ClientController.getUser();
        Parent root = null;

        if(currRequest.isSuspended()) {
            IcmUtils.displayInformationMsg("Frozen Request", "Frozen Request", "This request is suspended");
            if(currUser.getPosition() != Position.ITD_MANAGER) {
                return;
            }
        }

        if (currUser.getTitle() != ChangeInitiator.Title.INFOENGINEER) {
            root = FXMLLoader.load(getClass().getResource("initiator/InitiatorButtons.fxml"));
            buttonsPane.getChildren().setAll(root);
            return;
        }

        switch (currUser.getPosition()) {
            case ITD_MANAGER:
                root = FXMLLoader.load(getClass().getResource("itd/ITDButtons.fxml"));
                break;
            case CCC:
                root = FXMLLoader.load(getClass().getResource("ccc/CCCButtons.fxml"));
                break;
            case CHAIRMAN:
                FXMLLoader loader = new FXMLLoader();
                root = loader.load(getClass().getResource("ccc/CCCButtons.fxml").openStream());
                CCCButtons chairman = loader.getController();
                chairman.enableChairmanButtons();
                break;
            case SUPERVISOR:
                root = FXMLLoader.load(getClass().getResource("supervisor/SupervisorButtons.fxml"));
                break;
            case REGULAR:
                root = FXMLLoader.load(getClass().getResource("initiator/InitiatorButtons.fxml"));
                break;
        }

        if (root != null) {
            buttonsPane.getChildren().setAll(root);
        }

        for (IEPhasePosition ie: iePhasePositionMap.values() ) {
            if (ie.getInformationEngineer().getId().equals(currUser.getId())) {
                switch (ie.getPhasePosition()) {
                    case EXECUTIVE_LEADER:
                        root = FXMLLoader.load(getClass().getResource("executiveLeader/ExecutiveLeaderButtons.fxml"));
                        break;
                    case EVALUATOR:
                        root = FXMLLoader.load(getClass().getResource("evaluator/EvaluatorButtons.fxml"));
                        break;
                    case TESTER:
                        root = FXMLLoader.load(getClass().getResource("tester/TesterButtons.fxml"));
                        break;
                    case PHASE_LEADER:
                        root = FXMLLoader.load(getClass().getResource("phaseLeader/PhaseLeaderButtons.fxml"));
                        break;

                }
            }
        }
        if (root != null)
            buttonsPane.getChildren().setAll(root);
    }

    @FXML
    void logout(ActionEvent event) throws IOException {
        ClientController.setUser(null);
        IcmUtils.loadScene(this, IcmUtils.Scenes.Login);
    }


}
