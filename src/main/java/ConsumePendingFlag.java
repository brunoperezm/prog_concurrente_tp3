class ConsumePendingFlag extends Thread {
    final TasksManager.CPUNumber mCPUNumber;
    final Monitor mMonitor;

    ConsumePendingFlag(Monitor monitor , TasksManager.CPUNumber cpuNumber) {
        mCPUNumber = cpuNumber;
        mMonitor = monitor;
    }



    @Override
    public void run() {
        PN.Transitions ConsumePendingTask1;
        PN.Transitions ConsumePendingTask2;
        if(mCPUNumber.equals(mCPUNumber.CPU1)) {
            ConsumePendingTask1 = PN.Transitions.CONSUME_PENDING_TASK_TOKEN_1;
            ConsumePendingTask2 = PN.Transitions.ZT17;

        } else {
            ConsumePendingTask1 = PN.Transitions.CONSUME_PENDING_TASK_TOKEN_2;
            ConsumePendingTask2 = PN.Transitions.ZT18;
        }
        while(!interrupted()) {
            mMonitor.fireTransitions(Utils.getSingleTransition(ConsumePendingTask1));
            mMonitor.fireTransitions(Utils.getSingleTransition(ConsumePendingTask2));
        }
    }
}
