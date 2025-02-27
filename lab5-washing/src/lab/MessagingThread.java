package lab;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

public class MessagingThread<M> extends Thread {
	private BlockingQueue<M> blockingQueue;
	
	public MessagingThread() {
		blockingQueue = new LinkedBlockingQueue<>();
	}

    /** Called by another thread, to send a message to this thread. 
     * @throws InterruptedException */
    public void send(M message) throws InterruptedException {
        // TODO: implement this method (one or a few lines)
    	blockingQueue.offer(message);
    }
    
    /** Returns the first message in the queue, or blocks if none available. */
    protected M receive() throws InterruptedException {
        // TODO: implement this method (one or a few lines)
        return blockingQueue.take();
    }
    
    /** Returns the first message in the queue, or blocks up to 'timeout'
        milliseconds if none available. Returns null if no message is obtained
        within 'timeout' milliseconds. */
    protected M receiveWithTimeout(long timeout) throws InterruptedException {
        // TODO: implement this method (one or a few lines)
        return blockingQueue.poll(timeout, TimeUnit.MILLISECONDS);
    }
}