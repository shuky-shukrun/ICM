package server;

import client.crDetails.CrDetails;
import entities.*;

import java.sql.Date;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class DBConnection {
	private static final int BUFFER_SIZE = 4096;
	private Connection sqlConnection;
	private PreparedStatement ps;

	public DBConnection(String url, String username, String password) {
		// Driver definition
		try {
			Class.forName("com.mysql.cj.jdbc.Driver").newInstance();
			System.out.println("Driver definition succeed");
		} catch (Exception ex) {
			/* handle the error */
			System.out.println("Driver definition failed");
			System.exit(1);
		}

		// SQL connection to server
		try {
			sqlConnection = DriverManager.getConnection("jdbc:mysql://" + url + "?serverTimezone=IST", username,
					password);
			System.out.println("SQL connection succeed");

		} catch (SQLException ex) {/* handle any errors */
			System.out.println("SQLException: " + ex.getMessage());
			System.out.println("SQLState: " + ex.getSQLState());
			System.out.println("VendorError: " + ex.getErrorCode());
			System.exit(1);
		}

	}

	public List<ChangeInitiator> login(List<String> params) {
		System.out.println("database received login request for: " + params);
		List<ChangeInitiator> userDetails = new ArrayList<>();

		try {
			PreparedStatement ps = sqlConnection
					.prepareStatement("SELECT * FROM users WHERE IDuser = ? AND password = ?");
			ps.setString(1, params.get(0));
			ps.setString(2, params.get(1));
			ResultSet rs = ps.executeQuery();

			// wrong user name or password
			if (rs.next() == false) {
				System.out.println("user not found");
				return null;
			}
			ChangeInitiator user = new ChangeInitiator();

			user.setId(rs.getInt("IDuser"));
			user.setFirstName(rs.getString("firstName"));
			user.setLastName(rs.getString("lastName"));
			user.setEmail(rs.getString("email"));
			user.setPassword(rs.getString("password"));
			user.setTitle(ChangeInitiator.Title.valueOf(rs.getString("title")));
			user.setPhoneNumber(rs.getString("phone"));
			user.setDepartment(CiDepartment.valueOf(rs.getString("department")));
			String userPosition = rs.getString("position");
			if (userPosition != null)
				user.setPosition(Position.valueOf(userPosition));

			userDetails.add(user);

			ps.close();

		} catch (SQLException e) {
			e.printStackTrace();
		}
		System.out.println("user details returned");
		return userDetails;
	}

	public List<List<ChangeRequest>> getAllRequests(List<ChangeInitiator> userList) {

		List<ChangeRequest> myRequests = new ArrayList<>();
		List<ChangeRequest> inMyTreatmentRequests = new ArrayList<>();
		List<List<ChangeRequest>> allRequests = new ArrayList<>();

		ChangeInitiator currUser = userList.get(0);
		try {

			// create and execute the query
			// the user is the change initiator
			ps = sqlConnection.prepareStatement("SELECT CR.crID, CR.crInfoSystem, CR.crDate, CR.crCurrPhaseName "
					+ "FROM changeRequest CR " + "WHERE CR.crIDuser = ?;");
			ps.setInt(1, currUser.getId());
			// go throw the results and add it to arrayList
			Set<ChangeRequest> tempSet = insertRequestsIntoList(currUser.getId());
			myRequests.addAll(tempSet);
			allRequests.add(myRequests);

			if (currUser.getTitle() != ChangeInitiator.Title.INFOENGINEER)
				return allRequests;

			tempSet = new HashSet<>();
			switch (currUser.getPosition()) {
			case ITD_MANAGER:
				ps = sqlConnection.prepareStatement("SELECT CR.crID, CR.crInfoSystem, CR.crDate, CR.crCurrPhaseName "
						+ "FROM changeRequest CR " + "WHERE CR.crSuspended = 1");
				break;
			case SUPERVISOR:
				ps = sqlConnection.prepareStatement(
						"SELECT CR.crID, CR.crInfoSystem, CR.crDate, CR.crCurrPhaseName " + "FROM changeRequest CR");
				break;
			case CHAIRMAN:
			case CCC:
				ps = sqlConnection.prepareStatement("SELECT CR.crID, CR.crInfoSystem, CR.crDate, CR.crCurrPhaseName "
						+ "FROM changeRequest CR,  ieInPhase IE " + "WHERE CR.crCurrPhaseName = 'EXAMINATION' "
						+ "OR (CR.crCurrPhaseName = IE.iePhaseName AND " + "IE.iePhasePosition = 'TESTER')");
				break;
			case REGULAR:
				ps = sqlConnection.prepareStatement("SELECT CR.crID, CR.crInfoSystem, CR.crDate, CR.crCurrPhaseName "
						+ "FROM changeRequest CR, ieInPhase IE " + "WHERE CR.crID = IE.crID AND "
						+ "CR.crCurrPhaseName = IE.iePhaseName AND " + "IE.IDieInPhase = ?");
				ps.setInt(1, currUser.getId());
				break;
			}

			// get requests where the user has any position
			tempSet.addAll(insertRequestsIntoList(currUser.getId()));
			inMyTreatmentRequests.addAll(tempSet);

			allRequests.add(inMyTreatmentRequests);

		} catch (SQLException e) {
			e.printStackTrace();
		}
		return allRequests;
	}

	public void updateRequestDetails(List<String> requirementList) {
		try {
			// create and execute the query
			PreparedStatement ps = sqlConnection.prepareStatement("UPDATE Requirement SET rStatus=? WHERE id=?");
			ps.setString(1, requirementList.get(0));
			ps.setInt(2, Integer.parseInt(requirementList.get(1)));
			ps.executeUpdate();
			System.out.println("status updated");
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	// helper function for getAllRequests()
	private Set<ChangeRequest> insertRequestsIntoList(int userId) throws SQLException {
		ResultSet rs = ps.executeQuery();

		Set<ChangeRequest> requestSet = new HashSet<>();
		rs.beforeFirst();
		while (rs.next()) {
			ChangeRequest row = new ChangeRequest();
			row.setId(rs.getInt("crID"));
			row.setInfoSystem(InfoSystem.valueOf(rs.getString("crInfoSystem")));
			row.setDate(rs.getDate("crDate").toLocalDate());
			row.setCurrPhaseName(Phase.PhaseName.valueOf(rs.getString("crCurrPhaseName")));
			requestSet.add(row);
			System.out.println(row);
		}
		ps.close();
		return requestSet;
	}

	/**
	 * This method handles any messages received from the client.
	 *
	 * @param params [0] current user ID.
	 * @param params [1] change request ID.
	 */
	public List<ChangeRequest> getRequestDetails(List<Integer> params) {
		ChangeRequest cr = new ChangeRequest();
		List<Phase> crPhaseList = new ArrayList<>();
		List<ChangeRequest> crList = new ArrayList<>();

		try {
			// get request basic info
			ps = sqlConnection.prepareStatement("SELECT * FROM changeRequest WHERE crID = ?");
			ps.setInt(1, params.get(1));

			ResultSet rs = ps.executeQuery();
			rs.beforeFirst();
			rs.next();

			ChangeInitiator initiator = new ChangeInitiator();
			cr.setId(rs.getInt("crID"));
			cr.setInfoSystem(InfoSystem.valueOf(rs.getString("crInfoSystem")));
			cr.setInitiator(initiator);
			initiator.setId(rs.getInt("crIDuser"));
			cr.setDate(rs.getDate("crDate").toLocalDate());
			cr.setCurrState(rs.getString("crCurrState"));
			cr.setRequestedChange(rs.getString("crRequestedChange"));
			cr.setReasonForChange(rs.getString("crReasonForChange"));
			cr.setComment(rs.getString("crComments"));
			cr.setCurrPhaseName(Phase.PhaseName.valueOf(rs.getString("crCurrPhaseName")));
			cr.setSuspended(rs.getBoolean("crSuspended"));

			ps.close();

			// get request current phase
			ps = sqlConnection.prepareStatement("SELECT * FROM phase WHERE phIDChangeRequest = ? AND phPhaseName = ?");
			ps.setInt(1, cr.getId());
			ps.setString(2, cr.getCurrPhaseName().toString());

			rs = ps.executeQuery();
			rs.beforeFirst();
			rs.next();

			Phase currPhase = new Phase();

			currPhase.setChangeRequestId(cr.getId());
			currPhase.setName(cr.getCurrPhaseName());
			currPhase.setDeadLine(rs.getDate("phDeadLine").toLocalDate());
			currPhase.setPhaseStatus(Phase.PhaseStatus.valueOf(rs.getString("phStatus")));
			currPhase.setExtensionRequest(rs.getBoolean("phExtensionRequestDecision"));
			// TODO: handle phExtensionRequestDecision
//            Date date = rs.getDate("phExceptionTime");
//            if(date != null) {
//                LocalDate exceptionDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
//                currPhase.setExceptionTime(exceptionDate);
//           }

			crPhaseList.add(currPhase);
			ps.close();

			// get Information Engineer Phase Position for the current user
			ps = sqlConnection.prepareStatement("SELECT * FROM ieInPhase WHERE crID = ? AND iePhaseName = ?");
			ps.setInt(1, cr.getId());
			ps.setString(2, cr.getCurrPhaseName().toString());

			Map<IEPhasePosition.PhasePosition, IEPhasePosition> iePhasePositionMap = new HashMap<>();

			rs = ps.executeQuery();
			rs.beforeFirst();
			while (rs.next()) {
				IEPhasePosition iePhasePosition = new IEPhasePosition();
				iePhasePosition.setInformationEngineer(new InformationEngineer());
				iePhasePosition.getInformationEngineer().setId(rs.getInt("IDieInPhase"));
				iePhasePosition.setCrID(params.get(1));
				iePhasePosition.setPhaseName(currPhase.getName());
				iePhasePosition
						.setPhasePosition(IEPhasePosition.PhasePosition.valueOf(rs.getString("iePhasePosition")));

				iePhasePositionMap.put(iePhasePosition.getPhasePosition(), iePhasePosition);
			}

			currPhase.setIePhasePosition(iePhasePositionMap);
			cr.setPhases(crPhaseList);
			crList.add(cr);

			ps.close();

			// get initiator details
			ps = sqlConnection.prepareStatement("SELECT * FROM users WHERE IDuser = ?");
			ps.setInt(1, cr.getInitiator().getId());
			rs = ps.executeQuery();
			rs.beforeFirst();
			rs.next();

			initiator.setFirstName(rs.getString("firstName"));
			initiator.setLastName(rs.getString("lastName"));
			initiator.setEmail(rs.getString("email"));
			ps.close();

			System.out.println("database got leader");

		} catch (SQLException e) {
			e.printStackTrace();
		}
		return crList;
	}

	public List<Phase> getPhaseDetails(List<ChangeRequest> crList) {

		ChangeRequest currRequest = new ChangeRequest();
		Phase currPhase = new Phase();
		List<Phase> phases = new ArrayList<>();
		currRequest = crList.get(0);
		System.out.println(currRequest);
		// System.out.println(currRequest.getId());
		// System.out.println(currRequest.getCurrPhaseName().toString());

		try {
			PreparedStatement ps = sqlConnection
					.prepareStatement("SELECT * FROM phase WHERE phIDChangeRequest = ? AND phPhaseName = ?");
			ps.setInt(1, currRequest.getId());
			ps.setString(2, currRequest.getCurrPhaseName().toString());

			ResultSet rs = ps.executeQuery();
			rs.beforeFirst();
			rs.next();

			currPhase.setChangeRequestId(currRequest.getId());
			currPhase.setName(currRequest.getCurrPhaseName());
			currPhase.setDeadLine(rs.getDate("phDeadLine").toLocalDate());
			currPhase.setPhaseStatus(Phase.PhaseStatus.valueOf(rs.getString("phStatus")));
			currPhase.setExtensionRequest(rs.getBoolean("phExtensionRequestDecision"));

			phases.add(currPhase);
			ps.close();

		} catch (SQLException e) {
			e.printStackTrace();
		}
		return phases;
	}

	public List<Boolean> updatePhaseExtensionTime(List<Phase> pList) {

		List<Boolean> updateList = new ArrayList<>();
		boolean update = false;
		Phase currPhase = new Phase();
		currPhase = pList.get(0);
		System.out.println(currPhase);
		java.util.Date date = Date
				.from(currPhase.getTimeExtensionRequest().atStartOfDay(ZoneId.systemDefault()).toInstant());
		java.sql.Date sqlDate = new java.sql.Date(date.getTime());

		try {
			PreparedStatement ps = sqlConnection.prepareStatement(
					"UPDATE cbaricmy_ICM.phase SET phTimeExtensionRequest=?,phStatus=?,phTimeExtensionDescription=? WHERE phIDChangeRequest = ? AND phPhaseName = ?");
			ps.setDate(1, sqlDate);
			ps.setString(2, currPhase.getPhaseStatus().toString());
			ps.setString(3, currPhase.getDescription());
			ps.setInt(4, currPhase.getChangeRequestId());
			ps.setString(5, currPhase.getName().toString());

			// System.out.println(sqlDate+ " " +currPhase.getPhaseStatus().toString()+ "
			// "+currPhase.isExtensionRequest());
			// System.out.println(currPhase.getChangeRequestId()+" "+
			// currPhase.getName().toString());

			ps.executeUpdate();
			ps.close();
			System.out.println("phase extension updated");
			update = true;
			updateList.add(update);

		} catch (SQLException e) {
			e.printStackTrace();
		}
		return updateList;

	}

	public List<Boolean> createEvaluationReport(List<String> requirementList1) {
		boolean flag = false;
		List<Boolean> l = new ArrayList<Boolean>();
		// insert new evaluation report to db
		try {
			System.out.println("insert new evaluation report");
			PreparedStatement ps = sqlConnection.prepareStatement(
					"INSERT INTO evaluationReport(cRequestID,infoSystem,requestedChange,expectedResult,risksAndConstraints,EvaluatedTime) VALUES(?,?,?,?,?,?)");
			ps.setInt(1, Integer.parseInt(requirementList1.get(0)));
			ps.setString(2, requirementList1.get(1));
			ps.setString(3, requirementList1.get(2));
			ps.setString(4, requirementList1.get(3));
			ps.setString(5, requirementList1.get(4));
			ps.setDate(6, Date.valueOf(requirementList1.get(5)));
			ps.executeUpdate();
			flag = true;
			l.add(flag);
		} catch (SQLException e) {
			System.out.println(e.getMessage());
			flag = false;
			l.add(flag);
			e.printStackTrace();
		}
		// insert examination phase to specific request with status phase leader
		// assigned
		try {
			// when updated-PreparedStatement ps=sqlConnection.prepareStatement("UPDATE
			// phase SET phStatus='IN_PROCESS' where phIDChangeRequest=? AND phDeadline=?");
			PreparedStatement ps = sqlConnection
					.prepareStatement("INSERT INTO phase VALUES(?,'EXAMINATION',?,'IN_PROCESS',null,null,null,0)");// delete
																													// when
																													// updated
			// stay when updated!!!
			ps.setInt(1, Integer.parseInt(requirementList1.get(0)));// id
			PreparedStatement ps1 = sqlConnection
					.prepareStatement("SELECT phDeadline FROM phase where phIDChangeRequest=?");
			ps1.setInt(1, Integer.parseInt(requirementList1.get(0)));
			ResultSet rs = ps1.executeQuery();
			rs.next();
			Date d = rs.getDate(1);
			Calendar c = Calendar.getInstance();
			c.setTime(d);
			c.add(Calendar.DATE, 7);
			d = new Date(c.getTimeInMillis());
			ps.setDate(2, d);
			ps.executeUpdate();
			flag = true;
			l.add(flag);
		} catch (SQLException e) {
			System.out.println(e.getMessage());
			flag = false;
			l.add(flag);
			e.printStackTrace();
		}
		// update phase of specific request to examination in change request table
		try {
			System.out.println("update current phase of request to examination");
			PreparedStatement ps1 = sqlConnection
					.prepareStatement("UPDATE changeRequest SET crCurrPhaseName = 'EXAMINATION' WHERE crID = ?");
			ps1.setInt(1, Integer.parseInt(requirementList1.get(0)));
			ps1.executeUpdate();
			flag = true;
			l.add(flag);
		} catch (SQLException e) {
			flag = false;
			l.add(flag);
			e.printStackTrace();
		}
		// update evaluation phase of specific request status to done
		try {
			PreparedStatement ps1 = sqlConnection.prepareStatement(
					"UPDATE phase SET phStatus = 'DONE' WHERE phIDChangeRequest = ? AND phPhaseName='EVALUATION'");
			ps1.setInt(1, Integer.parseInt(requirementList1.get(0)));
			ps1.executeUpdate();
			flag = true;
			l.add(flag);
		} catch (SQLException e) {
			flag = false;
			l.add(flag);
			e.printStackTrace();
		}

		return l;

	}

	public List<Boolean> requestTimeEvaluation(List<Object> requestTimeDetails) {
		List<Boolean> list = new ArrayList<Boolean>();
		try {
			PreparedStatement ps = sqlConnection.prepareStatement(
					"UPDATE cbaricmy_ICM.phase SET phDeadline = ? ,phStatus='TIME_REQUESTED' WHERE phIDChangeRequest =?");
			ps.setInt(2, (int) requestTimeDetails.get(0));
			ps.setDate(1, Date.valueOf((LocalDate) requestTimeDetails.get(1)));
			ps.executeUpdate();
			list.add(true);

		} catch (SQLException e) {
			System.out.println(e.getMessage());
			list.add(false);
			e.printStackTrace();
		}
		return list;
	}

	public List<Object> forgotPasswordRequest(List<String> params) {
		List<Object> l = new ArrayList<Object>();
		try {
			PreparedStatement ps = sqlConnection.prepareStatement(
					"SELECT IDuser,firstName,lastName,password FROM cbaricmy_ICM.users where email=?");
			ps.setString(1, params.get(0));
			ResultSet rs = ps.executeQuery();

			if (rs.next()) {
				l.add(true);
				l.add(rs.getInt("IDUser"));
				l.add(rs.getString("firstName"));
				l.add(rs.getString("lastName"));
				l.add(rs.getString("password"));
				l.add(params.get(0));
			} else
				l.add(false);

		} catch (SQLException e) {

			System.out.println(e.getMessage());
			e.printStackTrace();
		}
		return l;
	}

	public void addNewRequest(ChangeRequest newRequest) {
		System.out.println("Database handle addNewRequest");
		// insert request
		try {
			ps = sqlConnection.prepareStatement("INSERT INTO changeRequest "
					+ "(crIDuser, crInfoSystem, crCurrState, crRequestedChange, crReasonForChange, "
					+ "crComments, crDate, crCurrPhaseName, crSuspended) " + "VALUE (?,?,?,?,?,?,?,?, 0)");
			ps.setInt(1, newRequest.getInitiator().getId());
			ps.setString(2, newRequest.getInfoSystem().toString());
			ps.setString(3, newRequest.getCurrState());
			ps.setString(4, newRequest.getRequestedChange());
			ps.setString(5, newRequest.getReasonForChange());
			ps.setString(6, newRequest.getComment());
			ps.setDate(7, java.sql.Date.valueOf(newRequest.getDate()));
			ps.setString(8, newRequest.getCurrPhaseName().toString());
			System.out.println("Database insert request");
			ps.executeUpdate();
			ps.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}

		try {
			ps = sqlConnection.prepareStatement("SELECT MAX(crID) FROM changeRequest");
			ResultSet rs = ps.executeQuery();
			rs.next();
			newRequest.setId(rs.getInt(1));
			System.out.println("get request id: " + newRequest.getId());

			ps.close();

		} catch (SQLException e) {
			e.printStackTrace();
		}

		// insert phase
		try {
			ps = sqlConnection.prepareStatement("INSERT INTO phase "
					+ "(phIDChangeRequest, phPhaseName, phDeadline, phStatus) " + "VALUE (?,?,?,?)");
			ps.setInt(1, newRequest.getId());
			ps.setString(2, newRequest.getCurrPhaseName().toString());
			ps.setDate(3, java.sql.Date.valueOf(newRequest.getDate().plusDays(7)));
			ps.setString(4, newRequest.getPhases().get(0).getPhaseStatus().toString());

			ps.executeUpdate();
			ps.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		// insert IEinPhase
		try {
			ps = sqlConnection.prepareStatement("INSERT INTO ieInPhase "
					+ "(IDieInPhase, crID, iePhaseName, iePhasePosition) " + "VALUE (?,?,?,?)");
			ps.setInt(1, 1);
			ps.setInt(2, newRequest.getId());
			ps.setString(3, newRequest.getCurrPhaseName().toString());
			ps.setString(4, IEPhasePosition.PhasePosition.PHASE_LEADER.toString());

			ps.executeUpdate();
			ps.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public List<EvaluationReport> getEvaluationReportDetails(List<Integer> params) {
		System.out.println("good");
		EvaluationReport e = new EvaluationReport();
		List<EvaluationReport> evRptDetails = new ArrayList<>();

		PreparedStatement ps;
		try {

			ps = sqlConnection.prepareStatement("SELECT * FROM evaluationReport WHERE cRequestId = ?");
			System.out.println("very good1");
			ps.setInt(1, params.get(0));
			System.out.println("very good2");

			ResultSet rs = ps.executeQuery();
			rs.next();
			System.out.println("very very good");
			// eReport = new EvaluationReport();
			e.setInfoSystem(InfoSystem.valueOf(rs.getString("infoSystem")));
			System.out.println("very very good1");
			e.setRequiredChange(rs.getString("requestedChange"));
			System.out.println("very very good2");
			e.setExpectedResult(rs.getString("expectedResult"));
			System.out.println("very very good3");
			e.setRisksAndConstraints(rs.getString("risksAndConstraints"));
			System.out.println("very very good4");
			e.setEvaluatedTime(rs.getDate("EvaluatedTime").toLocalDate());
			System.out.println("very very very good");
			ps.close();
			System.out.println("database got leader");

			evRptDetails.add(e);

		} catch (SQLException e1) {
			e1.printStackTrace();
		}

		return evRptDetails;
	}

	public List<Boolean> existsEvaluationReport(List<Integer> params) {
		int count = 0;
		List<Boolean> l = new ArrayList<Boolean>();
		PreparedStatement stmt3;
		try {
			stmt3 = sqlConnection.prepareStatement("SELECT COUNT(*) As count FROM evaluationReport where cRequestId=?");
			stmt3.setInt(1, params.get(0));
			ResultSet rs3 = stmt3.executeQuery();
			while (rs3.next()) {
				count = rs3.getInt("count");
			}
			if (count >= 1)
				l.add(true);
			else
				l.add(false);

		} catch (SQLException e) {
			e.printStackTrace();
		}
		return l;

	}

	public void downloadFiles(int id) {
		
		
		
		ResultSet rs = null;
		List<File> l = new ArrayList<File>();
		String selectSQL = "SELECT fileName,file FROM files where CrID=23";
		PreparedStatement pstmt=null;
		try {
			
			pstmt = sqlConnection.prepareStatement(selectSQL);
			rs = pstmt.executeQuery();
			
			//pstmt.setInt(1, CrDetails.getCurrRequest().getId());
			while (rs.next()) {
		
				File file = new File(rs.getString("fileName"));
				l.add(file);
			
			}
			
			createZipFromMultipleFiles("C:\\stam", l);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void createZipFromMultipleFiles(String zipName, List<File> srcFiles) {
		try {
			System.out.println(srcFiles.toString());
			// create byte buffer
			byte[] buffer = new byte[102400];

			FileOutputStream fos = new FileOutputStream(zipName);

			ZipOutputStream zos = new ZipOutputStream(fos);

			for (int i = 0; i < srcFiles.size(); i++) {

				File srcFile = srcFiles.get(i);
				FileInputStream fis = new FileInputStream(zipName);
			

				// begin writing a new ZIP entry, positions the stream to the start of the entry
				// data
				zos.putNextEntry(new ZipEntry(srcFile.getName()));

				int length;
				
				while ((length = fis.read(buffer)) > 0) {
					zos.write(buffer, 0, length);
				}

				zos.closeEntry();

				// close the InputStream
				fis.close();

			}

			// close the ZipOutputStream
			zos.close();
			System.out.println("Succeed");

		} catch (IOException ioe) {
			System.out.println("Error creating zip file: " + ioe);
		}
	}

}
