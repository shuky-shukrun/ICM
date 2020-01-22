package unittests;

import java.time.LocalDate;

public interface IDBConnection {
    int getFReportDetails(LocalDate from, LocalDate to);
    int getAReportDetails(LocalDate from, LocalDate to);
    int getCReportDetails(LocalDate from, LocalDate to);
    int getDReportDetails(LocalDate from, LocalDate to);
    int getTReportDetails(LocalDate from, LocalDate to);
}
