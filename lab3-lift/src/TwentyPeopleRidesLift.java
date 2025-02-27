import lift.LiftView;
import lift.Monitor;
import lift.LiftViewThread;
import lift.Passenger;
import lift.PassengerThread;

public class TwentyPeopleRidesLift {

	public static void main(String[] args) {

		Monitor monitor = new Monitor();
		LiftViewThread view = new LiftViewThread(monitor);
		Thread viewThread = new Thread(view);
		viewThread.start();
		
		PassengerThread[] passengers = new PassengerThread[20];
		for(int i=0; i<20; i++) {
			passengers[i] = new PassengerThread(view, monitor);
			passengers[i].start();
		}

	}

}
