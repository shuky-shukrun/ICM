package entities;

import java.io.File;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

public class ChangeRequest implements Serializable {

	private Integer id;
	private ChangeInitiator initiator;
	private InfoSystem infoSystem;
	private String currState;
	private String requestedChange;
	private String reasonForChange;
	private String comment;
	private File[] files;
	private List<String> filesNames;
	private LocalDate date;
	private List<Phase> phases;
	private Phase.PhaseName currPhaseName;
	private Phase.PhaseStatus currPhaseStatus;
	private boolean suspended;




	public Integer getId() {
		return this.id;
	}

	/**
	 * 
	 * @param id
	 */
	public void setId(Integer id) {
		this.id = id;
	}

	public ChangeInitiator getInitiator() {
		return initiator;
	}

	public void setInitiator(ChangeInitiator initiator) {
		this.initiator = initiator;
	}

	public InfoSystem getInfoSystem() {
		return this.infoSystem;
	}

	/**
	 * 
	 * @param infoSystem
	 */
	public void setInfoSystem(InfoSystem infoSystem) {
		this.infoSystem = infoSystem;
	}

	public String getCurrState() {
		return this.currState;
	}

	/**
	 * 
	 * @param currState
	 */
	public void setCurrState(String currState) {
		this.currState = currState;
	}

	public String getRequestedChange() {
		return this.requestedChange;
	}

	/**
	 * 
	 * @param requestedChange
	 */
	public void setRequestedChange(String requestedChange) {
		this.requestedChange = requestedChange;
	}

	public String getReasonForChange() {
		return this.reasonForChange;
	}

	/**
	 * 
	 * @param reasonForChange
	 */
	public void setReasonForChange(String reasonForChange) {
		this.reasonForChange = reasonForChange;
	}

	public String getComment() {
		return this.comment;
	}

	/**
	 * 
	 * @param comment
	 */
	public void setComment(String comment) {
		this.comment = comment;
	}

	public File[] getFiles() {
		return this.files;
	}

	/**
	 * 
	 * @param files
	 */
	public void setFiles(File[] files) {
		this.files = files;
	}

	public LocalDate getDate() {
		return this.date;
	}

	/**
	 * 
	 * @param date
	 */
	public void setDate(LocalDate date) {
		this.date = date;
	}

	public List<Phase> getPhases() {
		return this.phases;
	}

	/**
	 * 
	 * @param phases
	 */
	public void setPhases(List<Phase> phases) {
		this.phases = phases;
	}

	public Phase.PhaseName getCurrPhaseName() {
		return this.currPhaseName;
	}

	/**
	 * 
	 * @param currPhaseName
	 */
	public void setCurrPhaseName(Phase.PhaseName currPhaseName) {
		this.currPhaseName = currPhaseName;
	}

	/**
	 * 
	 * @param suspended
	 */
	public void setSuspended(boolean suspended) {
		this.suspended = suspended;
	}

	public boolean isSuspended() {
		return suspended;
	}
	public Phase.PhaseStatus getCurrPhaseStatus() {
		return currPhaseStatus;
	}

	public void setCurrPhaseStatus(Phase.PhaseStatus currPhaseStatus) {
		this.currPhaseStatus = currPhaseStatus;
	}

	public List<String> getFilesNames() {
		return filesNames;
	}

	public void setFilesNames(List<String> filesNames) {
		this.filesNames = filesNames;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		ChangeRequest that = (ChangeRequest) o;
		return id.equals(that.id);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id);
	}
}