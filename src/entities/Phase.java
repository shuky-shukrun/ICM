package entities;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Map;

public class Phase implements Serializable {

	private PhaseName name;
	private LocalDate deadLine;
	private PhaseStatus phaseStatus;
	private boolean extensionRequest;
	private Integer changeRequestId;
	private LocalDate exceptionTime;
	private LocalDate timeExtensionRequest;   ///// Check with team!!!!!!!
	private String description;      ///// Check with team!!!!!!!
	private String setDecisionDescription;		//if there is a decision to make about the privies phase- chairman and tester
	private Map<IEPhasePosition.PhasePosition, IEPhasePosition> iePhasePosition;
	
	public enum PhaseName {
		SUBMITTED,
		EVALUATION,
		EXAMINATION,
		EXECUTION,
		VALIDATION,
		CLOSING
	}

	public enum PhaseStatus {
		SUBMITTED,
		PHASE_LEADER_ASSIGNED,
		PHASE_EXEC_LEADER_ASSIGNED,
		TIME_REQUESTED,
		EXTENSION_TIME_APPROVED,
		IN_PROCESS,
		DECLINED,
		DONE,
		EXTENSION_TIME_REQUESTED,
		TIME_DECLINED
	}
	/**
	 * gets phase name
	 * @return phase name
	 */
	public PhaseName getName() {
		return this.name;
	}

	/**
	 * Sets phase name
	 * @param name
	 */
	public void setName(PhaseName name) {
		this.name = name;
	}
	/**
	 * gets phase deadline
	 * @return phase deadline
	 */

	public LocalDate getDeadLine() {
		return this.deadLine;
	}

	/**
	 * 
	 * @param deadLine
	 */
	public void setDeadLine(LocalDate deadLine) {
		this.deadLine = deadLine;
	}
	/**
	 * gets phase status
	 * @return phase status
	 */

	public PhaseStatus getPhaseStatus() {
		return this.phaseStatus;
	}
	/**
	 * gets phase description
	 * @return phase description
	 */

	public String getDescription() {
		return this.description;
	}

	/**
	 * 
	 * @param phaseStatus
	 */
	public void setPhaseStatus(PhaseStatus phaseStatus) {
		this.phaseStatus = phaseStatus;
	}
	/**
	 * checks if this request is extension request
	 * @return true-extension request,false-else
	 */
	public boolean isExtensionRequest() {
		return this.extensionRequest;
	}

	/**
	 * 
	 * @param extensionRequest
	 */
	public void setExtensionRequest(boolean extensionRequest) {
		this.extensionRequest = extensionRequest;
	}

	/**
	 * gets change request id
	 * @return change request id
	 */
	public Integer getChangeRequestId() {
		return this.changeRequestId;
	}

	/**
	 * 
	 * @param changeRequestId
	 */
	public void setChangeRequestId(Integer changeRequestId) {
		this.changeRequestId = changeRequestId;
	}

	/**
	 * gets exception time
	 * @return exception time
	 */
	public LocalDate getExceptionTime() {
		return exceptionTime;
	}
	/**
	 * gets extension time
	 * @return extension time
	 */
	public LocalDate getTimeExtensionRequest() {
		return timeExtensionRequest;
	}
	/**
	 * sets exception time
	 * @param exceptionTime
	 */
	public void setExceptionTime(LocalDate exceptionTime) {
		this.exceptionTime = exceptionTime;
	}
	/**
	 * sets extension time
	 * @param extension Time
	 */
	public void setTimeExtensionRequest(LocalDate timeExtesion) {
		this.timeExtensionRequest = timeExtesion;
	}
	/**
	 * gets info engineer in phase position
	 * @return info engineer in phase position
	 */
	public Map<IEPhasePosition.PhasePosition, IEPhasePosition> getIePhasePosition() {
		return iePhasePosition;
	}
	/**
	 * sets the ie phase position
	 * @param iePhasePosition
	 */
	public void setIePhasePosition(Map<IEPhasePosition.PhasePosition, IEPhasePosition> iePhasePosition) {
		this.iePhasePosition = iePhasePosition;
	}
	/**
	 * 
	 * @param description
	 */
	public void setDescription(String description) {
		this.description = description;
	}
	/**
	 * 
	 * @param decision
	 */
	public void setSetDecisionDescription(String decision) {
		this.setDecisionDescription = decision;
	}
	/**
	 * 
	 * @return
	 */
	public String getSetDecisionDescription() {
		return setDecisionDescription;
	}

}