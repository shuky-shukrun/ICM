package unittests;

import client.mainWindow.itdCreateReport.ActivityReportController;
import entities.Report.ReportType;

import org.junit.Assert;
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

public class ActivityReportTest {

	EchoServer echoServer;
	ActivityReportController activityReportController;
	List<Integer> activeList;
	Integer totalActive;
	
	@BeforeEach
	public void setUp() throws Exception {
		echoServer = new EchoServer(5555, "fake", "fake", "fake", false);
		activityReportController = new ActivityReportController();
		activeList = new ArrayList<>();
		FakeDBConnection.i=0;
		totalActive = 0;
		for (int i = 0; i < 8; i++) {
			int activeCount = echoServer.getIdbConnection().getAReportDetails(LocalDate.now(), LocalDate.now().plusDays(100));
			activeList.add(activeCount);
			totalActive += activeCount;
		}
	}
	//check if total number of active requests is correct
	@Test
	public void testTotalActiveRequests() {
		assertEquals(36, totalActive);
	}
	//check if median of active requests is correct
	@Test
	public void testMedianOfActiveRequests() {
		int size1 = activeList.size();
		Integer[] numArray1 = new Integer[size1];
		activeList.toArray(numArray1);
		Arrays.sort(numArray1);

		double med = activityReportController.median(numArray1);
		assertEquals(4.5, med);
	}
	//check if standard deviation of active requests is correct
	@Test
	public void testStandardDivisionOfActiveRequests() {
		int size1 = activeList.size();
		Integer[] numArray1 = new Integer[size1];
		activeList.toArray(numArray1);
		Arrays.sort(numArray1);

		double std = activityReportController.std(numArray1);
		assertEquals(2.29128784747792, std);
	}
	//check if median of active requests is correct
	@Test
	public void testMedianOfActiveRequestsTotalIsZero() {
		Integer[] numArray1 = new Integer[1];
		numArray1[0] = 0;
		activeList.toArray(numArray1);
		Arrays.sort(numArray1);

		double median = activityReportController.median(numArray1);
		assertEquals(0.0, median);
	}
	//check if standard deviation of active requests is correct
	@Test
	public void testStandardDivisionOfActiveRequestsTotalIsZero() {
		Integer[] numArray1 = new Integer[1];
		numArray1[0] = 0;
		activeList.toArray(numArray1);
		Arrays.sort(numArray1);

		double std = activityReportController.std(numArray1);
		assertEquals(0.0, std);
	}
	//check if save of activity report correct
	@Test
	public void testSaveActivityReport() {
		boolean flag=echoServer.save(LocalDate.now(), LocalDate.now().plusDays(100), ReportType.Activity_Report);
		assertTrue(flag);
	}	
	//check if the system will not allowed to save the same report 
	@Test
	public void testSaveSameActivityReport() {
		echoServer.save(LocalDate.now(), LocalDate.now().plusDays(100), ReportType.Activity_Report);
		 boolean flag=echoServer.save(LocalDate.now(), LocalDate.now().plusDays(100), ReportType.Activity_Report);
		assertFalse(flag);
	}
	//check if save of performance report correct
	@Test
	public void testSavePreformanceReport() {
		boolean flag=echoServer.save(LocalDate.now(), LocalDate.now().plusDays(100), ReportType.Performance_Report);
		assertTrue(flag);
	}	
	//check if the system will not allowed to save the same report 
	@Test
	public void testSaveSamePreformanceReport() {
		echoServer.save(LocalDate.now(), LocalDate.now().plusDays(100), ReportType.Performance_Report);
		boolean flag=echoServer.save(LocalDate.now(), LocalDate.now().plusDays(100), ReportType.Performance_Report);
		assertFalse(flag);
	}
	//check if save of delays report correct
	@Test
	public void testSaveDelaysReport() {
		boolean flag=echoServer.save(LocalDate.now(), LocalDate.now().plusDays(100), ReportType.Delays_Report);
		assertTrue(flag);
	}	
	//check if the system will not allowed to save the same report 
	@Test
	public void testSaveSameDelaysReport() {
		echoServer.save(LocalDate.now(), LocalDate.now().plusDays(100), ReportType.Delays_Report);
		boolean flag=echoServer.save(LocalDate.now(), LocalDate.now().plusDays(100), ReportType.Delays_Report);
		assertFalse(flag);
	}
	
	//checks if specific activity report exists
	@Test
	public void testExistsActivityReport() {
		echoServer.save(LocalDate.now(), LocalDate.now().plusDays(100), ReportType.Activity_Report);
		boolean flag=echoServer.isExistsReport(LocalDate.now(), LocalDate.now().plusDays(100), ReportType.Activity_Report);
		assertTrue(flag);
	}
	//checks if specific activity report not exists
	@Test
	public void testNotExistsActivityReport() {
		boolean flag=echoServer.isExistsReport(LocalDate.now(), LocalDate.now().plusDays(100), ReportType.Activity_Report);
		assertFalse(flag);
	}
	//checks if specific performance report exists
	@Test
	public void testExistsPerformanceReport() {
		echoServer.save(LocalDate.now(), LocalDate.now().plusDays(100), ReportType.Performance_Report);
		boolean flag=echoServer.isExistsReport(LocalDate.now(), LocalDate.now().plusDays(100), ReportType.Performance_Report);
		assertTrue(flag);
	}
	//checks if specific performance report not exists
	@Test
	public void testNotExistsPerformanceReport() {
		boolean flag=echoServer.isExistsReport(LocalDate.now(), LocalDate.now().plusDays(100), ReportType.Performance_Report);
		assertFalse(flag);
	}
	//checks if specific delays report exists
	@Test
	public void testExistsDelaysReport() {
		echoServer.save(LocalDate.now(), LocalDate.now().plusDays(100), ReportType.Delays_Report);
		boolean flag=echoServer.isExistsReport(LocalDate.now(), LocalDate.now().plusDays(100), ReportType.Delays_Report);
		assertTrue(flag);
	}
	//checks if specific delays report not exists
	@Test
	public void testNotExistsDelaysReport() {
		boolean flag=echoServer.isExistsReport(LocalDate.now(), LocalDate.now().plusDays(100), ReportType.Delays_Report);
		assertFalse(flag);
	}
	//checks if specific performance report not exists when another report saved already
	@Test
	public void testNotExistsDifferentReports() {
		echoServer.save(LocalDate.now(), LocalDate.now().plusDays(10), ReportType.Performance_Report);
		boolean flag=echoServer.isExistsReport(LocalDate.now(), LocalDate.now().plusDays(100), ReportType.Performance_Report);
		assertFalse(flag);
	}

}
