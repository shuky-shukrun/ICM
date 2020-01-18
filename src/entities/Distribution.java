package entities;

public class Distribution {
	private int num;
	private int dis;
	private InfoSystem infoSystem;
	/**
	 * constructor that creates new distribution
	 * @param num
	 * @param dis
	 * @param infoSystem
	 */
	public Distribution(int num,int dis,InfoSystem infoSystem) {
		this.dis=dis;
		this.num=num;
		this.infoSystem=infoSystem;
	}
	/**
	 * Gets the info system
	 * @return info system
	 */
	public InfoSystem getInfoSystem() {
		return infoSystem;
	}
	/**
	 * sets the info system
	 * @param infoSystem
	 */
	public void setInfoSystem(InfoSystem infoSystem) {
		this.infoSystem = infoSystem;
	}
	/**
	 * Gets the value 
	 * @return value
	 */
	public int getNum() {
		return num;
	}
	/**
	 * sets the value
	 * @param value
	 */
	public void setNum(int num) {
		this.num = num;
	}
	/**
	 * Gets the distribution 
	 * @return distribution
	 */
	public int getDis() {
		return dis;
	}
	/**
	 * sets the distribution
	 * @param distribution
	 */
	public void setDis(int dis) {
		this.dis = dis;
	}
	/**
	 * returns a string that describes the object
	 */
	public String toString() {
		if(this.infoSystem==null)
			return this.num+"="+this.dis;
		return this.num+"="+this.dis+" for "+this.infoSystem.toString();
				
	}
	
}
