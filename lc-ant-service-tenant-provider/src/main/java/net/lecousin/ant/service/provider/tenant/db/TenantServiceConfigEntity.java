package net.lecousin.ant.service.provider.tenant.db;

import java.io.Serializable;
import java.util.Optional;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.lecousin.ant.connector.database.annotations.Entity;
import net.lecousin.ant.connector.database.annotations.GeneratedValue;
import net.lecousin.ant.connector.database.annotations.Index;
import net.lecousin.ant.connector.database.model.IndexType;
import net.lecousin.ant.core.expression.impl.FieldReference;
import net.lecousin.ant.core.expression.impl.StringFieldReference;

@Entity(domain = "tenant", name = "service_config")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Index(fields = { "tenantId", "service", "configKey" }, type = IndexType.UNIQUE)
public class TenantServiceConfigEntity {
	
	public static final StringFieldReference.Nullable FIELD_SERVICE = new StringFieldReference.Nullable("service");
	public static final StringFieldReference FIELD_TENANT_ID = new StringFieldReference("tenantId");
	public static final StringFieldReference FIELD_CONFIG_KEY = new StringFieldReference("configKey");
	public static final FieldReference.Nullable<Serializable> FIELD_CONFIG_VALUE = new FieldReference.Nullable<>("configValue");

	@Id
	@GeneratedValue
	private String id;
	@Version
	private long version;
	
	private Optional<String> service;
	private String tenantId;
	private String configKey;
	private Serializable configValue;
	
}
