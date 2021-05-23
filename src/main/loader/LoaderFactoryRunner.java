package main.loader;

import main.tasks.TaskRunner2;
import javax.management.ObjectName;
import java.lang.management.ManagementFactory;
import java.util.HashMap;
import java.util.Map;

public class LoaderFactoryRunner implements Runnable {
    private static final String DEFAULT_DIR = "/test";
    private static final String DEFAULT_CLASSNAME = "Test";
    private static final Long DEFAULT_PERIOD = 2000L;
    private static final String OBJECT_NAME = "LoaderMBeanAgent:name=factory";
    public static final String DEFAULT_TASK_NAME = "Task1";
    private static Map<String, TaskRunner2> tasks;
    private static Map<String, Thread> threads;

    @Override
    public void run() {
        try {
            initializeMaps();
            LoaderFactory loaderFactory =  new LoaderFactory(DEFAULT_TASK_NAME, DEFAULT_DIR, DEFAULT_CLASSNAME, DEFAULT_PERIOD);
            ManagementFactory.getPlatformMBeanServer().registerMBean(loaderFactory, new ObjectName(OBJECT_NAME));
            startLoading(loaderFactory);
            
            while (true){
                mainThreadTask();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void mainThreadTask() throws InterruptedException {
        Thread.sleep(5000);
        System.out.println("This is main Thread");
    }

    private void startLoading(LoaderFactory loaderFactory) throws ClassNotFoundException {
        var loader = loaderFactory.getLoader();
        var clazz = loader.loadClass(loaderFactory.getClassName());
        var taskRunner2 = new TaskRunner2(clazz, loaderFactory.getPeriod());
        var thread = new Thread(taskRunner2);
        thread.start();
        tasks.put(DEFAULT_TASK_NAME, taskRunner2);
        threads.put(DEFAULT_TASK_NAME, thread);
    }

    private void initializeMaps() {
        tasks = new HashMap<>();
        threads = new HashMap<>();
    }

    public static Map<String, TaskRunner2> getTasks() {
        return tasks;
    }

    public static Map<String, Thread> getThreads() {
        return threads;
    }
}
