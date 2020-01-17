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
		Delays_Report,
		Custom
	}

	public static ObservableList<ReportType> getAllReportTypes() {
		ObservableList<ReportType> reportTypes = FXCollections.observableArrayList();
		reportTypes.add(ReportType.Activity_Report);
		reportTypes.add(ReportType.Performance_Report);
		reportTypes.add(ReportType.Delays_Report);
		reportTypes.add(ReportType.Custom);

		return reportTypes;
	}

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