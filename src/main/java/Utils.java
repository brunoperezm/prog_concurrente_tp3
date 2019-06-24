import java.util.LinkedHashMap;

class Utils {

	public static LinkedHashMap<PN.Transitions, Runnable> getSingleTransition(PN.Transitions t) {
		LinkedHashMap<PN.Transitions, Runnable> transitionsRunnableLinkedHashMap = new LinkedHashMap<>();
		transitionsRunnableLinkedHashMap.put(t, () -> {});
		return transitionsRunnableLinkedHashMap;
	}
}
