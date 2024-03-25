package net.lecousin.ant.service.tenant;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import net.lecousin.ant.core.api.PageRequest;
import net.lecousin.ant.core.api.PageResponse;
import net.lecousin.ant.core.condition.Condition;
import net.lecousin.ant.core.springboot.api.CRUDService;
import net.lecousin.ant.service.tenant.dto.Tenant;
import reactor.core.publisher.Mono;

public interface TenantService extends CRUDService<Tenant> {
	
	String ADMIN_TENANT_PUBLIC_ID = "admin";

	default Mono<Tenant> findByPublicId(String publicId) {
		return search(
			Optional.of(Condition.field("publicId").is(publicId)),
			PageRequest.first()
		).mapNotNull(PageResponse::firstOrNull);
	}
	
	Mono<Map<String, Serializable>> getConfigs(String tenantId, List<String> requestedConfigurations);
	
	default Mono<Serializable> getConfig(String tenantId, String configuration) {
		return getConfigs(tenantId, List.of(configuration))
			.map(map -> map.get(configuration));
	}
	
}
