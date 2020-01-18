package server;

import java.io.Serializable;
import java.util.List;

public class ServerService implements Serializable {

    private DatabaseService databaseService;
    private List params;

    public ServerService(DatabaseService databaseService, List params) {
    	this.databaseService = databaseService;
        this.params = params;
    }
    /**
     * Gets database connection object
     * @return database connection
     */
    public DatabaseService getDatabaseService() {
        return databaseService;
    }

    /**
     * Sets database connection
     */
    public void setDatabaseService(DatabaseService databaseService) {
        this.databaseService = databaseService;
    }

    /**
     * Gets List of params from ServerService object
     * @return List object
     */
    public List getParams() {
        return params;
    }

    /**
     * Sets List of params for ServerService object
     */
    public void setParams(List params) {
        this.params = params;
    }

    /**
     * This enum describes the type of service that requests from the server.
     */
    public enum DatabaseService {
        Error,
        Login,
        Get_All_Requests,
        Get_Request_Details,
        Update_Phase_Extension,
        Create_Evaluation_Report,
        Request_Time_Evaluation,
        Forgot_Password,
        Add_New_Request,
        View_Evaluation_Report,
        Set_Decision,
        Get_Info_Engineers,
        Itd_Update_Permissions,
        Is_Exists_Eva_Report, 
        download_files,
        Execution_Confirmation,
        Return_Request,
        Attach_Files, 
        Freeze_Request, 
        Thaw_Request,
        Close_Request,      
        Assign_Tester,
        Replace_Tester,
        Get_Phase_Leaders_And_Workers,
        Supervisor_Update_Phase_Leaders_And_Workers,
        Get_Selected_Phase_Leaders_And_Workers,
        Request_Time_EXAMINATION,
        Load_Extension_Time,
        Approve_Phase_Time,
        Reject_Phase_Time,
        Get_Employee,
        Register_IT,
        Email_ITD_Extension_Time_Approved,
        Update_Exception_Time,
        Get_Activity_Report_Details,
        Get_Performance_Report_Details,
        Get_Delays_Report_Details,
        Edit_Request
    }
}
