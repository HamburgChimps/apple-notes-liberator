package de.hamburgchimps;

import io.quarkus.arc.Arc;
import picocli.CommandLine.Command;

import javax.transaction.Transactional;

@Command
public class LiberateCommand implements Runnable {
    @Override
    @Transactional
    public void run() {
        // Fake request context so that quarkus will call our tenant resolver
        Arc.container().requestContext().activate();
        var e = new TestEntity();
        e.persist();
    }

}
