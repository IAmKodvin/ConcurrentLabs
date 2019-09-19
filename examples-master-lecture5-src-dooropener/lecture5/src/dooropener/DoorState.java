package dooropener;

public class DoorState {
    private final DoorIO io;
    private boolean pressed, closed, open;

    public DoorState(DoorIO io) {
        this.io = io;
        pressed = closed = open = false;
    }

    public synchronized void reportButton() {
        pressed = true;
        notifyAll();
    }

    public synchronized void reportClosed() {
        closed = true;
        notifyAll();
    }

    public synchronized void reportOpen() {
        open = true;
        notifyAll();
    }

    public synchronized void handleOpening() throws InterruptedException {
        while (! pressed) {
            wait();
        }
        pressed = false;
        
        open = false;
        io.startOpening();
        while (! open) {
            wait();
        }
        io.stop();
    }
    
    public synchronized void handleClosing() throws InterruptedException {
        io.startClosing();
        closed = false;
        while (! (closed || pressed)) {
            wait();
        }
        io.stop();
    }

}
