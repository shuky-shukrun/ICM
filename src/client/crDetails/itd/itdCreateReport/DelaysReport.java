package client.crDetails.itd.itdCreateReport;

import java.io.IOException;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import client.ClientController;
import client.ClientUI;
import javafx.fxml.FXML;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
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
	private TableView<Integer> numberTable;
	@FXML
	private TableView<Integer> delaysTable;
	@FXML
	private TableView<Integer> systemTable;
	
	private ClientController clientController;

	
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
		ServerService getFrozen = new ServerService(ServerService.DatabaseService.Get_Delays_Report_Details, params);
		clientController.handleMessageFromClientUI(getFrozen);

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
		System.out.println("ronit"+delaysMed);
		
		double delaysStd= std(numArray1);
		double durationStd= std(numArray2);
		
		String s = String.valueOf(delaysMed);
		String s1 = String.valueOf(delaysStd);
		String s2 = String.valueOf(durationMed);
		String s3 = String.valueOf(durationStd);
		System.out.println(s);
		
		medNumber.setText(s);
		medNumber.setDisable(true);
		stdNumber.textProperty().set(s1);
		stdNumber.setDisable(true);
		medDelays.textProperty().set(s2);
		medDelays.setDisable(true);
		stdDelays.textProperty().set(s3);
		stdDelays.setDisable(true);
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

}
