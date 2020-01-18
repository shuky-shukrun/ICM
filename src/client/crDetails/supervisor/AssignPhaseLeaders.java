package client.crDetails.supervisor;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import client.ClientController;
import client.ClientUI;
import client.crDetails.CrDetails;
import client.crDetails.supervisor.SupervisorButtons;
import common.IcmUtils;
import entities.ChangeInitiator;
import entities.ChangeRequest;
import entities.IEPhasePosition;
import entities.InfoSystem;
import entities.InformationEngineer;
import entities.Phase;
import entities.Phase.PhaseName;
import entities.Phase.PhaseStatus;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import server.ServerService;

public class AssignPhaseLeaders implements ClientUI {

	@FXML
	private ChoiceBox<ChangeInitiator> evaluationPhaseLeaderChoiceBox;
	@FXML
	private ChoiceBox<ChangeInitiator> evaluatorChoiceBox;
	@FXML
	private ChoiceBox<ChangeInitiator> examinationPhaseLeaderChoiceBox;
	@FXML
	private ChoiceBox<ChangeInitiator> executionPhaseLeaderChoiceBox;
	@FXML
	private ChoiceBox<ChangeInitiator> executiveLeaderChoiceBox;
	@FXML
	private ChoiceBox<ChangeInitiator> validationPhaseLeaderChoiceBox;
	@FXML
	private Button submitButton;
	@FXML
	private Button cancelButton;
	@FXML
	private Label helpLabel;
	@FXML
	private Label titleLabel;
	@FXML
	private Label label1;
	@FXML
	private Label label2;
	@FXML
	private Label pathLabel;
	
	
	private ChangeInitiator initiatorOfCr;
	private ClientController clientController;
	private List<List<ChangeInitiator>> optionalWorkersList = new ArrayList<>();
	private ObservableList<ChangeInitiator> phaseLeaderAndExLeaderDetailsList = FXCollections.observableArrayList();
	private ObservableList<ChangeInitiator> evaluatorDetailsList = FXCollections.observableArrayList();
	private List<IEPhasePosition> newPhaseLeadersAndWorkersList = new ArrayList<>();
    private IEPhasePosition evaluationPhaseLeader= new IEPhasePosition(); 
    private IEPhasePosition evaluator= new IEPhasePosition(); 
    private IEPhasePosition examinationPhaseLeader= new IEPhasePosition(); 
    private IEPhasePosition executionPhaseLeader= new IEPhasePosition();
    private IEPhasePosition executiveLeader= new IEPhasePosition();
    private IEPhasePosition validationPhaseLeader= new IEPhasePosition(); 
    private int crId;
    private Phase newCurrPhase;
    private Phase oldPhase;
    
