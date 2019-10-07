package lab;

import javax.swing.plaf.basic.BasicInternalFrameTitlePane.SystemMenuBar;

import wash.WashingIO;

public class SpinController extends MessagingThread<WashingMessage> {
	private WashingIO io;
	private boolean spinLeft;
	private int state;

	public SpinController(WashingIO io) {
		this.io = io;    
		boolean spinLeft = true;
		this.state = 0;
		
	}

	@Override
	public void run() {
		while(true) {
			try {

				// ... TODO ...
				// wait for up to a (simulated) minute for a WashingMessage
				WashingMessage m = receiveWithTimeout(60000 / Wash.SPEEDUP);
				
				
				// if m is null, it means a minute passed and no message was received
				if (m != null) {

					System.out.println("got " + m);

						switch(m.getCommand()) {
						case WashingMessage.SPIN_OFF :
							System.out.println("Spinning Off");
							state = WashingMessage.SPIN_OFF;
							stopSpinning();
							break; // optional

						case WashingMessage.SPIN_SLOW :
							System.out.println("Spinning Slow");
							state = WashingMessage.SPIN_SLOW;
							spinSlow();
							break; // optional

						case WashingMessage.SPIN_FAST :
							System.out.println("Spinning Fast");
							state = WashingMessage.SPIN_FAST;
							spinFast();
							break; // optional
						}

					// ... TODO ...
				} else if (state == WashingMessage.SPIN_SLOW){ 
					spinSlow();
				}
			} catch (InterruptedException unexpected) {
				// we don't expect this thread to be interrupted,
				// so throw an error if it happens anyway
				throw new Error(unexpected);
			}

	}
}


private void spinSlow() {
	if(spinLeft) {
		spinLeft = false;
		io.setSpinMode(3);
		System.out.println("direction right");
	} else {
		spinLeft = true;
		io.setSpinMode(2);
		System.out.println("direction left");
	}


}

private void spinFast() {
	if(io.getWaterLevel() == 0) {
		io.setSpinMode(io.SPIN_FAST);
	}
}

private void stopSpinning() {
	io.setSpinMode(io.SPIN_IDLE);
}


}
