package client.mainWindow.itdAssignPermissions;

import client.ClientController;
import client.ClientUI;
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
import server.ServerService;

import java.io.IOException;
import java.util.*;

public class ITDAssignPermissions implements ClientUI {

    @FXML
    private ChoiceBox<ChangeInitiator> supervisorChoiceBox;

    @FXML
    private ChoiceBox<ChangeInitiator> cccMember1ChoiceBox;

    @FXML
    private ChoiceBox<ChangeInitiator> cccMember2ChoiceBox;

    @FXML
    private ChoiceBox<ChangeInitiator> cccChairmanChoiceBox;

    @FXML
    private Button updatePermissionsButton;

    @FXML
    private Button cancelButton;

    // class variables
    ClientController clientController;
    ObservableList<ChangeInitiator> infoEngineersList = FXCollections.observableArrayList();
    ObservableList<ChangeInitiator> supervisorList = FXCollections.observableArrayList();
    ObservableList<ChangeInitiator> ccc1List = FXCollections.observableArrayList();
    ObservableList<ChangeInitiator> ccc2List = FXCollections.observableArrayList();
    ObservableList<ChangeInitiator> cccChairmanList = FXCollections.observableArrayList();
    List<ChangeInitiator> oldSelection = new ArrayList<>();
    ChangeInitiator selectedSupervisor;
    ChangeInitiator selectedCcc1;
    ChangeInitiator selectedCcc2;
    ChangeInitiator selectedChairman;

    public void initialize() {
        // init client controller
        try {
            clientController = ClientController.getInstance(this);
        } catch (IOException e) {
            e.printStackTrace();
            String msg = e.getMessage() + "\nTry to logout and login again.";
            IcmUtils.displayErrorMsg("Loading Error", "Error!", msg);
        }

        // get info engineers from database
        List<ChangeInitiator> infoEngineersList = new ArrayList<>();
        ServerService getInfoEngineers = new ServerService(ServerService.DatabaseService.Get_Info_Engineers, infoEngineersList);
        clientController.handleMessageFromClientUI(getInfoEngineers);

        // init choice validation (allow each name to be chosen only in one position)
        addChangeListener(supervisorChoiceBox, cccMember1ChoiceBox, cccMember2ChoiceBox, cccChairmanChoiceBox);
        addChangeListener(cccMember1ChoiceBox, supervisorChoiceBox, cccMember2ChoiceBox, cccChairmanChoiceBox);
        addChangeListener(cccMember2ChoiceBox, cccMember1ChoiceBox, supervisorChoiceBox, cccChairmanChoiceBox);
        addChangeListener(cccChairmanChoiceBox, cccMember1ChoiceBox, cccMember2ChoiceBox, supervisorChoiceBox);

        // disable Create button any field is invalid
        BooleanBinding bb = Bindings.createBooleanBinding(() -> {
                    ChangeInitiator supervisor = supervisorChoiceBox.getSelectionModel().getSelectedItem();
                    ChangeInitiator ccc1 = cccMember1ChoiceBox.getSelectionModel().getSelectedItem();
                    ChangeInitiator ccc2 = cccMember2ChoiceBox.getSelectionModel().getSelectedItem();
                    ChangeInitiator cccChairman = cccChairmanChoiceBox.getSelectionModel().getSelectedItem();

                    // disable, if one selection is missing or from is not smaller than to
                    return (supervisor == null || ccc1 == null || ccc2 == null || cccChairman == null);
                }, supervisorChoiceBox.valueProperty(),
                cccMember1ChoiceBox.valueProperty(),
                cccMember2ChoiceBox.valueProperty(),
                cccChairmanChoiceBox.valueProperty()
        );
        updatePermissionsButton.disableProperty().bind(bb);
    }

