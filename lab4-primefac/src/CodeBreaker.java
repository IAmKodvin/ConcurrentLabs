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
        w.enableErrorChecks();

        this.workList        = w.getWorkList();
        this.progressList    = w.getProgressList();
        this.mainProgressBar = w.getProgressBar();
        mainProgressBar.setMaximum(0);
        
        
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
    	SwingUtilities.invokeLater(() -> interceptGUI(message, n));
    }
    
    private void interceptGUI(String message, BigInteger n) {
    	// Worklist
    	WorklistItem newWork = new WorklistItem(n, message);
    	JButton b = new JButton("Break!");
    	newWork.add(b);
    	
    	// Progresslist
		ProgressItem newProgress = new ProgressItem(n, message);
    	
		//Button Actions
		b.addActionListener(e -> breakButtonAction(newWork, newProgress, message, n));
		workList.add(newWork);
    }
    
    private void breakButtonAction(WorklistItem newItem, ProgressItem newProgress, String message, BigInteger n) {

    	//Remove worklist, add progressList
    	progressList.add(newProgress);
    	workList.remove(newItem);
 
    	//Update main progress
    	mainProgressBar.setMaximum(mainProgressBar.getMaximum() + 1000000);

    	ProgressTracker tracker = new Tracker(newProgress.getProgressBar(), mainProgressBar);
    	//pool.execute(() -> crackCode(message, n, tracker, newProgress));
    	
    	// Optional
    	Future future = pool.submit(() -> crackCode(message, n, tracker, newProgress));
    	JButton c = new JButton("Cancel");
    	newProgress.add(c);
    	c.addActionListener(e -> cancelButtonAction(future));
    }
    
    private void crackCode(String message, BigInteger n, ProgressTracker tracker, ProgressItem progressItem) {
    	String decoded = Factorizer.crack(message, n, tracker);
    	SwingUtilities.invokeLater(() -> {
    		progressItem.getTextArea().setText(decoded);	
    		onCrackDone(progressItem);
    	});    	
    }
    
    private void onCrackDone(ProgressItem progressItem) {
    	JButton r = new JButton("Remove");
    	progressItem.add(r);
    	r.addActionListener(e -> removeButtonAction(progressItem));
    }
    
    private void removeButtonAction(ProgressItem progressItem) {
    	progressList.remove(progressItem);
    	//Update main progress
    	mainProgressBar.setValue(mainProgressBar.getValue() - 1000000);
    	mainProgressBar.setMaximum(mainProgressBar.getMaximum() - 1000000);
    }
    
    private void cancelButtonAction(Future future) {
    	future.cancel(true);
    }
    
    // ------------------------------------------------------------------------------
    
    private static class Tracker implements ProgressTracker {
        private int totalProgress = 0;
        private int prevPercent = -1;
        private JProgressBar progressBar;
        private JProgressBar mainProgressBar;
        
        public Tracker(JProgressBar progressBar, JProgressBar mainProgressBar) {
        	super();
        	this.progressBar = progressBar;
        	this.mainProgressBar = mainProgressBar;
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
        	SwingUtilities.invokeLater(() -> updateBar(ppmDelta));  
        }
        
        private void updateBar(int ppmDelta) {
        	totalProgress += ppmDelta;
        	progressBar.setValue(totalProgress);
        	mainProgressBar.setValue(mainProgressBar.getValue() + ppmDelta);	
        }        
    }
}
