package client.crDetails.evaluator;

import javafx.event.ActionEvent;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import client.ClientController;
import client.ClientUI;
import client.crDetails.CrDetails;
import common.IcmUtils;
import javafx.beans.binding.BooleanBinding;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;

import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import server.ServerService;
import server.ServerService.DatabaseService;

public class CreateEvaluationReport implements ClientUI {
	private ClientController clientController;
	@FXML
	private ChoiceBox<String> infoSystemChoiceBox;
	@FXML
	private TextArea requiredChangeTextArea;
	@FXML
	private TextArea expectedResultTextArea;
	@FXML
	private TextArea risksAndConstraintsTextArea;
	@FXML
	private DatePicker EvaluatedTimeDatePicker;
	@FXML
	private Button cancelButton;
	@FXML
	private Button createButton;
	@FXML
	private Button moreInformation;
	
	private String info;
	/**
	 * Initialize the create evaluation report dialog
	 */
	public void initialize() {
		try {
			clientController = ClientController.getInstance(this);
		} catch (IOException e) {
			e.printStackTrace();
		}
		//initialize the combobox of info systems
		List<String> list = new ArrayList<String>();
		list.add("MOODLE");
		list.add("LIBRARY");
		list.add("STUDENT_INFO_CENTER");
		list.add("LECTURER_INFO_CENTER");
		list.add("EMPLOYEE_INFO_CENTER");
		list.add("CLASS_COMPUTER");
		list.add("LAB_COMPUTER");
		list.add("COLLEGE_SITE");
		ObservableList<String> obList = FXCollections.observableList(list);
		infoSystemChoiceBox.getItems().clear();
		infoSystemChoiceBox.setItems(obList);
		infoSystemChoiceBox.setValue(CrDetails.getCurrRequest().getInfoSystem().toString());
		infoSystemChoiceBox.setDisable(true);
		   // disable Create button any field is invalid
		BooleanBinding bb = new BooleanBinding() {
		    {
		        super.bind(requiredChangeTextArea.textProperty(),
		                expectedResultTextArea.textProperty(),
		                risksAndConstraintsTextArea.textProperty(),
		                EvaluatedTimeDatePicker.valueProperty());
		    }

		    @Override
		    // disable, if one selection is missing or evaluated time is later than the deadline of the phase
		    protected boolean computeValue() {
		        return (requiredChangeTextArea.getText().isEmpty()
		                ||  expectedResultTextArea.getText().isEmpty()
		                ||risksAndConstraintsTextArea.getText().isEmpty()||EvaluatedTimeDatePicker.getValue()==null
		               || EvaluatedTimeDatePicker.getValue().compareTo(CrDetails.getCurrRequest().getPhases().get(0).getDeadLine())>=0);
		    }
		};
		
		createButton.disableProperty().bind(bb);
		//System.out.println(EvaluatedTimeDatePicker.getValue().toString());
		boolean flag=bb.get();
		if(flag&&EvaluatedTimeDatePicker.getValue()!=null&&EvaluatedTimeDatePicker.getValue().compareTo(CrDetails.getCurrRequest().getPhases().get(0).getDeadLine())>0)
			info="date entered is later than deadline";
		else if(flag)
			info="empty fields";
		else if(!flag)
			moreInformation.setDisable(true);
	
		

	}

	@FXML
	/**
	 * Back to change request summary dialog when cancel button pressed
	 * @param e-cancel button pressed event
	 */
	public void cancelEvaluationReport(ActionEvent e) {
		try {
			IcmUtils.loadScene(this, IcmUtils.Scenes.Change_Request_Summary);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}

	@FXML
	/**
	 * Creates evaluation report if possible when create button pressed
	 * @param e-create button pressed event
	 */
	public void createEvaluationReport(ActionEvent e) {
		boolean flag = true;
		String temp = "";
		
			List<Object> l = new ArrayList<Object>();
			temp += "" + CrDetails.getCurrRequest().getId();
			l.add(temp);
			l.add(CrDetails.getCurrRequest().getInfoSystem().toString());
			l.add(requiredChangeTextArea.getText());
			l.add(expectedResultTextArea.getText());
			l.add(risksAndConstraintsTextArea.getText());
			l.add(EvaluatedTimeDatePicker.getValue());
			ServerService serverService = new ServerService(DatabaseService.Create_Evaluation_Report, l);
			clientController.handleMessageFromClientUI(serverService);
		
	}

	/**
	 *   check if a given string is number
	 * @param strNum
	 * @return true-string is number,false-else
	 */
	private boolean isNumeric(String strNum) {
		if (strNum == null) {
			return false;
		}
		try {
			int d = Integer.parseInt(strNum);
		} catch (NumberFormatException nfe) {
			return false;
		}
		return true;
	}

	@Override
	/**
	 * Show pop-up with the information if the create evaluation report succeed
	 * @param serverService-ServerService object that the client controller send
	 */
	public void handleMessageFromClientController(ServerService serverService) {
		List<Boolean> list = serverService.getParams();
		if (list.get(0) == true && list.get(1) == true)
			IcmUtils.displayConfirmationMsg("creating evaluation report success");
		else
			IcmUtils.displayErrorMsg("creating evaluation report failed!!");

	}
	@FXML
	public void moreInformationEvent(ActionEvent e) {
		switch(info) {
		case "empty fields":
			IcmUtils.displayInformationMsg("Information message", "one or more empty fields");
			break;
		case "date entered is later than deadline":
			IcmUtils.displayInformationMsg("Information message", "the date you picked is later than the phase deadline");
			break;
		}
	}
}
