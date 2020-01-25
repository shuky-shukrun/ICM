package unittests;

import java.time.LocalDate;

import entities.Report;

public interface IDBConnection {
    int getAReportDetails(LocalDate from, LocalDate to);
    boolean saveReport(LocalDate from,LocalDate to,Report.ReportType type);
    boolean isExistsReport(LocalDate from,LocalDate to,Report.ReportType type);
}
