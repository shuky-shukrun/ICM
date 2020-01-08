package client.crDetails.ccc;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import client.ClientController;
import client.ClientUI;
import client.crDetails.CrDetails;
import common.IcmUtils;
import entities.EvaluationReport;
import javafx.beans.value.WritableValue;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextArea;
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
	private TextArea timeTextArea;
	
	private ClientController clientController;	
    private CrDetails crDetails;
    private static EvaluationReport currReport;
	
    public static EvaluationReport getCurrReport() {
        return currReport;
    }

    public static void setCurrReport(EvaluationReport currReport) {
        ViewEvaluationReport.currReport = currReport;
    }
	
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
	
	 public void handleMessageFromClientController(ServerService serverService) {
     	System.out.println("adding details to screen");

        List<EvaluationReport> reportList = serverService.getParams();
        
        setCurrReport(reportList.get(0));
        
        infoSystemChoiceBox.setItems(FXCollections.observableArrayList(currReport.getInfoSystem().toString()));
        infoSystemChoiceBox.getSelectionModel().select(0);
        // TODO: infoSystemChoiceBox set Editable(false)
        requiredChangeTextArea.textProperty().setValue(currReport.getRequiredChange());
        requiredChangeTextArea.setEditable(false);
        expectedResultTextArea.textProperty().setValue(currReport.getExpectedResult());
        expectedResultTextArea.setEditable(false);
        risksAndConstraintsTextArea.textProperty().setValue(currReport.getRisksAndConstraints());
        risksAndConstraintsTextArea.setEditable(false);
        timeTextArea.textProperty().setValue(currReport.getEvaluatedTime().toString());
        timeTextArea.setEditable(false);
        


	 }
	 
	 public void backAction(ActionEvent e) {
		 
		 IcmUtils.getPopUp().close();
	 }
}
