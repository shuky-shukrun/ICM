package client.mainWindow;

import client.ClientController;
import client.ClientUI;
import client.crDetails.CrDetails;
import client.crDetails.itd.itdCreateReport.ITDCreateReport;
import client.mainWindow.newRequest.NewRequest;
import common.IcmUtils;
import entities.*;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import server.ServerService;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


public class MainWindow implements ClientUI {

    @FXML
    private TextField searchChangeRequestTextField;
    @FXML
    private TabPane tabPane;

    @FXML
    private Tab inMyTreatmentTab;
    @FXML
    private Tab myRequestsTab;
    // my change requests tab
    @FXML
    private TableView<ChangeRequest> myTableView;
    @FXML
    private TableColumn<ChangeRequest, Integer> idColumn;
    @FXML
    private TableColumn<ChangeRequest, InfoSystem> infoSystemColumn;
    @FXML
    private TableColumn<ChangeRequest, LocalDate> dateColumn;
    @FXML
    private TableColumn<ChangeRequest, Phase.PhaseName> currPhaseColumn;

    // in my treatment tab
    @FXML
    private TableView<ChangeRequest> workTableView;
    @FXML
    private TableColumn<ChangeRequest, Integer> idColumn1;
    @FXML
    private TableColumn<ChangeRequest, InfoSystem> infoSystemColumn1;
    @FXML
    private TableColumn<ChangeRequest, LocalDate> dateColumn1;
    @FXML
    private TableColumn<ChangeRequest, Phase.PhaseName> currPhaseColumn1;

    // search results tab
    @FXML
    private Tab searchTab;
    @FXML
    private TableView<ChangeRequest> searchTableView;
    @FXML
    private TableColumn<ChangeRequest, Integer> idColumn2;
    @FXML
    private TableColumn<ChangeRequest, InfoSystem> infoSystemColumn2;
    @FXML
    private TableColumn<ChangeRequest, LocalDate> dateColumn2;
    @FXML
    private TableColumn<ChangeRequest, Phase.PhaseName> currPhaseColumn2;

    @FXML
    private Button itdManagerAssignPermissionsButton;
    @FXML
    private Button createReportButton;
    @FXML
    private Button registerButton;
    @FXML
    private Label userNameLabel;
    @FXML
    private AnchorPane mainAnchorPane;


    // class local variables
    private ClientController clientController;
    private ObservableList<ChangeRequest> myRequests;
    private ObservableList<ChangeRequest> inMyTreatmentRequests;

