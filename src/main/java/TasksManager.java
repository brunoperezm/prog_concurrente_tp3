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
	    // Prepare
		PN.Transitions StartService;
		PN.Transitions EndServiceRate;

	    if(mCPUNumber.equals(CPUNumber.CPU1)){
	    	StartService = PN.Transitions.START_SERVICE_1;
			EndServiceRate = PN.Transitions.END_SERVICE_RATE_1;
		}
	    else {
			StartService = PN.Transitions.START_SERVICE_2;
			EndServiceRate = PN.Transitions.END_SERVICE_RATE_2;
		}
	    // Infinite Loop
		while (!interrupted()) {
				mMonitor.fireTransitions(Utils.getSingleTransition(StartService));
				mMonitor.fireTransitions(Utils.getSingleTransition(EndServiceRate));
		}
	}
}
