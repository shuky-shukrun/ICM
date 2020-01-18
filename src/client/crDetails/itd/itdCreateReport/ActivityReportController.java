package client.crDetails.itd.itdCreateReport;

import java.io.IOException;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.time.temporal.ChronoUnit;

import client.ClientController;
import client.ClientUI;
import entities.Distribution;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import server.ServerService;

public class ActivityReportController implements ClientUI {
	@FXML
	private TextField medActive;
	@FXML
	private TextField stdActive;
	@FXML
	private TextField countActive;
	@FXML
	private TableView<Distribution> ActiveTable;
	@FXML
	private TableColumn<Distribution, Integer> cntActive;
	@FXML
	private TableColumn<Distribution, Integer> disActive;
	@FXML
	private TextField stdFrozen;
	@FXML
	private TextField medFrozen;
	@FXML
	private TextField countFrozen;
	@FXML
	private TableView<Distribution> FrozenTable;
	@FXML
	private TableColumn<Distribution, Integer> cntFrozen;
	@FXML
	private TableColumn<Distribution, Integer> disFrozen;
	@FXML
	private TextField medClosed;
	@FXML
	private TextField stdClosed;
	@FXML
	private TextField countClosed;
	@FXML
	private TableView<Distribution> ClosedTable;
	@FXML
	private TableColumn<Distribution, Integer> cntClosed;
	@FXML
	private TableColumn<Distribution, Integer> disClosed;
	@FXML
	private TextField medDeclined;
	@FXML
	private TextField stdDeclined;
	@FXML
	private TextField countDeclined;
	@FXML
	private TableView<Distribution> DeclinedTable;
	@FXML
	private TableColumn<Distribution, Integer> cntDeclined;
	@FXML
	private TableColumn<Distribution, Integer> disDeclined;
	@FXML
	private TextField medWorkDays;
	@FXML
	private TextField stdWorkDays;
	@FXML
	private TextField countWorkDays;
	@FXML
	private TableView<Distribution> WorkDaysTable;
	@FXML
	private TableColumn<Distribution, Integer> cntWorkDays;
	@FXML
	private TableColumn<Distribution, Integer> disWorkDays;
	@FXML
	private AnchorPane mainAnchorPane;
	private ClientController clientController;
	private ObservableList<Distribution> listActive;
	private ObservableList<Distribution> listFrozen;
	private ObservableList<Distribution> listDeclined;
	private ObservableList<Distribution> listClosed;
	private ObservableList<Distribution> listTotalDays;
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
		listActive = FXCollections.observableArrayList();
		listFrozen = FXCollections.observableArrayList();
		listDeclined = FXCollections.observableArrayList();
		listClosed = FXCollections.observableArrayList();
		listTotalDays = FXCollections.observableArrayList();
		initTableValueFactory(cntActive, disActive);
		
		cntFrozen.setStyle("-fx-alignment: CENTER");
		disFrozen.setStyle("-fx-alignment: CENTER");
		
		initTableValueFactory(cntFrozen, disFrozen);
		cntDeclined.setStyle("-fx-alignment: CENTER");
		disDeclined.setStyle("-fx-alignment: CENTER");
		
		initTableValueFactory(cntDeclined, disDeclined);
		cntClosed.setStyle("-fx-alignment: CENTER");
		disClosed.setStyle("-fx-alignment: CENTER");
		
		initTableValueFactory(cntClosed, disClosed);
		
		cntWorkDays.setStyle("-fx-alignment: CENTER");
		disWorkDays.setStyle("-fx-alignment: CENTER");
		
		initTableValueFactory(cntWorkDays, disWorkDays);

