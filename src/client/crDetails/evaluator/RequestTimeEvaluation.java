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
	
	/**
	 * initialize the request time dialog
	 */
	public void initialize()
	{
		try {
			clientController = ClientController.getInstance(this);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@FXML
	/**
	 * Back to the change request summary dialog when cancel button pressed
	 * @param e- cancel button pressed event
	 */
	public void cancelTimeRequest(ActionEvent e) {
		try {
			IcmUtils.loadScene(this, IcmUtils.Scenes.Change_Request_Summary);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}
	
	@FXML
	/**
	 * Request approval for time of evaluation phase when apply button pressed
	 * @param e-apply button pressed event
	 */
	public void applyTimeRequest(ActionEvent e) {
		//create ServerService object with the picked date and the id of the request ,in order to send it to the 
		//client controller 
		List<Object> l=new ArrayList<Object>();
		LocalDate date=datePickid.getValue();
		l.add(CrDetails.getCurrRequest().getId());
		l.add(date);
		ServerService serverService=new ServerService(DatabaseService.Request_Time_Evaluation, l);
		clientController.handleMessageFromClientUI(serverService);
	}
	@Override
	/**
	 * Shows pop-up with the information about if the request time succeed
	 * @param serverService -ServerService object that the client controller send to the client gui
	 */
	public void handleMessageFromClientController(ServerService serverService) {
		List<Boolean>list=serverService.getParams();
		if(list.get(0)==true)
			IcmUtils.displayConfirmationMsg("request time success");
		else
			IcmUtils.displayErrorMsg("request time failed");
		

	}

}
