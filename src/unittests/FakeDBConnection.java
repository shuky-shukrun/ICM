package unittests;

import java.time.LocalDate;

public class FakeDBConnection implements IDBConnection {
	public static int i=0;
    @Override
    public int getFReportDetails(LocalDate from, LocalDate to) {
    	i++;
        return i;
    }

    @Override
    public int getAReportDetails(LocalDate from, LocalDate to) {
    	i++;
        return i;
     
    }

    @Override
    public int getCReportDetails(LocalDate from, LocalDate to) {
    	i++;
        return i;
    }

    @Override
    public int getDReportDetails(LocalDate from, LocalDate to) {
    	i++;
        return i;
    }

    @Override
    public int getTReportDetails(LocalDate from, LocalDate to) {
    	i++;
        return i;
    }
}
