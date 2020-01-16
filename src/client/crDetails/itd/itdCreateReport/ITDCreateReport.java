package client.crDetails.itd.itdCreateReport;

import client.ClientUI;
import common.IcmUtils;
import entities.Report;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.DialogPane;
import server.ServerService;

import java.io.IOException;
import java.time.LocalDate;

public class ITDCreateReport implements ClientUI {

    @FXML
    private DialogPane createReportDialogPane;
    @FXML
    private  DatePicker startDateDatePicker;
    @FXML
    private  DatePicker endDateDatePicker;
    @FXML
    private ChoiceBox<Report.ReportType> reportTypeChoiceBox;

    public static LocalDate startDate;
    public static LocalDate endDate;
    
    public static LocalDate getStartDate() {
		return startDate;
	}

	public static void setStartDate(LocalDate startDate) {
		ITDCreateReport.startDate = startDate;
	}

	public static LocalDate getEndDate() {
		return endDate;
	}

	public static void setEndDate(LocalDate endDate) {
		ITDCreateReport.endDate = endDate;
	}

    public  DatePicker getStartDateDatePicker() {
        return startDateDatePicker;
    }

    public  DatePicker getEndDateDatePicker() {
        return endDateDatePicker;
    }

    public ChoiceBox<Report.ReportType> getReportTypeChoiceBox() {
        return reportTypeChoiceBox;
    }


    public void initialize() {

        // add report types to choice box
        reportTypeChoiceBox.setItems(Report.getAllReportTypes());

        // add changeListener to choice box - open new report scene when custom is selected
        reportTypeChoiceBox.valueProperty().addListener(new ChangeListener<Report.ReportType>() {
            @Override
            public void changed(ObservableValue<? extends Report.ReportType> observable, Report.ReportType oldValue, Report.ReportType newValue) {
                if(newValue == Report.ReportType.Custom) {
                    System.out.println("Custom report!");
                }
            }
        });

    }

    public void createReport(LocalDate from, LocalDate to) throws IOException {
        System.out.println("createReportFunc");
        ITDCreateReport.setStartDate(from);
        ITDCreateReport.setEndDate(to);
        switch (reportTypeChoiceBox.getSelectionModel().getSelectedItem()) {
            case A:
                System.out.println("A report created");
                IcmUtils.popUpScene(this, "Activity report", "/client/crDetails/itd/itdCreateReport/activityReportScreen.fxml", 751, 612);
                break;
            case B:
                System.out.println("B report created");
                IcmUtils.popUpScene(this, "Activity report", "/client/crDetails/itd/itdCreateReport/performanceReportScreen.fxml", 481, 320);

                break;
            case C:
                System.out.println("C report created");
                break;
            case Custom:
                System.out.println("Custom report created");
                break;
        }
    }


    @Override
    public void handleMessageFromClientController(ServerService serverService) {

    }

}
