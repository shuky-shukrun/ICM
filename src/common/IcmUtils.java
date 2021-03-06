package common;

import client.ClientMain;
import client.ClientUI;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Optional;

public class IcmUtils {

	private static Stage popUp;
	
    public enum Scenes {
        Login,
        Main_Window,
        Change_Request_Summary
        }
    /**
     * Display confirmation message with title,header text and content text
     * @param title
     * @param headerText
     * @param contentText
     * @return all the options for buttons to press in the pop up
     */
    public static  Optional<ButtonType> displayConfirmationMsg(String title, String headerText, String contentText) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(headerText);
        alert.setContentText(contentText);
        return alert.showAndWait();
    }
    /**
     * Displays information message with title,header text and content text
     * @param title
     * @param headerText
     * @param contentText
     */
    public static void displayInformationMsg(String title, String headerText, String contentText) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(headerText);
        alert.setContentText(contentText);
        alert.showAndWait();
    }
    /**
     * Displays error message with title,header text and content text
     * @param title
     * @param headerText
     * @param contentText
     */
    public static void displayErrorMsg(String title, String headerText, String contentText) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(headerText);
        alert.setContentText(contentText);
        alert.showAndWait();
    }
    /**
     * Loads specific scenes
     * @param clientUI
     * @param sceneName
     * @throws IOException
     */
    public static void loadScene(ClientUI clientUI, IcmUtils.Scenes sceneName) throws IOException {
        String sceneTitle;
        String fxmlPath;
        int width;
        int height;

        switch (sceneName) {
            case Login:
                sceneTitle = "ICM - Login";
                fxmlPath = "/client/login/Login.fxml";
                width = 800;
                height = 500;
                break;
            case Main_Window:
                sceneTitle = "ICM Main Window";
                fxmlPath = "/client/mainWindow/MainWindow.fxml";
                width = 1100;
                height = 840;
                break;

            case Change_Request_Summary:
                sceneTitle = "ICM Change Request Summary";
                fxmlPath = "/client/crDetails/CrDetails.fxml";
                width = 1100;
                height = 840;
                break;
                
            default:
                throw new IOException();
        }

        loadScene(clientUI, sceneTitle, fxmlPath, width, height);
    }
    /**
     * Loads every scene
     * @param clientUI
     * @param sceneTitle
     * @param fxmlPath
     * @param width
     * @param height
     * @throws IOException
     */
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

    /**
     * Loads pop up
     * @param clientUI
     * @param sceneTitle
     * @param fxmlPath
     * @param width
     * @param height
     * @throws IOException
     */
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
         popUpStage.setResizable(false);
         popUp=popUpStage;
         popUpStage.showAndWait();
    }
    /**
     * Sets pop up
     * @param NewPopUp
     */
    public static void setPopUp (Stage NewPopUp) {
    	IcmUtils.popUp=NewPopUp;
    }
    /**
     * Gets pop up
     * @return stage of pop up
     */
    public static Stage getPopUp () {
    	return IcmUtils.popUp;
    }
}