    /**
     * Initialize assign phase leaders window
     */
	public void initialize() {
		try {
			clientController = ClientController.getInstance(this);

			switch (CrDetails.getCurrRequest().getPhases().get(0).getName()) {
			
			case SUBMITTED:

				//unable submit until all employees are selected
				BooleanBinding test = Bindings.createBooleanBinding(() -> {
					ChangeInitiator evPhaseLeader = evaluationPhaseLeaderChoiceBox.getSelectionModel().getSelectedItem();
					ChangeInitiator ev = evaluatorChoiceBox.getSelectionModel().getSelectedItem();
					ChangeInitiator examPhaseLeader = examinationPhaseLeaderChoiceBox.getSelectionModel().getSelectedItem();
					ChangeInitiator exePhaseLeader = executionPhaseLeaderChoiceBox.getSelectionModel().getSelectedItem();
					ChangeInitiator exe = executiveLeaderChoiceBox.getSelectionModel().getSelectedItem();
					ChangeInitiator valPhaseLeader = validationPhaseLeaderChoiceBox.getSelectionModel().getSelectedItem();
							return (evPhaseLeader == null || examPhaseLeader==null ||exePhaseLeader==null ||exe==null ||valPhaseLeader==null );
		        }, 	evaluationPhaseLeaderChoiceBox.valueProperty(),
						evaluatorChoiceBox.valueProperty(),
						examinationPhaseLeaderChoiceBox.valueProperty(),
						examinationPhaseLeaderChoiceBox.valueProperty(),
						executionPhaseLeaderChoiceBox.valueProperty(),
						executiveLeaderChoiceBox.valueProperty(),
						validationPhaseLeaderChoiceBox.valueProperty()			
		        );
				submitButton.disableProperty().bind(test);

				// Get phase leaders and workers details from DB
				crId=CrDetails.getCurrRequest().getId();
				InfoSystem infoSystem =CrDetails.getCurrRequest().getInfoSystem();
				initiatorOfCr= CrDetails.getCurrRequest().getInitiator();
				InformationEngineer informationEngineer=new InformationEngineer();

				informationEngineer.setDepartment(initiatorOfCr.getDepartment());
				informationEngineer.setEmail(initiatorOfCr.getEmail());
				informationEngineer.setFirstName(initiatorOfCr.getFirstName());
				informationEngineer.setId(initiatorOfCr.getId());
				informationEngineer.setLastName(initiatorOfCr.getLastName());
				informationEngineer.setPassword(initiatorOfCr.getPassword());
				informationEngineer.setPhoneNumber(initiatorOfCr.getPhoneNumber());
				informationEngineer.setPosition(initiatorOfCr.getPosition());
				informationEngineer.setTitle(initiatorOfCr.getTitle());	
				informationEngineer.setManagedSystem(infoSystem); // add infoSystem of CurrRequest to the ChangeInitiator of the request

				helpLabel.setText("please assign phase leaders for change request " +CrDetails.getCurrRequest().getId().toString());
				List<InformationEngineer> ChangeInitiatorList = new ArrayList<>();
				ChangeInitiatorList.add(informationEngineer);
				System.out.printf("%s\n",ChangeInitiatorList);
				ServerService getPhaseLeaders = new ServerService(ServerService.DatabaseService.Get_Phase_Leaders_And_Workers, ChangeInitiatorList);
				clientController.handleMessageFromClientUI(getPhaseLeaders);

				// add Change listeners to Choice Box
				addChangeListener(executionPhaseLeaderChoiceBox, executiveLeaderChoiceBox);
				addChangeListener(executiveLeaderChoiceBox, executionPhaseLeaderChoiceBox );
				addChangeListener(evaluationPhaseLeaderChoiceBox, evaluatorChoiceBox);
				addChangeListener(evaluatorChoiceBox, evaluationPhaseLeaderChoiceBox );

				break;

			default:

				helpLabel.setVisible(false);
				submitButton.setVisible(false);
				label1.setVisible(false);
				label2.setVisible(false);
	            titleLabel.setText("View phase leaders");
	            pathLabel.setText("Home page >Change request details >View phase leaders");
				cancelButton.setText("Close");
				List<ChangeRequest> changeRequestsList =new ArrayList<>();
				changeRequestsList.add(CrDetails.getCurrRequest());
				ServerService getIEPhasePosition = new ServerService(ServerService.DatabaseService.Get_Selected_Phase_Leaders_And_Workers, changeRequestsList);
				clientController.handleMessageFromClientUI(getIEPhasePosition);

				break;
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	
	@FXML
	/**
	 * submit the chosen phase leaders when the submit button pressed and send data to DB.
	 * @param event-submit button pressed event
	 */
	void submitAssignPhaseLeaders(ActionEvent event) {
		
		evaluationPhaseLeader= addDetails(evaluationPhaseLeaderChoiceBox, "EVALUATION", "PHASE_LEADER");
		evaluator= addDetails(evaluatorChoiceBox, "EVALUATION", "EVALUATOR");
		examinationPhaseLeader= addDetails(examinationPhaseLeaderChoiceBox, "EXAMINATION", "PHASE_LEADER");
		executionPhaseLeader= addDetails(executionPhaseLeaderChoiceBox, "EXECUTION", "PHASE_LEADER");
		executiveLeader= addDetails(executiveLeaderChoiceBox, "EXECUTION", "EXECUTIVE_LEADER");
		validationPhaseLeader= addDetails(validationPhaseLeaderChoiceBox, "VALIDATION", "PHASE_LEADER");

		newPhaseLeadersAndWorkersList.add(evaluationPhaseLeader);
		newPhaseLeadersAndWorkersList.add(examinationPhaseLeader);
		newPhaseLeadersAndWorkersList.add(executionPhaseLeader);
		newPhaseLeadersAndWorkersList.add(validationPhaseLeader);
		newPhaseLeadersAndWorkersList.add(evaluator);
		newPhaseLeadersAndWorkersList.add(executiveLeader);
		
		ServerService updatePhaseLeaders = new ServerService(ServerService.DatabaseService.Supervisor_Update_Phase_Leaders_And_Workers, newPhaseLeadersAndWorkersList);
		clientController.handleMessageFromClientUI(updatePhaseLeaders);
	}

	@FXML
	/**
	 * close the assign phase leaders dialog.
	 * @param event-cancel button pressed event
	 */
	void cancelAssignPhaseLeaders(ActionEvent event) {
		IcmUtils.getPopUp().close();
	}
	
	
	@Override
	/**
	 * handle the returned value from server.
	 * @param serverService-ServerService object that the client controller send
	 */
	public void handleMessageFromClientController(ServerService serverService) {
		switch (serverService.getDatabaseService()) {
		case Get_Phase_Leaders_And_Workers:
			optionalWorkersList= serverService.getParams();
			phaseLeaderAndExLeaderDetailsList.setAll(optionalWorkersList.get(0));
			evaluatorDetailsList.setAll(optionalWorkersList.get(1));
			
		evaluationPhaseLeaderChoiceBox.setItems(phaseLeaderAndExLeaderDetailsList);
		examinationPhaseLeaderChoiceBox.setItems(phaseLeaderAndExLeaderDetailsList);
		executionPhaseLeaderChoiceBox.setItems(phaseLeaderAndExLeaderDetailsList);
		validationPhaseLeaderChoiceBox.setItems(phaseLeaderAndExLeaderDetailsList);
		executiveLeaderChoiceBox.setItems(phaseLeaderAndExLeaderDetailsList);
		evaluatorChoiceBox.setItems(evaluatorDetailsList);
		evaluatorChoiceBox.setValue(evaluatorDetailsList.get(0));
			break;

		case Supervisor_Update_Phase_Leaders_And_Workers:
			List<Boolean> update=serverService.getParams();
			boolean checkUpdate= update.get(0);
			if(checkUpdate== true) {
			
				IcmUtils.displayInformationMsg(
						"Phase Leaders assigned",
						"Phase Leaders has been successfully assigned",
						"Evaluation Phase Leader: " +
					    evaluationPhaseLeader.getInformationEngineer().toString() + "\n"+ "Evaluator: " + evaluator.getInformationEngineer().toString() +
					    "\n"+"Examination Phase Leader: " + examinationPhaseLeader.getInformationEngineer().toString() + "\n"+"Execution Phase Leader: " + 
					    executionPhaseLeader.getInformationEngineer().toString() + "\n" +"Executive Leader: " + 
					    executiveLeader.getInformationEngineer().toString() + "\n" +"Validation Phase Leader: " + 
					    validationPhaseLeader.getInformationEngineer().toString() + "\n");
				newCurrPhase=SupervisorButtons.getPhase();
				newCurrPhase = new Phase();
				newCurrPhase.setName(PhaseName.EVALUATION);
				newCurrPhase.setPhaseStatus(PhaseStatus.PHASE_LEADER_ASSIGNED);
				SupervisorButtons.setCurrPhase(newCurrPhase);
				oldPhase=SupervisorButtons.getPhase();
				oldPhase.setName(PhaseName.SUBMITTED);
				List<Phase> phList = new ArrayList<>();
				phList.add(oldPhase);
				ServerService updateExceptionTime1 = new ServerService(ServerService.DatabaseService.Update_Exception_Time, phList);
				clientController.handleMessageFromClientUI(updateExceptionTime1);
				IcmUtils.getPopUp().close();
			}
		break;	
		
		case Get_Selected_Phase_Leaders_And_Workers:
			List<ChangeInitiator> L1 =serverService.getParams();
			
			evaluationPhaseLeaderChoiceBox.setItems(FXCollections.observableArrayList(L1.get(0)));
			evaluationPhaseLeaderChoiceBox.getSelectionModel().select(0);
			
			evaluatorChoiceBox.setItems(FXCollections.observableArrayList(L1.get(1)));
			evaluatorChoiceBox.getSelectionModel().select(0);
			
			examinationPhaseLeaderChoiceBox.setItems(FXCollections.observableArrayList(L1.get(2)));
			examinationPhaseLeaderChoiceBox.getSelectionModel().select(0);
			
			executionPhaseLeaderChoiceBox.setItems(FXCollections.observableArrayList(L1.get(3)));
			executionPhaseLeaderChoiceBox.getSelectionModel().select(0);
			
			executiveLeaderChoiceBox.setItems(FXCollections.observableArrayList(L1.get(4)));
			executiveLeaderChoiceBox.getSelectionModel().select(L1.get(4));
			
			validationPhaseLeaderChoiceBox.setItems(FXCollections.observableArrayList(L1.get(5)));
			validationPhaseLeaderChoiceBox.getSelectionModel().select(0);	
			
			break;	
	}
	}	
	
	/**
     * helper function to add listener to ChoiceBox.
     *
     * @param max_Length the max length of change request id
     * @return EventHandler that can be assign to TextField
     */
	 private void addChangeListener(ChoiceBox src, ChoiceBox a) {
	        src.valueProperty().addListener((observable, oldValue, newValue) -> {
	            if(a.getSelectionModel().getSelectedItem() != null &&
	                    a.getSelectionModel().getSelectedItem().equals(newValue))
	                a.getSelectionModel().clearSelection();
	        });
	    }
	 
	 private IEPhasePosition addDetails (ChoiceBox<ChangeInitiator> choiceBox, String phaseName, String position ) {
		 
		    IEPhasePosition iePhasePosition = new IEPhasePosition(); 
		    iePhasePosition.setCrID(crId);
		    iePhasePosition.setInformationEngineer(choiceBox.getSelectionModel().getSelectedItem());
		    iePhasePosition.setPhaseName(Phase.PhaseName.valueOf(phaseName));
		    iePhasePosition.setPhasePosition(IEPhasePosition.PhasePosition.valueOf(position));
		 
			return iePhasePosition;  
	 }

}
