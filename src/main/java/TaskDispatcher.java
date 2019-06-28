import java.util.LinkedHashMap;

class TaskDispatcher extends  Thread {
	private final Policy mPolicy;
	Monitor mMonitor;

	private int i1;
	private int i2;

	public TaskDispatcher(Monitor monitor, Policy policy) {
		this.mMonitor = monitor;
		this.mPolicy = policy;
	}

	@Override
	public void run() {
		i1 = 0;
		i2 = 0;
        while(!interrupted()){
			String task = "Tarea n: " + i1 + i2;

			LinkedHashMap<PN.Transitions, Runnable> arrivalRateTransition = new LinkedHashMap<>();
			arrivalRateTransition.put(PN.Transitions.ARRIVAL_RATE, () -> {});

			mMonitor.fireTransitions(arrivalRateTransition);


			TasksManager.CPUNumber cpuNumber = mPolicy.getCpuBuffer();

			if (cpuNumber.equals(TasksManager.CPUNumber.CPU1)) {
				LinkedHashMap<PN.Transitions, Runnable> startBufferTransition = new LinkedHashMap<>();
				startBufferTransition.put(PN.Transitions.START_BUFFER_1, () -> {});

				if(mMonitor.fireTransitions(startBufferTransition)){
					i1 ++;
					System.out.println("Mande tarea: " + (i1 + i2) + " al buffer " + cpuNumber.toString());
				}

			} else {
				LinkedHashMap<PN.Transitions, Runnable> startBufferTransition = new LinkedHashMap<>();
				startBufferTransition.put(PN.Transitions.START_BUFFER_2, () -> {});

				if(mMonitor.fireTransitions(startBufferTransition)){
					i2 ++;
					System.out.println("Mande tarea: " + (i1 + i2) + " al buffer " + cpuNumber.toString());
				}
			}
			if(i1 + i2 >= Main.TOTAL_TASKS) {
				System.out.println("Tareas Buffer 1: " + i1);
				System.out.println("Tareas Buffer 2: " + i2);
				break;
			}
		}
	}
}
