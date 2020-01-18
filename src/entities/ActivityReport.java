package entities;

import java.util.Map;

public class ActivityReport extends Report {

	private double median;
	private double standardDeviation;
	private Map distributionFrequency;
	/**
	 * Gets median
	 * @return median
	 */
	public double getMedian() {
		return this.median;
	}

	/**
	 * Sets median
	 * @param median
	 */
	public void setMedian(double median) {
		this.median = median;
	}
	/**
	 * Gets standard deviation
	 * @return standard deviation
	 */
	public double getStandardDeviation() {
		return this.standardDeviation;
	}

	/**
	 * Sets standard deviation 
	 * @param standardDeviation
	 */
	public void setStandardDeviation(double standardDeviation) {
		this.standardDeviation = standardDeviation;
	}
	/**
	 * Gets distribution frequency
	 * @return distribution frequency
	 */
	public Map getDistributionFrequency() {
		return this.distributionFrequency;
	}

	/**
	 * Sets distribution frequency
	 * @param distributionFrequency
	 */
	public void setDistributionFrequency(Map distributionFrequency) {
		this.distributionFrequency = distributionFrequency;
	}

}