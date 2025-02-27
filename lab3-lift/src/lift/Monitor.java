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
	
	public Monitor(){
		this.here = 0;
		this.next = 0;
		this.load = 0;
		this.increment = 1;
		waitEntry = new int[7];
		waitExit = new int[7];
	}
	
	public synchronized void waitOnLiftAndEnter(Passenger passenger, int start, int destination) throws InterruptedException {
		waitEntry[start]++;
		System.out.println("Person arrived on floor" + start);
		notifyAll();
		// destination-start is direction of travel, increment*(direction) < 0 WAIT
		while((start != here) || (here!=next) || (load == 4)) {
			System.out.println("Person waiting");
			wait();
		}
		passenger.enterLift();
		increaseExit(destination);
		increaseLoad();
		decreaseWaiting(start);
		System.out.println("Done waiting, start=" + start + " destination=" + destination );
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
	
	public synchronized void waitInLiftAndExit(Passenger passenger, int destination) throws InterruptedException {
		while(getHere() != destination) {
			wait();
		}
		passenger.exitLift();
		decreaseLoad();
		decreaseExit(destination);
	}
	
	public synchronized boolean activePassengers() {
		for(int i=0; i<7; i++) {
			if((waitEntry[i] != 0) || (waitExit[i] !=0)) {
				return true;
			}
		}
		return false;
	}
	
	public synchronized int[] moveLift(LiftView view) throws InterruptedException {
		// Lift is staying still on floor here == next
		here = next;
		notifyAll();
		while( checkFloorEntering() || checkFloorExiting() || !activePassengers()) {
			System.out.println("Stopcheck waiting");
			wait();
		}

		int[] a = new int[2];
		
		// if no one wants to enter or exit in direction of lift travel, switch increment
		if(here == 6) {
			increment = -1;
		}
		if(here == 0) {
			increment = 1;
		}
		
		next = here + increment;
		notifyAll();
		a[0] = here;
		a[1] = next;

		return a;
	}
	
	
}
