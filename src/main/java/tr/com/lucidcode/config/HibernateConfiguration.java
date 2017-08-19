package tr.com.lucidcode.config;

import java.io.IOException;
import java.util.Properties;

import org.hibernate.cfg.Environment;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.service.ServiceRegistryBuilder;

public class HibernateConfiguration {
	
	public static ServiceRegistry hibernateConfig() {
		Properties properties = new Properties();
		properties.setProperty(Environment.DIALECT,"org.hibernate.dialect.MySQLDialect");
		properties.setProperty(Environment.URL, "jdbc:mysql://finance-db-identifier.cahy8ao5jcat.us-east-2.rds.amazonaws.com:3306/finance");
		properties.setProperty(Environment.DRIVER, "com.mysql.jdbc.Driver");


		properties.setProperty("hibernate.connection.provider_class", "org.hibernate.service.jdbc.connections.internal.C3P0ConnectionProvider");
		properties.setProperty("hibernate.c3p0.min_size", "5");
		properties.setProperty("hibernate.c3p0.max_size", "20");
		properties.setProperty("hibernate.c3p0.timeout", "300");
		properties.setProperty("hibernate.c3p0.max_statements", "50");
		properties.setProperty("hibernate.c3p0.idle_test_period", "300");

		properties.setProperty(Environment.USER, System.getProperty("RDS_SECRET_USER"));
		properties.setProperty(Environment.PASS, System.getProperty("RDS_SECRET_PASS"));


		ServiceRegistryBuilder serviceRegistryBuilder = new ServiceRegistryBuilder();
		serviceRegistryBuilder.applySettings(properties);
		
		ServiceRegistry serviceRegistry = serviceRegistryBuilder.buildServiceRegistry();
		return serviceRegistry;
	}
}
