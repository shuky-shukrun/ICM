package client.crDetails.itd.itdCreateReport;

import java.io.IOException;
import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.time.temporal.ChronoUnit;

import client.ClientController;
import client.ClientUI;
import entities.ChangeInitiator;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
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
	@FXML
	private AnchorPane mainAnchorPane;
	private ClientController clientController;
	static HashMap<Integer, Integer> hm = new HashMap<Integer, Integer>();


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
		long daysBetween = ChronoUnit.DAYS.between(startDate, endDate);
		long weeksBetween = daysBetween / 7;
		long leftBetween = daysBetween % 7;
		params.add(weeksBetween);
		params.add(leftBetween);
		ServerService getFrozen = new ServerService(ServerService.DatabaseService.Get_Report_Details, params);
		clientController.handleMessageFromClientUI(getFrozen);
		ServerService getActive = new ServerService(ServerService.DatabaseService.Get_Active_Details, params);
		clientController.handleMessageFromClientUI(getActive);

	}

	@Override
	public void handleMessageFromClientController(ServerService serverService) {
		// params is the list of values for statistic
		List<List<Integer>> params = serverService.getParams();
		List frozenCount= params.get(0);
		List activeCount=params.get(1);
		List closedCount=params.get(2);
		System.out.println("hello");
		System.out.println(params.get(0));
		System.out.println(params.get(1));
		System.out.println(params.get(2));
		System.out.println("bye");

		int size1 = params.get(0).size();
		int size2 = params.get(1).size();
		int size3 = params.get(2).size();
		Integer[] numArray1 = new Integer[size1];
		Integer[] numArray2 = new Integer[size2];
		Integer[] numArray3 = new Integer[size3];
		params.get(0).toArray(numArray1);
		params.get(1).toArray(numArray2);
		params.get(2).toArray(numArray3);
		Arrays.sort(numArray1);
		Arrays.sort(numArray2);
		Arrays.sort(numArray3);

		double frozenMed= median(numArray1);
		double activeMed= median(numArray2);
		double closedMed= median(numArray3);

		double frozenStd= std(numArray1);
		double activeStd= std(numArray1);
		double closedStd= std(numArray1);

		String s = String.valueOf(frozenMed);
		String s1 = String.valueOf(frozenStd);
		String s2 = String.valueOf(activeMed);
		String s3 = String.valueOf(activeStd);
		String s4 = String.valueOf(closedMed);
		String s5 = String.valueOf(closedStd);
		String s6= String.valueOf(numArray1.length);
		String s7= String.valueOf(numArray2.length);
		String s8= String.valueOf(size3);
		
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
		countFrozen.textProperty().set(s6);
		countActive.textProperty().set(s7);
		countClosed.textProperty().set(s8);


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
		double sum=0.0;
		for (int i = 0; i < Array.length; i++) {
			sum = sum + Array[i];
			System.out.println(sum);
		}
		double avg = sum / (double) Array.length;
		System.out.println(avg);
		double sd = 0;
		for (int i = 0; i < Array.length; i++) {
			sd += ((Array[i] - avg) * (Array[i] - avg)) / (Array.length - 1);
		}
		double standardDeviation = Math.sqrt(sd);
		return standardDeviation;
	}
	
	public void frq(Integer[] Array) {
		for (int i = 0; i < Array.length; i++) {
			if (hm.containsKey(Array[i]))
				hm.put(Array[i], hm.get(Array[i]) + 1);
			else
				hm.put(Array[i], 1);
		}
		System.out.println(hm);
	}

}
