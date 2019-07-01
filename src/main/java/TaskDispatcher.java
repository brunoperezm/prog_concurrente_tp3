
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

			mMonitor.fireTransitions(PN.Transitions.ARRIVAL_RATE);

			Monitor.BoolTransitionWrapper bt = mMonitor.taskDispatch();

			TasksManager.CPUNumber cpuNumber =
					(bt.getTransition().equals(PN.Transitions.START_BUFFER_1)
					? TasksManager.CPUNumber.CPU1
					: TasksManager.CPUNumber.CPU2);

			if (cpuNumber.equals(TasksManager.CPUNumber.CPU1)) i1++;
			else i2++;

			if (bt.getStatus())
				System.out.println("Mande tarea: " + (i1 + i2) + " al buffer " + cpuNumber.toString());

			if(i1 + i2 >= Main.TOTAL_TASKS) {
				System.out.println("Tareas Buffer 1: " + i1);
				System.out.println("Tareas Buffer 2: " + i2);
				break;
			}
		}
	}
}
