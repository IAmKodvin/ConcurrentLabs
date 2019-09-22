package lift;
public class Monitor {
	private int here; // If here!=next, here (floor number) tells from which floor
	// the lift is moving and next to which floor it is moving.
	private int next; // If here==next, the lift is standing still on the floor
	// given by here.
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
		waitEntry = new int[7];
		waitExit = new int[7];
	}
	
	public synchronized void receiveWaitingOnLift(int from) {
		waitEntry[from]++;
		notifyAll();
	}
	
	public synchronized void receiveWaitingOnExit(int destination) {
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
		System.out.println("Updated here:" + here);
		this.here = here;
		notifyAll();
	}
	public synchronized void updateNext(int next) {
		this.next = next;
		notifyAll();
	}
	public synchronized void updateLoad(int load) {
		this.load = load;
		notifyAll();
	}
	
	public synchronized boolean checkFloorEntering() {
		if(waitEntry[here] == 0 || load == 4) {
			return false;
		} else {
			return true;
		}
	}
	
	public synchronized boolean checkFloorExiting() {
		if(waitExit[here] == 0 || load == 0) {
			return false;
		} else {
			return true;
		}
	}
	
	public synchronized boolean getPassengerAction() {
		return passangerAction;
	}
	public synchronized void updatePassengerAction(boolean action) {
		this.passangerAction = action;
		notifyAll();
	}
	
}
