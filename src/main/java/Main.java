import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static java.lang.Thread.interrupted;

public class Main {

	static final int TOTAL_TASKS = 100;

	static final int ARRIVAL_RATE_1_ALFA = 1;
	static final int ARRIVAL_RATE_1_BETA = 20000000;

	static final int SERVICE_RATE_1_ALFA = 20;
	static final int SERVICE_RATE_1_BETA = 1000;

	static final int SERVICE_RATE_2_ALFA = 20;
	static final int SERVICE_RATE_2_BETA = 1000;

	static final int LOG_RATE_SECONDS = 100;


	public static void main(String[] args) {
		Date initTime;
		TransitionLogger transitionLogger = new TransitionLogger("out\\transitions.txt");
		transitionLogger.start();
		PN pn = new PN(true);
		Policy mPolicy = new SharedLoadPolicy(pn);
		Monitor monitor = new Monitor(pn, mPolicy, transitionLogger.transitionQueue);

		Loger loger;

		TasksManager tasksManager1 = new TasksManager(
			monitor,
			TasksManager.CPUNumber.CPU1,
			SERVICE_RATE_1_ALFA,
			SERVICE_RATE_1_BETA
		);
		TasksManager tasksManager2 = new TasksManager(
			monitor,
			TasksManager.CPUNumber.CPU2,
			SERVICE_RATE_2_ALFA,
			SERVICE_RATE_2_BETA
		);

		CPUStateManager cpuStateManager1 = new CPUStateManager(monitor, TasksManager.CPUNumber.CPU1);
		CPUStateManager cpuStateManager2 = new CPUStateManager(monitor, TasksManager.CPUNumber.CPU2);

		ConsumePendingFlag consumePendingFlag1 = new ConsumePendingFlag(monitor, TasksManager.CPUNumber.CPU1);
		ConsumePendingFlag consumePendingFlag2 = new ConsumePendingFlag(monitor, TasksManager.CPUNumber.CPU2);

		TaskDispatcher taskDispatcher = new TaskDispatcher(monitor);


		List<Thread> threadList = Arrays.asList(tasksManager1, tasksManager2, cpuStateManager1, cpuStateManager2, taskDispatcher);
		loger = new Loger(monitor, threadList, pn, "out\\log.txt");
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
			while (tasksManager1.getTotalTasksServed() + tasksManager2.getTotalTasksServed() < TOTAL_TASKS) {
				Thread.sleep(1000);
				System.out.print(".");
			}
			elapsedTime = (int) (new Date().getTime() - initTime.getTime()) / 1000;
			System.out.println(TOTAL_TASKS + " tareas servidas en " + elapsedTime + " segundos.");
			tasksManager1.interrupt();
			tasksManager2.interrupt();
			loger.stop(elapsedTime);
			transitionLogger.interrupt();
			consumePendingFlag1.interrupt();
			consumePendingFlag2.interrupt();
			cpuStateManager1.interrupt();
			cpuStateManager2.interrupt();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		TInvariantConsumer detector = new TInvariantConsumer();
		detector.mainCheck();

	}
}
