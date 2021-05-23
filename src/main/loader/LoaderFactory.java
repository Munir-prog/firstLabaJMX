package main.loader;

import main.TaskPR;
import main.tasks.TaskRunner;
import main.tasks.TaskRunner2;

import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class LoaderFactory implements LoaderFactoryMBean {

    private ClassLoader loader;
    private String className;
    private Long period;
    private String taskName;
    private String classPath;
    private String getClassPath;
    private static Map<String, TaskRunner2> tasks;
    private static Map<String, Thread> threads;
    private static final Map<String, TaskPR> profileTasks = new HashMap<String, TaskPR>();
    private static final Map<String, Thread> profileThreads = new HashMap<>();
    private static final Map<String, String> classNames = new HashMap<>();


    public LoaderFactory(String taskName, String dir, String className, Long period) throws Exception {
        this.taskName = taskName;
        loader = newLoader(dir);
        this.className = className;
        this.period = period;
        this.classPath = dir;
        classNames.put(taskName, className);
    }

    @Override   //    Name of task  /path to class    /main class name  /period
    public void submit(String name, String classpath, String mainClass, Long period) throws Exception {
        loader = newLoader(classpath);
        tasks = LoaderFactoryRunner.getTasks();
        threads = LoaderFactoryRunner.getThreads();
        this.className = mainClass;
        var clazz = loader.loadClass(mainClass);
        classNames.put(name, mainClass);

        for (Map.Entry<String, TaskRunner2> task : tasks.entrySet()) {
            if (!task.getValue().getClazz().equals(clazz)){
                var taskRunner2 = new TaskRunner2(clazz, period);
                var thread = new Thread(taskRunner2);
                thread.start();
                tasks.put(name, taskRunner2);
                threads.put(name, thread);
            }
        }
    }

    @Override
    public String status(String name) { // shows status of Task. "String name" is The name of task(default is "Task1// ")
        tasks = LoaderFactoryRunner.getTasks();
        for (Map.Entry<String, TaskRunner2> entry : LoaderFactory.tasks.entrySet()) {
            if (entry.getKey().equals(name)){
                var status = entry.getValue().getStatus();
                return status;
            }
        }
        for (Map.Entry<String, TaskPR> entry : profileTasks.entrySet()) {
            if (entry.getKey().equals(name)){
                var status = entry.getValue().getStatus();
                return status;
            }
        }
        return "not found";
    }

    @Override
    public void cancel(String name) {
        tasks = LoaderFactoryRunner.getTasks();
        threads = LoaderFactoryRunner.getThreads();

        for (Map.Entry<String, Thread> entry : threads.entrySet()) {
            if (entry.getKey().equals(name)){
                entry.getValue().interrupt();
            }
        }

        threads.remove(name);
        tasks.remove(name);
        System.out.println(tasks);
        System.out.println(threads);

        for (Map.Entry<String, Thread> entry : profileThreads.entrySet()) {
            if (entry.getKey().equals(name)){
                entry.getValue().interrupt();
            }
        }

        profileThreads.remove(name);
        profileTasks.remove(name);
        System.out.println(profileTasks);
        System.out.println(profileThreads);


    }

    @Override
    public void startProfiling(String name) {
        tasks = LoaderFactoryRunner.getTasks();
        threads = LoaderFactoryRunner.getThreads();

        for (Map.Entry<String, Thread> entry : threads.entrySet()) {
            if (entry.getKey().equals(name)){
                entry.getValue().interrupt();
            }
        }

        threads.remove(name);
        tasks.remove(name);
        for (Map.Entry<String, String> entry : classNames.entrySet()) {
            if (entry.getKey().equals(name)){
                className = entry.getValue();
            }
        }
        submitProfiling(name, classPath, className, period);
    }

    private void submitProfiling(String name, String classPath, String className, Long period) {
        try {
            System.out.println(name + "  " + classPath + "  " + className + "  " + period);
            loader = newLoader(classPath);
            var clazz = loader.loadClass(className);
            var taskRunner = new TaskRunner(clazz, period);
            var taskProfileRunner = new TaskPR(clazz, period, name);
            var thread = new Thread(taskProfileRunner);
            thread.start();
            profileTasks.put(name, taskProfileRunner);
            profileThreads.put(name, thread);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void stopProfiling(String name) {
        for (Map.Entry<String, Thread> entry : profileThreads.entrySet()) {
            if (entry.getKey().equals(name)){
                entry.getValue().interrupt();
            }
        }
        profileTasks.remove(name);
        profileThreads.remove(name);

        for (Map.Entry<String, String> entry : classNames.entrySet()) {
            if (entry.getKey().equals(name)){
                className = entry.getValue();
            }
        }
        unSubmitProfiling(name, classPath, className, period);
    }
    private void unSubmitProfiling(String name, String classPath, String className, Long period) {
        try {
            System.out.println(name + "  " + classPath + "  " + className + "  " + period);
            loader = newLoader(classPath);
            var clazz = loader.loadClass(className);
            var taskRunner2 = new TaskRunner2(clazz, period);
            var thread = new Thread(taskRunner2);
            thread.start();
            tasks.put(name, taskRunner2);
            threads.put(name, thread);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public String getClassName() {
        return className;
    }

    private ClassLoader newLoader(String dir) throws Exception {
        var path = Path.of(dir);
        if (!Files.isDirectory(path))
            throw new RuntimeException();
        return new URLClassLoader(new URL[] {
                path.toUri().toURL()
        });
    }

    public ClassLoader getLoader() {
        return loader;
    }

    public Long getPeriod() {
       return period;
    }
}
