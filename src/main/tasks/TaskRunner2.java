package main.tasks;

public class TaskRunner2 implements Runnable{

    private String[] args;
    private final Class<?> clazz;
    private final Long period;
    private String status;

    public TaskRunner2(Class<?> clazz, Long period) {
        this.clazz = clazz;
        this.period = period;
    }


    @Override
    public void run() {
        try {
            status = "running";
            while (runTime(period)) {
                clazz.getMethod("main", String[].class).invoke(null, (Object) args);
                int test = 8;
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
