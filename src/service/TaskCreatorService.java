package service;

public class TaskCreatorService {
    public static Runnable createRunnableTask(String task_name) {
        return () -> {
            System.out.printf("Starting %s at %d%n", task_name, System.currentTimeMillis());
            try {
                Thread.sleep(1000);

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.printf("Ending %s at %d%n", task_name, System.currentTimeMillis());
        };
    }
}
