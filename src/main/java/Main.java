import java.util.Arrays;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

public class Main {



	public static void main(String args[]) {
		Date initTime;
		PN pn = new PN();
		Monitor monitor = new Monitor(pn);

		Loger loger;

		TasksManager tasksManager1 = new TasksManager(monitor, TasksManager.CPUNumber.CPU1, 5, 10);
		TasksManager tasksManager2 = new TasksManager(monitor, TasksManager.CPUNumber.CPU2, 15, 20);

		CPUStateManager cpuStateManager1 = new CPUStateManager(monitor, TasksManager.CPUNumber.CPU1);
		CPUStateManager cpuStateManager2 = new CPUStateManager(monitor, TasksManager.CPUNumber.CPU2);
		TaskDispatcher taskDispatcher = new TaskDispatcher(monitor, pn);

		List<Thread> threadList = Arrays.asList(tasksManager1, tasksManager2, cpuStateManager1, cpuStateManager2, taskDispatcher);
		loger = new Loger(monitor, threadList, pn, "out/log.txt");
		loger.start();

		initTime = new Date();
		taskDispatcher.start();
		tasksManager1.start();
		tasksManager2.start();
		cpuStateManager1.start();
		cpuStateManager2.start();

		try {
			taskDispatcher.join();
			int elapsedTime = (int) (new Date().getTime() - initTime.getTime()) / 1000;
			System.out.println("1000 tareas despachadas en " + elapsedTime + " segundos.");
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

	}
}
