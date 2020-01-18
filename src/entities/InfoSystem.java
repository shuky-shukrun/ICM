package entities;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public enum InfoSystem {
    MOODLE,
    LIBRARY,
    STUDENT_INFO_CENTER,
    LECTURER_INFO_CENTER,
    EMPLOYEE_INFO_CENTER,
    CLASS_COMPUTER,
    LAB_COMPUTER,
    COLLEGE_SITE;
	/**
	 * gets all info system in a list
	 * @return observable list that contains the info systems
	 */
    public static ObservableList<InfoSystem> getAll() {
        ObservableList<InfoSystem> infoSystems = FXCollections.observableArrayList();

        infoSystems.setAll(MOODLE, LIBRARY, STUDENT_INFO_CENTER, LECTURER_INFO_CENTER,
                EMPLOYEE_INFO_CENTER, CLASS_COMPUTER, LAB_COMPUTER, COLLEGE_SITE);

        return infoSystems;
    }
}