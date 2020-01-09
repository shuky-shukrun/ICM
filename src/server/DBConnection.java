package server;

import client.crDetails.CrDetails;
import entities.*;
import entities.Phase.PhaseStatus;

import java.io.IOException;
import java.sql.Date;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
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
			Date deadLine = rs.getDate("phDeadLine");
			if(deadLine != null) {
				currPhase.setDeadLine(deadLine.toLocalDate());
			}
			currPhase.setPhaseStatus(Phase.PhaseStatus.valueOf(rs.getString("phStatus")));
			currPhase.setExtensionRequest(rs.getBoolean("phExtensionRequestDecision"));
			currPhase.setSetDecisionDescription(rs.getString("phSetDecisionDescription"));//tom add
            Date date = rs.getDate("phExceptionTime");
            if(date != null) {
                currPhase.setExceptionTime(date.toLocalDate());
           }

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
		// update examination phase to specific request with status phase leader
		// assigned
		try {
			PreparedStatement ps = sqlConnection.prepareStatement(
					"UPDATE phase SET phStatus='IN_PROCESS',phDeadline=? where phIDChangeRequest=? AND phPhaseName='EXAMINATION'");
			ps.setInt(2, Integer.parseInt(requirementList1.get(0)));// id
			PreparedStatement ps1 = sqlConnection.prepareStatement(
					"SELECT phDeadline FROM phase where phIDChangeRequest=? AND phPhaseName='EVALUATION'");
			ps1.setInt(1, Integer.parseInt(requirementList1.get(0)));
			ResultSet rs = ps1.executeQuery();
			rs.next();
			Date d = rs.getDate(1);
			Calendar c = Calendar.getInstance();
			c.setTime(d);
			c.add(Calendar.DATE, 7);
			d = new Date(c.getTimeInMillis());
			ps.setDate(1, d);
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
					"UPDATE cbaricmy_ICM.phase SET phDeadline = ? ,phStatus='TIME_REQUESTED' WHERE phIDChangeRequest =? and phPhaseName='EVALUATION'");
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

	public void addNewRequest(ChangeRequest newRequest) throws IOException {
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
			throw new IOException(e);
		}

		try {
			ps = sqlConnection.prepareStatement("SELECT MAX(crID) FROM changeRequest");
			ResultSet rs = ps.executeQuery();
			rs.next();
			newRequest.setId(rs.getInt(1));
			System.out.println("get request id: " + newRequest.getId());

			ps.close();

		} catch (SQLException e) {
			throw new IOException(e);
		}

		// insert SUBMITTED phase
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
			throw new IOException(e);
		}

		// insert all other phases
		try {
			ps = sqlConnection.prepareStatement("INSERT INTO phase " + "(phIDChangeRequest, phPhaseName, phStatus) "
					+ "VALUES (?,?,?), (?,?,?), (?,?,?), (?,?,?), (?,?,?)");

			ps.setInt(1, newRequest.getId());
			ps.setString(2, Phase.PhaseName.EXAMINATION.toString());
			ps.setString(3, Phase.PhaseStatus.SUBMITTED.toString());

			ps.setInt(4, newRequest.getId());
			ps.setString(5, Phase.PhaseName.EXECUTION.toString());
			ps.setString(6, Phase.PhaseStatus.SUBMITTED.toString());

			ps.setInt(7, newRequest.getId());
			ps.setString(8, Phase.PhaseName.VALIDATION.toString());
			ps.setString(9, Phase.PhaseStatus.SUBMITTED.toString());

			ps.setInt(10, newRequest.getId());
			ps.setString(11, Phase.PhaseName.CLOSING.toString());
			ps.setString(12, Phase.PhaseStatus.SUBMITTED.toString());

			ps.setInt(13, newRequest.getId());
			ps.setString(14, Phase.PhaseName.EVALUATION.toString());
			ps.setString(15, Phase.PhaseStatus.SUBMITTED.toString());

			ps.executeUpdate();
			ps.close();
		} catch (SQLException e) {
			throw new IOException(e);
		}
		// enter the files to files table
		if (newRequest.getFiles() != null) {
			uploadFiles(newRequest.getId(), newRequest.getFiles());
		}
	}

	public	boolean uploadFiles(int id, File[] listParams) {
		System.out.println("server upload given files");
		int i;

		for (i = 0; i < listParams.length; i++) {
			uploadFile(id, listParams[i].getName(), listParams[i].getPath());
		}
		System.out.println("success upload");
		return true;
	}

	private boolean uploadFile(int id, String fileName, String filePath) {
		String sql = "INSERT INTO files values (?,?,?,?)";
		try {
			PreparedStatement statement = sqlConnection.prepareStatement(sql);
			statement.setInt(1, id);
			statement.setString(2, fileName);
			statement.setString(3, filePath);
			File file = new File(filePath);
			FileInputStream input = new FileInputStream(file);
			statement.setBinaryStream(4, input);
			statement.executeUpdate();
			return true;
		} catch (SQLException e) { // TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
		
	}

	public List<EvaluationReport> getEvaluationReportDetails(List<Integer> params) {

		EvaluationReport e = new EvaluationReport();
		List<EvaluationReport> evRptDetails = new ArrayList<>();

		PreparedStatement ps;
		try {

			ps = sqlConnection.prepareStatement("SELECT * FROM evaluationReport WHERE cRequestId = ?");
			ps.setInt(1, params.get(0));
			ResultSet rs = ps.executeQuery();
			rs.next();
			e.setInfoSystem(InfoSystem.valueOf(rs.getString("infoSystem")));
			e.setRequiredChange(rs.getString("requestedChange"));
			e.setExpectedResult(rs.getString("expectedResult"));
			e.setRisksAndConstraints(rs.getString("risksAndConstraints"));
			e.setEvaluatedTime(rs.getDate("EvaluatedTime").toLocalDate());
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

	/**
	 * download files of specific change request
	 * 
	 * @param id-change      request id
	 * @param zipName-always sent change_request_idOfChangeRequest
	 * @return
	 */

	public List<Object> downloadFiles(int id, File folder, String zipName) {
		int count = 0;
		List<Object> lr = new ArrayList<Object>();
		ResultSet rs = null;
		List<File> l = new ArrayList<File>();
		// get from database all the change request files
		String selectSQL = "SELECT fileName,file FROM files where CrID=?";
		PreparedStatement pstmt = null;
		try {

			pstmt = sqlConnection.prepareStatement(selectSQL);
			pstmt.setInt(1, id);
			rs = pstmt.executeQuery();
			// convert from blobs to files
			while (rs.next()) {
				count++;
				Blob blob = rs.getBlob("file");
				InputStream in = blob.getBinaryStream();
				File someFile = new File(rs.getString("fileName"));
				OutputStream out = new FileOutputStream(someFile);
				byte[] buff = new byte[4096]; // how much of the blob to read/write at a time
				int len = 0;

				while ((len = in.read(buff)) != -1) {

					out.write(buff, 0, len);
				}

				l.add(someFile);
			}
			if (count == 0) {

				lr.add("noFiles");
				return lr;
			}

			File zipFile = new File(folder.getPath() + "/" + zipName + ".zip");
			return createZipFromMultipleFiles(zipFile, l);

		} catch (Exception e1) {

			e1.printStackTrace();

			lr.add("exception");
			lr.add(e1);
			return lr;
		}
	}

	/**
	 * create zip folder with given files
	 * 
	 * @param zipName
	 * @param srcFiles
	 */
	private List<Object> createZipFromMultipleFiles(File zipName, List<File> srcFiles) {
		List<Object> l = new ArrayList<Object>();
		try {
			// create byte buffer
			byte[] buffer = new byte[259000];

			FileOutputStream fos = new FileOutputStream(zipName);

			ZipOutputStream zos = new ZipOutputStream(fos);

			for (int i = 0; i < srcFiles.size(); i++) {

				File srcFile = srcFiles.get(i);
				System.out.println(srcFile.getPath());
				FileInputStream fis = new FileInputStream(srcFile);

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

			l.add("success");

		} catch (IOException ioe) {
			System.out.println("Error creating zip file: " + ioe);

			l.add("exception");
			l.add(ioe);
		}
		return l;

	}

	public List<ChangeInitiator> getInfoEngineers() throws SQLException {
		System.out.println("database handle getInfoEngineers");
		List<ChangeInitiator> infoEngineersList = new ArrayList<>();

		ps = sqlConnection.prepareStatement("SELECT * FROM users WHERE title = 'INFOENGINEER'");
		ResultSet rs = ps.executeQuery();

		while (rs.next()) {
			ChangeInitiator infoEngineer = new ChangeInitiator();
			infoEngineer.setId(rs.getInt("IDuser"));
			infoEngineer.setFirstName(rs.getString("firstName"));
			infoEngineer.setLastName(rs.getString("lastName"));
			infoEngineer.setPhoneNumber(rs.getString("phone"));
			infoEngineer.setEmail(rs.getString("email"));
			infoEngineer.setTitle(ChangeInitiator.Title.INFOENGINEER);
			infoEngineer.setDepartment(CiDepartment.IT);
			infoEngineer.setPosition(Position.valueOf(rs.getString("position")));

			infoEngineersList.add(infoEngineer);
		}
		ps.close();
		return infoEngineersList;
	}

	public void itdUpdatePermissions(List<List<ChangeInitiator>> params) throws SQLException {
		System.out.println("database handle itdUpdatePermissions");
		List<Position> positionList = new ArrayList<>();
		positionList.add(Position.SUPERVISOR);
		positionList.add(Position.CCC);
		positionList.add(Position.CCC);
		positionList.add(Position.CHAIRMAN);
		System.out.println(params);

		List<ChangeInitiator> oldSelection = params.get(0);
		List<ChangeInitiator> newSelection = params.get(1);

		for (int i = 0; i < 4; i++) {
			ps = sqlConnection.prepareStatement("UPDATE users SET position=? WHERE IDuser=?");
			ps.setString(1, Position.REGULAR.toString());
			ps.setInt(2, oldSelection.get(i).getId());
			ps.executeUpdate();
			ps.close();
		}

		for (int i = 0; i < 4; i++) {
			ps = sqlConnection.prepareStatement("UPDATE users SET position=? WHERE IDuser=?");
			ps.setString(1, positionList.get(i).toString());
			ps.setInt(2, newSelection.get(i).getId());
			ps.executeUpdate();
			ps.close();
		}
		System.out.println("database finish itdUpdatePermissions");
	}

	public List<Boolean> setDecision(List<String> params) {
		boolean flag = false;
		List<Boolean> list = new ArrayList<Boolean>();
		PreparedStatement ps;
		String decision = new String();
		int crId = Integer.parseInt(params.get(2));

		// update decision in the db - set to null at first
		try {
			System.out.println("insert the decision to datebase");
			ps = sqlConnection.prepareStatement(
					"UPDATE phase SET phSetDecisionDescription = ? WHERE phIDChangeRequest = ? AND phPhaseName = ?");
			decision = params.get(0) + ":" + params.get(1);
			ps.setString(1, decision);
			ps.setInt(2, crId);
			ps.setString(3, params.get(3));
			ps.executeUpdate();
			flag = true;
			list.add(flag);
		} catch (SQLException e) {
			flag = false;
			list.add(flag);
			e.printStackTrace();
		}

		// update the current phase to done
		try {
			ps = sqlConnection.prepareStatement("UPDATE phase SET phStatus = 'DONE' WHERE phIDChangeRequest = ?");
			ps.setInt(1, crId);
			ps.executeUpdate();
			flag = true;
			list.add(flag);
		} catch (SQLException e) {
			flag = false;
			list.add(flag);
			e.printStackTrace();
		}

		// update phase and status of specific request according to the decision.
		decision = params.get(0);
		switch (decision) {
		case "Decline The Change":
			try {
				ps = sqlConnection
						.prepareStatement("UPDATE changeRequest SET crCurrPhaseName = 'CLOSING' WHERE crID = ?");
				ps.setInt(1, crId);
				ps.executeUpdate();
				ps = sqlConnection
						.prepareStatement("UPDATE phase SET phStatus = 'DECLINED' WHERE phIDChangeRequest = ? AND phPhaseName='CLOSING'");
				ps.setInt(1, crId);
				ps.executeUpdate();
				flag = true;
				list.add(flag);
			} catch (SQLException e) {
				flag = false;
				list.add(flag);
				e.printStackTrace();
			}
			break;

		case "Commite The Change":
			try {
				ps = sqlConnection
						.prepareStatement("UPDATE changeRequest SET crCurrPhaseName = 'EXECUTION' WHERE crID = ?");
				ps.setInt(1, crId);
				ps.executeUpdate();
				flag = true;
				list.add(flag);
			} catch (SQLException e) {
				flag = false;
				list.add(flag);
				e.printStackTrace();
			}
			break;

		case "Ask For Additional Data":
			try {
				ps = sqlConnection
						.prepareStatement("UPDATE changeRequest SET crCurrPhaseName = 'EVALUATION' WHERE crID = ?");
				ps.setInt(1, crId);
				ps.executeUpdate();
				ps = sqlConnection.prepareStatement(
						"UPDATE phase SET phStatus = 'PHASE_EXEC_LEADER_ASSIGNED' WHERE phIDChangeRequest = ?");
				ps.setInt(1, crId);
				ps.executeUpdate();
				ps = sqlConnection.prepareStatement(
						"UPDATE phase SET phSetDecisionDescription = 'Ask For Additional Data' WHERE phIDChangeRequest = ? and phPhaseName='EVALUATION'");
				flag = true;
				list.add(flag);
			} catch (SQLException e) {
				flag = false;
				list.add(flag);
				e.printStackTrace();
			}
			break;

		case "Approve The Change":
			try {
				ps = sqlConnection
						.prepareStatement("UPDATE changeRequest SET crCurrPhaseName = 'CLOSING' WHERE crID = ?");
				ps.setInt(1, crId);
				ps.executeUpdate();
				ps = sqlConnection
						.prepareStatement("UPDATE phase SET phStatus = 'IN_PROCESS' WHERE phIDChangeRequest = ?");
				ps.setInt(1, crId);
				ps.executeUpdate();
				flag = true;
				list.add(flag);
			} catch (SQLException e) {
				flag = false;
				list.add(flag);
				e.printStackTrace();
			}
			break;

		case "Report Test Failure":
			try {
				ps = sqlConnection
						.prepareStatement("UPDATE changeRequest SET crCurrPhaseName = 'EXECUTION' WHERE crID = ?");
				ps.setInt(1, crId);
				ps.executeUpdate();
				ps = sqlConnection.prepareStatement(
						"UPDATE phase SET phStatus = 'PHASE_LEADER_ASSIGNED' WHERE phIDChangeRequest = ?");
				ps.setInt(1, crId);
				ps.executeUpdate();
				flag = true;
				list.add(flag);
			} catch (SQLException e) {
				flag = false;
				list.add(flag);
				e.printStackTrace();
			}
			break;

		}

		return list;
	}

	public List<ChangeInitiator> getphaseLeadersDetails(List<ChangeInitiator> ChangeInitiatorList) {

		List<ChangeInitiator> phaseLeadersList = new ArrayList<>();
		ChangeInitiator crInitiator = new ChangeInitiator();
		crInitiator = ChangeInitiatorList.get(0);
		System.out.println(crInitiator);

		try {
			PreparedStatement ps = sqlConnection
					.prepareStatement("SELECT * FROM users WHERE IDuser != ? AND title=? AND position=?");
			ps.setInt(1, crInitiator.getId());
			ps.setString(2, ChangeInitiator.Title.INFOENGINEER.toString());
			ps.setString(3, Position.REGULAR.toString());

			ResultSet rs = ps.executeQuery();
			rs.beforeFirst();
			while (rs.next()) {
				ChangeInitiator row = new ChangeInitiator();
				row.setId(rs.getInt("IDuser"));
				row.setFirstName(rs.getString("firstName"));
				row.setLastName(rs.getString("lastName"));
				row.setEmail(rs.getString("email"));
				row.setPassword(rs.getString("password"));
				row.setTitle(ChangeInitiator.Title.valueOf(rs.getString("title")));
				row.setPhoneNumber(rs.getString("phone"));
				row.setDepartment(CiDepartment.valueOf(rs.getString("department")));
				row.setPosition(Position.valueOf(rs.getString("position")));
				phaseLeadersList.add(row);
				System.out.println(row);
			}
			ps.close();
			System.out.println("DB get phase leaders");

		} catch (SQLException e) {
			e.printStackTrace();
		}
		return phaseLeadersList;

	}

	public boolean checkReturnRequest(int id) {
		try {
			PreparedStatement ps = sqlConnection.prepareStatement(
					"select phSetDecisionDescription from phase where phIDChangeRequest=? and phPhaseName='EVALUATION'");
			ps.setInt(1, id);
			ResultSet rs = ps.executeQuery();
			rs.next();
			if (rs.getString("phSetDecisionDescription") != null
					&& rs.getString("phSetDecisionDescription").equals("Ask For Additional Data")) {
				ps = sqlConnection.prepareStatement("delete from evaluationReport where cRequestID=?");
				ps.setInt(1, id);
				ps.executeUpdate();
				System.out.println("true");
				return true;

			} else
				return false;

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}

	}

	public boolean freezeRequest(int id1) {
		PreparedStatement ps;
		try {
			ps = sqlConnection.prepareStatement("UPDATE changeRequest SET crSuspended=1 WHERE crID=?");
			ps.setInt(1, id1);
			ps.executeUpdate();
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
		
	}


	public void executionConfirmation(ChangeRequest changeRequest) throws SQLException {
		System.out.println("database handle executionConfirmation");

		// update phase status to DONE
		ps = sqlConnection.prepareStatement("UPDATE phase SET phStatus = ? " +
				"WHERE (phIDChangeRequest = ? AND phPhaseName = ?)");
		ps.setString(1, Phase.PhaseStatus.DONE.toString());
		ps.setInt(2, changeRequest.getId());
		ps.setString(3, Phase.PhaseName.EXECUTION.toString());
		ps.executeUpdate();
		ps.close();

		// update current phase in change request table
		ps = sqlConnection.prepareStatement("UPDATE changeRequest SET crCurrPhaseName = ? " +
				"WHERE crID = ?");
		ps.setString(1, Phase.PhaseName.VALIDATION.toString());
		ps.setInt(2, changeRequest.getId());
		ps.executeUpdate();
		ps.close();

		System.out.println("database finish executionConfirmation");

	}
	

	public boolean thawRequest(int id1) {
		PreparedStatement ps;
		try {
			ps = sqlConnection.prepareStatement("UPDATE changeRequest SET crSuspended=0 where crID=?");
			ps.setInt(1, id1);
			ps.executeUpdate();
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
		
		
	}

	public boolean closeRequest(int id1) {
		try {
			PreparedStatement ps=sqlConnection.prepareStatement("UPDATE phase SET phStatus='DONE' WHERE phIDChangeRequest=? AND phPhaseName='CLOSING'");
			ps.setInt(1, id1);
			ps.executeUpdate();
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
		
	}


	public List<ChangeInitiator> getCCC() throws SQLException {
		System.out.println("database handle getCCC");
		List<ChangeInitiator> cccList = new ArrayList<>();

		ps = sqlConnection.prepareStatement("SELECT * FROM users WHERE position = 'ccc'");
		ResultSet rs = ps.executeQuery();

		while (rs.next()) {
			ChangeInitiator ccc = new ChangeInitiator();
			ccc.setId(rs.getInt("IDuser"));
			ccc.setFirstName(rs.getString("firstName"));
			ccc.setLastName(rs.getString("lastName"));
			ccc.setEmail(rs.getString("email"));
			ccc.setPassword(rs.getString("password"));
			ccc.setTitle(ChangeInitiator.Title.valueOf(rs.getString("title")));
			ccc.setPhoneNumber(rs.getString("phone"));
			ccc.setDepartment(CiDepartment.valueOf(rs.getString("Department")));
			ccc.setPosition(Position.CCC);

			cccList.add(ccc);
		}
		ps.close();
		System.out.println(cccList);
		return cccList;

	}

	public void replaceTester(ChangeInitiator a, ChangeInitiator b, Integer id) throws SQLException {
		System.out.println("database handle replaceTester");
		System.out.println(a);
		System.out.println(b);
		

		ps = sqlConnection
				.prepareStatement("SELECT COUNT(*) As count FROM ieInPhase where crID=? and iePhaseName='VALIDATION' ");
		ps.setInt(1, id);
		ResultSet rs = ps.executeQuery();
		rs.next();
		int count = rs.getInt("count");
		if (count==0) {
			System.out.println("1");
			ps = sqlConnection.prepareStatement("INSERT INTO ieInPhase "
					+ "(IDieInPhase, crID, iePhaseName, iePhasePosition, evaluationReportId) " + "VALUE (?,?,?,?,?)");
			ps.setInt(1, b.getId());
			ps.setInt(2, id);
			ps.setString(3, Phase.PhaseName.VALIDATION.toString());
			ps.setString(4, IEPhasePosition.PhasePosition.TESTER.toString());
			ps.setString(5, id.toString());
			ps.executeUpdate();
			ps.close();
			System.out.println(a);
			System.out.println(b);

		} else {
			ps = sqlConnection
					.prepareStatement("UPDATE cbaricmy_ICM.ieInPhase set IDieInPhase=? where crID=? and iePhaseName=?");
			ps.setInt(1, b.getId());
			ps.setInt(2, id);
			ps.setString(3, Phase.PhaseName.VALIDATION.toString());
			ps.executeUpdate();
			ps.close();
		}

	}
}
