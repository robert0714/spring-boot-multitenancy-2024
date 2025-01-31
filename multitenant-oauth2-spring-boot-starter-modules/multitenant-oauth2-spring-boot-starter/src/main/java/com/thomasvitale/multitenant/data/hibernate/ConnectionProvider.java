package com.thomasvitale.multitenant.data.hibernate;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map; 

import javax.sql.DataSource; 
import org.hibernate.cfg.AvailableSettings;
import org.hibernate.engine.jdbc.connections.spi.MultiTenantConnectionProvider;
import org.springframework.boot.autoconfigure.orm.jpa.HibernatePropertiesCustomizer;

import com.thomasvitale.multitenant.tenantdetails.TenantDetailsService;
 
public class ConnectionProvider implements MultiTenantConnectionProvider<String>, HibernatePropertiesCustomizer {
 
	private static final long serialVersionUID = -640059839136369038L;
	
	private final DataSource dataSource;
    private final TenantDetailsService tenantDetailsService;
    private final Map<String,String> cache;
    private final String defaultSchema;

    public ConnectionProvider(DataSource dataSource, TenantDetailsService tenantDetailsService) {
		this.dataSource = dataSource;
        this.tenantDetailsService = tenantDetailsService;
        this.cache = new HashMap<>();
        this.defaultSchema = tenantDetailsService.getDefaultSchema().orElse("public") ;	
    }

	@Override
	public Connection getAnyConnection() throws SQLException { 		
		return getConnection(defaultSchema);
	}

	@Override
	public void releaseAnyConnection(Connection connection) throws SQLException {
		connection.close();
	}

	@Override
	public Connection getConnection(String tenantIdentifier) throws SQLException {
        var tenantDetails = tenantDetailsService.loadTenantByIdentifier(tenantIdentifier);
        var schema = tenantDetails != null ? tenantDetails.schema() : tenantIdentifier;
		Connection connection = dataSource.getConnection();
		 
		final String dpn = this.cache.getOrDefault("dpn", retriveDPN(connection));
		this.cache.putIfAbsent("dpn", dpn);
		
		switch (dpn) {
		case "PostgreSQL":
			connection.setSchema(schema);
			break;
		case "H2":
			connection.setSchema(schema);
			break;
		case "MySQL":
			connection.createStatement().execute("USE %s".formatted(schema));
			break;
		case "MariaDB":
			connection.createStatement().execute("USE %s".formatted(schema));
			break;				
		default:
			throw new UnsupportedOperationException("Unimplemented method 'getConnection'.");
		}
		
		return connection;
	}

	protected static String retriveDPN(final Connection connection) throws SQLException {
		DatabaseMetaData metadata = connection.getMetaData();
		final String dpn = metadata.getDatabaseProductName();
		return dpn;
	}
	@Override
	public void releaseConnection(String tenantIdentifier, Connection connection) throws SQLException { 
		final String dpn = this.cache.get("dpn");	
		
		switch (dpn) {
		case "PostgreSQL":
			connection.setSchema(defaultSchema); 
			break;
		case "H2":
			connection.setSchema(defaultSchema); 
			break;
		case "MySQL": 
			 connection.createStatement().execute("USE %s".formatted(defaultSchema));			 
			break;
		case "MariaDB":
			 connection.createStatement().execute("USE %s".formatted(defaultSchema));			 
			break;	
		default:
			throw new UnsupportedOperationException("Unimplemented method 'releaseConnection'.");
		}
		connection.close();
  	}

	@Override
	public boolean supportsAggressiveRelease() {
		return true;
	}

	@Override
	public boolean isUnwrappableAs(Class<?> unwrapType) {
		return false;
	}

	@Override
	public <T> T unwrap(Class<T> unwrapType) {
		throw new UnsupportedOperationException("Unimplemented method 'unwrap'.");
	}

	@Override
	public void customize(Map<String, Object> hibernateProperties) {
		hibernateProperties.put(AvailableSettings.MULTI_TENANT_CONNECTION_PROVIDER, this);
	}

}
