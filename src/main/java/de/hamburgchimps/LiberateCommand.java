package de.hamburgchimps;

import io.quarkus.arc.Arc;
import picocli.CommandLine.Command;

import javax.inject.Inject;
import javax.transaction.Transactional;

@Command
public class LiberateCommand implements Runnable {
    @Inject
    MigrationManager migrationManager;

    @Override
    @Transactional
    public void run() {
        migrationManager.migrate();
        // Fake request context so that quarkus will call our tenant resolver
        Arc.container().requestContext().activate();
        var e = new TestEntity();
        e.persist();
    }

}
