package net.lecousin.ant.service.provider.tenant;

import java.util.Collections;
import java.util.List;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import net.lecousin.ant.core.security.PermissionDeclaration;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class TenantPermissions {

	public static final PermissionDeclaration READ = new PermissionDeclaration(TenantServiceImpl.SERVICE_NAME, "read", List.of());
	public static final PermissionDeclaration UPDATE = new PermissionDeclaration(TenantServiceImpl.SERVICE_NAME, "update", List.of(READ));
	public static final PermissionDeclaration DELETE = new PermissionDeclaration(TenantServiceImpl.SERVICE_NAME, "delete", List.of(READ));
	public static final PermissionDeclaration CREATE = new PermissionDeclaration(TenantServiceImpl.SERVICE_NAME, "create", List.of(UPDATE, DELETE));
	public static final PermissionDeclaration CONFIG = new PermissionDeclaration(TenantServiceImpl.SERVICE_NAME, "config", List.of(READ));
	
	public static final List<PermissionDeclaration> ALL = Collections.unmodifiableList(List.of(READ, UPDATE, DELETE, CREATE, CONFIG));
	
}
