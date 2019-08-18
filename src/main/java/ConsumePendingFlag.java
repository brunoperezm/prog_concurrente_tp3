class ConsumePendingFlag extends Thread {
    private final TasksManager.CPUNumber mCPUNumber;
    private final Monitor mMonitor;

    ConsumePendingFlag(Monitor monitor , TasksManager.CPUNumber cpuNumber) {
        mCPUNumber = cpuNumber;
        mMonitor = monitor;
    }



    @Override
    public void run() {
        PN.Transitions consumePendingTask1;
        if(mCPUNumber.equals(TasksManager.CPUNumber.CPU1)) {
            consumePendingTask1 = PN.Transitions.CONSUME_PENDING_TASK_TOKEN_1;

        } else {
            consumePendingTask1 = PN.Transitions.CONSUME_PENDING_TASK_TOKEN_2;
        }
        while(!interrupted()) {
            mMonitor.fireTransitions(consumePendingTask1);
        }
    }
}
