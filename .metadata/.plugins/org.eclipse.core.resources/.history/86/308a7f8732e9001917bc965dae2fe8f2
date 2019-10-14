package lab;

import wash.WashingIO;

public class WaterController extends MessagingThread<WashingMessage> {
	private WashingIO io;
	private int dt;
	private double level;
	private MessagingThread<WashingMessage> commander;
	private boolean filling;
	private boolean draining;

    public WaterController(WashingIO io) {
        this.io = io;
        this.dt = 2;
        this.level=0;
        this.filling = false;
        this.draining = false;
    }

    @Override
    public void run() {
        // TODO
		while(true) {
    	try {
			WashingMessage m = receiveWithTimeout(dt / Wash.SPEEDUP);
			
			if (m != null) {
				commander = m.getSender();
				
			
				if(m.getCommand() == WashingMessage.WATER_IDLE) {
					io.drain(false);
					io.fill(false);
				}
				if(m.getCommand() == WashingMessage.WATER_FILL) {
					io.drain(false);
					draining = false;
					
					level = m.getValue();
				    io.fill(true);
				    filling = true;
				    fill();
				}
				if(m.getCommand() == WashingMessage.WATER_DRAIN) {
					io.fill(false);
					filling = false;
					io.drain(true);
				}
				
			} else {
				if (filling) {
					fill();
				}
				if (draining) {
					drain();
				}
				
			}
			
			
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		}
    }

	private void fill() throws InterruptedException {
		// TODO Auto-generated method stub
		if((io.getWaterLevel() >= level) && filling){
			io.fill(false);
			filling = false;
			commander.send(new WashingMessage(this, WashingMessage.ACKNOWLEDGMENT));
		}
		}
	
	private void drain() throws InterruptedException {
		// TODO Auto-generated method stub
		if((io.getWaterLevel() == 0) && draining){
			io.drain(false);
			draining = false;
			commander.send(new WashingMessage(this, WashingMessage.ACKNOWLEDGMENT));
		}
		}
	
	}
