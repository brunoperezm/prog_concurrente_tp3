import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class Main {

	public static int TOTAL_TASKS = 1000;

	public static int ARRIVAL_RATE_1_ALFA = 10;
	public static int ARRIVAL_RATE_1_BETA = 20000000;

	public static int SERVICE_RATE_1_ALFA = 20;
	public static int SERVICE_RATE_1_BETA = 100;

	public static int SERVICE_RATE_2_ALFA = 20;
	public static int SERVICE_RATE_2_BETA = 100;


	public static void main(String args[]) {
		Date initTime;
		PN pn = new PN();
		Policy policy = new Policy(pn);
		Monitor monitor = new Monitor(pn);

		Loger loger;

		TasksManager tasksManager1 = new TasksManager(monitor, TasksManager.CPUNumber.CPU1, SERVICE_RATE_1_ALFA, SERVICE_RATE_1_BETA);
		TasksManager tasksManager2 = new TasksManager(monitor, TasksManager.CPUNumber.CPU2, SERVICE_RATE_1_ALFA, SERVICE_RATE_2_BETA);

		CPUStateManager cpuStateManager1 = new CPUStateManager(monitor, TasksManager.CPUNumber.CPU1);
		CPUStateManager cpuStateManager2 = new CPUStateManager(monitor, TasksManager.CPUNumber.CPU2);

		ConsumePendingFlag consumePendingFlag1 = new ConsumePendingFlag(monitor, TasksManager.CPUNumber.CPU1);
		ConsumePendingFlag consumePendingFlag2 = new ConsumePendingFlag(monitor, TasksManager.CPUNumber.CPU2);

		TaskDispatcher taskDispatcher = new TaskDispatcher(monitor, policy);


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

		try {
			taskDispatcher.join();
			int elapsedTime = (int) (new Date().getTime() - initTime.getTime()) / 1000;
			System.out.println(TOTAL_TASKS + " tareas despachadas en " + elapsedTime + " segundos.");
			loger.stop(elapsedTime);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

	}
}