    public void initialize() {
        try {
            clientController = ClientController.getInstance(this);
        } catch (IOException e) {
            e.printStackTrace();
        }

        ChangeInitiator currUser = ClientController.getUser();
        userNameLabel.setText(currUser.getFirstName() + " " + currUser.getLastName());

        // show assign permissions and create report buttons only if the user is the ITD Manager
        if (currUser.getPosition() != Position.ITD_MANAGER) {
            itdManagerAssignPermissionsButton.setVisible(false);
            createReportButton.setVisible(false);
            registerButton.setVisible(false);
        }

        // init request lists
        myRequests = FXCollections.observableArrayList();
        inMyTreatmentRequests = FXCollections.observableArrayList();

        // init tables columns
        initTableValueFactory(idColumn, infoSystemColumn, dateColumn, currPhaseColumn);
        initTableValueFactory(idColumn1, infoSystemColumn1, dateColumn1, currPhaseColumn1);
        initTableValueFactory(idColumn2, infoSystemColumn2, dateColumn2, currPhaseColumn2);

        // init tables double clicks to open change request
        initRowDoubleClick(myTableView);
        initRowDoubleClick(workTableView);
        initRowDoubleClick(searchTableView);

        //hide search table when it unnecessary
        searchTab.setDisable(true);

        // load data into tables
        // prepare service request to pass to server
        List<ChangeInitiator> userList = new ArrayList<>();
        userList.add(ClientController.getUser());
        ServerService serverService = new ServerService(ServerService.DatabaseService.Get_All_Requests, userList);
        // pass to client controller.
        // client controller uses 'handleMessageFromClientController' function to load server answer into the ui
        clientController.handleMessageFromClientUI(serverService);

        //initialize search change listener
        searchChangeRequestTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            ObservableList<ChangeRequest> searchResult = FXCollections.observableArrayList();

            if(!newValue.trim().equals("")) {
                ChangeRequest searchValue = new ChangeRequest();
                searchValue.setId(Integer.parseInt(newValue));
                int index = myRequests.indexOf(searchValue);

                if (index != -1) {
                    searchResult.setAll(myRequests.get(index));
                } else if (inMyTreatmentRequests != null) {
                    index = inMyTreatmentRequests.indexOf(searchValue);
                    if (index != -1) {
                        searchResult.setAll(inMyTreatmentRequests.get(index));
                    } else {
                        searchTab.setDisable(true);
                        tabPane.getSelectionModel().select(myRequestsTab);
                    }
                }
                searchTableView.setItems(searchResult);
                searchTab.setDisable(false);
                tabPane.getSelectionModel().select(searchTab);
            } else {
                searchTab.setDisable(true);
                tabPane.getSelectionModel().select(myRequestsTab);
            }
        });
    }

    private void initRowDoubleClick(TableView<ChangeRequest> myTableView) {
        myTableView.setRowFactory(tv -> {
            TableRow<ChangeRequest> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && (!row.isEmpty())) {
                    CrDetails.setCurrRequest(myTableView.getSelectionModel().getSelectedItem());
                    showRequestDialog();
                }
            });
            return row;
        });
    }

    private void initTableValueFactory(TableColumn<ChangeRequest, Integer> idColumn,
                                       TableColumn<ChangeRequest, InfoSystem> infoSystemColumn,
                                       TableColumn<ChangeRequest, LocalDate> dateColumn,
                                       TableColumn<ChangeRequest, Phase.PhaseName> currPhaseColumn) {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        infoSystemColumn.setCellValueFactory(new PropertyValueFactory<>("infoSystem"));
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));
        currPhaseColumn.setCellValueFactory(new PropertyValueFactory<>("currPhaseName"));
    }

    private void showRequestDialog() {
        try {
            IcmUtils.loadScene(this, IcmUtils.Scenes.Change_Request_Summary);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @FXML
    void logout(ActionEvent event) throws IOException {
        ClientController.setUser(null);
        IcmUtils.loadScene(this, IcmUtils.Scenes.Login);
    }

    @FXML
    void search(ActionEvent event) {



    }

    @FXML
    void showCreateReportDialog(ActionEvent event) throws IOException {

        // load dialog pane
        Dialog<ButtonType> createReportDialog = new Dialog<>();
        createReportDialog.initOwner(mainAnchorPane.getScene().getWindow());

        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/client/crDetails/itd/itdCreateReport/ITDCreateReport.fxml"));
        createReportDialog.getDialogPane().setContent(loader.load());
        createReportDialog.setTitle("ITD Create Report");

        ButtonType createButton = new ButtonType("Create");
        createReportDialog.getDialogPane().getButtonTypes().add(createButton);
        createReportDialog.getDialogPane().getButtonTypes().add(ButtonType.CANCEL);

        ITDCreateReport ITDCreateReport = loader.getController();

        // disable Create button any field is invalid
        BooleanBinding bb = Bindings.createBooleanBinding(() -> {
            LocalDate from = ITDCreateReport.getStartDateDatePicker().getValue();
            LocalDate to = ITDCreateReport.getEndDateDatePicker().getValue();
            Report.ReportType reportType = ITDCreateReport.getReportTypeChoiceBox().getSelectionModel().getSelectedItem();

            // disable, if one selection is missing or from is not smaller than to
            return (from == null || to == null || (from.compareTo(to) >= 0) || reportType == null);
        }, ITDCreateReport.getStartDateDatePicker().valueProperty(),
                ITDCreateReport.getEndDateDatePicker().valueProperty(),
                ITDCreateReport.getReportTypeChoiceBox().valueProperty()
        );
        createReportDialog.getDialogPane().lookupButton(createButton).disableProperty()
                .bind(bb);

        // if create button is pressed, create report
        Optional<ButtonType> result = createReportDialog.showAndWait();
        if (result.isPresent() && result.get() == createButton) {
            ITDCreateReport.createReport();
        }
    }

    @FXML
    void showItdManagerAssignPermissionsDialog(ActionEvent event) throws IOException {
        IcmUtils.popUpScene(this, "ITD Assign Permissions", "/client/mainWindow/itdAssignPermissions/ITDAssignPermissions.fxml", 472, 452);

    }
    @FXML
    void registerEvent(ActionEvent event) throws IOException {
        	
       	 IcmUtils.popUpScene(this, "Register IT", "/client/mainWindow/ITRegistration.fxml", 400, 300);

       
    }

    @FXML
    void showNewRequestDialog(ActionEvent event) throws IOException {
        IcmUtils.popUpScene(this, "New Change Request", "/client/mainWindow/newRequest/NewRequest.fxml", 585, 730);
    }

    @Override
    public void handleMessageFromClientController(ServerService serverService) {
        List<List<ChangeRequest>> allRequests = serverService.getParams();
        myRequests.setAll(allRequests.get(0));
        myTableView.setItems(myRequests);
        if (ClientController.getUser().getTitle() != ChangeInitiator.Title.INFOENGINEER) {
            inMyTreatmentTab.setDisable(true);
        }
        else {
            inMyTreatmentRequests.setAll(allRequests.get(1));
            workTableView.setItems(inMyTreatmentRequests);
        }
    }

}
