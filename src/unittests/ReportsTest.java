package unittests;

import client.mainWindow.itdCreateReport.ActivityReportController;
import entities.Report.ReportType;

import org.junit.jupiter.api.Test;

import org.junit.jupiter.api.BeforeEach;
import server.EchoServer;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class ReportsTest {

	EchoServer echoServer;
	ActivityReportController activityReportController;
	List<Integer> activeRequestsList;
	Integer totalActiveRequests;
	
	@BeforeEach
	public void setUp() throws Exception {
		echoServer = new EchoServer(5555, "fake", "fake", "fake", false);
		activityReportController = new ActivityReportController();
		activeRequestsList = new ArrayList<>();
		FakeDBConnection.i=0;
		totalActiveRequests = 0;
		for (int i = 0; i < 8; i++) {
			int activeCount = echoServer.getIdbConnection().getAReportDetails(LocalDate.now(), LocalDate.now().plusDays(100));
			activeRequestsList.add(activeCount);
			totalActiveRequests += activeCount;
		}
	}
	//check if total number of active requests is correct
	@Test
	public void testTotalActiveRequests() {
		assertEquals(36, totalActiveRequests);
	}
	//check if median of active requests is correct
	@Test
	public void testMedianOfActiveRequests() {
		int numOfWeeks = activeRequestsList.size();
		Integer[] weeksArray = new Integer[numOfWeeks];
		activeRequestsList.toArray(weeksArray);
		Arrays.sort(weeksArray);

		double med = activityReportController.median(weeksArray);
		assertEquals(4.5, med);
	}
	//check if standard deviation of active requests is correct
	@Test
	public void testStandardDivisionOfActiveRequests() {
		int numOfWeeks = activeRequestsList.size();
		Integer[] weeksArray = new Integer[numOfWeeks];
		activeRequestsList.toArray(weeksArray);
		Arrays.sort(weeksArray);

		double std = activityReportController.std(weeksArray);
		assertEquals(2.29128784747792, std);
	}
	//check if median of active requests is correct
	@Test
	public void testMedianOfActiveRequestsTotalIsZero() {
		Integer[] numOfWeeks = new Integer[1];
		numOfWeeks[0] = 0;
		activeRequestsList.toArray(numOfWeeks);
		Arrays.sort(numOfWeeks);

		double median = activityReportController.median(numOfWeeks);
		assertEquals(0.0, median);
	}
	//check if standard deviation of active requests is correct
	@Test
	public void testStandardDivisionOfActiveRequestsTotalIsZero() {
		Integer[] numOfWeeks = new Integer[1];
		numOfWeeks[0] = 0;
		activeRequestsList.toArray(numOfWeeks);
		Arrays.sort(numOfWeeks);

		double std = activityReportController.std(numOfWeeks);
		assertEquals(0.0, std);
	}
	//check if save of activity report correct
	@Test
	public void testSaveActivityReport() {
		boolean reportSaved = echoServer.save(LocalDate.now(), LocalDate.now().plusDays(100), ReportType.Activity_Report);
		assertTrue(reportSaved);
	}	
	//check if the system will not allowed to save the same report 
	@Test
	public void testSaveSameActivityReport() {
		echoServer.save(LocalDate.now(), LocalDate.now().plusDays(100), ReportType.Activity_Report);
		 boolean reportSaved = echoServer.save(LocalDate.now(), LocalDate.now().plusDays(100), ReportType.Activity_Report);
		assertFalse(reportSaved );
	}
	//check if save of performance report correct
	@Test
	public void testSavePerformanceReport() {
		boolean reportSaved = echoServer.save(LocalDate.now(), LocalDate.now().plusDays(100), ReportType.Performance_Report);
		assertTrue(reportSaved );
	}	
	//check if the system will not allowed to save the same report 
	@Test
	public void testSaveSamePerformanceReport() {
		echoServer.save(LocalDate.now(), LocalDate.now().plusDays(100), ReportType.Performance_Report);
		boolean reportSaved = echoServer.save(LocalDate.now(), LocalDate.now().plusDays(100), ReportType.Performance_Report);
		assertFalse(reportSaved );
	}
	//check if save of delays report correct
	@Test
	public void testSaveDelaysReport() {
		boolean reportSaved = echoServer.save(LocalDate.now(), LocalDate.now().plusDays(100), ReportType.Delays_Report);
		assertTrue(reportSaved );
	}	
	//check if the system will not allowed to save the same report 
	@Test
	public void testSaveSameDelaysReport() {
		echoServer.save(LocalDate.now(), LocalDate.now().plusDays(100), ReportType.Delays_Report);
		boolean reportSaved = echoServer.save(LocalDate.now(), LocalDate.now().plusDays(100), ReportType.Delays_Report);
		assertFalse(reportSaved );
	}
	
	//checks if specific activity report exists
	@Test
	public void testExistsActivityReport() {
		echoServer.save(LocalDate.now(), LocalDate.now().plusDays(100), ReportType.Activity_Report);
		boolean reportExist = echoServer.isExistsReport(LocalDate.now(), LocalDate.now().plusDays(100), ReportType.Activity_Report);
		assertTrue(reportExist );
	}
	//checks if specific activity report not exists
	@Test
	public void testNotExistsActivityReport() {
		boolean reportExist = echoServer.isExistsReport(LocalDate.now(), LocalDate.now().plusDays(100), ReportType.Activity_Report);
		assertFalse(reportExist );
	}
	//checks if specific performance report exists
	@Test
	public void testExistsPerformanceReport() {
		echoServer.save(LocalDate.now(), LocalDate.now().plusDays(100), ReportType.Performance_Report);
		boolean reportExist = echoServer.isExistsReport(LocalDate.now(), LocalDate.now().plusDays(100), ReportType.Performance_Report);
		assertTrue(reportExist );
	}
	//checks if specific performance report not exists
	@Test
	public void testNotExistsPerformanceReport() {
		boolean reportExist = echoServer.isExistsReport(LocalDate.now(), LocalDate.now().plusDays(100), ReportType.Performance_Report);
		assertFalse(reportExist );
	}
	//checks if specific delays report exists
	@Test
	public void testExistsDelaysReport() {
		echoServer.save(LocalDate.now(), LocalDate.now().plusDays(100), ReportType.Delays_Report);
		boolean reportExist = echoServer.isExistsReport(LocalDate.now(), LocalDate.now().plusDays(100), ReportType.Delays_Report);
		assertTrue(reportExist );
	}
	//checks if specific delays report not exists
	@Test
	public void testNotExistsDelaysReport() {
		boolean reportExist = echoServer.isExistsReport(LocalDate.now(), LocalDate.now().plusDays(100), ReportType.Delays_Report);
		assertFalse(reportExist );
	}
	//checks if specific performance report not exists when another report saved already
	@Test
	public void testNotExistsDifferentReports() {
		echoServer.save(LocalDate.now(), LocalDate.now().plusDays(10), ReportType.Performance_Report);
		boolean reportExist = echoServer.isExistsReport(LocalDate.now(), LocalDate.now().plusDays(100), ReportType.Performance_Report);
		assertFalse(reportExist);
	}

}
