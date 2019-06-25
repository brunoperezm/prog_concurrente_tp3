import java.util.LinkedHashMap;

class TaskDispatcher extends  Thread {
	private final PN mPN;
	Monitor mMonitor;

	public TaskDispatcher(Monitor monitor, PN pn) {
		this.mMonitor = monitor;
		this.mPN = pn;
	}

	@Override
	public void run() {
		for (int i = 0; i< 1000; i++) {
			String task = "Tarea n: " + i;

			LinkedHashMap<PN.Transitions, Runnable> arrivalRateTransition = new LinkedHashMap<>();
			arrivalRateTransition.put(PN.Transitions.ARRIVAL_RATE, () -> {});

			while (!mMonitor.fireTransitions(arrivalRateTransition)) {}


			TasksManager.CPUNumber cpuNumber = Policy.getCpuBuffer(mPN);

			if (cpuNumber.equals(TasksManager.CPUNumber.CPU1)) {
				LinkedHashMap<PN.Transitions, Runnable> startBufferTransition = new LinkedHashMap<>();
				startBufferTransition.put(PN.Transitions.START_BUFFER_1, () -> {});

				while (!mMonitor.fireTransitions(startBufferTransition)) {}

			} else {
				LinkedHashMap<PN.Transitions, Runnable> startBufferTransition = new LinkedHashMap<>();
				startBufferTransition.put(PN.Transitions.START_BUFFER_2, () -> {});

				while (!mMonitor.fireTransitions(startBufferTransition)) {}
			}
			System.out.println("Mande tarea: " + i + " al buffer " + cpuNumber.toString());
		}
	}
}
