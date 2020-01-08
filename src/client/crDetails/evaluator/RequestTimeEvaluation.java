package client.crDetails.evaluator;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import client.ClientController;
import client.ClientUI;
import client.crDetails.CrDetails;
import common.IcmUtils;
import entities.Phase;
import javafx.beans.binding.BooleanBinding;
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
	@FXML
	private Button moreInformation;
	
	private String info;
	private int flagHelp;
	private CrDetails crDetails;
	
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
		// disable request time button any field is invalid
		BooleanBinding bb = new BooleanBinding() {
			{
				super.bind( datePickid.valueProperty());
			}

			@Override
			// disable, if one selection is missing or evaluated time is later than the
			// deadline of the phase
			protected boolean computeValue() {
			
				if( datePickid.valueProperty().get() == null)
					flagHelp=0;
				else if(datePickid.valueProperty().get().compareTo(LocalDate.now())<= 0)//check with team
					flagHelp=1;
				else
					flagHelp=2;
				return ( datePickid.getValue() == null|| datePickid.getValue().compareTo(LocalDate.now()) < 0);
			}
		};

		applyButton.disableProperty().bind(bb);
		moreInformation.visibleProperty().bind(bb);
	
		
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
		Phase.PhaseName phase= crDetails.getCurrRequest().getCurrPhaseName();
		switch(phase) {
			case EVALUATION:
				ServerService serverService=new ServerService(DatabaseService.Request_Time_Evaluation, l);
				clientController.handleMessageFromClientUI(serverService);
				break;
		}

	}
	@FXML
	public void moreInformationEvent(ActionEvent e) {
		if(flagHelp==0)
			info = "empty fields";
		if(flagHelp==1)
			info="not legal date";
		switch(info) {
		case "empty fields":
			IcmUtils.displayInformationMsg("Information message", "the date field is empty");
			break;
		case "not legal date":
			IcmUtils.displayInformationMsg("information message", "the date you entered is earlier than today");
		}
	}
	
	@FXML
	/**
	 * Back to the change request summary dialog when cancel button pressed
	 * @param e- cancel button pressed event
	 */
	public void cancelTimeRequest(ActionEvent e) {
		IcmUtils.getPopUp().close();
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
