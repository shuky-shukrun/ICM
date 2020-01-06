package client.crDetails.initiator;

import java.io.File;
import java.util.List;

import client.ClientUI;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.stage.FileChooser;
import server.ServerService;

public class InitiatorButtons implements ClientUI {

    @FXML
    private Button attachFilesButton;

    @FXML
    public void attachFiles(ActionEvent event) {
    	FileChooser fileCh=new FileChooser();
    	List<File> filesToAttach=fileCh.showOpenMultipleDialog(client.ClientMain.getPrimaryStage());
    	
    }
    @Override
    public void handleMessageFromClientController(ServerService serverService) {

    }
}
