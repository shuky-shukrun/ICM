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

    public DatabaseService getDatabaseService() {
        return databaseService;
    }

    public void setDatabaseService(DatabaseService databaseService) {
        this.databaseService = databaseService;
    }

    public List getParams() {
        return params;
    }

    public void setParams(List params) {
        this.params = params;
    }

    public enum DatabaseService {
        Error,
        Login,
        Get_All_Requests,
        Get_All_Requests_New,
        Get_Request_Details,
        Update_Request_Status,
        Get_Phase_Details,
        Update_Phase_Extension,
        Submit_New_Request,
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
        Register_IT
    }
}
