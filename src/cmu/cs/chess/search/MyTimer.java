package cmu.cs.chess.search;

/**
 * @author jzhou & cliu
 *
 */
public class MyTimer implements Timer {
	// private int initialTime;
	private int increment;
	private long startTime;
	private int allocated;

	private boolean noTimeup = false;

	public MyTimer(int initialTime, int increment) {
		// this.initialTime = initialTime;
		this.increment = increment;
	}

	public void start(int myTime, int opTime) {
		startTime = System.currentTimeMillis();
		allocated = allocateTime(myTime, opTime);
	}

	public boolean timeup() {
		if (noTimeup)
			return false;
		if ((System.currentTimeMillis() - startTime) > allocated) {
			return true;
		}
		return false;
	}

	/*
	 * This method computes and returns an amount of time to allocate to the
	 * current move. It takes as parameters my time left, and the opponent's
	 * time left. 
	 */
	private int allocateTime(int timeLeft, int opTimeLeft) {
		double t = .9 * increment + timeLeft / 30.0;
		if (t > timeLeft)
			t = .9 * timeLeft;
		return (int) t;
	}

	public void notOkToTimeup() {
		noTimeup = true;
	}

	public void okToTimeup() {
		noTimeup = false;
	}
}
