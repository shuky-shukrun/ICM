package client.mainWindow.itdCreateReport;

import java.io.IOException;

import java.time.LocalDate;
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
	/**
	 * Initialize the activity report screen
	 */
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
	/**
	 * Initialize all the tables in the report
	 * @param cnt-the column of values
	 * @param dis-the column of the frequency of the values
	 */
	private void initTableValueFactory(TableColumn<Distribution, Integer> cnt,
			TableColumn<Distribution, Integer> dis) {
		cnt.setCellValueFactory(new PropertyValueFactory<>("num"));
		dis.setCellValueFactory(new PropertyValueFactory<>("dis"));
	}
	/**
	 * Calculates the median of an array
	 * @param Array-the given array of elements
	 * @return the median
	 */
	public double median(Integer[] Array) {
		double median;
		if (Array.length % 2 == 0)
			median = ((double) Array[Array.length / 2] + (double) Array[Array.length / 2 - 1]) / 2;
		else
			median = (double) Array[Array.length / 2];

		return median;
	}
	/**
	 * Calculates the standard deviation of an array
	 * @param Array-the given array of elements
	 * @return the standard deviation
	 */
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
	/**
	 * Calculates the frequencies of elements in an array
	 * @param Array-the given array of elements
	 * @return list of frequencies
	 */
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
	/**
	 * Handle the message that returns from server
	 */
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
		int totalDays = 0;

		Integer[] frozenRequestArray = new Integer[frozenCount.size()];
		Integer[] activeRequestArray = new Integer[activeCount.size()];
		Integer[] closedRequestArray = new Integer[closedCount.size()];
		Integer[] declinedRequestArray = new Integer[declinedCount.size()];
		Integer[] totalRequestArray = new Integer[totalDaysCount.size()];

		frozenCount.toArray(frozenRequestArray);
		activeCount.toArray(activeRequestArray);
		closedCount.toArray(closedRequestArray);
		declinedCount.toArray(declinedRequestArray);
		totalDaysCount.toArray(totalRequestArray);

		Arrays.sort(frozenRequestArray);
		Arrays.sort(activeRequestArray);
		Arrays.sort(closedRequestArray);
		Arrays.sort(declinedRequestArray);
		Arrays.sort(totalRequestArray);

		for (int i = 0; i < frozenCount.size(); i++) {
			totalFrozen = totalFrozen + frozenRequestArray[i];
		}
		for (int i = 0; i < activeCount.size(); i++) {
			totalActive = totalActive + activeRequestArray[i];
		}
		for (int i = 0; i < closedCount.size(); i++) {
			totalClosed = totalClosed + closedRequestArray[i];
		}
		for (int i = 0; i < declinedCount.size(); i++) {
			totalDeclined = totalDeclined + declinedRequestArray[i];
		}
		for (int i = 0; i < totalDaysCount.size(); i++) {
			totalDays = totalDays + totalRequestArray[i];
		}
		//calculate the medians
		double frozenMed = median(frozenRequestArray);
		double activeMed = median(activeRequestArray);
		double closedMed = median(closedRequestArray);
		double declinedMed = median(declinedRequestArray);
		double daysMed = median(totalRequestArray);

		//calculate the standard deviation
		double frozenStd = std(frozenRequestArray);
		double activeStd = std(activeRequestArray);
		double closedStd = std(closedRequestArray);
		double declinedStd = std(declinedRequestArray);
		double DaysStd = std(totalRequestArray);

		medFrozen.textProperty().set(String.valueOf(frozenMed));
		stdFrozen.textProperty().set(String.valueOf(frozenStd));
		medActive.textProperty().set(String.valueOf(activeMed));
		stdActive.textProperty().set(String.valueOf(activeStd));
		medClosed.textProperty().set(String.valueOf(closedMed));
		stdClosed.textProperty().set(String.valueOf(closedStd));
		medDeclined.textProperty().set(String.valueOf(declinedMed));
		stdDeclined.textProperty().set(String.valueOf(declinedStd));
		countFrozen.textProperty().set(String.valueOf(totalFrozen));
		countActive.textProperty().set(String.valueOf(totalActive));
		countClosed.textProperty().set(String.valueOf(totalClosed));
		countDeclined.textProperty().set(String.valueOf(totalDeclined));
		medWorkDays.textProperty().set(String.valueOf(daysMed));
		stdWorkDays.textProperty().set(String.valueOf(DaysStd));
		countWorkDays.textProperty().set(String.valueOf(totalDays));
		
		//active table
		List<Distribution>l=frq(activeRequestArray);
		listActive.setAll(l);
		ActiveTable.setItems(listActive);
		//frozen table
		List<Distribution>l1=frq(frozenRequestArray);
		listFrozen.setAll(l1);
		FrozenTable.setItems(listFrozen);
		//declined table
		List<Distribution>l2=frq(declinedRequestArray);
		listDeclined.setAll(l2);
		DeclinedTable.setItems(listDeclined);
		//closed table
		List<Distribution>l3=frq(closedRequestArray);
		listClosed.setAll(l3);
		ClosedTable.setItems(listClosed);
		//total Work Days table
		List<Distribution>l4=frq(totalRequestArray);
		listTotalDays.setAll(l4);
		WorkDaysTable.setItems(listTotalDays);

	}



}
