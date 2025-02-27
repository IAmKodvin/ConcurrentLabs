package lab;

import wash.WashingIO;

public class TemperatureController extends MessagingThread<WashingMessage> {
	private WashingIO io;
    private int dt;
    private double mu;
    private double ml;
    private double temp;
    private boolean ack;
    private MessagingThread<WashingMessage> commander;

    
    public TemperatureController(WashingIO io) {
        // TODO
    	this.dt = 10;
    	this.mu = 0.0478 * dt;
    	this.ml = 0.000952 * dt;
    	this.io = io;
    	this.ack = true;
    }

    @Override
    public void run() {
        // TODO
		while(true) {
    	try {
			WashingMessage m = receiveWithTimeout(dt*1000 / Wash.SPEEDUP);
			
			if (m != null) {
				commander = m.getSender();
				
				if(m.getCommand() == WashingMessage.TEMP_IDLE) {
					temp = 0;
					io.heat(false);
					commander.send(new WashingMessage(this, WashingMessage.ACKNOWLEDGMENT));
				}
				
				if(m.getCommand() == WashingMessage.TEMP_SET) {
					temp = m.getValue();
					regulate();
					ack = false;
				}
				
			} else {
				regulate();
				if ((io.getTemperature() >= temp  - 2 ) && !ack) {
					ack = true;
					commander.send(new WashingMessage(this, WashingMessage.ACKNOWLEDGMENT));
				}
				
			}
			
			
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		}
    }

	private void regulate() {
		
		// TODO Auto-generated method stub
		if(io.getTemperature()<(temp-2+ml)){
			if(io.getWaterLevel()>0) {
				io.heat(true);
			}
		}
		if(io.getTemperature()>(temp-mu)){
			io.heat(false);
		}
	}
}
