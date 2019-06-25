import java.util.Queue;

class TasksManager extends Thread {
	enum CPUNumber {
		CPU1,
		CPU2;
	}
	CPUNumber mCPUNumber;
	Queue<String> tasksBuffer;
	Monitor mMonitor;
	TasksManager(Monitor monitor, CPUNumber cpuNumber) {
		this.mMonitor = monitor;
		this.mCPUNumber = cpuNumber;
	}

	@Override
	public void run() {
		while (!interrupted()) {
			if (mCPUNumber.equals(CPUNumber.CPU1)) {
				mMonitor.fireTransitions(Utils.getSingleTransition(PN.Transitions.START_SERVICE_1));
				mMonitor.fireTransitions(Utils.getSingleTransition(PN.Transitions.END_SERVICE_RATE_1));

			} else {
				mMonitor.fireTransitions(Utils.getSingleTransition(PN.Transitions.START_SERVICE_2));
				mMonitor.fireTransitions(Utils.getSingleTransition(PN.Transitions.END_SERVICE_RATE_2));
			}
		}
	}
}