package entities;

import java.io.Serializable;
import java.time.LocalDate;

public class EvaluationReport implements Serializable {

	private int reportId;
	private InfoSystem infoSystem;
	private String requiredChange;
	private String expectedResult;
	private String risksAndConstraints;
	private LocalDate evaluatedTime;

	/**
	 * gets report id
	 * @return report id
	 */
	public int getReportId() {
		return this.reportId;
	}

	/**
	 * 
	 * @param reportId
	 */
	public void setReportId(int reportId) {
		this.reportId = reportId;
	}
	/**
	 * gets info system
	 * @return info system
	 */
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
	/**
	 * Gets required change
	 * @return required change
	 */
	public String getRequiredChange() {
		return this.requiredChange;
	}

	/**
	 * 
	 * @param requiredChange
	 */
	public void setRequiredChange(String requiredChange) {
		this.requiredChange = requiredChange;
	}
	/**
	 * Gets expected result
	 * @return expected result
	 */
	public String getExpectedResult() {
		return this.expectedResult;
	}

	/**
	 * 
	 * @param expectedResult
	 */
	public void setExpectedResult(String expectedResult) {
		this.expectedResult = expectedResult;
	}
	/**
	 * Gets risks and constraints
	 * @return risks and constraints
	 */
	public String getRisksAndConstraints() {
		return this.risksAndConstraints;
	}

	/**
	 * 
	 * @param risksAndConstraints
	 */
	public void setRisksAndConstraints(String risksAndConstraints) {
		this.risksAndConstraints = risksAndConstraints;
	}
	/**
	 * Gets evaluated time
	 * @return evaluated time
	 */
	public LocalDate getEvaluatedTime() {
		return evaluatedTime;
	}

	/**
	 * 
	 * @param EvaluatedTime
	 */
	public void setEvaluatedTime(LocalDate EvaluatedTime) {
		this.evaluatedTime = EvaluatedTime;
	}

}