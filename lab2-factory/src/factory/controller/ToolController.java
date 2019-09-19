package factory.controller;

import factory.model.DigitalSignal;
import factory.model.WidgetKind;
import factory.swingview.Factory;

public class ToolController {

	private final DigitalSignal conveyor, press, paint;
	private final long pressingMillis, paintingMillis;
	private boolean isPressing, isPainting;

	public ToolController(DigitalSignal conveyor,
			DigitalSignal press,
			DigitalSignal paint,
			long pressingMillis,
			long paintingMillis)
	{
		this.conveyor = conveyor;
		this.press = press;
		this.paint = paint;
		this.pressingMillis = pressingMillis;
		this.paintingMillis = paintingMillis;
		this.isPainting = false;
		this.isPressing = false;
	}

	public synchronized void onPressSensorHigh(WidgetKind widgetKind) throws InterruptedException {

		if (widgetKind == WidgetKind.BLUE_RECTANGULAR_WIDGET) {

			pressStartHelp();
			waitOutside(pressingMillis);            
			pressOffHelp();            
			waitOutside(pressingMillis);   // press needs this time to retract            
			pressOffNotifyHelp();

			while(isPainting) {
				System.out.println("Waiting in painting");
				wait();
			}            
			conveyor.on();
		}
	}


	public synchronized void onPaintSensorHigh(WidgetKind widgetKind) throws InterruptedException {

		if (widgetKind == WidgetKind.ORANGE_ROUND_WIDGET) {

			paintStartHelp();            
			waitOutside(paintingMillis);
			paintOffHelp();            
			waitOutside(paintingMillis);   // press needs this time to retract
			paintOffNotifyHelp();            

			while(isPressing) {
				System.out.println("Waiting in painting");
				wait();
			}           
			conveyor.on();
		}
	}


	// ------------------------------ Help Method On -------------------------------
	/** Helper method: sleep outside of monitor for ’millis’ milliseconds. */
	private void waitOutside(long millis) throws InterruptedException {
		
		long timeToWakeUp = System.currentTimeMillis() + millis;
		
		while ( timeToWakeUp > System.currentTimeMillis() ) {
			
			long dt = timeToWakeUp - System.currentTimeMillis();/* number of milliseconds left */ 
			//System.out.println("dt: " + dt);
			
			//if(dt > 0) {
			wait(dt);
			//System.out.println("inne i if");
			//}
			
		}
	}

	private synchronized void pressStartHelp() {
		conveyor.off();
		press.on();
		isPressing = true;
	}

	private synchronized void pressOffHelp() {
		press.off();
	}

	private synchronized void pressOffNotifyHelp() {
		isPressing = false;
		notifyAll();
	}

	private synchronized void paintStartHelp() {
		conveyor.off();
		paint.on();
		isPainting = true;
	}

	private synchronized void paintOffHelp() {
		paint.off();
	}

	private synchronized void paintOffNotifyHelp() {
		isPainting = false;
		notifyAll();
	}

	// --------------------- Help Methods End ------------------------------

	public static void main(String[] args) {
		Factory factory = new Factory();
		factory.startSimulation();
	}
}
