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
	private int flagHelp;

	/**
	 * Initialize the create evaluation report dialog
	 */
	public void initialize() {
		info = "empty fields";
		try {
			clientController = ClientController.getInstance(this);
		} catch (IOException e) {
			e.printStackTrace();
		}
		// initialize the combobox of info systems
		List<String> list = new ArrayList<String>();
		list.add(CrDetails.getCurrRequest().getInfoSystem().toString());
		ObservableList<String> obList = FXCollections.observableList(list);
		infoSystemChoiceBox.getItems().clear();
		infoSystemChoiceBox.setItems(obList);
		infoSystemChoiceBox.setValue(CrDetails.getCurrRequest().getInfoSystem().toString());
		// disable Create button any field is invalid
		BooleanBinding bb = new BooleanBinding() {
			{
				super.bind(requiredChangeTextArea.textProperty(), expectedResultTextArea.textProperty(),
						risksAndConstraintsTextArea.textProperty(), EvaluatedTimeDatePicker.valueProperty());
			}

			@Override
			// disable, if one selection is missing or evaluated time is later than the
			// deadline of the phase
			protected boolean computeValue() {
				if( EvaluatedTimeDatePicker.valueProperty().get() == null)
						info="empty fields";
			
				else if(EvaluatedTimeDatePicker.valueProperty().get().compareTo(CrDetails.getCurrRequest().getPhases().get(0).getDeadLine()) >= 0)
						info="later date";
				else if(EvaluatedTimeDatePicker.valueProperty().get().compareTo(CrDetails.getCurrRequest().getDate())<0)
					info="earlier date";
				return (requiredChangeTextArea.getText().isEmpty() || expectedResultTextArea.getText().isEmpty()
						|| risksAndConstraintsTextArea.getText().isEmpty() || EvaluatedTimeDatePicker.getValue() == null
						|| EvaluatedTimeDatePicker.getValue()
								.compareTo(CrDetails.getCurrRequest().getPhases().get(0).getDeadLine()) >= 0||EvaluatedTimeDatePicker.valueProperty().get().compareTo(CrDetails.getCurrRequest().getDate())<0);
			}
		};

		createButton.disableProperty().bind(bb);
		moreInformation.visibleProperty().bind(bb);
		

	}

	@FXML
	/**
	 * Creates evaluation report if possible when create button pressed
	 * 
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
		l.add(EvaluatedTimeDatePicker.getValue().toString());
		ServerService serverService = new ServerService(DatabaseService.Create_Evaluation_Report, l);
		clientController.handleMessageFromClientUI(serverService);

	}

	@FXML
	/**
	 * Back to change request summary dialog when cancel button pressed
	 * 
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
	public void moreInformationEvent(ActionEvent e) {
		
		switch (info) {
		case "empty fields":
			IcmUtils.displayInformationMsg("Information message",
					"one or more empty fields");
			break;
		case "later date":
			IcmUtils.displayInformationMsg("Information message","you entered later date than deadline");
			break;
		case "earlier date":
			IcmUtils.displayInformationMsg("Information message","you entered earlier date than the date when the request submitted");
			break;
		}
	}

	@Override
	/**
	 * Show pop-up with the information if the create evaluation report succeed
	 * 
	 * @param serverService-ServerService object that the client controller send
	 */
	public void handleMessageFromClientController(ServerService serverService) {
		List<Boolean> list = serverService.getParams();
		if (list.get(0) == true && list.get(1) == true)
			IcmUtils.displayConfirmationMsg("creating evaluation report success");
		else
			IcmUtils.displayErrorMsg("creating evaluation report failed!!");

	}

}
