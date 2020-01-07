package common;

import client.ClientMain;
import client.ClientUI;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;

public class IcmUtils {

	private static Stage popUp;
	
    public enum Scenes {
        Main_Window,
        Login,
        Forgot_Password,
        Main_Window_New,
        Change_Request_Summary,
        Create_Evaluation_Report,
        show_Request_Time_Dialog,
        Itd_Create_Report,
        Extension_Time_Request,
        View_Evaluation_Report
        }

    public static void displayConfirmationMsg(String title, String headerText, String contentText) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(headerText);
        alert.setContentText(contentText);
        alert.showAndWait();
    }

    public static void displayConfirmationMsg(String title, String contentText) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setContentText(contentText);
        alert.showAndWait();
    }

    public static void displayConfirmationMsg(String contentText) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setContentText(contentText);
        alert.showAndWait();
    }

    public static void displayInformationMsg(String title, String headerText, String contentText) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(headerText);
        alert.setContentText(contentText);
        alert.showAndWait();
    }

    public static void displayInformationMsg(String title, String contentText) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setContentText(contentText);
        alert.showAndWait();
    }

    public static void displayInformationMsg(String contentText) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setContentText(contentText);
        alert.showAndWait();
    }


    public static void displayErrorMsg(String title, String headerText, String contentText) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(headerText);
        alert.setContentText(contentText);
        alert.showAndWait();
    }

    public static void displayErrorMsg(String title, String contentText) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setContentText(contentText);
        alert.showAndWait();
    }

    public static void displayErrorMsg(String contentText) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setContentText(contentText);
        alert.showAndWait();
    }

    public static void loadScene(ClientUI clientUI, IcmUtils.Scenes sceneName) throws IOException {
        String sceneTitle;
        String fxmlPath;
        int width;
        int height;

        switch (sceneName) {
            case Login:
                sceneTitle = "ICM - Login";
                fxmlPath = "/client/login/Login.fxml";
                width = 500;
                height = 410;
                break;
           case Main_Window:
                sceneTitle = "ICM Main Window";
                fxmlPath = "/client/mainWindow/MainWindowUI.fxml";
                width = 590;
                height = 565;
                break;
            case Main_Window_New:
                sceneTitle = "ICM Main Window";
                fxmlPath = "/client/mainWindow/MainWindow.fxml";
                width = 1000;
                height = 650;
                break;

            case Change_Request_Summary:
                sceneTitle = "ICM Change Request Summary";
                fxmlPath = "/client/crDetails/CrDetails.fxml";
                width = 1000;
                height = 650;
                break;
       
            case show_Request_Time_Dialog:
            	sceneTitle = "ICM request time dialog";
                fxmlPath = "/client/crDetails/evaluator/TimeRequest.fxml";
                width = 600;
                height = 632;
                break;
                
            case Itd_Create_Report:
                sceneTitle = "Create Reports";
                fxmlPath = "/client/crDetails/CrDetails.fxml";
                width = 1000;
                height = 650;
                break;

            case View_Evaluation_Report:
                sceneTitle = "ICM View Evaluation Report";
                fxmlPath = "/client/crDetails/ccc/viewEvaluationReport.fxml";
                width = 600;
                height = 632;
                break;


            default:
                throw new IOException();
        }

        loadScene(clientUI, sceneTitle, fxmlPath, width, height);
    }

    public static void loadScene(ClientUI clientUI, String sceneTitle, String fxmlPath, int width, int height) throws IOException {
        System.out.println("Loading scene: " + sceneTitle);
        FXMLLoader loader = new FXMLLoader();
        Parent root = loader.load(clientUI.getClass().getResource(fxmlPath));
        Scene scene = new Scene(root, width, height);
        Stage primaryStage = ClientMain.getPrimaryStage();
        primaryStage.setScene(scene);
        primaryStage.setTitle(sceneTitle);
        primaryStage.show();
    }

    public static void loadHomeScene(ClientUI clientUI) throws IOException {
        loadScene(clientUI, "ICM Main Window", "/client/mainWindow/MainWindowUI.fxml", 590, 565);
    }
    
    public static void popUpScene(ClientUI clientUI, String sceneTitle, String fxmlPath, int width, int height) throws IOException {
    	 System.out.println("Loading pop-up scene: " + sceneTitle);
         FXMLLoader loader = new FXMLLoader();
         Parent root = loader.load(clientUI.getClass().getResource(fxmlPath));
         Scene scene = new Scene(root, width, height);
         Stage popUpStage = new Stage();
         popUpStage.setScene(scene);
         popUpStage.setTitle(sceneTitle);
         popUpStage.initModality(Modality.WINDOW_MODAL);
         popUpStage.initOwner(ClientMain.getPrimaryStage());
         popUp=popUpStage;
         popUpStage.showAndWait();
    }
    
    public static void setPopUp (Stage NewPopUp) {
    	IcmUtils.popUp=NewPopUp;
    }
    
    public static Stage getPopUp () {
    	return IcmUtils.popUp;
    }
}
