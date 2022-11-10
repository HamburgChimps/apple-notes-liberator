package de.hamburgchimps;

import io.quarkus.hibernate.orm.runtime.tenant.TenantResolver;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
// TODO: @next-1 add comment about why this is necessary.
public class NotesTenantResolver implements TenantResolver {
    @Override
    public String getDefaultTenantId() {
        return "notes";
    }

    @Override
    public String resolveTenantId() {
        return "notes";
    }
}
