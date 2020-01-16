package client.crDetails.itd.itdCreateReport;

import java.io.IOException;
import java.sql.Array;
import java.sql.Date;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.time.temporal.ChronoUnit;

import client.ClientController;
import client.ClientUI;
import entities.ChangeInitiator;
import entities.ChangeRequest;
import entities.InfoSystem;
import entities.Phase;
import entities.distribution;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.MapValueFactory;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import server.ServerService;

public class ActivityReportController implements ClientUI {
	@FXML
	private TextField medActive;
	@FXML
	private TextField stdActive;
	@FXML
	private TableView<distribution> ActiveTable;
	@FXML
	private TableColumn<distribution, Integer> cntActive;
	@FXML
	private TableColumn<distribution, Integer> disActive;
	@FXML
	private TableView<Integer[]> FrozenTable;
	@FXML
	private TableColumn<Integer, Integer> cntFrozen;
	@FXML
	private TableColumn<Integer, Integer> disFrozen;
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
	private TableColumn<Integer, Integer> disClosed;
	@FXML
	private TextField medDeclined;
	@FXML
	private TextField stdDeclined;
	@FXML
	private TableView<Integer> DeclinedTable;
	@FXML
	private TableColumn<Integer, Integer> cntDeclined;
	@FXML
	private TableColumn<Integer, Integer> disDeclined;
	@FXML
	private TextField medWorkDays;
	@FXML
	private TextField stdWorkDays;
	@FXML
	private TableView<Integer> WorkDaysTable;
	@FXML
	private TableColumn<Integer, Integer> cntWorkDays;
	@FXML
	private TableColumn<Integer, Integer> disWorkDays;
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
	@FXML
	private AnchorPane mainAnchorPane;
	private ClientController clientController;
	private ObservableList<distribution> helper;

	public static final String Column1MapKey = "A";
	public static final String Column2MapKey = "B";

	public void initialize() {
		try {
			clientController = ClientController.getInstance(this);
		} catch (IOException e) {
			e.printStackTrace();
		}

		LocalDate startDate = ITDCreateReport.getStartDate();
		LocalDate endDate = ITDCreateReport.getEndDate();
		List<Object> params = new ArrayList<>();
		params.add(startDate);
		params.add(endDate);
		// long numOfDays= Date.valueOf((LocalDate)endDate).getTime()-
		// Date.valueOf((LocalDate)startDate).getTime();
		// TimeUnit.DAYS.convert(numOfDays, TimeUnit.MILLISECONDS);
		cntActive.setStyle("-fx-alignment: CENTER");
		disActive.setStyle("-fx-alignment: CENTER");
		helper = FXCollections.observableArrayList();
		initTableValueFactory(cntActive, disActive);

		long daysBetween = ChronoUnit.DAYS.between(startDate, endDate);
		long weeksBetween = daysBetween / 7;
		long leftBetween = daysBetween % 7;
		params.add(weeksBetween);
		params.add(leftBetween);
		ServerService getFrozen = new ServerService(ServerService.DatabaseService.Get_Activity_Report_Details, params);
		clientController.handleMessageFromClientUI(getFrozen);

	}

	private void initTableValueFactory(TableColumn<distribution, Integer> cntActive2,
			TableColumn<distribution, Integer> disActive2) {
		cntActive2.setCellValueFactory(new PropertyValueFactory<>("num"));
		disActive2.setCellValueFactory(new PropertyValueFactory<>("dis"));
	}

