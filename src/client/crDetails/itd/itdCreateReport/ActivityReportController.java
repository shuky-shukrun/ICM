package client.crDetails.itd.itdCreateReport;

import java.io.IOException;
import java.sql.Array;
import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.time.temporal.ChronoUnit;

import client.ClientController;
import client.ClientUI;
import entities.ChangeInitiator;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
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
		long daysBetween = ChronoUnit.DAYS.between(startDate, endDate);
		long weeksBetween = daysBetween / 7;
		long leftBetween = daysBetween % 7;
		params.add(weeksBetween);
		params.add(leftBetween);
		ServerService getFrozen = new ServerService(ServerService.DatabaseService.Get_Activity_Report_Details, params);
		clientController.handleMessageFromClientUI(getFrozen);

	}

	@Override
	public void handleMessageFromClientController(ServerService serverService) {
		// params is the list of values for statistic
		List<List<Integer>> params = serverService.getParams();
		List frozenCount= params.get(0);
		List activeCount=params.get(1);
		List closedCount=params.get(2);
		List declinedCount=params.get(3);

		System.out.println("hello");
		System.out.println(frozenCount);
		System.out.println(activeCount);
		System.out.println(closedCount);
		System.out.println(declinedCount);
		System.out.println("bye");

		int totalFrozen=0;
		int totalActive=0;
		int totalClosed=0;
		int totalDeclined=0;
		
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
		
		for(int i=0; i<size1; i++) {
			totalFrozen= totalFrozen+ numArray1[i];
		}
		for(int i=0; i<size2; i++) {
			totalActive= totalActive+ numArray2[i];
		}
		for(int i=0; i<size3; i++) {
			totalClosed= totalClosed+ numArray3[i];
		}
		for(int i=0; i<size4; i++) {
			totalDeclined= totalDeclined+ numArray4[i];
		}

		double frozenMed= median(numArray1);
		double activeMed= median(numArray2);
		double closedMed= median(numArray3);
		double declinedMed= median(numArray4);

		double frozenStd= std(numArray1);
		double activeStd= std(numArray2);
		double closedStd= std(numArray3);
		double declinedStd= std(numArray4);

		String s = String.valueOf(frozenMed);
		String s1 = String.valueOf(frozenStd);
		String s2 = String.valueOf(activeMed);
		String s3 = String.valueOf(activeStd);
		String s4 = String.valueOf(closedMed);
		String s5 = String.valueOf(closedStd);
		String s6 = String.valueOf(declinedMed);
		String s7 = String.valueOf(declinedStd);
		String s8= String.valueOf(totalFrozen);
		String s9= String.valueOf(totalActive);
		String s10= String.valueOf(totalClosed);
		String s11= String.valueOf(totalDeclined);
		
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
		
		System.out.println("1");
		System.out.println(frq(numArray1));
		System.out.println("2");
		System.out.println(frq(numArray2));
		System.out.println("3");
		System.out.println(frq(numArray3));
		System.out.println("4");
		
//		private final ObservableList<Integer[]> data =FXCollections.observableArrayList(frq(numArray1));
//		cntFrozen.setMinWidth(100);
//        cntFrozen.setCellValueFactory(new PropertyValueFactory<>("numArray1"));
//        disFrozen.setMinWidth(100);
//        disFrozen.setCellValueFactory(new PropertyValueFactory<>("frq(numArray1)"));
//        FrozenTable.setItems();

		cntFrozen.setCellValueFactory(new PropertyValueFactory<Integer, Integer>("count"));
		disFrozen.setCellValueFactory(new PropertyValueFactory<>("freq"));
	    ObservableList data = FXCollections.observableArrayList();
	    FrozenTable.setItems(data);
	    data.add(frq(numArray1));
		



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
		double sd = 0.0;
		
		for (int i = 0; i < Array.length; i++) {
			sum = sum + Array[i];
		}
		
		double avg = sum / (double) Array.length;
		
        for(double num: Array) {
            sd += Math.pow(num - avg, 2);
        }
        
        return Math.sqrt(sd/Array.length);

	}
	
	public Integer[] frq(Integer[] Array) {
		
		Integer[] fr= new Integer[Array.length];
	       int visited = -1;  
	        for(int i = 0; i < Array.length; i++){  
	            int count = 1;  
	            for(int j = i+1; j < Array.length; j++){  
	                if(Array[i] == Array[j]){  
	                    count++;  
	                    //To avoid counting same element again  
                    fr[j] = visited;  
	                }  
	            }  
	            if(fr[i] != visited)  
	                fr[i] = count;  
	        } 
	        return fr;
	}
		
//		hm.clear();
//		for (int i = 0; i < Array.length; i++) {
//			if (hm.containsKey(Array[i]))
//				hm.put(Array[i], hm.get(Array[i]) + 1);
//			else
//				hm.put(Array[i], 1);
//		}
//		return(hm);
//	}

}
