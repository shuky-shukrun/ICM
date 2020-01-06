package client.crDetails.initiator;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import client.ClientController;
import client.ClientUI;
import client.crDetails.CrDetails;
import common.IcmUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.stage.FileChooser;
import server.ServerService;
import server.ServerService.DatabaseService;

public class InitiatorButtons implements ClientUI {
	private ClientController clientController;
    @FXML
    private Button attachFilesButton;
    public void initialize() {
    	try {
			clientController = ClientController.getInstance(this);
			} catch (IOException e) {
			e.printStackTrace();
		}
    }
    @FXML
    public void attachFiles(ActionEvent event) {
    	List<Object>tempL=new ArrayList<>();
    	
    	FileChooser fileCh=new FileChooser();
    	List<File> filesToAttach=fileCh.showOpenMultipleDialog(client.ClientMain.getPrimaryStage());
    	File[]arr=new File[filesToAttach.size()];
    	int i=0;
    	for(File f:filesToAttach) {
    		arr[i]=f;
    		i++;
    	}
    	tempL.add(CrDetails.getCurrRequest().getId());
    	tempL.add(arr);
    	clientController.handleMessageFromClientUI(new ServerService(DatabaseService.Attach_Files,tempL ));
    	
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
