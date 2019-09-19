package factory.controller;

import factory.model.DigitalSignal;
import factory.model.WidgetKind;
import factory.swingview.Factory;

public class ToolController {
    private final DigitalSignal conveyor, press, paint;
    private final long pressingMillis, paintingMillis;
    
    private volatile long startPressingTime;
    private volatile boolean isPressing;
    private volatile boolean isPainting;
    
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
        this.startPressingTime = 0;
        this.isPressing = false;
        this.isPainting = false;
    }

    public void onPressSensorHigh(WidgetKind widgetKind) throws InterruptedException {
        //
        // TODO: you will need to modify this method
        //
        if (widgetKind == WidgetKind.BLUE_RECTANGULAR_WIDGET) {
            isPressing = true;
        	conveyor.off();
            press.on();
            Thread.sleep(pressingMillis);
            press.off();
            Thread.sleep(pressingMillis);
            notifyAll();
            if(!isPainting) {
            	conveyor.on();
            }
            isPressing = false;
            
        }
    }

    public void onPaintSensorHigh(WidgetKind widgetKind) throws InterruptedException {
        //
        // TODO: you will need to modify this method
        //
        if (widgetKind == WidgetKind.ORANGE_ROUND_WIDGET) {
        	isPainting = true;
            paint.on();
            Thread.sleep(paintingMillis);
            paint.off();
            Thread.sleep(paintingMillis);
           	while(isPressing) {
           		wait();
           	}
           	isPainting = false;
           	conveyor.on();
        }
    }
    
    // -----------------------------------------------------------------------
    
    private synchronized void conveyorHandler() throws InterruptedException {
    	while(isPainting || isPressing) {
    		conveyor.off();
    		wait();
    	}
    	conveyor.on();
    }
    
    private synchronized void pressSynced() throws InterruptedException {
    	long time = System.currentTimeMillis();

        isPressing = false;
        notifyAll();
       
    }
    
    private synchronized void paintSynced() throws InterruptedException {
    	isPainting = true;
        paint.on();
        Thread.sleep(paintingMillis);
        paint.off();
        Thread.sleep(paintingMillis);
       	isPainting = false;
        notifyAll();
    }
    
    public static void main(String[] args) {
        Factory factory = new Factory();
        factory.startSimulation();
    }
}
