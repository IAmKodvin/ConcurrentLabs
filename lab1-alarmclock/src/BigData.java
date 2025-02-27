import java.util.concurrent.Semaphore;
import clock.ClockOutput;


public class BigData {
	private long alarmTime;
	private int h;
	private int m;
	private int s;
	private int count;
	private boolean alarmOn;
	private Semaphore mutex;
	int currentTime;
	private ClockOutput out;


	public BigData(ClockOutput out) {
		this.h = 0;
		this.m = 0;
		this.s = 0;
		this.count = 0;
		this.alarmTime = 000000;
		this.alarmOn = false;
		this.mutex = new Semaphore(1);
		this.out = out;
	}

	public int getDisplayTime() {
		return h*10000 + m*100 + s;
	}

	public void setDisplayTime(int newTime) {
		try {
			mutex.acquire();
		} catch (InterruptedException e) {
			throw new Error(e);
		}
		h = newTime / 10000;
		newTime = newTime - h*10000;
		m = newTime / 100;
		newTime = newTime - m*100;
		s = newTime;
		mutex.release();
	}

	public void updateDisplayTime() throws InterruptedException {
		mutex.acquire();
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
		mutex.release();
	}

	public long getAlarmTime() {
		return alarmTime;
	}

	public void setAlarmTime(long newAlarmTime) {
		try {
			mutex.acquire();
		} catch (InterruptedException e) {
			throw new Error(e);
		}
		alarmTime = newAlarmTime;
		count = 0;
		mutex.release();
	}

	public boolean getAlarmOn() {
		return alarmOn;
	}

	public void setAlarmOn() throws InterruptedException {
		mutex.acquire();
		out.setAlarmIndicator(true);
		alarmOn = true;
		mutex.release();
	}

	public void setAlarmOff() throws InterruptedException {
		mutex.acquire();
		out.setAlarmIndicator(false);
		count = 0;
		alarmOn = false;
		mutex.release();
	}

	public void tic() throws InterruptedException {

		//System.out.println(now);
		updateDisplayTime();
		try {
			mutex.acquire();
		} catch (InterruptedException e) {
			throw new Error(e);
		}

		currentTime =  getDisplayTime();
		alarmTime = getAlarmTime();

		//System.out.println(currentTime);
		out.displayTime(currentTime);
		mutex.release();
		checkAlarm();

	}

	public void checkAlarm() throws InterruptedException {
		mutex.acquire();
		if (alarmOn && ((currentTime==alarmTime) || (0 < count && count<20))) {
			out.alarm();
			
			count ++;
			
		}

		if (alarmOn && count == 20)  {
			setAlarmOff();
		}
		mutex.release();

	}

}
