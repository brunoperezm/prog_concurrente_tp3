
class TaskDispatcher extends  Thread {
	private final Monitor mMonitor;

	private int i1;
	private int i2;
	private boolean verbose;

	public TaskDispatcher(Monitor monitor) {
		this.mMonitor = monitor;
	}

	@Override
	public void run() {
		i1 = 0;
		i2 = 0;
        while(!interrupted()){
			String task = "Tarea n: " + i1 + i2;

			mMonitor.fireTransitions(PN.Transitions.ARRIVAL_RATE);

			Monitor.BoolTransitionWrapper bt = mMonitor.taskDispatch();

			TasksManager.CPUNumber cpuNumber =
					(bt.getTransition().equals(PN.Transitions.START_BUFFER_1)
					? TasksManager.CPUNumber.CPU1
					: TasksManager.CPUNumber.CPU2);

			if (bt.getStatus()) {
				if (cpuNumber.equals(TasksManager.CPUNumber.CPU1)) i1++;
				else i2++;

				if (verbose) System.out.println("Mande tarea: " + (i1 + i2) + " al buffer " + cpuNumber.toString());
			}

			if(i1 + i2 >= Main.TOTAL_TASKS) {
				System.out.println("Tareas Buffer 1: " + i1);
				System.out.println("Tareas Buffer 2: " + i2);
				break;
			}
		}
	}
}
