
public class BigData {
	private long alarmTime;
	private int h;
	private int m;
	private int s;
	private boolean alarmOn;
	
	public BigData() {
		this.h = 0;
		this.m = 0;
		this.s = 0;
		this.alarmTime = 000000;
		this.alarmOn = false;
	}
	
	public int getDisplayTime() {
		return h*10000 + m*100 + s;
	}
	
	public void setDisplayTime(int newTime) {
		h = newTime / 10000;
		newTime = newTime - h*10000;
		m = newTime / 100;
		newTime = newTime - m*100;
		s = newTime;
	}
	
	public void updateDisplayTime() {
		s ++;
		if (s > 59){
			s = 0;
			m++;
		}
		if (m > 59){
			m = 0;
			h++;
		}
		if (h > 23){
			h = 0;
			m = 0;
			s = 0;
		}
	}
	
/*
	private long convertToMilli(int time) {
		String s = Integer.toString(time);
		long millis = 0
		for (int i = 0; i < s.length(); i++){
			int nbr = 
			if(i<2) {
				millis += s.charAt(i)
			}
		    char c = s.charAt(i);        
		    //Process char
		}
	}
*/
	public long getAlarmTime() {
		return alarmTime;
	}
	
	public void setAlarmTime(long newAlarmTime) {
		alarmTime = newAlarmTime;
	}
	
	public boolean getAlarmOn() {
		return alarmOn;
	}
	
	public void setAlarmOn() {
		alarmOn = true;
	}
	
	public void setAlarmOff() {
		alarmOn = false;
	}
	
	
}
