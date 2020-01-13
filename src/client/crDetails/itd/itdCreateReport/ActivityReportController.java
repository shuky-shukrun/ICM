package client.crDetails.itd.itdCreateReport;

import java.io.IOException;

import client.ClientController;
import client.ClientUI;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import server.ServerService;

public class ActivityReportController implements ClientUI {
	@FXML
	private TextField medActive;
	@FXML
	private TextField stdActive;
	@FXML
	private TableView<Integer> ActiveTable;
	@FXML
	private TableColumn<Integer, Integer> cntActive;
	@FXML
	private TableColumn<Double, Double> disActive;
	@FXML
	private TableView<Integer[]> FrozenTable;
	@FXML
	private TableColumn<Integer, Integer> cntFrozen;
	@FXML
	private TableColumn<Double, Double> disFrozen;
	@FXML
	private TextField stdFrozen;
	@FXML
	private TextField medFrozen;
	@FXML
	private TextField medClosed;
	@FXML
	private TextField stdClosed;
	@FXML
	private TableView<Integer> ClosedTable;
    @FXML
    private TableColumn<Integer, Integer> cntClosed;
    @FXML
    private TableColumn<Double, Double> disClosed;
	@FXML
	private TextField medDeclined;
	@FXML
	private TextField stdDeclined;
	@FXML
	private TableView<Integer> DeclinedTable;
	@FXML
	private TableColumn<Integer, Integer> cntDeclined;
    @FXML
	private TableColumn<Double, Double> disDeclined;
	@FXML
	private TextField medWorkDays;
	@FXML
	private TextField stdWorkDays;
	@FXML
	private TableView<Integer> WorkDaysTable;
    @FXML
    private TableColumn<Integer, Integer> cntWorkDays;
    @FXML
    private TableColumn<Double, Double> disWorkDays;
	@FXML
	private TextField countWorkDays;
	@FXML
	private TextField countDeclined;
	@FXML
	private TextField countClosed;
	@FXML
	private TextField countFrozen;
	@FXML
	private TextField countActive;
	private ClientController clientController;
	public void initialize() {
		try {
			clientController=ClientController.getInstance(this);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	@Override
	public void handleMessageFromClientController(ServerService serverService) {
		// TODO Auto-generated method stub
		
	}
}
