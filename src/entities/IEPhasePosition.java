package entities;

import java.io.Serializable;
import java.util.Objects;

public class IEPhasePosition implements Serializable {

	private Integer crID;
	private ChangeInitiator informationEngineer;
	private Phase.PhaseName phaseName;
	private PhasePosition phasePosition;

	public enum PhasePosition {
		EVALUATOR,
		EXECUTIVE_LEADER,
		TESTER,
		PHASE_LEADER
	}

	/**
	 * gets change request id
	 * @return change request id
	 */
	public Integer getCrID() {
		return crID;
	}

	public void setCrID(Integer crID) {
		this.crID = crID;
	}
	/**
	 * gets information engineer
	 * @return information engineer
	 */
	public ChangeInitiator getInformationEngineer() {
		return this.informationEngineer;
	}

	/**
	 * 
	 * @param informationEngineer
	 */
	public void setInformationEngineer(ChangeInitiator informationEngineer) {
		this.informationEngineer = informationEngineer;
	}
	/**
	 * gets phase name
	 * @return phase name
	 */
	public Phase.PhaseName getPhaseName() {
		return this.phaseName;
	}

	/**
	 * 
	 * @param phaseName
	 */
	public void setPhaseName(Phase.PhaseName phaseName) {
		this.phaseName = phaseName;
	}
	/**
	 * gets phase position
	 * @return phase position
	 */
	public PhasePosition getPhasePosition() {
		return this.phasePosition;
	}

	/**
	 * 
	 * @param phasePosition
	 */
	public void setPhasePosition(PhasePosition phasePosition) {
		this.phasePosition = phasePosition;
	}

	/**
	 * checks if two objects are equal
	 */
	@Override
	public boolean equals(Object o) {
		return super.equals(o);
	}
	/**
	 * return hash code for an object
	 */
	@Override
	public int hashCode() {
		return super.hashCode();
	}
}