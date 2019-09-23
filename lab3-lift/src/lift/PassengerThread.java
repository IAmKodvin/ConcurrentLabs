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
			monitor.receiveWaitingOnLift(start, destination);
			monitor.embark(passenger, start, destination);		
			monitor.waitInLiftAndExit(passenger, destination);
			passenger.end();
			
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
        
    }

	private void walkToLift() throws InterruptedException {
		sleep(rnd.nextInt(46)*1000);
		this.passenger.begin();
		//sleep(3500);
	}
	
	
}
