package net.lecousin.ant.service.tenant;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import net.lecousin.ant.core.springboot.api.CRUDService;
import net.lecousin.ant.core.springboot.api.TextSearchService;
import net.lecousin.ant.service.tenant.dto.Tenant;
import reactor.core.publisher.Mono;

public interface TenantService extends CRUDService<Tenant>, TextSearchService<Tenant> {
	
	Mono<Map<String, Serializable>> getServiceConfigurations(String service, String tenantId, List<String> requestedConfigurations);
	
	default Mono<Optional<Serializable>> getServiceConfiguration(String service, String tenantId, String configuration) {
		return getServiceConfigurations(service, tenantId, List.of(configuration))
			.map(map -> Optional.ofNullable(map.get(configuration)));
	}
	
}
