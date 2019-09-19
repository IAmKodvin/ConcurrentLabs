package lift;

public class LiftThread extends LiftView implements Runnable {
	private PassengerThread[] passengers;
	private Thread[] threads;
	
	public LiftThread() {
		super();
		
		passengers = new PassengerThread[20];
		for(int i=0; i<20; i++) {
			passengers[i] = new PassengerThread();
		}
		threads = new Thread[20];
		for(int i=0; i<20; i++) {
			threads[i] = new Thread(passengers[i].run());
		}
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		
	}

}
