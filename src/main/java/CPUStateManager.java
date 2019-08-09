class CPUStateManager extends Thread {


	final TasksManager.CPUNumber mCPUNumber;
	final Monitor mMonitor;
	public CPUStateManager(Monitor monitor, TasksManager.CPUNumber cpu) {
		mCPUNumber = cpu;
		mMonitor = monitor;
		this.setName("CpuStateManager " + cpu.toString());
	}

	@Override
	public void run() {
		// Prepare
		PN.Transitions WakeUp;
		PN.Transitions ReturnPendingTask;
		PN.Transitions PowerUpDelay;
		PN.Transitions PowerDownThreshold;

		if(mCPUNumber.equals(TasksManager.CPUNumber.CPU1)){
			WakeUp = PN.Transitions.WAKE_UP_1;
			ReturnPendingTask = PN.Transitions.RETURN_PENDING_TASK_1;
			PowerUpDelay = PN.Transitions.POWER_UP_DELAY_1;
			PowerDownThreshold = PN.Transitions.POWER_DOWN_THRESHOLD_1;
		}
		else {
			WakeUp = PN.Transitions.WAKE_UP_2;
			ReturnPendingTask = PN.Transitions.RETURN_PENDING_TASK_2;
			PowerUpDelay = PN.Transitions.POWER_UP_DELAY_2;
			PowerDownThreshold = PN.Transitions.POWER_DOWN_THRESHOLD_2;
		}

		// Infinity Loop
		while (!interrupted()) {
			mMonitor.fireTransitions(WakeUp);
			mMonitor.fireTransitions(ReturnPendingTask);
			try {
				Thread.sleep(50);
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
			}
			mMonitor.fireTransitions(PowerUpDelay);
			mMonitor.fireTransitions(PowerDownThreshold);
		}
	}
}
