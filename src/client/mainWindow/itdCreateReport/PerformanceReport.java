package client.mainWindow.itdCreateReport;

import java.io.IOException;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

import client.ClientController;
import client.ClientUI;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import server.ServerService;

public class PerformanceReport implements ClientUI {

	@FXML
	private TextField totalExtension;
	
	private ClientController clientController;

	/**
	 * Initializes the performance report screen
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

		long daysBetween = ChronoUnit.DAYS.between(startDate, endDate);
		long weeksBetween = daysBetween / 7;
		long leftBetween = daysBetween % 7;
		params.add(weeksBetween);
		params.add(leftBetween);
		ServerService getFrozen = new ServerService(ServerService.DatabaseService.Get_Performance_Report_Details, params);
		clientController.handleMessageFromClientUI(getFrozen);


	}
	/**
	 * handle the message that returns from the server
	 */

	@Override
	public void handleMessageFromClientController(ServerService serverService) {
		
		List<List<Integer>> params = serverService.getParams();
		List extensionList= params.get(0);
		List dateList= params.get(1);
		System.out.println(params.get(0));
		System.out.println(params.get(1));
		
		//this size equals to the size of the second list
		int size= extensionList.size();
		long count=0;
		
		for(int i=0; i<size; i++) {
			String s= extensionList.get(i).toString();
			String s1= dateList.get(i).toString();
			LocalDate localDate = LocalDate.parse(s);
			LocalDate localDate1 = LocalDate.parse(s1);
			long daysBetween = ChronoUnit.DAYS.between(localDate1,localDate);
			System.out.println(daysBetween);
			count= count+daysBetween;
			System.out.println(count);
		}
		String s = String.valueOf(count);
		totalExtension.textProperty().set(s);
		
	}

}
