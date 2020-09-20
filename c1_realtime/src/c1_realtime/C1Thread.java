package c1_realtime;

public class C1Thread {
	
	public void runnable() {
		int[] period = {500, 600, 1000, 1200, 1500, 2000, 400};
		for(int i = 0; i<period.length; i++) {
			new Thread(new PeriodRunnable(period[i])).start();
		}
	}
	public void extended() {
		Periodic p1 = new Periodic(100);
		p1.start();
		int[] period = {500, 600, 1000, 1200, 1500, 2000, 400};
		for(int i = 0; i<period.length; i++) {
			new Periodic(period[i]).start();
		}
	}
	
	public static void main(String[] args){
		C1Thread c = new C1Thread();
		c.runnable();
	}

}




