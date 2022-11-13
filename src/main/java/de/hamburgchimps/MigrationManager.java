package de.hamburgchimps;

import org.flywaydb.core.Flyway;

import javax.enterprise.context.ApplicationScoped;
import javax.transaction.Transactional;

@ApplicationScoped
public class MigrationManager {
    @Transactional(Transactional.TxType.NOT_SUPPORTED)
    public void migrate() {
        // We have to configure flyway manually as quarkus will configure
        // flyway to run on the default datasource and not the one returned by
        // our TenantConnectionResolver instance.
        Flyway flyway = Flyway
                .configure()
                .dataSource("jdbc:sqlite:foo", "", "")
                .cleanDisabled(false)
                .load();

        flyway.clean();
        flyway.migrate();
    }
}
