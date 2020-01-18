package server;// This file contains material supporting section 3.7 of the textbook:

// "Object Oriented Software Engineering" and is issued under the open-source
// license found at www.lloseng.com 

import common.JavaEmail;
import entities.ChangeInitiator;
import entities.ChangeRequest;
import entities.EvaluationReport;
import entities.IEPhasePosition;
import entities.InformationEngineer;
import entities.Phase;
import javafx.application.Platform;
import ocsf.server.AbstractServer;
import ocsf.server.ConnectionToClient;
import server.ServerService.DatabaseService;

import javax.mail.MessagingException;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.*;

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

    private class ServerTimer extends TimerTask {

        @Override
        public void run() {
            try {
                dbConnection.sendNotifications();
            } catch (SQLException | MessagingException e) {
                e.printStackTrace();
            }
        }

    }

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

        //the Date and time at which you want to execute
        DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            Date date = dateFormatter.parse("2020-01-19 07:00:00");
            Timer timer = new Timer("Server Timer");
            ServerTimer serverTimer = new ServerTimer();
            long period = 1000L * 60L * 60L * 24L;
            timer.schedule(serverTimer, date, period);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

	// Instance methods ************************************************

	/**
	 * This method handles any messages received from the client.
	 *
	 * @param msg    The message received from the client.
	 * @param client The connection from which the message originated.
	 */
	public void handleMessageFromClient(Object msg, ConnectionToClient client) {
		ServerService serverService = (ServerService) msg;
		try {
            System.out.println("Message received: " + msg + " from " + client);
            // extract the requested service from the server
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
                        String line1 = "Hey  "+(String)l.get(2)+" "+(String)l.get(3);
                        String line2 = "Your id is: "+(int)l.get(1)+"    ";
                        String line3 = "Your password is: "+(String)l.get(4);
                        String text = line1 + "\n" + line2+"\n"+line3;
                        JavaEmail emailer=new JavaEmail();
                        emailer.setMailServerProperties();

                        try {
                            emailer.sendEmail((String)l.get(5), "Restore Password", text);
                        } catch (MessagingException e) {
                            e.printStackTrace();
                        }
                    }
                    break;

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
            	case Is_Exists_Eva_Report:
        			System.out.println("server check if evaluation report exists");
        			List<Boolean>flags=dbConnection.existsEvaluationReport(serverService.getParams());
        			try {
        				client.sendToClient(new ServerService(DatabaseService.Is_Exists_Eva_Report,flags));
        			}
        			catch(IOException e) {
        				e.printStackTrace();
        			}
        			break;
                case View_Evaluation_Report:

                    System.out.println("server handle View_Evaluation_Report");
                    //List<Integer> evRpParams = serverService.getParams();

                    List<EvaluationReport> EvRp = dbConnection.getEvaluationReportDetails(serverService.getParams());
                    System.out.println("Get_Evaluation_Report_Details server got data");
                    serverService.setParams(EvRp);

                    try {

                        client.sendToClient(serverService);
                        System.out.println("sent evaluation details to client");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;


                case Request_Time_Evaluation:
                    System.out.println("server handle request time for evaluation phase");
                    List<Object> requestTimeDetails = serverService.getParams();
                    List<Boolean> list2 = dbConnection.requestTimeEvaluation(requestTimeDetails);
                    ServerService s1 = new ServerService(DatabaseService.Request_Time_Evaluation, list2);
                    try {
                        client.sendToClient(s1);
                        System.out.println("request time evaluation status sent to client");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
                case Request_Time_EXAMINATION:
                    System.out.println("server handle request time for evaluation phase");
                    List<Object> requestTimeDetails1 = serverService.getParams();
                    List<Boolean> list3 = dbConnection.requestTimeExamination(requestTimeDetails1);
                    ServerService s2 = new ServerService(DatabaseService.Request_Time_EXAMINATION, list3);
                    try {
                        client.sendToClient(s2);
                        System.out.println("request time examination status sent to client");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                case Update_Phase_Extension:
                    System.out.println("server handle Update_Phase_Extension");
                    List<Phase> phaseList2 = serverService.getParams();
                    System.out.println(phaseList2.get(0));
                    List<Boolean> isUpdate = dbConnection.updatePhaseExtensionTime(phaseList2);
                    System.out.println("Update_Phase_Extension server got data");
                    serverService.setParams(isUpdate);
                    try {
                        client.sendToClient(serverService);
                        System.out.println("server finish Update_Phase_Extension");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;

                case Add_New_Request:
                    System.out.println("server handle Add_New_Request");
                    List<ChangeRequest> changeRequestList = serverService.getParams();
                    ChangeRequest newRequest = changeRequestList.get(0);
                    dbConnection.addNewRequest(newRequest);
                    client.sendToClient(serverService);
					System.out.println("server finish Add_New_Request");
                    break;
                case download_files:
                    System.out.println("server handle download files");
                    List<Object> flag=dbConnection.downloadFiles((int)serverService.getParams().get(0),(File)serverService.getParams().get(1),"Change_Request_"+(int)serverService.getParams().get(0));
                    
                    try {
                        client.sendToClient(new ServerService(DatabaseService.download_files,flag));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
                case Get_Info_Engineers:
                    System.out.println("server handle Get_Info_Engineers");
                    serverService.setParams(dbConnection.getInfoEngineers());
                    client.sendToClient(serverService);
                    System.out.println("server finish Get_Info_Engineers");
                    break;
                case Itd_Update_Permissions:
                    System.out.println("server handle Itd_Update_Permissions");
                    dbConnection.itdUpdatePermissions(serverService.getParams());
                    client.sendToClient(serverService);
                    System.out.println("server finish Itd_Update_Permissions");
                    break;
                case Set_Decision:
                    System.out.println("server handle set decision for tester in validation phase");
                    List<String> decision = serverService.getParams();
                    List<Boolean> details =dbConnection.setDecision(decision);
                    ServerService srvrService= new ServerService(DatabaseService.Set_Decision, details);
                    client.sendToClient(srvrService);
                    System.out.println("set decision status sent to client");
                    break;
                case Get_Phase_Leaders_And_Workers:
                    System.out.println("server handle Get_Phase_Leaders_And_Workers");
                    List<InformationEngineer> ChangeInitiatorParams = serverService.getParams();
                    List<List<ChangeInitiator>> workersList = dbConnection.getPhaseLeadersDetails(ChangeInitiatorParams);
                    System.out.println("Get_Phase_Leaders_And_Workers server got data");
                    serverService.setParams(workersList);
                    try {
                        client.sendToClient(serverService);
                        System.out.println("sent phase leaders and workers details to client");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
				    break;
                case Supervisor_Update_Phase_Leaders_And_Workers:
                    System.out.println("server handle Supervisor_Update_Phase_Leaders_And_Workers");
                    List<IEPhasePosition> newPhaseLeadersAndWorkersList = serverService.getParams();
                    List<Boolean> isUpdate1 = dbConnection.supervisorUpdatePhaseLeaders(newPhaseLeadersAndWorkersList);
                    System.out.println("Supervisor_Update_Phase_Leaders server got data");
                    serverService.setParams(isUpdate1);
                    try {
                        client.sendToClient(serverService);
                        System.out.println("server finish Supervisor_Update_Phase_Leaders");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
				    break;
                case Get_Selected_Phase_Leaders_And_Workers:
                    System.out.println("server handle Get_Selected_Phase_Leaders_And_Workers");
                    List<ChangeRequest> changeRequestsList = serverService.getParams();
                    List<ChangeInitiator> selectedPhaseLeadersAndWorkers = dbConnection.getSelectedPhaseLeadersAndWorkers(changeRequestsList);
                    System.out.println("Get_Selected_Phase_Leaders_And_Workers server got data");
                    serverService.setParams(selectedPhaseLeadersAndWorkers);
                    try {
                        client.sendToClient(serverService);
                        System.out.println("sent selected phase leaders and workers details to client");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
				    break;
				    
                case Execution_Confirmation:
                    System.out.println("Server handle Execution_Confirmation");
                    List<ChangeRequest> requestList = serverService.getParams();
                    dbConnection.executionConfirmation(requestList.get(0));
                    System.out.println("Server finish Execution_Confirmation");
                    break;
                case Return_Request:
                	System.out.println("server handle check if request return");
                	int id=(Integer)serverService.getParams().get(0);
                	boolean flagR=dbConnection.checkReturnRequest(id);
                	System.out.println(flagR);
                	List<Boolean>ld=new ArrayList<Boolean>();
                	ld.add(flagR);
                	client.sendToClient(new ServerService(DatabaseService.Return_Request, ld));
                	break;
                case Attach_Files:
                	System.out.println("server handle attach files");
                	File[]fil=null;
                	
                	int id1=(Integer)serverService.getParams().get(0);
                	fil = (File[])serverService.getParams().get(1);
                		
               
                	boolean flagAttach=dbConnection.uploadFiles(id1, fil);
                	List<Boolean>flagss=new ArrayList<Boolean>();
                	flagss.add(flagAttach);
                	client.sendToClient(new ServerService(DatabaseService.Attach_Files, flagss));
                	break;
                case Freeze_Request:
                	System.out.println("server handle freeze request");
                	id1=(Integer)serverService.getParams().get(0);
                	boolean flagFreeze=dbConnection.freezeRequest(id1);
                	flagss=new ArrayList<Boolean>();
                	flagss.add(flagFreeze);
                	client.sendToClient(new ServerService(DatabaseService.Freeze_Request, flagss));
                	break;
                case Thaw_Request:
                	System.out.println("server handle thaw request");
                	id1=(Integer)serverService.getParams().get(0);
                	boolean flagThaw=dbConnection.thawRequest(id1);
                	flagss=new ArrayList<Boolean>();
                	flagss.add(flagThaw);
                	client.sendToClient(new ServerService(DatabaseService.Thaw_Request, flagss));
                	break;
                case Close_Request:
                	System.out.println("server handle close request");
                	id1=Integer.parseInt((String)serverService.getParams().get(0));
                	boolean flagw=dbConnection.closeRequest(id1);
                	if(flagw==true) {
                		String line3="";
                		String line1 = "Hey  "+(String)serverService.getParams().get(2);
                        String line2="Your request no. "+id1+" closed";
                        if(((String)serverService.getParams().get(1)).equals("DECLINE"))
                        	line3="status of request:DECLINE";
                        else if(((String)serverService.getParams().get(1)).equals("IN_PROCESS"))
                        	line3="status of request:APPROVED";
                        String text = line1 + "\n" + line2+"\n"+line3;
                        JavaEmail emailer=new JavaEmail();
                        emailer.setMailServerProperties();

                        Thread mail = new Thread() {
                            @Override
                            public void run() {
                                try {
                                    emailer.sendEmail(((String)serverService.getParams().get(3)), "Request Decision", text);
                                } catch (MessagingException e) {
                                    e.printStackTrace();
                                }
                            }
                        };
                        mail.start();
                	}
                	flagss=new ArrayList<Boolean>();
                	flagss.add(flagw);
                    try {
                        client.sendToClient(new ServerService(DatabaseService.Close_Request, flagss));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                	break;
                case Assign_Tester:

                    System.out.println("server handle Get CCC");
                    serverService.setParams(dbConnection.getCCC());
                    client.sendToClient(serverService);
                    System.out.println("server finish Get CCC");
                    break;
                case Replace_Tester:
                    System.out.println("server handle Replace Tester");
                    dbConnection.replaceTester((ChangeInitiator)serverService.getParams().get(0),
                    							(ChangeInitiator)serverService.getParams().get(1),
                    							(Integer)serverService.getParams().get(2));
                    client.sendToClient(serverService);
                    System.out.println("server finish replace tester");
				    
                case Load_Extension_Time:
                	System.out.println("server handle load extension time request details");
                	List<String> timeExtension = dbConnection.getExtensionTime(serverService.getParams());
                	System.out.println("Extension time details server got data");
                	ServerService srvr = new ServerService(DatabaseService.Load_Extension_Time, timeExtension);
                	//serverService.setParams(timeExtension);
                	client.sendToClient(srvr);
                    System.out.println("sent extension time request details to client");                	
                	break;
				    
                case Approve_Phase_Time:
                	System.out.println("server handle approve requested phase time by supervisor");
                	List<String> params = serverService.getParams();
                    List<Boolean> timeApprove =dbConnection.timeApproved(params);
                    ServerService serversrvc= new ServerService(DatabaseService.Approve_Phase_Time, timeApprove);
                    client.sendToClient(serversrvc);
                    System.out.println("approve time status sent to client");
                    break;
                    
                case Reject_Phase_Time:
                	System.out.println("server handle reject requested phase time by supervisor");
                	List<String> params2 = serverService.getParams();
                    List<Boolean> timeReject =dbConnection.timeRejected(params2);
                    ServerService serversrvc2= new ServerService(DatabaseService.Reject_Phase_Time, timeReject);
                    client.sendToClient(serversrvc2);
                    System.out.println("reject time status sent to client");
                    break;
                case Get_Employee:
                    System.out.println("server handle Get Employee");
                    serverService.setParams(dbConnection.getEmployee());
                    client.sendToClient(serverService);
                    System.out.println("server finish Get Employee");
                    break;
                case Register_IT:
                    System.out.println("server handle Register IT");
                    dbConnection.registerIT((ChangeInitiator)serverService.getParams().get(0),
                    							(Integer)serverService.getParams().get(1));
                    client.sendToClient(serverService);
                    System.out.println("server finish register IT");
                    break;
                    
                case Email_ITD_Extension_Time_Approved:
                    System.out.println("server handle email ITD manager- extension time approved");
                    List<Object> lst=dbConnection.getITDInfo();
                    List<String> dtls = serverService.getParams();
                    if((Boolean)lst.get(0)==false)
                        try {
                            client.sendToClient(new ServerService(DatabaseService.Email_ITD_Extension_Time_Approved, dtls));
                        } catch (IOException e1) {
                            e1.printStackTrace();
                        }
                    else
                    {
                        String line1 = "Hello  "+(String)lst.get(3)+" "+(String)lst.get(2);
                        String line2 = "Extension time has been approved to change request number: "+dtls.get(0);
                        String line3 = "The new deadline to this change request is: "+dtls.get(1);
                        String text = line1 + "\n" + line2+"\n"+line3;
                        JavaEmail emailer=new JavaEmail();
                        emailer.setMailServerProperties();

                        try {
                            emailer.sendEmail((String)lst.get(1), "Inform extension time", text);
                        } catch (MessagingException e) {
                            e.printStackTrace();
                        }
                    }
                    break;
                case Update_Exception_Time:
                    System.out.println("server handle Update Exception Time");
                    List<Phase> phaseList = serverService.getParams();
                    dbConnection.updateExceptionTime(phaseList);
                    System.out.println("Update_Exception_Time server got data");
					break;
					case Get_Activity_Report_Details:
					System.out.println("server Get Report Details");
					List<Integer> frozenList = new ArrayList<>();
					List<Integer> activeList = new ArrayList<>();
					List<Integer> closedList = new ArrayList<>();
					List<Integer> declinedList = new ArrayList<>();
					List<List<Integer>> countList = new ArrayList<>();
					
					LocalDate startDate= (LocalDate) serverService.getParams().get(0);
					LocalDate from= startDate;
					LocalDate endDate= (LocalDate) serverService.getParams().get(1);
					LocalDate to= endDate;
					long weeks= (long) serverService.getParams().get(2);
					long left= (long) serverService.getParams().get(2);
					if (left == 0) {
						for (int i = 0; i <= weeks; i++) {
							from = startDate.plusDays(7 * i);
							to = startDate.plusDays(7 * 1 + 6);
							int frozenCount = dbConnection.getFReportDetails(from,to);
							frozenList.add(frozenCount);
							int activeCount=dbConnection.getAReportDetails(from,to);
							activeList.add(activeCount);
							int closedCount=dbConnection.getCReportDetails(from,to);
							closedList.add(closedCount);
							int declinedCount = dbConnection.getDReportDetails(from,to);
							declinedList.add(declinedCount);
							countList.add(frozenList);
							countList.add(activeList);
							countList.add(closedList);
							countList.add(declinedList);
							serverService.setParams(countList);
							client.sendToClient(serverService);
	
						}
					} else {
						for (int i = 0; i < weeks; i++) {
							from= from.plusDays(7*i);
							to= from.plusDays(7*i+6);
							int count = dbConnection.getFReportDetails(from,to);
							frozenList.add(count);
							serverService.setParams(frozenList);
							client.sendToClient(serverService);
						}
	
					}
					System.out.println("server finish Get Report Details");
					break;
					
				case Get_Performance_Report_Details:
					System.out.println("server Get Performance Report Details");
					List<List<LocalDate>> totalList= new ArrayList<>();
					LocalDate startDate1= (LocalDate) serverService.getParams().get(0);
					LocalDate from1= startDate1;
					LocalDate endDate1= (LocalDate) serverService.getParams().get(1);
					LocalDate to1= endDate1;
					List<LocalDate> timeList = dbConnection.getPerformanceReportDetails(from1,to1);
					System.out.println(timeList);
					totalList.add(timeList);
					List<LocalDate> timeList1 = dbConnection.getPerformanceReportDetails1(from1,to1);
					System.out.println(timeList1);
					totalList.add(timeList1);
					serverService.setParams(totalList);
					client.sendToClient(serverService);
					System.out.println("server finish Get Performance Report Details");
					break;
					
				case Get_Delays_Report_Details:
				System.out.println("server Get Report Details");				
				List<List<Integer>> sumList = new ArrayList<>();
				List<Integer> numOfDelaysList = new ArrayList<>();
				List<Integer> durationOfDelaysList= new ArrayList<>();
				LocalDate startDate2= (LocalDate) serverService.getParams().get(0);
				LocalDate from2= startDate2;
				LocalDate endDate2= (LocalDate) serverService.getParams().get(1);
				LocalDate to2= endDate2;
				long weeks2= (long) serverService.getParams().get(2);
				long left2= (long) serverService.getParams().get(2);
				if (left2 == 0) {
					for (int i = 0; i <= weeks2; i++) {
						from = startDate2.plusDays(7 * i);
						to = startDate2.plusDays(7 * 1 + 6);
						int numOfDelaysCount = dbConnection.getDelaysReportDetails(from,to);
						numOfDelaysList.add(numOfDelaysCount);
						int delayCount=dbConnection.getDelaysReportDetails1(from,to);
						System.out.println(delayCount);
						durationOfDelaysList.add(delayCount);
						sumList.add(numOfDelaysList);
						sumList.add(durationOfDelaysList);
						serverService.setParams(sumList);
						client.sendToClient(serverService);

					}
				} else {
					for (int i = 0; i < weeks2; i++) {
						from2= from2.plusDays(7*i);
						to2= from2.plusDays(7*i+6);
						int count2 = dbConnection.getFReportDetails(from2,to2);
						numOfDelaysList.add(count2);
						serverService.setParams(numOfDelaysList);
						client.sendToClient(serverService);
					}

				}
				System.out.println("server finish Get Report Details");
				break;
                	
            }
        } catch (IOException | SQLException e) {
            e.printStackTrace();
			System.out.println("\n\nError: " + e.getMessage());
			List<Exception> ExceptionsList = new ArrayList<>();
			ExceptionsList.add(e);
			serverService.setDatabaseService(DatabaseService.Error);
			serverService.setParams(ExceptionsList);
			try {
				client.sendToClient(serverService);
			} catch (IOException ex) {
				ex.printStackTrace();
				System.out.println("\n\nError: " + ex.getMessage());
			}
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
