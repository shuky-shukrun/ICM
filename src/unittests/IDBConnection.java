package unittests;

import java.time.LocalDate;

import entities.Report;

public interface IDBConnection {
    int getFReportDetails(LocalDate from, LocalDate to);
    int getAReportDetails(LocalDate from, LocalDate to);
    int getCReportDetails(LocalDate from, LocalDate to);
    int getDReportDetails(LocalDate from, LocalDate to);
    int getTReportDetails(LocalDate from, LocalDate to);
    boolean saveReport(LocalDate from,LocalDate to,Report.ReportType type);
    boolean isExistsReport(LocalDate from,LocalDate to,Report.ReportType type);
}
