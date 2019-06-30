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
	int alfa, beta;
	TasksManager(Monitor monitor, CPUNumber cpuNumber, int alfa, int beta) {
		this.mMonitor = monitor;
		this.mCPUNumber = cpuNumber;
		this.alfa = alfa;
		this.beta = beta;
		this.setName("TastManager " + cpuNumber.toString());
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
			mMonitor.fireTransitions(Utils.getSingleTransition(StartService));
			mMonitor.fireTransitions(Utils.getSingleTransition(EndServiceRate));
		}
	}
}
