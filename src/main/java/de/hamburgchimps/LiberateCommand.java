package de.hamburgchimps;

import picocli.CommandLine.Command;

@Command
public class LiberateCommand implements Runnable {

    String name;

    @Override
    public void run() {
        System.out.printf("Hello %s, go go commando!\n", name);
    }

}
