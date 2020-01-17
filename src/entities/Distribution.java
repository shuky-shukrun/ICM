package entities;

public class Distribution {
	private int num;
	private int dis;
	private InfoSystem infoSystem;
	public Distribution(int num,int dis,InfoSystem infoSystem) {
		this.dis=dis;
		this.num=num;
		this.infoSystem=infoSystem;
	}
	public InfoSystem getInfoSystem() {
		return infoSystem;
	}
	public void setInfoSystem(InfoSystem infoSystem) {
		this.infoSystem = infoSystem;
	}
	public int getNum() {
		return num;
	}
	public void setNum(int num) {
		this.num = num;
	}
	public int getDis() {
		return dis;
	}
	public void setDis(int dis) {
		this.dis = dis;
	}
	public String toString() {
		if(this.infoSystem==null)
			return this.num+"="+this.dis;
		return this.num+"="+this.dis+" for "+this.infoSystem.toString();
				
	}
	
}
