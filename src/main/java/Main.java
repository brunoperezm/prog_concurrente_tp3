import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static java.lang.Thread.interrupted;

public class Main {

	static final int TOTAL_TASKS = 1000;

	static int ARRIVAL_RATE_1_ALFA = 5;
	static final int ARRIVAL_RATE_1_BETA = 20000000;

	static int SERVICE_RATE_1_ALFA = 10;
	static final int SERVICE_RATE_1_BETA = 1000;

	static int SERVICE_RATE_2_ALFA = 10;
	static final int SERVICE_RATE_2_BETA = 1000;


	public static void main(String[] args) {
		try{
			switch(args.length){
				case 3:
					SERVICE_RATE_1_ALFA = Integer.parseInt(args[1]);
					SERVICE_RATE_2_ALFA = Integer.parseInt(args[2]);
				case 1:
					ARRIVAL_RATE_1_ALFA = Integer.parseInt(args[0]);
					break;
			}
		}
		catch (NumberFormatException e){
			System.out.println("Error en argumento... utilizando argumentos por defecto.");
			ARRIVAL_RATE_1_ALFA = 5;
			SERVICE_RATE_1_ALFA = 10;
			SERVICE_RATE_2_ALFA = 10;
		}
		Date initTime;
		TransitionLogger transitionLogger = new TransitionLogger("out/transitions.txt");
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


		Thread autoloopResolver1 = new Thread(() -> {
			while (!interrupted()) {
				monitor.fireTransitions(PN.Transitions.ZT19);
			}
		});
		autoloopResolver1.start();
		Thread autoloopResolver2 = new Thread(() -> {
			while (!interrupted()) {
				monitor.fireTransitions(PN.Transitions.ZT20);
			}
		});
		autoloopResolver2.start();

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
			autoloopResolver1.interrupt();
			autoloopResolver2.interrupt();
			consumePendingFlag1.interrupt();
			consumePendingFlag2.interrupt();
			cpuStateManager1.interrupt();
			cpuStateManager2.interrupt();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

	}
}
