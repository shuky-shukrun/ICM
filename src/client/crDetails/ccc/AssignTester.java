package client.crDetails.ccc;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import client.ClientController;
import client.ClientUI;
import client.crDetails.CrDetails;
import common.IcmUtils;
import entities.ChangeInitiator;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import server.DBConnection;
import server.ServerService;

public class AssignTester implements ClientUI {

	@FXML
	private Button okButton;
	@FXML
	private Button cancelButton;
	@FXML
	private ChoiceBox<ChangeInitiator> testerChoiceBox;

	private ClientController clientController;
	private CrDetails crDetails;
	private DBConnection dbConnection;
	ObservableList<ChangeInitiator> cccList = FXCollections.observableArrayList();
	ChangeInitiator selectedTester;
	ChangeInitiator oldSelection = new ChangeInitiator();

	public void initialize() {
		// okButton.setDisable(true);
		try {
			clientController = ClientController.getInstance(this);

		} catch (IOException e) {
			e.printStackTrace();
		}
		List<ChangeInitiator> cccList = new ArrayList<>();
		ServerService getCCC = new ServerService(ServerService.DatabaseService.Assign_Tester, cccList);
		clientController.handleMessageFromClientUI(getCCC);
		BooleanBinding bb = Bindings.createBooleanBinding(() -> {
			ChangeInitiator tester = testerChoiceBox.getSelectionModel().getSelectedItem();

			// disable, if one selection is missing or from is not smaller than to
			return (tester == null);
		}, testerChoiceBox.valueProperty());
		okButton.disableProperty().bind(bb);

	}


	public void handleMessageFromClientController(ServerService serverService) {

		List<ChangeInitiator> params = serverService.getParams();
		cccList.setAll(params);
		testerChoiceBox.setItems(cccList);


	}

	public void submitTester(ActionEvent e) {
		selectedTester = testerChoiceBox.getSelectionModel().getSelectedItem();
		ChangeInitiator selected = new ChangeInitiator();
		selected= (selectedTester);

		List<Object> oldAndNewSelection = new ArrayList<>();
		oldAndNewSelection.add(oldSelection);
		oldAndNewSelection.add(selected);
		oldAndNewSelection.add((crDetails.getCurrRequest().getId()));
		System.out.println("3");
		ServerService serverService = new ServerService(ServerService.DatabaseService.Replace_Tester,oldAndNewSelection);
		clientController.handleMessageFromClientUI(serverService);
		IcmUtils.displayInformationMsg("Updated!");
		IcmUtils.getPopUp().close();
	}

	public void cancelAction(ActionEvent e) {
		IcmUtils.getPopUp().close();

	}

}