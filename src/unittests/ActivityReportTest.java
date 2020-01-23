package unittests;

import client.mainWindow.itdCreateReport.ActivityReportController;
import org.junit.Assert;
import org.junit.jupiter.api.Test;

import org.junit.jupiter.api.BeforeEach;
import server.EchoServer;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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

	@Test
	public void testTotalActiveRequests() {
		assertEquals(36, totalActive);
	}

	@Test
	public void testMedianOfActiveRequests() {
		int size1 = activeList.size();
		Integer[] numArray1 = new Integer[size1];
		activeList.toArray(numArray1);
		Arrays.sort(numArray1);

		double med = activityReportController.median(numArray1);
		assertEquals(4.5, med);
	}

	@Test
	public void testStandardDivisionOfActiveRequests() {
		int size1 = activeList.size();
		Integer[] numArray1 = new Integer[size1];
		activeList.toArray(numArray1);
		Arrays.sort(numArray1);

		double std = activityReportController.std(numArray1);
		assertEquals(2.29128784747792, std);
	}

	@Test
	public void testMedianOfActiveRequestsTotalIsZero() {
		Integer[] numArray1 = new Integer[1];
		numArray1[0] = 0;
		activeList.toArray(numArray1);
		Arrays.sort(numArray1);

		double median = activityReportController.median(numArray1);
		assertEquals(0.0, median);
	}

	@Test
	public void testStandardDivisionOfActiveRequestsTotalIsZero() {
		Integer[] numArray1 = new Integer[1];
		numArray1[0] = 0;
		activeList.toArray(numArray1);
		Arrays.sort(numArray1);

		double std = activityReportController.std(numArray1);
		assertEquals(0.0, std);
	}
}
