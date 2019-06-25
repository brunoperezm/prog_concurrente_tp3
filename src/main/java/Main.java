public class Main {



	public static void main(String args[]) {
		PN pn = new PN();
		Monitor monitor = new Monitor(pn);

		TasksManager tasksManager1 = new TasksManager(monitor, TasksManager.CPUNumber.CPU1);
		TasksManager tasksManager2 = new TasksManager(monitor, TasksManager.CPUNumber.CPU2);
		CPUStateManager cpuStateManager1 = new CPUStateManager(monitor, TasksManager.CPUNumber.CPU1);
		CPUStateManager cpuStateManager2 = new CPUStateManager(monitor, TasksManager.CPUNumber.CPU2);
		TaskDispatcher taskDispatcher = new TaskDispatcher(monitor, pn);

		taskDispatcher.start();
		tasksManager1.start();
		tasksManager2.start();
		cpuStateManager1.start();
		cpuStateManager2.start();



	}
}
