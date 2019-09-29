

import java.math.BigInteger;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingUtilities;

import client.view.ProgressItem;
import client.view.StatusWindow;
import client.view.WorklistItem;
import network.Sniffer;
import network.SnifferCallback;
import rsa.Factorizer;
import rsa.ProgressTracker;

public class CodeBreaker implements SnifferCallback {

    private final JPanel workList;
    private final JPanel progressList;
    
    private final JProgressBar mainProgressBar;
    
    private ExecutorService pool;

    // -----------------------------------------------------------------------
    
    private CodeBreaker() {
        StatusWindow w  = new StatusWindow();

        this.workList        = w.getWorkList();
        this.progressList    = w.getProgressList();
        this.mainProgressBar = w.getProgressBar();
        
        // Create thread pool
        this.pool = Executors.newFixedThreadPool(2);
        
        new Sniffer(this).start();
    }
    
    // -----------------------------------------------------------------------
    
    public static void main(String[] args) throws Exception {

        /*
         * Most Swing operations (such as creating view elements) must be
         * performed in the Swing EDT (Event Dispatch Thread).
         * 
         * That's what SwingUtilities.invokeLater is for.
         */

        SwingUtilities.invokeLater(() -> new CodeBreaker());
    }

    // -----------------------------------------------------------------------

    /** Called by a Sniffer thread when an encrypted message is obtained. */
    @Override
    public void onMessageIntercepted(String message, BigInteger n){
    	
    	Future<String> future = null;
    	
    	// Worklist
    	WorklistItem newWork = new WorklistItem(n, message);
    	JButton b = new JButton("Break!");
    	newWork.add(b);
    	
    	
    	// Progresslist
		ProgressItem newProgress = new ProgressItem(n, message);
		JButton c = new JButton("Cancel!");
		newProgress.add(c);
		
		//Button Actions, hur f�r vi ut v�ran future?
		b.addActionListener(e -> breakButtonAction(newWork, newProgress, message, n, future));
		
    	SwingUtilities.invokeLater(() -> {
    		// Action of Break Button
    		workList.add(newWork);
    	});
    	
		try {
			newProgress.getTextArea().setText(future.get());
		} catch (InterruptedException | ExecutionException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
    	    

    }
    
    private void breakButtonAction(WorklistItem newItem, ProgressItem newProgress, String message, BigInteger n, Future<String> future) {

    	// n�r beh�ver detta g�ras?
    	SwingUtilities.invokeLater(() -> {
    		progressList.add(newProgress);
    		workList.remove(newItem);

    	});

    	ProgressTracker tracker = new Tracker(newProgress.getProgressBar());
    	Callable<String> task = () -> Factorizer.crack(message, n, tracker);
    	future = pool.submit(task);
    	// Om vi kallar p� future.get() s� l�ser det sig h�r
    }
    
    private void cancelButtonAction() {
    	
    }
    
    // ------------------------------------------------------------------------------
    
    private static class Tracker implements ProgressTracker {
        private int totalProgress = 0;
        private int prevPercent = -1;
        private JProgressBar progressBar;
        
        public Tracker(JProgressBar progressBar) {
        	super();
        	this.progressBar = progressBar;
        }

        /**
         * Called by Factorizer to indicate progress. The total sum of
         * ppmDelta from all calls will add up to 1000000 (one million).
         * 
         * @param  ppmDelta   portion of work done since last call,
         *                    measured in ppm (parts per million)
         */
        @Override
        public void onProgress(int ppmDelta) {
            totalProgress += ppmDelta;
            int percent = totalProgress / 10000;
            if (percent != prevPercent) {
            	progressBar.setValue(totalProgress);
                prevPercent = percent;
            }
            
        }
    }
}
