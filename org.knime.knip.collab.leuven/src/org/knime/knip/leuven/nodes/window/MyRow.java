package org.knime.knip.leuven.nodes.window;

public class MyRow {
	public double xHead;
	public double yHead;
	public double xTail;
	public double yTail;
	public String wellPosition;
	public int timestamp;
	public boolean interpolated;
	
	public MyRow(double xHead, double yHead, double xTail, double yTail, String wellPosition, int timestamp, boolean interpolated) {
		this.xHead = xHead;
		this.yHead = yHead;
		this.xTail = xTail;
		this.yTail = yTail;
		this.wellPosition = wellPosition;
		this.timestamp = timestamp;
		this.interpolated = interpolated;
	}
}
