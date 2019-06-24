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
				while (!mMonitor.fireTransitions(Utils.getSingleTransition(PN.Transitions.START_SERVICE_1))) {}
				while (!mMonitor.fireTransitions(Utils.getSingleTransition(PN.Transitions.END_SERVICE_RATE_1))) {}

			} else {
				while (!mMonitor.fireTransitions(Utils.getSingleTransition(PN.Transitions.START_SERVICE_2))) {}
				while (!mMonitor.fireTransitions(Utils.getSingleTransition(PN.Transitions.END_SERVICE_RATE_2))) {}
			}
		}
	}
}
