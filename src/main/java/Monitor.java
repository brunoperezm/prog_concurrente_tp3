import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

class Monitor {
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
	public boolean fireTransitions(LinkedHashMap<PN.Transitions, Runnable> transitionsExecutableLinkedHashMap) {
		mLock.lock();
		try {
			for (PN.Transitions t : transitionsExecutableLinkedHashMap.keySet()) {
				// sleep in queue until condition is met
				while (!mPN.isTransitionEnabled(t)) {
					if (!conditions.get(t).await(200, TimeUnit.MILLISECONDS)) return false;
				}

				mPN.fire(t);
				transitionsExecutableLinkedHashMap.get(t).run();
			}

			// send a signal to all conditions with enabled transitions
			for (PN.Transitions t: mPN.getEnabledTransitions()) conditions.get(t).signal();
			return true;
		}
		catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			return false;
		}
		finally {
			mLock.unlock();
		}
	}
}