package de.hamburgchimps;

import io.quarkus.runtime.StartupEvent;
import org.flywaydb.core.Flyway;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.transaction.Transactional;

@ApplicationScoped
public class MigrationManager {
    @Inject
    Flyway flyway;

    @Transactional(Transactional.TxType.NOT_SUPPORTED)
    public void migrate(@Observes StartupEvent e) {
        flyway.clean();
        flyway.migrate();
    }
}
