import java.util.LinkedHashMap;

class TaskDispatcher extends  Thread {
	private final PN mPN;
	Monitor mMonitor;

	private int i;

	public TaskDispatcher(Monitor monitor, PN pn) {
		this.mMonitor = monitor;
		this.mPN = pn;
	}

	@Override
	public void run() {
		i = 0;
        while(!interrupted()){
			String task = "Tarea n: " + i;

			LinkedHashMap<PN.Transitions, Runnable> arrivalRateTransition = new LinkedHashMap<>();
			arrivalRateTransition.put(PN.Transitions.ARRIVAL_RATE, () -> {});

			mMonitor.fireTransitions(arrivalRateTransition);


			TasksManager.CPUNumber cpuNumber = Policy.getCpuBuffer(mPN);

			if (cpuNumber.equals(TasksManager.CPUNumber.CPU1)) {
				LinkedHashMap<PN.Transitions, Runnable> startBufferTransition = new LinkedHashMap<>();
				startBufferTransition.put(PN.Transitions.START_BUFFER_1, () -> {});

				if(mMonitor.fireTransitions(startBufferTransition)){
					i ++;
					System.out.println("Mande tarea: " + i + " al buffer " + cpuNumber.toString());
				}

			} else {
				LinkedHashMap<PN.Transitions, Runnable> startBufferTransition = new LinkedHashMap<>();
				startBufferTransition.put(PN.Transitions.START_BUFFER_2, () -> {});

				if(mMonitor.fireTransitions(startBufferTransition)){
					i ++;
					System.out.println("Mande tarea: " + i + " al buffer " + cpuNumber.toString());
				}
			}
			if(i > 1000) break;
		}
	}
}
