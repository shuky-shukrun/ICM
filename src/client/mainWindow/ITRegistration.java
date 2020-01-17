package client.mainWindow;

import java.io.IOException;
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

public class ITRegistration implements ClientUI {
	@FXML
	private Button submitButton;
	@FXML
	private Button cancelButton;
	@FXML
	private ChoiceBox<ChangeInitiator> employeeChoiceBox;

	private ClientController clientController;
	private CrDetails crDetails;
	private DBConnection dbConnection;
	ChangeInitiator selectedEmployee;
	ChangeInitiator oldSelection = new ChangeInitiator();
	ObservableList<ChangeInitiator> employeeList = FXCollections.observableArrayList();
	
	public void initialize() {
		
		try {
			clientController = ClientController.getInstance(this);

		} catch (IOException e) {
			e.printStackTrace();
		}
		
		List<ChangeInitiator> employeeList = new ArrayList<>();
		ServerService getEmployee = new ServerService(ServerService.DatabaseService.Get_Employee, employeeList);
		clientController.handleMessageFromClientUI(getEmployee);
		BooleanBinding bb = Bindings.createBooleanBinding(() -> {
			ChangeInitiator employee = employeeChoiceBox.getSelectionModel().getSelectedItem();

			// disable, if one selection is missing or from is not smaller than to
			return (employee == null);
		}, employeeChoiceBox.valueProperty());
		submitButton.disableProperty().bind(bb);
	}
	
	public void handleMessageFromClientController(ServerService serverService) {

		List<ChangeInitiator> params = serverService.getParams();
		employeeList.setAll(params);
		employeeChoiceBox.setItems(employeeList);
	}
	
	public void submitAction(ActionEvent e) {
		
		selectedEmployee = employeeChoiceBox.getSelectionModel().getSelectedItem();
		ChangeInitiator selected = new ChangeInitiator();
		selected= (selectedEmployee);
		List<Object> Selection = new ArrayList<>();
		Selection.add(selected);
		Selection.add(selected.getId());
		System.out.println(selected.getId());
		ServerService serverService = new ServerService(ServerService.DatabaseService.Register_IT,Selection);
		clientController.handleMessageFromClientUI(serverService);
		IcmUtils.displayInformationMsg("IT registration", "IT registration success", selected.getFirstName() + " " + selected.getLastName() + " " +
				"was registered to IT department successfully.");
		IcmUtils.getPopUp().close();
	}
	
	public void cancelAction(ActionEvent e) {
		IcmUtils.getPopUp().close();

	}

}
