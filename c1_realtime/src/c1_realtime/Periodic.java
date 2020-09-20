package c1_realtime;

public class Periodic extends Thread{
	private int period;
	
	public Periodic(int period) {
		this.period = period;
		
	}
	public void run() {
		try {
			System.out.println(String.format("prio=%d, period=%d", this.getPriority(), period));
			this.setPriority(getPriority() + 1);
			while(!this.isInterrupted()) {
				System.out.print(period);
			    System.out.print(", ");
			    Thread.sleep(period);
			}
		} catch (InterruptedException e) {
			
		}
		System.out.print("Thread got interrupted!");

	}
}
