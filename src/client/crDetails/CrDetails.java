package client.crDetails;


import client.ClientController;
import client.ClientUI;
import client.crDetails.ccc.CCCButtons;
import com.jfoenix.controls.JFXTabPane;
import common.IcmUtils;
import entities.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import server.ServerService;
import server.ServerService.DatabaseService;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
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
    private ListView<String> filesListView;
    @FXML
    private Button attachFilesButton;
    @FXML
    private Button downloadFilesButton;

    private static ChangeRequest currRequest;
    private ClientController clientController;
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/YYYY");

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
    /**
     * Download files of specific request
     * @param event-the event that "download files" pressed
     */

    @FXML
    public void downloadFiles(ActionEvent event) {
    	//select specific directory to save the files
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
                if(currRequest.getDate() != null)
                    openingDateTextField.setText(currRequest.getDate().format(formatter));
                initiatorTextField.setText(currRequest.getInitiator().getFirstName() + " " + currRequest.getInitiator().getLastName());
                infoSystemTextField.setText(currRequest.getInfoSystem().toString());
                currentPhaseTextField.setText(currRequest.getCurrPhaseName().toString());
                currentStateTextArea.textProperty().setValue(currRequest.getCurrState());
                reasonForChangeTextArea.textProperty().setValue(currRequest.getReasonForChange());
                requestedChangeTextField.textProperty().setValue(currRequest.getRequestedChange());
                commentsTextArea.textProperty().setValue(currRequest.getComment());
                currPhaseStatus.setText(currRequest.getPhases().get(0).getPhaseStatus().toString());

                if(currRequest.getCurrPhaseName() != Phase.PhaseName.SUBMITTED &&
                        currRequest.getCurrPhaseName() != Phase.PhaseName.CLOSING) {
                    IEPhasePosition phaseLeader = currRequest.getPhases().get(0).getIePhasePosition().get(IEPhasePosition.PhasePosition.PHASE_LEADER);
                    String phaseLeaderFn = phaseLeader.getInformationEngineer().getFirstName();
                    String phaseLeaderLn = phaseLeader.getInformationEngineer().getLastName();
                    phaseLeaderTextField.setText(phaseLeaderFn + " " + phaseLeaderLn);
                }

                LocalDate deadLine = currRequest.getPhases().get(0).getDeadLine();
                if(deadLine != null){
                    phaseDeadLineTextField.setText(deadLine.format(formatter));
                    if(deadLine.isBefore(LocalDate.now())) {
                        phaseDeadLineTextField.setStyle("-fx-text-inner-color: red; -fx-background-radius: 10");
                    }
                }

                ObservableList<String> filesList = FXCollections.observableArrayList();

                if(currRequest.getFilesNames().isEmpty()) {
                    downloadFilesButton.setDisable(true);
                }
                else {
                    filesList.setAll(currRequest.getFilesNames());
                    filesListView.setItems(filesList);
                }

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

            case Attach_Files:
                boolean flag = (Boolean) serverService.getParams().get(0);
                if (flag == true)
                    IcmUtils.displayInformationMsg("Attach Files - Success", "Success", "Files attached successfully");
                else
                    IcmUtils.displayErrorMsg("Attach Files - Error", "Error", "Attach files failed");
                break;
            case Error:
                SQLException sqlException = ((SQLException) serverService.getParams().get(0));
                IcmUtils.displayErrorMsg("Server Error", "Server Error", sqlException.getMessage());
                break;
               //display specific message about download files
            case download_files:
            	switch((String)serverService.getParams().get(0)) {
            case "success":
            		IcmUtils.displayInformationMsg("Download Finished","Download Finished", "Check your chosen folder.");
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
                attachFilesButton.setDisable(true);
                return;
            }
        }

        if (!currRequest.getInitiator().getId().equals(currUser.getId())) {
            attachFilesButton.setDisable(true);
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
                if(currRequest.getInitiator().equals(currUser) &&
                        currRequest.getCurrPhaseName() != Phase.PhaseName.EXAMINATION)
                    root = FXMLLoader.load(getClass().getResource("initiator/InitiatorButtons.fxml"));
                else
                    root = FXMLLoader.load(getClass().getResource("ccc/CCCButtons.fxml"));
                break;
            case CHAIRMAN:
                if(currRequest.getInitiator().equals(currUser) &&
                        currRequest.getCurrPhaseName() != Phase.PhaseName.EXAMINATION &&
                        currRequest.getCurrPhaseName() != Phase.PhaseName.VALIDATION)
                    root = FXMLLoader.load(getClass().getResource("initiator/InitiatorButtons.fxml"));
                else {
                    FXMLLoader loader = new FXMLLoader();
                    root = loader.load(getClass().getResource("ccc/CCCButtons.fxml"));
                }
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

    /**
     * Attach files for specific request
     * @param event-attach files button pressed
     */
    @FXML
    public void attachFiles(ActionEvent event) {
        //select files to attach
        List<Object> tempL = new ArrayList<>();
        FileChooser fileCh = new FileChooser();
        List<File> filesToAttach = fileCh.showOpenMultipleDialog(client.ClientMain.getPrimaryStage());
        //checks if there are no files to attach
        if (filesToAttach == null)
            return;

        File[] arr = new File[filesToAttach.size()];
        int i = 0;
        for (File f : filesToAttach) {
            arr[i] = f;
            i++;
        }
        tempL.add(CrDetails.getCurrRequest().getId());
        tempL.add(arr);
        clientController.handleMessageFromClientUI(new ServerService(DatabaseService.Attach_Files, tempL));
       
    }

    @FXML
    private void refresh() throws IOException {
        IcmUtils.loadScene(this, IcmUtils.Scenes.Change_Request_Summary);
    }

    @FXML
    void logout(ActionEvent event) throws IOException {
        ClientController.setUser(null);
        IcmUtils.loadScene(this, IcmUtils.Scenes.Login);
    }


}
