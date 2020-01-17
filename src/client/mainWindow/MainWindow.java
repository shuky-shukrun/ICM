package client.mainWindow;

import client.ClientController;
import client.ClientUI;
import client.crDetails.CrDetails;
import common.IcmUtils;
import entities.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import server.ServerService;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;


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

    /**
     * Initialize the main window
     */
    public void initialize() {
        try {
            clientController = ClientController.getInstance(this);
        } catch (IOException e) {
            e.printStackTrace();
        }

        ChangeInitiator currUser = ClientController.getUser();
        userNameLabel.setText(currUser.getFirstName() + " " + currUser.getLastName());

        searchChangeRequestTextField.addEventFilter(KeyEvent.KEY_TYPED , numeric_Validation(8));


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

    // set double click on table row
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

    /**
     * init TableView values.
     *
     * @param idColumn the ID column.
     * @param infoSystemColumn the info system.
     * @param dateColumn the date column.
     * @param currPhaseColumn the current phase column.
     */
    private void initTableValueFactory(TableColumn<ChangeRequest, Integer> idColumn,
                                       TableColumn<ChangeRequest, InfoSystem> infoSystemColumn,
                                       TableColumn<ChangeRequest, LocalDate> dateColumn,
                                       TableColumn<ChangeRequest, Phase.PhaseName> currPhaseColumn) {

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/YYYY");
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        infoSystemColumn.setCellValueFactory(new PropertyValueFactory<>("infoSystem"));
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));
        dateColumn.setCellFactory(col -> new TableCell<ChangeRequest, LocalDate>() {
            @Override
            protected void updateItem(LocalDate item, boolean empty) {

                super.updateItem(item, empty);
                if (empty)
                    setText(null);
                else
                    setText(String.format(item.format(formatter)));
            }
        });
        currPhaseColumn.setCellValueFactory(new PropertyValueFactory<>("currPhaseName"));
    }
    /**
     * shows change request details dialog.
     */
    @FXML
    private void showRequestDialog() {
        try {
            IcmUtils.loadScene(this, IcmUtils.Scenes.Change_Request_Summary);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * logout from system.
     * @throws IOException if have problem to load the scene from fxml file.
     */
    @FXML
    void logout() throws IOException {
        ClientController.setUser(null);
        IcmUtils.loadScene(this, IcmUtils.Scenes.Login);
    }

    /**
     * shows "ITD create report" dialog.
     * @throws IOException if have problem to load the the scene from fxml file.
     */
    @FXML
    void showCreateReportDialog() throws IOException {
        IcmUtils.popUpScene(this, "ITD Create Reports", "/client/crDetails/itd/itdCreateReport/ITDCreateReport.fxml", 588, 688);
    }

    /**
     * shows "ITD assign permissions" dialog.
     * @throws IOException if have problem to load the the scene from fxml file.
     */
    @FXML
    void showItdManagerAssignPermissionsDialog() throws IOException {
        IcmUtils.popUpScene(this, "ITD Assign Permissions", "/client/mainWindow/itdAssignPermissions/ITDAssignPermissions.fxml", 588, 688);
    }

    /**
     * shows "ITD Register IT" dialog.
     * @throws IOException if have problem to load the the scene from fxml file.
     */
    @FXML
    void registerEvent() throws IOException {
       	 IcmUtils.popUpScene(this, "Register IT", "/client/mainWindow/ITRegistration.fxml", 588, 688);
    }

    /**
     * shows "New Change Request" dialog.
     * @throws IOException if have problem to load the the scene from fxml file.
     */
    @FXML
    void showNewRequestDialog() throws IOException {
        IcmUtils.popUpScene(this, "New Change Request", "/client/mainWindow/newRequest/NewRequest.fxml", 658, 928);
    }

    /**
     * helper function to validate that search TextField contain only digits.
     *
     * @param max_Length the max length of change request id
     * @return EventHandler that can be assign to TextField
     */
    public EventHandler<KeyEvent> numeric_Validation(final Integer max_Length) {
        return new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent e) {
                TextField txt_TextField = (TextField) e.getSource();
                if (txt_TextField.getText().length() >= max_Length) {
                    e.consume();
                }
                if(!e.getCharacter().matches("[0-9]")){
                    e.consume();
                }
            }
        };
    }

    /**
     * reload the main window
     * @throws IOException if have problem to load the the scene from fxml file.
     */
    @FXML
    private void refresh() throws IOException {
        IcmUtils.loadScene(this, IcmUtils.Scenes.Main_Window);
    }

    /**
     * handle the returned value from server.
     *
     * @param serverService contain the enum and the params for it:
     *                      ServerService.DatabaseService.Get_All_Requests,
     *                      List of all the requests based on the user position.
     *                      it then loads the requests into the UI
     */
    @Override
    public void handleMessageFromClientController(ServerService serverService) {
        List<List<ChangeRequest>> allRequests = serverService.getParams();
        myRequests.setAll(allRequests.get(0));
        myTableView.setItems(myRequests);
        myTableView.getSortOrder().add(idColumn);
        if (ClientController.getUser().getTitle() != ChangeInitiator.Title.INFOENGINEER) {
            inMyTreatmentTab.setDisable(true);
        }
        else {
            inMyTreatmentRequests.setAll(allRequests.get(1));
            workTableView.setItems(inMyTreatmentRequests);
            workTableView.getSortOrder().add(idColumn1);
        }
    }
}
