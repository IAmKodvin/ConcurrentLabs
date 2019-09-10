import java.util.concurrent.Semaphore;

import clock.ClockInput;
import clock.ClockInput.UserInput;
import clock.ClockOutput;
import emulator.AlarmClockEmulator;

public class ClockMain {

    public static void main(String[] args) throws InterruptedException {
        AlarmClockEmulator emulator = new AlarmClockEmulator();

        ClockInput  in  = emulator.getInput();
        ClockOutput out = emulator.getOutput();
        BigData bigData = new BigData(out);
        
        System.out.println(in);
        System.out.println(out);
        
        out.displayTime(150237);   // arbitrary time: just an example
       
        
        Semaphore sem0 = in.getSemaphore();
        //Semaphore sem1 = new Semaphore(1); // Flytta denna till Bigdata
        
        UpdateClock updateThread = new UpdateClock(out, bigData);
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
            		System.out.println("Set new time" + value);
            		bigData.setDisplayTime(value);
            		break;
            	//set new alarmtime
            	case 2:
            		System.out.println("Set new Alarm" + value);
            		bigData.setAlarmTime(value);            		
            		break;
            	// pressed both
            	case 3:
            		if(bigData.getAlarmOn()) {
                		bigData.setAlarmOff();
            		} else {
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
