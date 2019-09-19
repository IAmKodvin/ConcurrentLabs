package dooropener;

public class SingleThreadedDoorOpener {

    public static void buttonReporter(DoorIO io, DoorState state) {
        try {
            while (true) {
                io.awaitButton();
                state.reportButton();
            }
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public static void openReporter(DoorIO io, DoorState state) {
        try {
            while (true) {
                io.awaitOpen();
                state.reportOpen();
            }
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public static void closedReporter(DoorIO io, DoorState state) {
        try {
            while (true) {
                io.awaitClosed();
                state.reportClosed();
            }
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws InterruptedException {
        DoorIO io = new DoorIO();
        io.start();

        DoorState state = new DoorState(io);
        Thread t1 = new Thread(() -> buttonReporter(io, state));
        Thread t2 = new Thread(() -> openReporter(io, state));
        Thread t3 = new Thread(() -> closedReporter(io, state));

        t1.start();
        t2.start();
        t3.start();

        while (true) {
            state.handleOpening();

            Thread.sleep(3000);

            state.handleClosing();
        }
    }
}
