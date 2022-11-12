package de.hamburgchimps;

import io.quarkus.hibernate.orm.PersistenceUnitExtension;
import io.quarkus.hibernate.orm.runtime.tenant.TenantResolver;

import javax.enterprise.context.ApplicationScoped;


// As far as I can tell, this plumbing becomes necessary if quarkus
// detects that a class that implements TenantConnectionResolver
// Also as far as I can tell, quarkus only bothers calling resolveTenantId() if
// there is an active request context. Otherwise, only getDefaultTenantId() is invoked.
@PersistenceUnitExtension
@ApplicationScoped
public class NotesTenantResolver implements TenantResolver {
    @Override
    public String getDefaultTenantId() {
        return "notes";
    }

    @Override
    public String resolveTenantId() {
        return getDefaultTenantId();
    }
}
