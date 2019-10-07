package lab;

import wash.WashingIO;

public class TemperatureController extends MessagingThread<WashingMessage> {
	private WashingIO io;
    private int dt;
    private double mu;
    private double ml;
    private double temp;

    
    public TemperatureController(WashingIO io) {
        // TODO
    	this.dt = 10;
    	this.mu = 0.0478 * dt;
    	this.ml = 0.000952 * dt;
    	this.io = io;
    }

    @Override
    public void run() {
        // TODO
		while(true) {
    	try {
			WashingMessage m = receiveWithTimeout(dt / Wash.SPEEDUP);
			
			if (m != null) {
				if(m.getCommand() == WashingMessage.TEMP_IDLE) {
					temp = 0;
					io.heat(false);
				}
				
				if(m.getCommand() == WashingMessage.TEMP_SET) {
					temp = m.getValue();
					regulate();
				}
				
			} else {
				regulate();
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
