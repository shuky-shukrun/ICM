package server;// This file contains material supporting section 3.7 of the textbook:

// "Object Oriented Software Engineering" and is issued under the open-source
// license found at www.lloseng.com 

import entities.ChangeInitiator;
import entities.ChangeRequest;
import entities.Phase;
import ocsf.server.AbstractServer;
import ocsf.server.ConnectionToClient;
import server.ServerService.DatabaseService;

import java.io.IOException;
import java.util.List;

import javax.mail.*;


import client.ClientController;
import client.ClientUI;
import common.IcmUtils;
import common.JavaEmail;

/**
 * This class overrides some of the methods in the abstract superclass in order
 * to give more functionality to the server.
 *
 * @author Dr Timothy C. Lethbridge
 * @author Dr Robert Lagani&egrave;re
 * @author Fran&ccedil;ois B&eacute;langer
 * @author Paul Holden
 * @version July 2000
 */
public class EchoServer extends AbstractServer {

	// Class variables *************************************************

	private DBConnection dbConnection;

	// Constructors ****************************************************

	/**
	 * Constructs an instance of the echo server.
	 *
	 * @param port The port number to connect on.
	 */
	public EchoServer(int port, String url, String username, String password) {
		super(port);
		dbConnection = new DBConnection(url, username, password);
	}

	// Instance methods ************************************************

	/**
	 * This method handles any messages received from the client.
	 *
	 * @param msg    The message received from the client.
	 * @param client The connection from which the message originated.
	 */
	public void handleMessageFromClient(Object msg, ConnectionToClient client) {

		System.out.println("Message received: " + msg + " from " + client);
		// extract the requested service from the server
		ServerService serverService = (ServerService) msg;

		switch (serverService.getDatabaseService()) {

		case Login:
			System.out.println("server received login request for: " + serverService.getParams());

			List<ChangeInitiator> loginRes = dbConnection.login(serverService.getParams());
			serverService.setParams(loginRes);
			try {
				client.sendToClient(serverService);
			} catch (IOException e) {
				e.printStackTrace();
			}

			break;
		case Forgot_Password:
			System.out.println("server handle forgot password request");
			List<Object>l=dbConnection.forgotPasswordRequest(serverService.getParams());
			if((Boolean)l.get(0)==false)
				try {
					client.sendToClient(new ServerService(DatabaseService.Forgot_Password, l));
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			else
			{
				String text="hey "+(String)l.get(2)+"\n your id: "+(int)l.get(1)+"\n your password is: "+(String)l.get(3);
				JavaEmail emailer=new JavaEmail();
				 emailer.setMailServerProperties();
				
				try {
					emailer.sendEmail((String)l.get(4), "restore password", text);
					
				} catch (MessagingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}

		case Get_All_Requests:
			System.out.println("server handle Get_All_Requests_New");
			// pass the request to the database
			List<List<ChangeRequest>> allRequests;
			allRequests = dbConnection.getAllRequests(serverService.getParams());
			serverService.setParams(allRequests);
			try {
				// pass the result back to client controller
				client.sendToClient(serverService);
				System.out.println("sent back to client controller");
			} catch (IOException e) {
				e.printStackTrace();
			}
			break;

		case Get_Request_Details:
			System.out.println("server handle Get_Request_Details");
			List<Integer> crParams = serverService.getParams();

			List<ChangeRequest> crList = dbConnection.getRequestDetails(crParams);
			System.out.println("Get_Request_Details server got data");
			serverService.setParams(crList);
			try {
				client.sendToClient(serverService);
				System.out.println("sent request details to client");
			} catch (IOException e) {
				e.printStackTrace();
			}
			break;
		case Create_Evaluation_Report:
			System.out.println("server handle create evaluation report");
			List<String> requirementList1 = serverService.getParams();
			List<Boolean> list = dbConnection.createEvaluationReport(requirementList1);
			ServerService s = new ServerService(DatabaseService.Create_Evaluation_Report, list);
			try {
				client.sendToClient(s);
				System.out.println("create evaluation report status sent to client");
			} catch (IOException e) {
				e.printStackTrace();
			}
			break;

		case Update_Request_Status:
			System.out.println("server handle Update_Request_Status");
			List<String> requirementList = serverService.getParams();
			System.out.println(requirementList);
			// pass the request to the database
			dbConnection.updateRequestDetails(requirementList);
			break;
		case Request_Time_Evaluation:
			System.out.println("server handle request time for evaluation phase");
			List<Object>requestTimeDetails=serverService.getParams();
			List<Boolean>list2=dbConnection.requestTimeEvaluation(requestTimeDetails);
			ServerService s1= new ServerService(DatabaseService.Request_Time_Evaluation, list2);
			try {
				client.sendToClient(s1);
				System.out.println("request time evaluation status sent to client");
			} catch (IOException e) {
				e.printStackTrace();
			}
			break;
      case Get_Phase_Details:
            	System.out.println("server handle Get_Phase_Details");
            	List<ChangeRequest> ChangeRequestList = serverService.getParams();
                System.out.println(ChangeRequestList.get(0));
                List<Phase> PhaseList =dbConnection.getPhaseDetails(ChangeRequestList);
                //System.out.println(PhaseList);
                System.out.println("Get_Phase_Details server got data");
                serverService.setParams(PhaseList);
                try {
                    client.sendToClient(serverService);
                    System.out.println("sent request details to client");
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            case Update_Phase_Extension:
            	System.out.println("server handle Update_Phase_Extension");
            	List<Phase> phaseList2 = serverService.getParams();
                System.out.println(phaseList2.get(0));
                List<Boolean> isUpdate =dbConnection.updatePhaseExtensionTime(phaseList2);
               // System.out.println(isUpdate);
                System.out.println("Update_Phase_Extension server got data");
                serverService.setParams(isUpdate);
                try {
                    client.sendToClient(serverService);
                    System.out.println("sent request details to client");
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break; 	
		}
	}

	/**
	 * This method overrides the one in the superclass. Called when the server
	 * starts listening for connections.
	 */
	protected void serverStarted() {
		System.out.println("Server listening for connections on port " + getPort());
	}

	/**
	 * This method overrides the one in the superclass. Called when the server stops
	 * listening for connections.
	 */
	protected void serverStopped() {
		System.out.println("Server has stopped listening for connections.");
	}
}
