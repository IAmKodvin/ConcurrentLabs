import java.util.concurrent.Semaphore;

import clock.ClockOutput;

public class UpdateClock implements Runnable{
	Semaphore sem1;
	ClockOutput out;
	BigData bigData;
	int count;

	public UpdateClock(ClockOutput out, BigData bigData) {
		this.sem1 = sem1;
		this.out = out;
		this.bigData = bigData;
		this.count = 0;		
	}

	private int convertTime(long time) {

		String h = Long.toString( (time / (1000*3600) + 2) % 24);
		String m = Long.toString((time % (1000*3600)) / (1000*60));
		String s = Long.toString((time % (1000*60)) / (1000));
		//System.out.println(h + ", " +  m + ", " + s);
		return Integer.parseInt(h + m + s);
	}

	public void run() {

		long nextStop = System.currentTimeMillis();
		while(true) {
			nextStop = nextStop + 1000;
			try {
				bigData.tic();
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			try {
				Thread.sleep(nextStop - System.currentTimeMillis());
			} catch (InterruptedException e) {
				throw new Error(e);
			}

		}

	}
}
