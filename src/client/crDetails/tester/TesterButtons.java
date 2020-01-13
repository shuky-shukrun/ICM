package client.crDetails.tester;

import java.io.IOException;

import client.ClientUI;
import client.crDetails.CrDetails;
import common.IcmUtils;
import entities.Phase;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import server.ServerService;

public class TesterButtons implements ClientUI {

    
    

    @Override
    public void handleMessageFromClientController(ServerService serverService) {

    }
	@FXML
	private Button setDecisionButton1;

	@FXML
	private Button viewEvaluationReportButton;

	private static Phase currPhase;
	private int flag = 0;

	public void initialize() {

		if (flag == 0) {
			currPhase = CrDetails.getCurrRequest().getPhases().get(0);
			flag = 1;
		}
		if (flag == 1) {
			if (!(currPhase.getName().equals(Phase.PhaseName.VALIDATION))) {
				setDecisionButton1.setDisable(true);
			}
		}

	}

	@FXML
	void showSetDecisionDialog(ActionEvent event) {
		try {

			IcmUtils.popUpScene(this, "Set Decision", "/client/crDetails/tester/setDecision/SetDecision.fxml", 400,
					300);
			initialize();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	@FXML
	void showEvaluationReport(ActionEvent event) throws IOException {

		IcmUtils.popUpScene(this, "EvaluationReport", "/client/crDetails/ccc/viewEvaluationReport.fxml", 600, 632);

	}

	public static void setPhase(Phase NewPhase) {
		currPhase = NewPhase;
	}

	public static Phase getPhase() {
		return currPhase;
	}

}
