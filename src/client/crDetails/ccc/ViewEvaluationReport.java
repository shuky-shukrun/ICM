package client.crDetails.ccc;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import client.ClientController;
import client.ClientUI;
import client.crDetails.CrDetails;
import entities.EvaluationReport;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.DatePicker;
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
	private DatePicker EvaluatedTimeDatePicker;
	@FXML
	private TextArea risksAndConstraintsTextArea;
	@FXML
	private TextArea expectedResultTextArea;
	@FXML
	private TextArea requiredChangeTextArea;
	
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
        
        createButton.setDisable(false);
    }
	
	 public void handleMessageFromClientController(ServerService serverService) {
     	System.out.println("adding details to screen");
         /*List<String> params = new ArrayList<>();
        //params.add(userName.getText());
        params.add(crDetails.getCurrRequest().getId().toString());
        ServerService evaluationService = new ServerService(ServerService.DatabaseService.View_Evaluation_Report, params);
        clientController.handleMessageFromClientUI(evaluationService);*/
        
        List<EvaluationReport> reportList = serverService.getParams();
        //ClientController.setEvaluationReport(reportList.get(0));
        
        
        setCurrReport(reportList.get(0));
        
        infoSystemChoiceBox.setValue(currReport.getInfoSystem().toString());
        requiredChangeTextArea.textProperty().setValue(currReport.getRequiredChange());
        expectedResultTextArea.textProperty().setValue(currReport.getExpectedResult());
        risksAndConstraintsTextArea.textProperty().setValue(currReport.getRisksAndConstraints());
        EvaluatedTimeDatePicker.setValue(currReport.getEvaluatedTime());

	 }
	 
	 public void cancleEvaluationReport(ActionEvent e) {
		 
	 }
	 
	 public static final LocalDate LOCAL_DATE (String dateString){
		    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
		    LocalDate localDate = LocalDate.parse(dateString, formatter);
		    return localDate;
		}

}
