package lift;

public class LiftViewThread extends Thread{
	private PassengerThread[] passengers;
	private LiftView view;
	private Monitor monitor;
	private int here;
	private int next;
	private int increment;
	
	public LiftViewThread(Monitor monitor) {
		super();
		this.view = new LiftView();
		
		this.monitor = monitor;
		this.here = 0;
		this.next = 0;
		this.increment = 1;
	}

	@Override
	public void run() {
		while(true){
				try {
					monitor.liftStopCheck();
					int[] a = monitor.moveLift(view);
					// monitor.liftMoving(true)
					view.moveLift(a[0], a[1]);
					monitor.updateHere(a[1]);
					//
					
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
	}
	
	public Passenger createPassenger() {
		return this.view.createPassenger();
	}
	
	
	
	

}
