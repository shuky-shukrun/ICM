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
	private ClientController clientController;
    @FXML
    private Button attachFilesButton;
    @FXML
    private Button moreInfo;
    public void initialize() {
    	try {
			clientController = ClientController.getInstance(this);
			moreInfo.setVisible(false);
			if(CrDetails.getCurrRequest().getPhases().get(0).getName() == Phase.PhaseName.CLOSING &&
								CrDetails.getCurrRequest().getPhases().get(0).getPhaseStatus() == Phase.PhaseStatus.DONE)
			{
				attachFilesButton.setDisable(true);
				moreInfo.setVisible(true);
			}
			} catch (IOException e) {
			e.printStackTrace();
		}
    }
    @FXML
    public void attachFiles(ActionEvent event) {
    	List<Object>tempL=new ArrayList<>();
    	FileChooser fileCh=new FileChooser();
    	List<File> filesToAttach=fileCh.showOpenMultipleDialog(client.ClientMain.getPrimaryStage());
    	if(filesToAttach == null)
    		return;

    	File[]arr=new File[filesToAttach.size()];
    	int i=0;
    	for(File f:filesToAttach) {
    		arr[i]=f;
    		i++;
    	}
    	tempL.add(CrDetails.getCurrRequest().getId());
    	tempL.add(arr);
       	clientController.handleMessageFromClientUI(new ServerService(DatabaseService.Attach_Files,tempL ));
    	IcmUtils.displayInformationMsg("attaching files in process...");
    }
    @FXML
    public void moreInfoAction() {
    	IcmUtils.displayInformationMsg("Information message", "this request closed","check your email for more details");
    }
    @Override
    public void handleMessageFromClientController(ServerService serverService) {
    	boolean flag=(Boolean)serverService.getParams().get(0);
    	if(flag==true)
    		IcmUtils.displayConfirmationMsg("Success", "Attach files successfully");
    	else
    		IcmUtils.displayErrorMsg("Error", "Attach files failed");
    }
}
