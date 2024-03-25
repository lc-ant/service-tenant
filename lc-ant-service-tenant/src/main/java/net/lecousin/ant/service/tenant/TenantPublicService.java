package net.lecousin.ant.service.tenant;

import net.lecousin.ant.service.tenant.dto.Tenant;
import reactor.core.publisher.Mono;

public interface TenantPublicService {

	Mono<Tenant> findById(String id);
	
	Mono<Tenant> findByPublicId(String publicId);
	
}
