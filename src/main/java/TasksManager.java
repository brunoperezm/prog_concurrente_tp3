import java.util.Queue;
import java.util.Random;

class TasksManager extends Thread {
	enum CPUNumber {
		CPU1,
		CPU2;
	}
	CPUNumber mCPUNumber;

	Queue<String> tasksBuffer;
	Monitor mMonitor;
	int alfa, beta, total;

	TasksManager(Monitor monitor, CPUNumber cpuNumber, int alfa, int beta) {
		this.mMonitor = monitor;
		this.mCPUNumber = cpuNumber;
		this.alfa = alfa;
		this.beta = beta;
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
			this.total++;
			mMonitor.fireTransitions(StartService);
			mMonitor.fireTransitions(EndServiceRate);
		}
	}

	public int getTotalTasksServed() {return this.total; }
}
