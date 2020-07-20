class TasksManager extends Thread {
	enum CPUNumber {
		CPU1,
		CPU2
    }
	private final CPUNumber mCPUNumber;

	private final Monitor mMonitor;
	private int total;

	TasksManager(Monitor monitor, CPUNumber cpuNumber) {
		this.mMonitor = monitor;
		this.mCPUNumber = cpuNumber;
		this.total = 0;
		this.setName("TaskManager " + cpuNumber.toString());
	}

	@Override
	public void run() {
	    // Prepare
		PN.Transitions StartService = (mCPUNumber.equals(CPUNumber.CPU1))
				? PN.Transitions.START_SERVICE_1
				: PN.Transitions.START_SERVICE_2;
		PN.Transitions EndServiceRate = (mCPUNumber.equals(CPUNumber.CPU1))
				? PN.Transitions.END_SERVICE_RATE_1
				: PN.Transitions.END_SERVICE_RATE_2;

		// Infinite Loop
		while (!interrupted()) {
			mMonitor.fireTransitions(StartService);
			mMonitor.fireTransitions(EndServiceRate);
			this.total++;
		}
	}

	int getTotalTasksServed() {return this.total; }
}
