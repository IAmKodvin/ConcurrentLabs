package lift;
public class Monitor {
	private int here; // If here!=next, here (floor number) tells from which floor
	// the lift is moving and next to which floor it is moving.
	private int next; // If here==next, the lift is standing still on the floor
	// given by here.
	private int increment;
	private int[] waitEntry; // Number of passengers waiting to enter the lift at the
	// various floors.
	private int[] waitExit; // Number of passengers (inside the lift) waiting to leave
	// the lift at the various floors.
	private int load; // Number of passengers currently in the lift.
	private boolean passangerAction;
	
	public Monitor(){
		this.here = 0;
		this.next = 0;
		this.load = 0;
		this.increment = 1;
		waitEntry = new int[7];
		waitExit = new int[7];
	}
	
	public synchronized void receiveWaitingOnLift(int start, int destination) throws InterruptedException {
		waitEntry[start]++;
		System.out.println("Person arrived");
		notifyAll();
		while((start != getHere()) || ( (start == getHere()) && (getLoad() == 4) )) {
			System.out.println("Person waiting");
			wait();
		} 
		System.out.println("Done waiting, start=" + start + " destination=" + destination );
		
	}
	
	public synchronized void receiveWaitingOnExit(int start, int destination) throws InterruptedException {
		waitExit[destination]++;
		notifyAll();
	}
	
	public synchronized int getHere() {
		return here;
	}
	
	public synchronized int getLoad() {
		return load;
	}
	
	public synchronized void updateHere(int here) {
		//System.out.println("Updated here:" + here);
		this.here = here;
		notifyAll();
	}
	public synchronized void updateNext(int next) {
		this.next = next;
		notifyAll();
	}
	public synchronized void increaseLoad() {
		this.load++;
		notifyAll();
	}
	
	public synchronized void decreaseLoad() {
		this.load--;
		notifyAll();
	}
	
	public synchronized void increaseExit(int floor) {
		this.waitExit[floor]++;
		notifyAll();
	}
	
	public synchronized void decreaseExit(int floor) {
		this.waitExit[floor]--;
		notifyAll();
	}
	
	public synchronized void decreaseWaiting(int floor) {
		this.waitEntry[floor]--;
		notifyAll();
	}
	
	public synchronized boolean checkFloorEntering() {
		if(waitEntry[here] > 0 && (load < 4)) {
			System.out.println("Enter true");
			return true;
		} else {
			return false;
		}
	}
	
	public synchronized boolean checkFloorExiting() {
		if(waitExit[here] > 0 ) {
			System.out.println("Exit true");
			return true;
		} else {
			return false;
		}
	}

	public synchronized void embark(Passenger passenger, int floor, int destination) {
		passenger.enterLift();
		increaseExit(destination);
		increaseLoad();
		decreaseWaiting(floor);
	}
	
	public synchronized void waitInLiftAndExit(Passenger passenger, int destination) throws InterruptedException {
		while(getHere() != destination) {
			wait();
		}
		passenger.exitLift();
		decreaseLoad();
		decreaseExit(destination);
	}
	
	public synchronized int[] moveLift(LiftView view) throws InterruptedException {
		int[] a = new int[2];
		
		if(getHere() == 6) {
			increment = -1;
		}
		if(getHere() == 0) {
			increment = 1;
		}
		
		next = here + increment;
		a[0] = here;
		a[1] = next;
		updateNext(next);
		return a;
	}
	
	public synchronized void liftStopCheck() throws InterruptedException {
		while( checkFloorEntering() || checkFloorExiting() ) {
				System.out.println("Stopcheck waiting");
				wait();
		}
	}
	
	
}
