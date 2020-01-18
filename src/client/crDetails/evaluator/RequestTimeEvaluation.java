package client.crDetails.evaluator;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import client.ClientController;
import client.ClientUI;
import client.crDetails.CrDetails;
import client.crDetails.executiveLeader.ExecutiveLeaderButtons;
import common.IcmUtils;
import common.IcmUtils.Scenes;
import entities.Phase;
import entities.Phase.PhaseName;
import entities.Phase.PhaseStatus;
import javafx.beans.binding.BooleanBinding;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.DateCell;
import javafx.scene.control.DatePicker;
import server.ServerService;
import server.ServerService.DatabaseService;
import sun.awt.image.BufImgSurfaceData.ICMColorData;

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
	private Phase newCurrPhase;
	
	/**
	 * initialize the request time dialog
	 */
	public void initialize()
	{
		try {
			clientController = ClientController.getInstance(this);
			if(CrDetails.getCurrRequest().getCurrPhaseName()==PhaseName.EVALUATION)
				newCurrPhase=EvaluatorButtons.getPhase1();
			else if(CrDetails.getCurrRequest().getCurrPhaseName()==PhaseName.EXECUTION)
				newCurrPhase=ExecutiveLeaderButtons.getPhase1();
		} catch (IOException e) {
			e.printStackTrace();
		}
		datePickid.setDayCellFactory(picker -> new DateCell() {
			public void updateItem(LocalDate date, boolean empty) {
				super.updateItem(date, empty);
				LocalDate deadLine = LocalDate.now().minusDays(1);
				
				setDisable(empty || date.compareTo(deadLine) <= 0 );
			}
		});
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
		newCurrPhase.setPhaseStatus(PhaseStatus.TIME_REQUESTED);
		List<Object> l=new ArrayList<Object>();
		
		LocalDate date=datePickid.getValue();
		
		l.add(CrDetails.getCurrRequest().getId());
		l.add(date);
		Phase.PhaseName phase= crDetails.getCurrRequest().getCurrPhaseName();
		System.out.println(phase);
		switch(phase) {
			case EVALUATION:
				ServerService serverService=new ServerService(DatabaseService.Request_Time_Evaluation, l);
				clientController.handleMessageFromClientUI(serverService);
				break;
			case EXECUTION:
				System.out.println("2");
				ServerService serverService1=new ServerService(DatabaseService.Request_Time_EXAMINATION, l);
				clientController.handleMessageFromClientUI(serverService1);
				IcmUtils.getPopUp().close();
				break;
		}
		

	}
	@FXML
	/**
	 * Displays info about why the submit button disabled
	 * @param e-the event when the more information button pressed
	 */
	public void moreInformationEvent(ActionEvent e) {
		if(flagHelp==0)
			info = "empty fields";
		if(flagHelp==1)
			info="not legal date";
		switch(info) {
		case "empty fields":
			IcmUtils.displayInformationMsg(
					"Request phase time Help",
					"No date has been chosen",
					"Please fill the date field.");
			break;
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
			IcmUtils.displayInformationMsg(
					"Time requested",
					"Time requested successfully",
					"Time requested successfully.\n" +
					"Request passed to supervisor for phase time approval.");
		else
			IcmUtils.displayErrorMsg("Time request error", "Time request failed", "Please contact ICM support team.");
		IcmUtils.getPopUp().close();
		

	}

}
