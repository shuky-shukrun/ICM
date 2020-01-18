package client.crDetails.ccc;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import client.ClientController;
import client.ClientUI;
import client.crDetails.CrDetails;
import common.IcmUtils;
import entities.EvaluationReport;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import server.ServerService;

public class ViewEvaluationReport implements ClientUI {
	
	@FXML
	private Button cancelButton;
	@FXML
	private Button createButton;
	@FXML
	private ChoiceBox<String> infoSystemChoiceBox;
	@FXML
	private TextArea risksAndConstraintsTextArea;
	@FXML
	private TextArea expectedResultTextArea;
	@FXML
	private TextArea requiredChangeTextArea;
	@FXML
	private TextField timeTextField;
	
	private ClientController clientController;	
    private CrDetails crDetails;
    private static EvaluationReport currReport;
	
    public static EvaluationReport getCurrReport() {
        return currReport;
    }

    public static void setCurrReport(EvaluationReport currReport) {
        ViewEvaluationReport.currReport = currReport;
    }
	
	/**
	 * Initialize the create evaluation report dialog
	 */
	public void initialize() {
        try {
            clientController = ClientController.getInstance(this);
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        List<Integer> params = new ArrayList<>();
        params.add(CrDetails.getCurrRequest().getId());
        //params.add(currReport.getReportId());
        ServerService loadRequestData = new ServerService(ServerService.DatabaseService.View_Evaluation_Report, params);

        clientController.handleMessageFromClientUI(loadRequestData);
        
       
    }
	
	/**
	 * @param serverService-ServerService object that the client controller send
	 * insert values into the fxml file of the viewEvaluationReport
	 */
	 public void handleMessageFromClientController(ServerService serverService) {
     	System.out.println("adding details to screen");

        List<EvaluationReport> reportList = serverService.getParams();
        
        setCurrReport(reportList.get(0));
        
        infoSystemChoiceBox.setItems(FXCollections.observableArrayList(currReport.getInfoSystem().toString()));
        infoSystemChoiceBox.getSelectionModel().select(0);
        requiredChangeTextArea.textProperty().setValue(currReport.getRequiredChange());
        requiredChangeTextArea.setEditable(false);
        expectedResultTextArea.textProperty().setValue(currReport.getExpectedResult());
        expectedResultTextArea.setEditable(false);
        risksAndConstraintsTextArea.textProperty().setValue(currReport.getRisksAndConstraints());
        risksAndConstraintsTextArea.setEditable(false);
        timeTextField.setText(currReport.getEvaluatedTime().toString());
        timeTextField.setEditable(false);



	 }
	 @FXML
		/**
		 * Back to change request summary dialog when cancel button pressed
		 * @param e-cancel button pressed event
		 */
	 public void backAction(ActionEvent e) {
		 
		 IcmUtils.getPopUp().close();
	 }
}
