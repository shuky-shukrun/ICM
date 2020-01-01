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
	private TextField EvaluatedTimeDatePicker;
	@FXML
	private Button cancelButton;
	@FXML
	private Button createButton;

	public void initialize() {
		try {
			clientController = ClientController.getInstance(this);
		} catch (IOException e) {
			e.printStackTrace();
		}
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

	}

	@FXML
	public void cancelEvaluationReport(ActionEvent e) {
		try {
			IcmUtils.loadScene(this, IcmUtils.Scenes.Change_Request_Summary);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}

	@FXML
	public void createEvaluationReport(ActionEvent e) {
		boolean flag = true;
		String temp = "";
		// checks if one or more fields are empty
		System.out.println(requiredChangeTextArea.getText().trim());
		if (requiredChangeTextArea.getText().trim().contentEquals("")) {
			flag = false;
		}
		if (expectedResultTextArea.getText().trim().contentEquals("")) {
			flag = false;
		}
		if (risksAndConstraintsTextArea.getText().trim().contentEquals("")) {
			flag = false;
		}
		if (EvaluatedTimeDatePicker.getText().trim().contentEquals("")) {
			flag = false;
		}
		// if all the fields are full then save to db the report
		// all the fields are string
		if (flag == true && isNumeric(EvaluatedTimeDatePicker.getText().trim())) {
			List<String> l = new ArrayList<String>();
			temp += "" + CrDetails.getCurrRequest().getId();
			l.add(temp);
			l.add(CrDetails.getCurrRequest().getInfoSystem().toString());
			l.add(requiredChangeTextArea.getText());
			l.add(expectedResultTextArea.getText());
			l.add(risksAndConstraintsTextArea.getText());
			LocalDate d1 = CrDetails.getCurrRequest().getDate();
			d1 = d1.plusDays(Integer.parseInt(EvaluatedTimeDatePicker.getText()));
			l.add(d1.toString());
			ServerService serverService = new ServerService(DatabaseService.Create_Evaluation_Report, l);
			clientController.handleMessageFromClientUI(serverService);
		} else
			IcmUtils.displayErrorMsg("one or more fields are empty");
	}

	// gets string and check if it is number
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
	public void handleMessageFromClientController(ServerService serverService) {
		List<Boolean> list = serverService.getParams();
		if (list.get(0) == true && list.get(1) == true)
			IcmUtils.displayConfirmationMsg("creating evaluation report success");
		else
			IcmUtils.displayErrorMsg("creating evaluation report failed!!");

	}
}
