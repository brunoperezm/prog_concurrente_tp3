import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class Main {

	static int TOTAL_TASKS = 1000;

	static int ARRIVAL_RATE_1_ALFA = 10;
	static int ARRIVAL_RATE_1_BETA = 20000000;

	static int SERVICE_RATE_1_ALFA = 20;
	static int SERVICE_RATE_1_BETA = 100;

	static int SERVICE_RATE_2_ALFA = 60;
	static int SERVICE_RATE_2_BETA = 100;


	public static void main(String[] args) {
		Date initTime;
		PN pn = new PN(true);
		Policy mPolicy = new SharedLoadPolicy(pn);
		Monitor monitor = new Monitor(pn, mPolicy);

		Loger loger;

		TasksManager tasksManager1 = new TasksManager(monitor, TasksManager.CPUNumber.CPU1, SERVICE_RATE_1_ALFA, SERVICE_RATE_1_BETA);
		TasksManager tasksManager2 = new TasksManager(monitor, TasksManager.CPUNumber.CPU2, SERVICE_RATE_1_ALFA, SERVICE_RATE_2_BETA);

		CPUStateManager cpuStateManager1 = new CPUStateManager(monitor, TasksManager.CPUNumber.CPU1);
		CPUStateManager cpuStateManager2 = new CPUStateManager(monitor, TasksManager.CPUNumber.CPU2);

		ConsumePendingFlag consumePendingFlag1 = new ConsumePendingFlag(monitor, TasksManager.CPUNumber.CPU1);
		ConsumePendingFlag consumePendingFlag2 = new ConsumePendingFlag(monitor, TasksManager.CPUNumber.CPU2);

		TaskDispatcher taskDispatcher = new TaskDispatcher(monitor);


		List<Thread> threadList = Arrays.asList(tasksManager1, tasksManager2, cpuStateManager1, cpuStateManager2, taskDispatcher);
		loger = new Loger(monitor, threadList, pn, "out/log.txt");
		loger.start();

		initTime = new Date();
		taskDispatcher.start();
		tasksManager1.start();
		tasksManager2.start();
		cpuStateManager1.start();
		cpuStateManager2.start();
		consumePendingFlag1.start();
		consumePendingFlag2.start();


		new Thread(new Runnable() {
			@Override
			public void run() {
				while (true) {
					monitor.fireTransitions(PN.Transitions.ZT19);
				}
			}
		}).start();
		new Thread(new Runnable() {
			@Override
			public void run() {
				while (true) {
					monitor.fireTransitions(PN.Transitions.ZT20);
				}
			}
		}).start();

		try {
			taskDispatcher.join();
			int elapsedTime = (int) (new Date().getTime() - initTime.getTime()) / 1000;
			System.out.println(TOTAL_TASKS + " tareas despachadas en " + elapsedTime + " segundos.");
			while (tasksManager1.getTotalTasksServed() + tasksManager2.getTotalTasksServed() < 1000);
			elapsedTime = (int) (new Date().getTime() - initTime.getTime()) / 1000;
			System.out.println(TOTAL_TASKS + " tareas servidas en " + elapsedTime + " segundos.");
			tasksManager1.interrupt();
			tasksManager2.interrupt();
			loger.stop(elapsedTime);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

	}
}