    @FXML
    void updatePermissions(ActionEvent event) {
        selectedSupervisor = supervisorChoiceBox.getSelectionModel().getSelectedItem();
        selectedCcc1 = cccMember1ChoiceBox.getSelectionModel().getSelectedItem();
        selectedCcc2 = cccMember2ChoiceBox.getSelectionModel().getSelectedItem();
        selectedChairman = cccChairmanChoiceBox.getSelectionModel().getSelectedItem();

        List<ChangeInitiator> newSelection = new ArrayList<>();
        newSelection.add(selectedSupervisor);
        newSelection.add(selectedCcc1);
        newSelection.add(selectedCcc2);
        newSelection.add(selectedChairman);

        // TODO: handle no change case!

        List<List<ChangeInitiator>> oldAndNewSelection = new ArrayList<>();
        oldAndNewSelection.add(oldSelection);
        oldAndNewSelection.add(newSelection);
        ServerService serverService = new ServerService(ServerService.DatabaseService.Itd_Update_Permissions, oldAndNewSelection);

        clientController.handleMessageFromClientUI(serverService);
    }

    @Override
    public void handleMessageFromClientController(ServerService serverService) {

        switch (serverService.getDatabaseService()) {
            case Get_Info_Engineers:
                List<ChangeInitiator> params = serverService.getParams();
                infoEngineersList.setAll(params);

                supervisorList.setAll(infoEngineersList);
                ccc1List.setAll(infoEngineersList);
                ccc2List.setAll(infoEngineersList);
                cccChairmanList.setAll(infoEngineersList);

                supervisorChoiceBox.setItems(supervisorList);
                cccMember1ChoiceBox.setItems(ccc1List);
                cccMember2ChoiceBox.setItems(ccc2List);
                cccChairmanChoiceBox.setItems(cccChairmanList);

                for (ChangeInitiator ci: params) {
                    switch (ci.getPosition()) {
                        case ITD_MANAGER:
                            infoEngineersList.remove(ci);
                            supervisorList.remove(ci);
                            ccc1List.remove(ci);
                            ccc2List.remove(ci);
                            cccChairmanList.remove(ci);
                            break;
                        case SUPERVISOR:
                            supervisorChoiceBox.getSelectionModel().select(ci);
                            oldSelection.add(ci);
                            break;
                        case CCC:
                            if (cccMember1ChoiceBox.getSelectionModel().getSelectedItem() == null)
                                cccMember1ChoiceBox.getSelectionModel().select(ci);
                            else
                                cccMember2ChoiceBox.getSelectionModel().select(ci);
                            oldSelection.add(ci);
                            break;
                        case CHAIRMAN:
                            cccChairmanChoiceBox.getSelectionModel().select(ci);
                            oldSelection.add(ci);
                            break;
                    }
                }
                break;

            case Itd_Update_Permissions:
                IcmUtils.displayInformationMsg("Updated!");
                IcmUtils.getPopUp().close();
                break;

            case Error:
                IOException ioException = ((IOException) serverService.getParams().get(0));
                IcmUtils.displayErrorMsg("Server Error", "Server Error", ioException.getMessage());
                break;
        }

    }

    private void addChangeListener(ChoiceBox src, ChoiceBox a, ChoiceBox b, ChoiceBox c) {
        src.valueProperty().addListener((observable, oldValue, newValue) -> {
            if(a.getSelectionModel().getSelectedItem() != null &&
                    a.getSelectionModel().getSelectedItem().equals(newValue))
                a.getSelectionModel().clearSelection();

            if(b.getSelectionModel().getSelectedItem()!= null &&
                    b.getSelectionModel().getSelectedItem().equals(newValue))
                b.getSelectionModel().clearSelection();

            if(c.getSelectionModel().getSelectedItem()!= null &&
                    c.getSelectionModel().getSelectedItem().equals(newValue))
                c.getSelectionModel().clearSelection();
        });
    }

    @FXML
    private void backToMainWindow() {
        IcmUtils.getPopUp().close();
    }
}