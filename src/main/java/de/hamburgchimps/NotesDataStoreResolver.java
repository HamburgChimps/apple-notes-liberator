package de.hamburgchimps;

import io.agroal.api.AgroalDataSource;
import io.agroal.api.configuration.supplier.AgroalPropertiesReader;
import io.quarkus.hibernate.orm.PersistenceUnitExtension;
import io.quarkus.hibernate.orm.runtime.customized.QuarkusConnectionProvider;
import io.quarkus.hibernate.orm.runtime.tenant.TenantConnectionResolver;
import io.quarkus.logging.Log;
import org.hibernate.engine.jdbc.connections.spi.ConnectionProvider;

import javax.enterprise.context.ApplicationScoped;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

@PersistenceUnitExtension
@ApplicationScoped
// TODO: @next add comment about why this is so complicated
public class NotesDataStoreResolver implements TenantConnectionResolver {
    @Override
    public ConnectionProvider resolve(String tenantId) {
        Log.debugv("okay what {0}", tenantId);
        Map<String,String> props = new HashMap<>();

        props.put(AgroalPropertiesReader.MAX_SIZE,"10");
        props.put(AgroalPropertiesReader.MIN_SIZE,"10");
        props.put(AgroalPropertiesReader.INITIAL_SIZE,"10");
        props.put(AgroalPropertiesReader.MAX_LIFETIME_S,"300");
        props.put(AgroalPropertiesReader.ACQUISITION_TIMEOUT_S,"30");
        props.put(AgroalPropertiesReader.JDBC_URL,"jdbc:sqlite:foo");
        props.put(AgroalPropertiesReader.PRINCIPAL,"username");
        props.put(AgroalPropertiesReader.CREDENTIAL,"password");

        try {
            return new QuarkusConnectionProvider(AgroalDataSource.from(new AgroalPropertiesReader().readProperties(props).get()));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
