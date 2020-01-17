package client.crDetails.itd.itdCreateReport;

import java.io.IOException;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import client.ClientController;
import client.ClientUI;
import entities.Distribution;
import entities.InfoSystem;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import server.ServerService;

public class DelaysReport implements ClientUI {
	
	@FXML
	private TextField medNumber;
	@FXML
	private TextField stdNumber;
	@FXML
	private TextField medDelays;
	@FXML
	private TextField stdDelays;
	@FXML
	private TextField medSystem;
	@FXML
	private TextField stdSystem;
	@FXML
	private TableView<Distribution> numberTable;
	@FXML
	private TableColumn<Distribution, Integer> cntNumber;
	@FXML
	private TableColumn<Distribution, Integer> disNumber;
	@FXML
	private TableView<Distribution> delaysTable;
	@FXML
	private TableColumn<Distribution, Integer> cntDelays;
	@FXML
	private TableColumn<Distribution, Integer> disDelays;
	@FXML
	private TableView<Distribution> systemTable;
	@FXML
	private TableColumn<Distribution, String> infoSystemCol;
	@FXML
	private TableColumn<Distribution, Integer> cntCol;
	private ClientController clientController;
	private ObservableList<Distribution> listNumber;
	private ObservableList<Distribution> listDelays;
	private ObservableList<Distribution> listPerInfoSystem;
	
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
		
	
		listNumber = FXCollections.observableArrayList();
		listDelays = FXCollections.observableArrayList();
		listPerInfoSystem = FXCollections.observableArrayList();
		
		cntNumber.setStyle("-fx-alignment: CENTER");
		disNumber.setStyle("-fx-alignment: CENTER");
		initTableValueFactory(cntNumber, disNumber);
		
		cntDelays.setStyle("-fx-alignment: CENTER");
		disDelays.setStyle("-fx-alignment: CENTER");
		initTableValueFactory(cntDelays, disDelays);
		
		infoSystemCol.setStyle("-fx-alignment: CENTER");
		cntCol.setStyle("-fx-alignment: CENTER");
		initOtherTableValueFactory(infoSystemCol, cntCol);
		
		
		long daysBetween = ChronoUnit.DAYS.between(startDate, endDate);
		long weeksBetween = daysBetween / 7;
		long leftBetween = daysBetween % 7;
		params.add(weeksBetween);
		params.add(leftBetween);
		ServerService getFrozen = new ServerService(ServerService.DatabaseService.Get_Delays_Report_Details, params);
		clientController.handleMessageFromClientUI(getFrozen);

	}


	private void initOtherTableValueFactory(TableColumn<Distribution, String> infoSystemCol,
			TableColumn<Distribution, Integer> cntCol) {
		// TODO Auto-generated method stub
		infoSystemCol.setCellValueFactory(new PropertyValueFactory<>("infoSystem"));
		cntCol.setCellValueFactory(new PropertyValueFactory<>("dis"));
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
	public List<Distribution> countPerInfoSystem(Integer[] Array) {
		List<Distribution>l=new ArrayList<Distribution>();
		l.add(new Distribution(0, 10, InfoSystem.LIBRARY));
		/*HashMap<Integer, Integer> hm = new HashMap<Integer, Integer>();
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
		  */
		return l;

	}
	@Override
	public void handleMessageFromClientController(ServerService serverService) {
		// params is the list of values for statistic
		List<List<Integer>> params = serverService.getParams();
		List delaysCount= params.get(0);
		List durationCount=params.get(1);

		System.out.println(delaysCount.get(0));
		System.out.println(durationCount.get(0));
		
		int size1=delaysCount.size();
		int size2=durationCount.size();
		
		Integer[] numArray1 = new Integer[size1];
		Integer[] numArray2 = new Integer[size2];
		
		delaysCount.toArray(numArray1);
		durationCount.toArray(numArray2);
		
		Arrays.sort(numArray1);
		Arrays.sort(numArray2);
		
		double delaysMed= median(numArray1);
		double durationMed= median(numArray2);
		
		double delaysStd= std(numArray1);
		double durationStd= std(numArray2);
		
		String s = String.valueOf(delaysMed);
		String s1 = String.valueOf(delaysStd);
		String s2 = String.valueOf(durationMed);
		String s3 = String.valueOf(durationStd);
		
		medNumber.setText(s);
		stdNumber.textProperty().set(s1);
		medDelays.textProperty().set(s2);
		stdDelays.textProperty().set(s3);
		
		//number of delays table
		List<Distribution>l=frq(numArray1);
		listNumber.setAll(l);
		numberTable.setItems(listNumber);
		//duration of delays table
		List<Distribution>l1=frq(numArray2);
		listDelays.setAll(l1);
		delaysTable.setItems(listDelays);
		//delays per info system table
		List<Distribution>l2=countPerInfoSystem(numArray2);
		listPerInfoSystem.setAll(l2);
		systemTable.setItems(listPerInfoSystem);
	}


}
