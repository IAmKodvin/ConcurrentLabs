package lift;
import java.util.Random;

public class PassengerThread extends Thread {
	Passenger passenger;
	Random rnd;
	Monitor monitor;
	LiftViewThread view;
	int start;
	int destination;
	
	public PassengerThread(LiftViewThread view, Monitor monitor) {
		super();
		this.rnd = new Random();
		this.view = view;
		this.monitor = monitor;
	
	}
	
	@Override
	public void run(){
		while(true) {
		try {
			this.passenger = view.createPassenger();		
			this.start = passenger.getStartFloor();
			this.destination = passenger.getDestinationFloor();
			sleep(rnd.nextInt(46)*1000);
			this.passenger.begin();
			monitor.waitOnLiftAndEnter(passenger, start, destination);
			monitor.waitInLiftAndExit(passenger, destination);
			passenger.end();
			
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
    }
	
}
