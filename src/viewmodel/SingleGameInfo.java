package viewmodel;

import processing.core.PApplet;
import processing.core.PImage;

public class SingleGameInfo {
	//game info
	float totalStartX, totalEndX;
	float zeroDiffY;
	
	int maxTime;
	int maxQuarter;
	int timeHover;
	String timeDisplay; 
	
	String leftName;
	String rightName;
	PImage leftImg;
	PImage rightImg;

	public SingleGameInfo (float totalStartX, float totalEndX, float zeroDiffY, int maxQuarter, String leftName, String rightName) {
		this.maxQuarter = maxQuarter;
		this.totalStartX = totalStartX;
		this.totalEndX = totalEndX;
		this.zeroDiffY = zeroDiffY;
		this.leftName = leftName;
		this.rightName = rightName;
	}
	public String setTimeDisplay(int timeHover){
		if (maxQuarter == 4) {
			this.maxTime = 4 * 720;
		} else {
			this.maxTime = (maxQuarter - 4) * 300 + 2880;
		}
		
		String quarter;
		if(timeHover<=720){
			quarter = "1st";
		}
		else if(timeHover<= 720*2){
			quarter =  "2nd";
			timeHover = timeHover-720;
		}
		else if(timeHover<=720*3){
			quarter =  "3rd";
			timeHover = timeHover-720*2;
		}
		else if(timeHover<=720*4){
			quarter =  "4th";
			timeHover = timeHover-720*3;
		}
		else{
			quarter =  "OT";
			timeHover = timeHover-720*4;
		}
		
		int minute = timeHover/60;
	 	int second = timeHover%60;
	 	
		if(minute<10&&second<10)
			timeDisplay = quarter + timeDisplay + "0"+minute+":0"+second;
		if(minute<10&&second>=10)
			timeDisplay = quarter + "0"+minute+":"+second;
		if(minute>=10&&second<10)
			timeDisplay = quarter + minute+":0"+second;
		else
			timeDisplay = quarter + "  "+minute+":"+second;
		
		return timeDisplay;
	}
	
	public void draw(SingleGameCanvas canvas, boolean zoomOut){
		//game info
		//1time
		timeHover = (int) PApplet.map(canvas.mouseX, totalStartX, totalEndX, 0, maxTime);
		this.timeDisplay = setTimeDisplay(timeHover);
		canvas.stroke(220);

		//two time line
		canvas.strokeWeight(1);
		canvas.line(totalStartX, zeroDiffY, totalEndX, zeroDiffY);
		//canvas.line(totalStartX, zeroDiffY + 300, totalEndX, zeroDiffY + 300);

		canvas.fill(220);
		if(!zoomOut&&canvas.mouseX>=totalStartX&&canvas.mouseX<=totalEndX){
			if(canvas.mouseY<=zeroDiffY){	
				canvas.beginShape();
					canvas.vertex(canvas.mouseX, zeroDiffY);
					canvas.vertex(canvas.mouseX-5, zeroDiffY+5);
					canvas.vertex(canvas.mouseX+5, zeroDiffY+5);
				canvas.endShape(PApplet.CLOSE);
				canvas.fill(180);
				canvas.textSize(20);
				canvas.textAlign(PApplet.CENTER);
				canvas.text(timeDisplay, canvas.mouseX+18, zeroDiffY - 5);	
			} else{
				canvas.beginShape();
					canvas.vertex(canvas.mouseX, zeroDiffY);
					canvas.vertex(canvas.mouseX-5, zeroDiffY+5);
					canvas.vertex(canvas.mouseX+5, zeroDiffY+5);
				canvas.endShape(PApplet.CLOSE);
				canvas.fill(180);
				canvas.textSize(20);
				canvas.textAlign(PApplet.CENTER);
				canvas.text(timeDisplay, canvas.mouseX+18, zeroDiffY - 5);	
			}
		}
		canvas.textAlign(PApplet.CENTER);
		
		
		//1time quarter
		for(int i=0; i<=maxQuarter; i++){
			canvas.strokeWeight(5);
			canvas.strokeCap(PApplet.PROJECT);
			//canvas.line(PApplet.map(i*720, 0, maxTime, totalStartX, totalEndX), zeroDiffY-302, PApplet.map(i*720, 0, maxTime, totalStartX, totalEndX), zeroDiffY-310);
			canvas.line(PApplet.map(i*720, 0, maxTime, totalStartX, totalEndX), zeroDiffY+5, PApplet.map(i*720, 0, maxTime, totalStartX, totalEndX), zeroDiffY-5);
		}
		
		//2team
		canvas.fill(30);
		canvas.imageMode(PApplet.CENTER);
		leftImg = canvas.loadImage("./img/"+leftName+".jpg");
		rightImg = canvas.loadImage("./img/"+rightName+".jpg");
		canvas.image(leftImg, 100, zeroDiffY-100, 90, 90);
		canvas.image(rightImg, 100, zeroDiffY+100, 90, 90);
		canvas.textAlign(PApplet.CENTER);
		canvas.textSize(40);
		canvas.text("VS", 100,zeroDiffY+10);
		
		//
		canvas.textSize(20);
		canvas.text("Players: "+leftName, 100,zeroDiffY-350);
		canvas.text("Players: "+rightName, 100,zeroDiffY+363);
		
		//Legend
		canvas.textSize(11);
		canvas.shape(canvas.twoPts, 800+600, zeroDiffY-280-200, 25, 25);
		canvas.text("2 points", 800+600, zeroDiffY-250-200);
		canvas.shape(canvas.threePts, 850+600, zeroDiffY-280-200, 25, 25);
		canvas.text("3 points", 850+600, zeroDiffY-250-200);
		canvas.shape(canvas.free, 900+600, zeroDiffY-280-200, 25, 25);
		canvas.text("free", 900+600, zeroDiffY-250-200);
		
		canvas.shape(canvas.ofRbd, 950+600, zeroDiffY-270-200, 30, 30);		
		canvas.text("offensive", 950+600, zeroDiffY-250-200);
		canvas.text("rebound", 950+600, zeroDiffY-240-200);
		canvas.shape(canvas.defRbd, 1000+600, zeroDiffY-270-200, 30, 30);
		canvas.text("defensive", 1000+600, zeroDiffY-250-200);
		canvas.text("rebound", 1000+600, zeroDiffY-240-200);
		
		canvas.shape(canvas.turnover, 1050+600, zeroDiffY-280-200, 25, 25);
		canvas.text("turnover", 1050+600, zeroDiffY-250-200);
		canvas.shape(canvas.ast, 1100+600, zeroDiffY-280-200, 25, 25);
		canvas.text("assist", 1100+600, zeroDiffY-250-200);
		canvas.shape(canvas.blk, 1150+600, zeroDiffY-280-200, 25, 25);
		canvas.text("block", 1150+600, zeroDiffY-250-200);
		canvas.shape(canvas.stl, 1200+600, zeroDiffY-280-200, 25, 25);
		canvas.text("steal", 1200+600, zeroDiffY-250-200);
		canvas.shape(canvas.foul, 1250+600, zeroDiffY-280-200, 25, 25);
		canvas.text("foul", 1250+600, zeroDiffY-250-200);
	}
	
	
}
