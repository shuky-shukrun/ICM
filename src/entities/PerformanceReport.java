package entities;

public class PerformanceReport extends Report {

	private int extensionDuration;
	private int repeatedPhasesDuration;
	/**
	 * gets extension duration
	 * @return extension duration
	 */
	public int getExtensionDuration() {
		return this.extensionDuration;
	}

	/**
	 * 
	 * @param extensionDuration
	 */
	public void setExtensionDuration(int extensionDuration) {
		this.extensionDuration = extensionDuration;
	}
	/**
	 * gets repeated phase duration
	 * @return repeated phase duration
	 */
	public int getRepeatedPhasesDuration() {
		return this.repeatedPhasesDuration;
	}

	/**
	 * 
	 * @param repeatedPhasesDuration
	 */
	public void setRepeatedPhasesDuration(int repeatedPhasesDuration) {
		this.repeatedPhasesDuration = repeatedPhasesDuration;
	}

}