package lift;
import java.util.Random;

public class PassengerThread extends Thread {
	Passenger passenger;
	Random rnd;
	Monitor monitor;
	int start;
	int destination;
	
	public PassengerThread(LiftViewThread view, Monitor monitor) {
		super();
		this.rnd = new Random();
		this.passenger = view.createPassenger();
		this.monitor = monitor;
		
		this.start = passenger.getStartFloor();
		this.destination = passenger.getDestinationFloor();
	}
	
	@Override
	public void run(){
		try {
			walkToLift();
			waitOnLift();
			walkIntoLift();
			waitInLift();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
        
    }
	
	private void walkToLift() throws InterruptedException {
		sleep(rnd.nextInt(46)*1000);
		this.passenger.begin();              // walk in (from left)
		sleep(4000);
	}
	
	private void waitOnLift() throws InterruptedException {
		monitor.receiveWaitingOnLift(start);
		while((start != monitor.getHere()) && (monitor.getLoad() == 4)) {
			wait();
		}
	}
	
	private void walkIntoLift() throws InterruptedException {
		while(monitor.getPassengerAction()) {
			wait();
		}
		System.out.println("Entering Lift Floor " + start);
		monitor.updatePassengerAction(true);
		this.passenger.enterLift();
		monitor.receiveWaitingOnExit(destination);
		monitor.updateLoad(1);
		monitor.updatePassengerAction(false);
		

	}
	
	private void waitInLift() throws InterruptedException{
		while(monitor.getHere() != destination && monitor.getPassengerAction()) {
			wait();
		}
		this.monitor.updatePassengerAction(true);
		this.passenger.exitLift();
		this.monitor.updatePassengerAction(false);
		monitor.updateLoad(-1);
		this.passenger.end();
	}
	
	private void exitLift() {
		
	}


}
