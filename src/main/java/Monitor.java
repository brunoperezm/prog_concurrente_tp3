import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Monitor {
	private final boolean verbose = false;
	private final PN mPN;

	private final Lock mLock;
	private final HashMap<PN.Transitions, Condition> conditions = new HashMap<>();

	Monitor(PN pn) {
		mPN = pn;

		mLock = new ReentrantLock(true);
		// create one condition per transition
		for (PN.Transitions t : PN.Transitions.values()) conditions.put(t, mLock.newCondition());
	}
	/**
	 * @return true if the transition could be fired, else otherwise */
	public boolean fireTransitions(PN.Transitions transition) {
		mLock.lock();
		try {
			// sleep in queue until condition is met
			int enabled = mPN.isTransitionEnabled(transition);
			while (enabled != 1) {
				if(transition.isTemporized() && enabled < 0){
					if(verbose) System.out.println(transition.toString() + " a dormir " + (-1 * enabled) + " milisegundos.");
					conditions.get(transition).await(-1 * enabled, TimeUnit.MILLISECONDS);
				}
				else {
					if(verbose) System.out.println(transition.toString() + " a dormir.");
					conditions.get(transition).await();
				}
				enabled = mPN.isTransitionEnabled(transition);
			}

			mPN.fire(transition);

			// send a signal to all conditions with enabled transitions
			for (PN.Transitions t: mPN.getEnabledTransitions()) conditions.get(t).signal();
			return true;
		}
		catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			return false;
		} catch (PN.InvalidPInvariantException e) {
			e.printStackTrace();
			return false;
		} finally {
			mLock.unlock();
		}
	}
}