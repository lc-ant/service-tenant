package net.lecousin.ant.service.provider.tenant;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import net.lecousin.ant.connector.database.DatabaseConnectorConfiguration;
import net.lecousin.ant.core.springboot.service.provider.LcAntServiceProviderConfiguration;

@Configuration
@EnableAutoConfiguration
@Import({DatabaseConnectorConfiguration.class, LcAntServiceProviderConfiguration.class})
@ComponentScan
public class TenantServiceConfiguration {

}
