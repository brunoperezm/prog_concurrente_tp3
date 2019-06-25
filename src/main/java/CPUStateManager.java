class CPUStateManager extends Thread {


	final TasksManager.CPUNumber mCPUNumber;
	final Monitor mMonitor;
	public CPUStateManager(Monitor monitor, TasksManager.CPUNumber cpu) {
		mCPUNumber = cpu;
		mMonitor = monitor;
	}

	@Override
	public void run() {
		while (!interrupted()) {
			if (mCPUNumber.equals(TasksManager.CPUNumber.CPU1)) {
				mMonitor.fireTransitions(Utils.getSingleTransition(PN.Transitions.WAKE_UP_1));
				mMonitor.fireTransitions(Utils.getSingleTransition(PN.Transitions.RETURN_PENDING_TASK_1));
				try {
					Thread.sleep(50);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				mMonitor.fireTransitions(Utils.getSingleTransition(PN.Transitions.POWER_UP_DELAY_1));
				mMonitor.fireTransitions(Utils.getSingleTransition(PN.Transitions.CONSUME_PENDING_TASK_TOKEN_1));
				mMonitor.fireTransitions(Utils.getSingleTransition(PN.Transitions.POWER_DOWN_THRESHOLD_1));


			} else {
				mMonitor.fireTransitions(Utils.getSingleTransition(PN.Transitions.WAKE_UP_2));
				mMonitor.fireTransitions(Utils.getSingleTransition(PN.Transitions.RETURN_PENDING_TASK_2));
				try {
					Thread.sleep(50);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				mMonitor.fireTransitions(Utils.getSingleTransition(PN.Transitions.POWER_UP_DELAY_2));
				mMonitor.fireTransitions(Utils.getSingleTransition(PN.Transitions.CONSUME_PENDING_TASK_TOKEN_2));
				mMonitor.fireTransitions(Utils.getSingleTransition(PN.Transitions.POWER_DOWN_THRESHOLD_2));
			}
		}
	}
}