		long daysBetween = ChronoUnit.DAYS.between(startDate, endDate);
		long weeksBetween = daysBetween / 7;
		long leftBetween = daysBetween % 7;
		params.add(weeksBetween);
		params.add(leftBetween);
		ServerService getFrozen = new ServerService(ServerService.DatabaseService.Get_Activity_Report_Details, params);
		clientController.handleMessageFromClientUI(getFrozen);

	}

	private void initTableValueFactory(TableColumn<Distribution, Integer> cnt,
			TableColumn<Distribution, Integer> dis) {
		cnt.setCellValueFactory(new PropertyValueFactory<>("num"));
		dis.setCellValueFactory(new PropertyValueFactory<>("dis"));
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

		for (int i=0; i<Array.length;i++) {
			sd = sd + Math.pow(Array[i] - avg, 2);
			//sd += Math.pow(num - avg, 2);
		}

		return Math.sqrt(sd /( Array.length));

	}

	public List<Distribution> frq(Integer[] Array) {
		List<Distribution>l=new ArrayList<Distribution>();
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
		        l.add(new Distribution((Integer)pair.getKey(), (Integer)pair.getValue(),null));
		        it.remove(); // avoids a ConcurrentModificationException
		    }
		  
		return l;

	}
	
	@Override
	public void handleMessageFromClientController(ServerService serverService) {
		// params is the list of values for statistic
		List<List<Integer>> params = serverService.getParams();
		
		List frozenCount = params.get(0);
		List activeCount = params.get(1);
		List closedCount = params.get(2);
		List declinedCount = params.get(3);
		List totalDaysCount = params.get(4);
		
		int totalFrozen = 0;
		int totalActive = 0;
		int totalClosed = 0;
		int totalDeclined = 0;
		int totaldays = 0;

		int size1 = frozenCount.size();
		int size2 = activeCount.size();
		int size3 = closedCount.size();
		int size4 = declinedCount.size();
		int size5 = totalDaysCount.size();
	
		Integer[] numArray1 = new Integer[size1];
		Integer[] numArray2 = new Integer[size2];
		Integer[] numArray3 = new Integer[size3];
		Integer[] numArray4 = new Integer[size4];
		Integer[] numArray5 = new Integer[size5];

		frozenCount.toArray(numArray1);
		activeCount.toArray(numArray2);
		closedCount.toArray(numArray3);
		declinedCount.toArray(numArray4);
		totalDaysCount.toArray(numArray5);

		Arrays.sort(numArray1);
		Arrays.sort(numArray2);
		Arrays.sort(numArray3);
		Arrays.sort(numArray4);
		Arrays.sort(numArray5);

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
		for (int i = 0; i < size5; i++) {
			totaldays = totaldays + numArray5[i];
		}
		//calculate the medians
		double frozenMed = median(numArray1);
		double activeMed = median(numArray2);
		double closedMed = median(numArray3);
		double declinedMed = median(numArray4);
		double daysMed = median(numArray5);
		System.out.println("ronit"+frozenMed);
		//calculate the standard deviation
		double frozenStd = std(numArray1);
		double activeStd = std(numArray2);
		double closedStd = std(numArray3);
		double declinedStd = std(numArray4);
		double DaysStd = std(numArray5);

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
		String s12 = String.valueOf(daysMed);
		String s13 = String.valueOf(DaysStd);
		String s14 = String.valueOf(totaldays);

		medFrozen.textProperty().set(s);
		stdFrozen.textProperty().set(s1);
		medActive.textProperty().set(s2);
		stdActive.textProperty().set(s3);
		medClosed.textProperty().set(s4);
		stdClosed.textProperty().set(s5);
		medDeclined.textProperty().set(s6);
		stdDeclined.textProperty().set(s7);
		countFrozen.textProperty().set(s8);
		countActive.textProperty().set(s9);
		countClosed.textProperty().set(s10);
		countDeclined.textProperty().set(s11);
		medWorkDays.textProperty().set(s12);
		stdWorkDays.textProperty().set(s13);
		countWorkDays.textProperty().set(s14);
		
		//active table
		List<Distribution>l=frq(numArray2);
		listActive.setAll(l);
		ActiveTable.setItems(listActive);
		//frozen table
		List<Distribution>l1=frq(numArray1);
		listFrozen.setAll(l1);
		FrozenTable.setItems(listFrozen);
		//declined table
		List<Distribution>l2=frq(numArray4);
		listDeclined.setAll(l2);
		DeclinedTable.setItems(listDeclined);
		//closed table
		List<Distribution>l3=frq(numArray3);
		listClosed.setAll(l3);
		ClosedTable.setItems(listClosed);
		//total Work Days table
		List<Distribution>l4=frq(numArray5);
		listTotalDays.setAll(l4);
		WorkDaysTable.setItems(listTotalDays);

	}



}
