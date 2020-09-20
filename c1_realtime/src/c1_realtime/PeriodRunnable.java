package c1_realtime;

public class PeriodRunnable extends Base implements Runnable  {

	private int period;
	
	public PeriodRunnable(int period) {
		this.period = period;
	}
	
	public void run() {
		try {
			System.out.println(String.format("prio=%d, period=%d", Thread.currentThread().getPriority(), period));
			Thread.currentThread().setPriority(Thread.currentThread().getPriority() + 1);
			while(true) {
				System.out.print(period);
			    System.out.print(", ");
			    Thread.sleep(period);
			}
		} catch (InterruptedException e) {
			
		}
		System.out.print("Thread got interrupted!");

	}
}
