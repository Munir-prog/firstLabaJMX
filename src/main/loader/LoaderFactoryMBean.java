package main.loader;

public interface LoaderFactoryMBean {
    void submit(String name, String classpath, String mainClass, Long period) throws Exception;
    String status(String name);
    void cancel(String name);
    void startProfiling(String name);
    void stopProfiling(String name);
}
