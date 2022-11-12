package de.hamburgchimps;

import io.agroal.api.AgroalDataSource;
import io.agroal.api.configuration.supplier.AgroalPropertiesReader;
import io.quarkus.hibernate.orm.PersistenceUnitExtension;
import io.quarkus.hibernate.orm.runtime.customized.QuarkusConnectionProvider;
import io.quarkus.hibernate.orm.runtime.tenant.TenantConnectionResolver;
import io.quarkus.logging.Log;
import org.hibernate.engine.jdbc.connections.spi.ConnectionProvider;

import javax.enterprise.context.ApplicationScoped;
import java.io.File;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

// Use the quarkus TenantConnectionResolver interface to determine the jdbc
// datasource at runtime as opposed to at compile-time. Quarkus still needs a
// default datasource defined in order to build hence the datasource.* properties in
// application.properties. Perhaps there is a better way of doing this but this is what
// I came up with.
// It feels like a lot of work just to dynamically/programmatically define a datasource.
// Using props for datasource initialization as demonstrated here ->
// https://github.com/quarkusio/quarkus/issues/7019#issuecomment-645548780
@PersistenceUnitExtension
@ApplicationScoped
public class NotesDataStoreResolver implements TenantConnectionResolver {
    @Override
    public ConnectionProvider resolve(String tenantId) {
        this.findNotesStoreLocation();
        Map<String,String> props = new HashMap<>();

        props.put(AgroalPropertiesReader.MAX_SIZE, "1");
        props.put(AgroalPropertiesReader.MIN_SIZE,"1");
        props.put(AgroalPropertiesReader.INITIAL_SIZE,"11");
        props.put(AgroalPropertiesReader.MAX_LIFETIME_S,"30");
        props.put(AgroalPropertiesReader.ACQUISITION_TIMEOUT_S,"3");
        props.put(AgroalPropertiesReader.JDBC_URL,"jdbc:sqlite::memory:");

        try {
            return new QuarkusConnectionProvider(AgroalDataSource.from(new AgroalPropertiesReader().readProperties(props).get()));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private String findNotesStoreLocation() {
        var f = new File(String.format("%s/Code/alexkolson", System.getProperty("user.home")));
        Log.debugv("{0}", f.getAbsolutePath());
        Log.debugv("{0}", f.exists());
        Log.debugv("{0}", f.isFile());
        return "";
    }
}
