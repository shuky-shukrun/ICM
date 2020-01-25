package unittests;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import entities.Report;
import entities.Report.ReportType;

public class FakeDBConnection implements IDBConnection {
	private List<Report>l=new ArrayList<>();
	public static int i=0;

	// get number of active requests between given dates
    @Override
    public int getAReportDetails(LocalDate from, LocalDate to) {
    	i++;
        return i;
     
    }

	// save the created report
	@Override
	public boolean saveReport(LocalDate from, LocalDate to, ReportType type) {
		
		Report r=new Report();
		r.setStartDate(from);
		r.setEndDate(to);
		r.setTitle(type.toString());
		if(l.contains(r))
			return false;
		l.add(r);
		return true;
		
	}

	// check if a given report is exist
	@Override
	public boolean isExistsReport(LocalDate from, LocalDate to, ReportType type) {
		Report r=new Report();
		r.setStartDate(from);
		r.setEndDate(to);
		r.setTitle(type.toString());
		if(l.contains(r))
			return true;
		return false;
	}
}
