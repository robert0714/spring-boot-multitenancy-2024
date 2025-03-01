package com.iisigroup.multitenant.sample.demo.flyway;

import javax.sql.DataSource; 

import org.flywaydb.core.Flyway;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;

import com.thomasvitale.multitenant.tenantdetails.TenantDetailsService;

@Component
public class TenantFlywayMigrationInitializer implements InitializingBean, Ordered {

    private static final String TENANT_MIGRATION_LOCATION = "db/migration/tenant";

    private final DataSource dataSource;
    private final Flyway defaultFlyway;
    private final TenantDetailsService tenantDetailsService;

    public TenantFlywayMigrationInitializer(DataSource dataSource, Flyway defaultFlyway, TenantDetailsService tenantDetailsService) {
        this.dataSource = dataSource;
        this.defaultFlyway = defaultFlyway;
        this.tenantDetailsService = tenantDetailsService;
    }

    @Override
    public void afterPropertiesSet() {
        tenantDetailsService.loadAllTenants().forEach(tenant -> {
            Flyway flyway = tenantFlyway(tenant.schema());
            flyway.migrate();
        });
    }

    private Flyway tenantFlyway(String schema) {
        return Flyway.configure()
                .configuration(defaultFlyway.getConfiguration())
                .locations(TENANT_MIGRATION_LOCATION)
                .dataSource(dataSource)
                .schemas(schema)
                .load();
    }

    @Override
    public int getOrder() {
        // Executed after the default schema initialization in FlywayMigrationInitializer.
        return 1;
    }

}
