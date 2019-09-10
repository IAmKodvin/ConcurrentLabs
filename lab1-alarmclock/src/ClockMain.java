import java.util.concurrent.Semaphore;

import clock.ClockInput;
import clock.ClockInput.UserInput;
import clock.ClockOutput;
import emulator.AlarmClockEmulator;

class UpdateClock implements Runnable{
	Semaphore sem0;
	Semaphore sem1;
	ClockOutput out;
	BigData bigData;
	int count;
	
	public UpdateClock(ClockOutput out, BigData bigData, Semaphore sem1,  Semaphore sem0) {
		this.sem0 = sem0;
		this.sem1 = sem1;
		this.out = out;
		this.bigData = bigData;
		this.count = 0;
		//test
		
	}
	
	private int convertTime(long time) {
		
		String h = Long.toString( (time / (1000*3600) + 2) % 24);
		String m = Long.toString((time % (1000*3600)) / (1000*60));
		String s = Long.toString((time % (1000*60)) / (1000));
		//System.out.println(h + ", " +  m + ", " + s);
		return Integer.parseInt(h + m + s);
	}
	
	public void run() {
		
		long nextStop = System.currentTimeMillis();
		try {
			while(true) {
				sem1.acquire();
				nextStop = nextStop + 1000;
				
				long now = System.currentTimeMillis();
				//System.out.println(now);
				bigData.updateDisplayTime();
				
				int currentTime =  bigData.getDisplayTime();
				long alarmTime = bigData.getAlarmTime();
				
				
				System.out.println(currentTime);
				out.displayTime(currentTime);
				
				// alarm indication
				if (bigData.getAlarmOn() & (currentTime == alarmTime + 20)) {
					bigData.setAlarmOff();
					count = 0;
				}
		
				if ((alarmTime <= currentTime & alarmTime <= currentTime + 20) & bigData.getAlarmOn()) {
						out.alarm();
						count += 1;
				}

				sem1.release();
				Thread.sleep(nextStop - System.currentTimeMillis());
				
			}
		} catch (InterruptedException e) {
			System.out.println("Exception in thread: "+ e.getMessage());
		}
	}
}

public class ClockMain {

    public static void main(String[] args) throws InterruptedException {
        AlarmClockEmulator emulator = new AlarmClockEmulator();

        ClockInput  in  = emulator.getInput();
        ClockOutput out = emulator.getOutput();
        BigData bigData = new BigData();
        
        System.out.println(in);
        System.out.println(out);
        
        out.displayTime(150237);   // arbitrary time: just an example
       
        
        Semaphore sem0 = in.getSemaphore();
        Semaphore sem1 = new Semaphore(1);
        
        UpdateClock updateThread = new UpdateClock(out, bigData, sem1, sem0);
        Thread t = new Thread(updateThread);
        t.start();
        
        while (true) {
            sem0.acquire();                        // wait for user input

            UserInput userInput = in.getUserInput();
            int choice = userInput.getChoice();
            int value = userInput.getValue();
            
            
            switch (choice) {
            	// set time
            	case 1:
            		sem1.acquire();
            		System.out.println("Set new time" + value);
            		
            		bigData.setDisplayTime(value);
            		sem1.release();
            		break;
            	//set new alarmtime
            	case 2:
               		sem1.acquire();
            		System.out.println("Set new Alarm" + value);
            		
            		bigData.setAlarmTime(value);
            		//bigData.setAlarmOn();
            		//out.setAlarmIndicator(true);
            		sem1.release();
            		break;
            	// pressed both
            	case 3:
            		if(bigData.getAlarmOn()) {
            			out.setAlarmIndicator(false);
                		bigData.setAlarmOff();
            		} else {
            			out.setAlarmIndicator(true);
            			bigData.setAlarmOn();
            		}
            		
            		break;
            	// user pressed a button but did not change time
            	case 4:
            		System.out.println("no change");
            		break;
            }

            //System.out.println("choice = " + choice + "  value=" + value);
            System.out.println("");
        }
    }
}
