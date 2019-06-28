class ConsumePendingFlag extends Thread {
    final TasksManager.CPUNumber mCPUNumber;
    final Monitor mMonitor;

    ConsumePendingFlag(Monitor monitor , TasksManager.CPUNumber cpuNumber) {
        mCPUNumber = cpuNumber;
        mMonitor = monitor;
    }



    @Override
    public void run() {
        PN.Transitions ConsumePendingTask;
        if(mCPUNumber.equals(mCPUNumber.CPU1)) {
            ConsumePendingTask = PN.Transitions.CONSUME_PENDING_TASK_TOKEN_1;
        } else {
            ConsumePendingTask = PN.Transitions.CONSUME_PENDING_TASK_TOKEN_2;
        }
        while(!interrupted()) {
            mMonitor.fireTransitions(Utils.getSingleTransition(ConsumePendingTask));
        }
    }
}
