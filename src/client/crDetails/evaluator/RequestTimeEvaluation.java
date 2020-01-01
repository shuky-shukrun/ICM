package client.crDetails.evaluator;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import client.ClientController;
import client.ClientUI;
import client.crDetails.CrDetails;
import common.IcmUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import server.ServerService;
import server.ServerService.DatabaseService;

public class RequestTimeEvaluation implements ClientUI {
	
	private ClientController clientController;
	@FXML
	private DatePicker datePickid;
	@FXML
	private Button cancelbtn;
	@FXML
	private Button applyButton;
	
	public void initialize()
	{
		try {
			clientController = ClientController.getInstance(this);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@FXML
	public void cancelTimeRequest(ActionEvent e) {
		try {
			IcmUtils.loadScene(this, IcmUtils.Scenes.Change_Request_Summary);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}
	
	@FXML
	public void applyTimeRequest(ActionEvent e) {
		List<Object> l=new ArrayList<Object>();
		LocalDate date=datePickid.getValue();
		l.add(CrDetails.getCurrRequest().getId());
		l.add(date);
		ServerService serverService=new ServerService(DatabaseService.Request_Time_Evaluation, l);
		clientController.handleMessageFromClientUI(serverService);
	}
	@Override
	public void handleMessageFromClientController(ServerService serverService) {
		List<Boolean>list=serverService.getParams();
		if(list.get(0)==true)
			IcmUtils.displayConfirmationMsg("request time success");
		else
			IcmUtils.displayErrorMsg("request time failed");
		

	}

}
