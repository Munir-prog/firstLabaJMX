package main;

import main.loader.LoaderFactoryRunner;
public class Main {


    public static void main(String[] args) throws Exception {

        var loaderFactoryRunner = new LoaderFactoryRunner();
        var thread = new Thread(loaderFactoryRunner);
        thread.start();

    }
}
