package client.mainWindow.itdCreateReport;

import client.ClientUI;
import common.IcmUtils;
import entities.Report;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import server.EchoServer;
import server.ServerService;

import java.io.IOException;
import java.time.LocalDate;

public class ITDCreateReport implements ClientUI {

    @FXML
    private DatePicker startDateDatePicker;
    @FXML
    private  DatePicker endDateDatePicker;
    @FXML
    private ChoiceBox<Report.ReportType> reportTypeChoiceBox;

    @FXML
    private Button createButton;
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


    public void initialize() {

        // add report types to choice box
        reportTypeChoiceBox.setItems(Report.getAllReportTypes());

        // disable Create button any field is invalid
        BooleanBinding bb = Bindings.createBooleanBinding(() -> {
                    LocalDate from = startDateDatePicker.getValue();
                    LocalDate to = endDateDatePicker.getValue();
                    Report.ReportType reportType = reportTypeChoiceBox.getSelectionModel().getSelectedItem();

                    // disable, if one selection is missing or from is not smaller than to
                    return (from == null || to == null || (from.compareTo(to) >= 0) || reportType == null);
                }, startDateDatePicker.valueProperty(),
                endDateDatePicker.valueProperty(),
                reportTypeChoiceBox.valueProperty()
        );
        createButton.disableProperty().bind(bb);
    }

    @FXML
    public void createReport() throws IOException {
        System.out.println("createReportFunc");
        ITDCreateReport.setStartDate(startDateDatePicker.getValue());
        ITDCreateReport.setEndDate(endDateDatePicker.getValue());
        switch (reportTypeChoiceBox.getSelectionModel().getSelectedItem()) {
            case Activity_Report:
                System.out.println("A report created");
                IcmUtils.popUpScene(this, "Activity report", "/client/mainWindow/itdCreateReport/ActivityReportScreen.fxml", 751, 612);
                break;
            case Performance_Report:
                System.out.println("B report created");
                IcmUtils.popUpScene(this, "Performance report", "/client/mainWindow/itdCreateReport/PerformanceReportScreen.fxml", 481, 320);

                break;
            case Delays_Report:
                System.out.println("C report created");
                IcmUtils.popUpScene(this, "Delays report", "/client/mainWindow/itdCreateReport/DelaysReport.fxml", 681, 572);

                break;
        }
        IcmUtils.getPopUp().close();
    }


    @Override
    public void handleMessageFromClientController(ServerService serverService) {

    }
}
