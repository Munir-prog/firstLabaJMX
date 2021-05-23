
package main;

public class TaskPR implements Runnable{

    private String[] args;
    private final Class<?> clazz;
    private final Long period;
    private String status;
    private String taskName;

    public TaskPR(Class<?> clazz, Long period, String taskName) {
        this.clazz = clazz;
        this.period = period;
        this.taskName = taskName;
    }

    @Override
    public void run() {
        try {
            var start = System.currentTimeMillis();
            int execution = 0;
            status = "running";
            while (runTime(period)) {
                execution++;
                clazz.getMethod("main", String[].class).invoke(null, (Object) args);
                var end = System.currentTimeMillis();
                System.out.println("[profile] task '" + taskName + "', execution #" + execution +
                        ", time elapsed: " + (end-start) + " ms");
            }
        } catch(Exception e){
            status = "error: " + e.getMessage();
            e.printStackTrace();
        }

    }

    private boolean runTime(Long period) throws InterruptedException {
        Thread.sleep(period);
        return true;
    }

    public Class<?> getClazz() {

        return clazz;
    }

    public String getStatus() {

        return status;
    }
}

