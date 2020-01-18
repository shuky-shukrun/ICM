package entities;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Report {

	private LocalDate startDate;
	private LocalDate endDate;
	private String title;
	private int reportId;

	public enum ReportType {
		// TODO: change reports name
		// TODO: add report implementation
		Activity_Report,
		Performance_Report,
		Delays_Report
	}
	/**
	 * gets all reports types
	 * @return obseravable list with all report types
	 */
	public static ObservableList<ReportType> getAllReportTypes() {
		ObservableList<ReportType> reportTypes = FXCollections.observableArrayList();
		reportTypes.add(ReportType.Activity_Report);
		reportTypes.add(ReportType.Performance_Report);
		reportTypes.add(ReportType.Delays_Report);
		return reportTypes;
	}
	/**
	 * gets start date 
	 * @return start date
	 */
	public LocalDate getStartDate() {
		return this.startDate;
	}

	/**
	 * 
	 * @param startDate
	 */
	public void setStartDate(LocalDate startDate) {
		this.startDate = startDate;
	}
	/**
	 * gets end date 
	 * @return end date
	 */
	public LocalDate getEndDate() {
		return this.endDate;
	}

	/**
	 * 
	 * @param endDate
	 */
	public void setEndDate(LocalDate endDate) {
		this.endDate = endDate;
	}
	/**
	 * gets title 
	 * @return title 
	 */
	public String getTitle() {
		return this.title;
	}

	/**
	 * 
	 * @param title
	 */
	public void setTitle(String title) {
		this.title = title;
	}
	/**
	 * gets report id
	 * @return report id 
	 */
	public int getReportId() {
		return this.reportId;
	}

	/**
	 * 
	 * @param reportId
	 */
	public void setReportId(int reportId) {
		this.reportId = reportId;
	}

}