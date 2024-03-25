package net.lecousin.ant.service.provider.tenant;

import java.util.List;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import net.lecousin.ant.connector.database.DatabaseConnectorConfiguration;
import net.lecousin.ant.core.springboot.security.Permission;
import net.lecousin.ant.core.springboot.service.provider.LcAntServiceProviderConfiguration;
import net.lecousin.ant.service.provider.tenant.security.Permissions;

@Configuration
@EnableAutoConfiguration
@Import({DatabaseConnectorConfiguration.class, LcAntServiceProviderConfiguration.class})
@ComponentScan
public class TenantServiceConfiguration {

	@Bean
	List<Permission> permissions() {
		return Permissions.ALL;
	}
	
}