	@Override
	public void handleMessageFromClientController(ServerService serverService) {
		// params is the list of values for statistic
		List<List<Integer>> params = serverService.getParams();
		List frozenCount = params.get(0);
		List activeCount = params.get(1);
		List closedCount = params.get(2);
		List declinedCount = params.get(3);

		System.out.println("hello");
		System.out.println(frozenCount);
		System.out.println(activeCount);
		System.out.println(closedCount);
		System.out.println(declinedCount);
		System.out.println("bye");

		int totalFrozen = 0;
		int totalActive = 0;
		int totalClosed = 0;
		int totalDeclined = 0;

		int size1 = frozenCount.size();
		int size2 = activeCount.size();
		int size3 = closedCount.size();
		int size4 = declinedCount.size();
		System.out.println(size4);

		Integer[] numArray1 = new Integer[size1];
		Integer[] numArray2 = new Integer[size2];
		Integer[] numArray3 = new Integer[size3];
		Integer[] numArray4 = new Integer[size4];

		frozenCount.toArray(numArray1);
		activeCount.toArray(numArray2);
		closedCount.toArray(numArray3);
		declinedCount.toArray(numArray4);

		Arrays.sort(numArray1);
		Arrays.sort(numArray2);
		Arrays.sort(numArray3);
		Arrays.sort(numArray4);

		for (int i = 0; i < size1; i++) {
			totalFrozen = totalFrozen + numArray1[i];
		}
		for (int i = 0; i < size2; i++) {
			totalActive = totalActive + numArray2[i];
		}
		for (int i = 0; i < size3; i++) {
			totalClosed = totalClosed + numArray3[i];
		}
		for (int i = 0; i < size4; i++) {
			totalDeclined = totalDeclined + numArray4[i];
		}

		double frozenMed = median(numArray1);
		double activeMed = median(numArray2);
		double closedMed = median(numArray3);
		double declinedMed = median(numArray4);

		double frozenStd = std(numArray1);
		double activeStd = std(numArray2);
		double closedStd = std(numArray3);
		double declinedStd = std(numArray4);

		String s = String.valueOf(frozenMed);
		String s1 = String.valueOf(frozenStd);
		String s2 = String.valueOf(activeMed);
		String s3 = String.valueOf(activeStd);
		String s4 = String.valueOf(closedMed);
		String s5 = String.valueOf(closedStd);
		String s6 = String.valueOf(declinedMed);
		String s7 = String.valueOf(declinedStd);
		String s8 = String.valueOf(totalFrozen);
		String s9 = String.valueOf(totalActive);
		String s10 = String.valueOf(totalClosed);
		String s11 = String.valueOf(totalDeclined);

		medFrozen.textProperty().set(s);
		medFrozen.setDisable(true);
		stdFrozen.textProperty().set(s1);
		stdFrozen.setDisable(true);
		medActive.textProperty().set(s2);
		medActive.setDisable(true);
		stdActive.textProperty().set(s3);
		stdActive.setDisable(true);
		medClosed.textProperty().set(s4);
		medClosed.setDisable(true);
		stdClosed.textProperty().set(s5);
		stdClosed.setDisable(true);
		medDeclined.textProperty().set(s6);
		stdDeclined.textProperty().set(s7);
		countFrozen.textProperty().set(s8);
		countActive.textProperty().set(s9);
		countClosed.textProperty().set(s10);
		countDeclined.textProperty().set(s11);
		//active table
		List<distribution>l=frq(numArray2);
		helper.setAll(l);
		ActiveTable.setItems(helper);
		System.out.println(ActiveTable.getItems().get(0).toString());
		

	}

	public double median(Integer[] Array) {
		double median;
		if (Array.length % 2 == 0)
			median = ((double) Array[Array.length / 2] + (double) Array[Array.length / 2 - 1]) / 2;
		else
			median = (double) Array[Array.length / 2];

		return median;
	}

	public double std(Integer[] Array) {
		double sum = 0.0;
		double sd = 0.0;

		for (int i = 0; i < Array.length; i++) {
			sum = sum + Array[i];
		}

		double avg = sum / (double) Array.length;

		for (double num : Array) {
			sd += Math.pow(num - avg, 2);
		}

		return Math.sqrt(sd / Array.length);

	}

	public List<distribution> frq(Integer[] Array) {
		List<distribution>l=new ArrayList<distribution>();
		HashMap<Integer, Integer> hm = new HashMap<Integer, Integer>();
		for (int i = 0; i < Array.length; i++) {
			if (hm.containsKey(Array[i]))
				hm.put(Array[i], hm.get(Array[i]) + 1);
			else

				hm.put(Array[i], 1);
		}
		 Iterator it = hm.entrySet().iterator();
		    while (it.hasNext()) {
		        Map.Entry pair = (Map.Entry)it.next();
		        l.add(new distribution((Integer)pair.getKey(), (Integer)pair.getValue()));
		        it.remove(); // avoids a ConcurrentModificationException
		    }
		  
		return l;

	}

}
