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
		TIME_APPROVED,
		IN_PROCESS,
		DONE,
		EXTENSION_TIME_REQUESTED
	}

	public PhaseName getName() {
		return this.name;
	}

	/**
	 * 
	 * @param name
	 */
	public void setName(PhaseName name) {
		this.name = name;
	}


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

	public PhaseStatus getPhaseStatus() {
		return this.phaseStatus;
	}
	
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

	public LocalDate getExceptionTime() {
		return exceptionTime;
	}
	
	public LocalDate getTimeExtensionRequest() {
		return timeExtensionRequest;
	}

	public void setExceptionTime(LocalDate exceptionTime) {
		this.exceptionTime = exceptionTime;
	}
	
	public void setTimeExtensionRequest(LocalDate timeExtesion) {
		this.timeExtensionRequest = timeExtesion;
	}

	public Map<IEPhasePosition.PhasePosition, IEPhasePosition> getIePhasePosition() {
		return iePhasePosition;
	}

	public void setIePhasePosition(Map<IEPhasePosition.PhasePosition, IEPhasePosition> iePhasePosition) {
		this.iePhasePosition = iePhasePosition;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	
	public void setSetDecisionDescription(String decision) {
		this.setDecisionDescription = decision;
	}
	
	public String getSetDecisionDescription() {
		return setDecisionDescription;
	}

}