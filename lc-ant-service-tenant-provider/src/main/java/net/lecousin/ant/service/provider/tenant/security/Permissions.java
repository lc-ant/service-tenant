package net.lecousin.ant.service.provider.tenant.security;

import java.util.Collections;
import java.util.List;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import net.lecousin.ant.core.springboot.security.Permission;
import net.lecousin.ant.service.provider.tenant.TenantConstants;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class Permissions {

	public static final Permission READ = new Permission(TenantConstants.SERVICE_NAME, "read", List.of());
	public static final Permission UPDATE = new Permission(TenantConstants.SERVICE_NAME, "update", List.of(READ));
	public static final Permission DELETE = new Permission(TenantConstants.SERVICE_NAME, "delete", List.of(READ));
	public static final Permission CREATE = new Permission(TenantConstants.SERVICE_NAME, "create", List.of(UPDATE, DELETE));
	
	public static final List<Permission> ALL = Collections.unmodifiableList(List.of(READ, UPDATE, DELETE, CREATE));
	
}
