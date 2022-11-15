package de.hamburgchimps;

import io.quarkus.logging.Log;
import picocli.CommandLine.Command;

@Command
public class LiberateCommand implements Runnable {
    @Override
    public void run() {
        Log.info("This is a command-line app to get your data out of apple notes and into something more accessible");
    }
}
