package entities;

import java.io.Serializable;
import java.util.Objects;

public class IEPhasePosition implements Serializable {

	private Integer crID;
	private InformationEngineer informationEngineer;
	private Phase.PhaseName phaseName;
	private PhasePosition phasePosition;

	public enum PhasePosition {
		EVALUATOR,
		EXECUTIVE_LEADER,
		TESTER,
		PHASE_LEADER
	}


	public Integer getCrID() {
		return crID;
	}

	public void setCrID(Integer crID) {
		this.crID = crID;
	}

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


	@Override
	public boolean equals(Object o) {
		return super.equals(o);
	}

	@Override
	public int hashCode() {
		return super.hashCode();
	}
}