package net.lecousin.ant.service.provider.tenant.db;

import java.io.Serializable;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.lecousin.ant.connector.database.annotations.Entity;
import net.lecousin.ant.connector.database.annotations.GeneratedValue;

@Entity(domain = "tenant", name = "config")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TenantConfigEntity {

	@Id
	@GeneratedValue
	private String id;
	@Version
	private long version;
	
	private String tenantId;
	private String configKey;
	private Serializable configValue;
	
}
