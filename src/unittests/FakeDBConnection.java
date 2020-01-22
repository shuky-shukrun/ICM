package unittests;

import java.time.LocalDate;

public class FakeDBConnection implements IDBConnection {
    @Override
    public int getFReportDetails(LocalDate from, LocalDate to) {
        return 7;
    }

    @Override
    public int getAReportDetails(LocalDate from, LocalDate to) {
        return 5;
    }

    @Override
    public int getCReportDetails(LocalDate from, LocalDate to) {
        return 8;
    }

    @Override
    public int getDReportDetails(LocalDate from, LocalDate to) {
        return 3;
    }

    @Override
    public int getTReportDetails(LocalDate from, LocalDate to) {
        return 10;
    }
}
