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
import entities.Phase;
import entities.Phase.PhaseStatus;
import javafx.beans.binding.BooleanBinding;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;

import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.DateCell;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextArea;

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

	private Phase newCurrPhase;
	private Phase oldCurrPhase;
	public static int flagHelp;
  

	/**
	 * Initialize the create evaluation report dialog
	 */
	public void initialize() {
		
		newCurrPhase=EvaluatorButtons.getPhase1();
		oldCurrPhase=EvaluatorButtons.getPhase1();
		try {
			clientController = ClientController.getInstance(this);
		} catch (IOException e) {
			e.printStackTrace();
		}
		EvaluatedTimeDatePicker.setDayCellFactory(picker -> new DateCell() {
			public void updateItem(LocalDate date, boolean empty) {
				super.updateItem(date, empty);
				LocalDate deadLine = LocalDate.now().minusDays(1);

				setDisable(empty || date.compareTo(deadLine) <= 0);
			}
		});
		// initialize the combo box of info systems
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
				return (requiredChangeTextArea.getText().isEmpty() || requiredChangeTextArea.getText().trim().equals("")
						|| expectedResultTextArea.getText().isEmpty()
						|| expectedResultTextArea.getText().trim().equals("")
						|| risksAndConstraintsTextArea.getText().isEmpty()
						|| risksAndConstraintsTextArea.getText().trim().equals("")
						|| EvaluatedTimeDatePicker.getValue() == null || EvaluatedTimeDatePicker.valueProperty().get()
								.compareTo(CrDetails.getCurrRequest().getDate()) < 0);
			}
		};

		createButton.disableProperty().bind(bb);
	

	}

	@FXML
	/**
	 * Creates evaluation report if possible when create button pressed
	 * @param e-create button pressed event
	 */
	public void createEvaluationReport(ActionEvent e) {
		String temp = "";
		newCurrPhase.setPhaseStatus(PhaseStatus.DONE);
		EvaluatorButtons.setPhase1(newCurrPhase);
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
	 * @param e-cancel button pressed event
	 */
	public void cancelEvaluationReport(ActionEvent e) {
		IcmUtils.getPopUp().close();
	}

	/**
	 * Displays information about why create button is disabled
	 * 
	 * @param e
	 */


	@Override
	/**
	 * Show pop-up with the information if the create evaluation report succeed
	 * 
	 * @param serverService-ServerService object that the client controller send
	 */
	public void handleMessageFromClientController(ServerService serverService) {
		List<Boolean> list = serverService.getParams();
		if (list.get(0) == true && list.get(1) == true) {
			IcmUtils.displayInformationMsg("creating evaluation report success");

		} else
			IcmUtils.displayErrorMsg("creating evaluation report failed!!");
		
		List<Phase> phList = new ArrayList<>();
		phList.add(oldCurrPhase);
		ServerService updateExceptionTime = new ServerService(ServerService.DatabaseService.Update_Exception_Time, phList);
		clientController.handleMessageFromClientUI(updateExceptionTime);	
		IcmUtils.getPopUp().close();

	}

}
