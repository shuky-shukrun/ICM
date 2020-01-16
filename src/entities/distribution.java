package entities;

public class distribution {
	private int num;
	private int dis;
	public distribution(int num,int dis) {
		this.dis=dis;
		this.num=num;
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
		return this.num+"="+this.dis;
	}
	
}
