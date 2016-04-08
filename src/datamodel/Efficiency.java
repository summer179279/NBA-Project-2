package datamodel;

public class Efficiency {
	float ratio;
	int value;
	int startTimeIndex, endTimeIndex;
	
	public Efficiency (float ratio, int value) {
		this.ratio = ratio;
		this.value = value;
	}
	public void setTimeIndex (int startTimeIndex, int endTimeIndex) {
		this.startTimeIndex = startTimeIndex;
		this.endTimeIndex = endTimeIndex;
	}
	
	public float getRatio () {
		return ratio;
	}
	public int getValue () {
		return value;
	}
	public int getStartTimeIndex () {
		return startTimeIndex;
	}
	public int getEndTimeIndex () {
		return endTimeIndex;
	}
}
