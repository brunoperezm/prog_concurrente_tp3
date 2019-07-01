import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Consumer;

public class Monitor {
	private final boolean verbose = false;
	private final PN mPN;

	private final Lock mLock;
	private final HashMap<PN.Transitions, Condition> conditions = new HashMap<>();

	private final ArrayList<PN.Transitions> firedTransitionsList = new ArrayList<>();
	private final ReentrantLock mLock2;

	Monitor(PN pn) {
		mPN = pn;

		mLock = new ReentrantLock(true);
		mLock2 = new ReentrantLock(false);
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

			firedTransitionsList.add(transition);
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

	public void checkTransitionInvariants() {
		mLock.lock();

		HashMap<PN.Transitions, Integer> permittedTransitions1 = new HashMap<>();
		permittedTransitions1.put(PN.Transitions.ARRIVAL_RATE, 1);
		permittedTransitions1.put(PN.Transitions.START_SERVICE_1 , 1);
		permittedTransitions1.put(PN.Transitions.END_SERVICE_RATE_1 , 1);
		permittedTransitions1.put(PN.Transitions.START_SERVICE_2 , 0);
		permittedTransitions1.put(PN.Transitions.END_SERVICE_RATE_2, 0);
		permittedTransitions1.put(PN.Transitions.CONSUME_PENDING_TASK_TOKEN_1, 1);
		permittedTransitions1.put(PN.Transitions.POWER_DOWN_THRESHOLD_1, 0);
		permittedTransitions1.put(PN.Transitions.POWER_UP_DELAY_1, 0);
		permittedTransitions1.put(PN.Transitions.RETURN_PENDING_TASK_1, 0);
		permittedTransitions1.put(PN.Transitions.START_BUFFER_1, 1);
		permittedTransitions1.put(PN.Transitions.WAKE_UP_1, 0);
		permittedTransitions1.put(PN.Transitions.POWER_UP_DELAY_2 , 0);
		permittedTransitions1.put(PN.Transitions.CONSUME_PENDING_TASK_TOKEN_2, 0);
		permittedTransitions1.put(PN.Transitions.POWER_DOWN_THRESHOLD_2, 0);
		permittedTransitions1.put(PN.Transitions.RETURN_PENDING_TASK_2, 0);
		permittedTransitions1.put(PN.Transitions.START_BUFFER_2, 0);
		permittedTransitions1.put(PN.Transitions.WAKE_UP_2, 0);
		permittedTransitions1.put(PN.Transitions.ZT17, 1);
		permittedTransitions1.put(PN.Transitions.ZT18, 0);



		HashMap<PN.Transitions, Integer> permittedTransitions2 = new HashMap<>();
		permittedTransitions2.put(PN.Transitions.ARRIVAL_RATE, 1);
		permittedTransitions2.put(PN.Transitions.START_SERVICE_1 , 1);
		permittedTransitions2.put(PN.Transitions.END_SERVICE_RATE_1 , 1);
		permittedTransitions2.put(PN.Transitions.START_SERVICE_2 , 0);
		permittedTransitions2.put(PN.Transitions.END_SERVICE_RATE_2, 0);
		permittedTransitions2.put(PN.Transitions.CONSUME_PENDING_TASK_TOKEN_1, 0);
		permittedTransitions2.put(PN.Transitions.POWER_DOWN_THRESHOLD_1, 1);
		permittedTransitions2.put(PN.Transitions.POWER_UP_DELAY_1, 1);
		permittedTransitions2.put(PN.Transitions.RETURN_PENDING_TASK_1, 1);
		permittedTransitions2.put(PN.Transitions.START_BUFFER_1, 1);
		permittedTransitions2.put(PN.Transitions.WAKE_UP_1, 1);
		permittedTransitions2.put(PN.Transitions.POWER_UP_DELAY_2 , 0);
		permittedTransitions2.put(PN.Transitions.CONSUME_PENDING_TASK_TOKEN_2, 0);
		permittedTransitions2.put(PN.Transitions.POWER_DOWN_THRESHOLD_2, 0);
		permittedTransitions2.put(PN.Transitions.RETURN_PENDING_TASK_2, 0);
		permittedTransitions2.put(PN.Transitions.START_BUFFER_2, 0);
		permittedTransitions2.put(PN.Transitions.WAKE_UP_2, 0);
		permittedTransitions2.put(PN.Transitions.ZT17, 0);
		permittedTransitions2.put(PN.Transitions.ZT18, 0);

		HashMap<PN.Transitions, Integer> permittedTransitions3 = new HashMap<>();
		permittedTransitions3.put(PN.Transitions.ARRIVAL_RATE, 1);
		permittedTransitions3.put(PN.Transitions.START_SERVICE_1 , 0);
		permittedTransitions3.put(PN.Transitions.END_SERVICE_RATE_1 , 0);
		permittedTransitions3.put(PN.Transitions.START_SERVICE_2 , 1);
		permittedTransitions3.put(PN.Transitions.END_SERVICE_RATE_2, 1);
		permittedTransitions3.put(PN.Transitions.CONSUME_PENDING_TASK_TOKEN_1, 0);
		permittedTransitions3.put(PN.Transitions.POWER_DOWN_THRESHOLD_1, 0);
		permittedTransitions3.put(PN.Transitions.POWER_UP_DELAY_1, 0);
		permittedTransitions3.put(PN.Transitions.RETURN_PENDING_TASK_1, 0);
		permittedTransitions3.put(PN.Transitions.START_BUFFER_1, 0);
		permittedTransitions3.put(PN.Transitions.WAKE_UP_1, 0);
		permittedTransitions3.put(PN.Transitions.POWER_UP_DELAY_2 , 0);
		permittedTransitions3.put(PN.Transitions.CONSUME_PENDING_TASK_TOKEN_2, 1);
		permittedTransitions3.put(PN.Transitions.POWER_DOWN_THRESHOLD_2, 0);
		permittedTransitions3.put(PN.Transitions.RETURN_PENDING_TASK_2, 0);
		permittedTransitions3.put(PN.Transitions.START_BUFFER_2, 1);
		permittedTransitions3.put(PN.Transitions.WAKE_UP_2, 0);
		permittedTransitions3.put(PN.Transitions.ZT17, 0);
		permittedTransitions3.put(PN.Transitions.ZT18, 1);

		HashMap<PN.Transitions, Integer> permittedTransitions4 = new HashMap<>();
		permittedTransitions4.put(PN.Transitions.ARRIVAL_RATE, 1);
		permittedTransitions4.put(PN.Transitions.START_SERVICE_1 , 0);
		permittedTransitions4.put(PN.Transitions.END_SERVICE_RATE_1 , 0);
		permittedTransitions4.put(PN.Transitions.START_SERVICE_2 , 1);
		permittedTransitions4.put(PN.Transitions.END_SERVICE_RATE_2, 1);
		permittedTransitions4.put(PN.Transitions.CONSUME_PENDING_TASK_TOKEN_1, 0);
		permittedTransitions4.put(PN.Transitions.POWER_DOWN_THRESHOLD_1, 0);
		permittedTransitions4.put(PN.Transitions.POWER_UP_DELAY_1, 0);
		permittedTransitions4.put(PN.Transitions.RETURN_PENDING_TASK_1, 0);
		permittedTransitions4.put(PN.Transitions.START_BUFFER_1, 0);
		permittedTransitions4.put(PN.Transitions.WAKE_UP_1, 0);
		permittedTransitions4.put(PN.Transitions.POWER_UP_DELAY_2 , 1);
		permittedTransitions4.put(PN.Transitions.CONSUME_PENDING_TASK_TOKEN_2, 0);
		permittedTransitions4.put(PN.Transitions.POWER_DOWN_THRESHOLD_2, 1);
		permittedTransitions4.put(PN.Transitions.RETURN_PENDING_TASK_2, 1);
		permittedTransitions4.put(PN.Transitions.START_BUFFER_2, 1);
		permittedTransitions4.put(PN.Transitions.WAKE_UP_2, 1);
		permittedTransitions4.put(PN.Transitions.ZT17, 0);
		permittedTransitions4.put(PN.Transitions.ZT18, 0);

		List<HashMap<PN.Transitions, Integer>> permList = new ArrayList<>();
		permList.add(permittedTransitions1);
		permList.add(permittedTransitions2);
		permList.add(permittedTransitions3);
		permList.add(permittedTransitions4);

		permList.forEach(transitionsIntegerHashMap -> {
			int matchCounterTop = 0;
			if (transitionsIntegerHashMap.equals(permittedTransitions1)) matchCounterTop = 6;
			if (transitionsIntegerHashMap.equals(permittedTransitions2)) matchCounterTop = 8;
			if (transitionsIntegerHashMap.equals(permittedTransitions3)) matchCounterTop = 5;
			if (transitionsIntegerHashMap.equals(permittedTransitions4)) matchCounterTop = 8;

			ArrayList<Object> transitionsPositionsAElmininar = new ArrayList<>();
			int matchCounter = 0;
			for (int i = 0; i<firedTransitionsList.size(); i++) {
				final Integer iterationNumber = i;
				PN.Transitions t = firedTransitionsList.get(i);
				if (transitionsIntegerHashMap.get(t).equals(1)) {
					if (matchCounter == matchCounterTop) {
						matchCounter = 0;
						transitionsPositionsAElmininar.forEach(integer -> {
							firedTransitionsList.remove(iterationNumber);
						});
						transitionsPositionsAElmininar = new ArrayList<>();
						break;
					} else {
						transitionsPositionsAElmininar.add(i);
						matchCounter++;
					}
				}
				else {
					matchCounter = 0;
					transitionsPositionsAElmininar = new ArrayList<>();
				}
			}
		});


		mLock.unlock();
	}
}