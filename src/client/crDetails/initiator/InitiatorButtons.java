package client.crDetails.initiator;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import client.ClientController;
import client.ClientUI;
import client.crDetails.CrDetails;
import common.IcmUtils;
import entities.Phase;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.stage.FileChooser;
import server.ServerService;
import server.ServerService.DatabaseService;
import sun.awt.image.BufImgSurfaceData.ICMColorData;

public class InitiatorButtons implements ClientUI {

	@FXML
	private Button moreInfo;

	public void initialize() {
		if (! (CrDetails.getCurrRequest().getPhases().get(0).getName() == Phase.PhaseName.CLOSING
				&& CrDetails.getCurrRequest().getPhases().get(0).getPhaseStatus() == Phase.PhaseStatus.DONE)) {
			moreInfo.setVisible(false);
		}
	}

	@FXML
	public void closedRequestInfoMsg() {
		IcmUtils.displayInformationMsg(
				"Closed Request",
				"Closed Request",
				"This request is closed. " +
				"Check your email for more details.");
	}

	@Override
	public void handleMessageFromClientController(ServerService serverService) {
	}
}
