package net.lecousin.ant.service.client.tenant;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.service.annotation.DeleteExchange;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;
import org.springframework.web.service.annotation.PostExchange;
import org.springframework.web.service.annotation.PutExchange;

import net.lecousin.ant.core.api.PageRequest;
import net.lecousin.ant.core.api.PageResponse;
import net.lecousin.ant.core.condition.Condition;
import net.lecousin.ant.core.springboot.service.client.LcAntServiceClient;
import net.lecousin.ant.service.tenant.TenantService;
import net.lecousin.ant.service.tenant.dto.Tenant;
import reactor.core.publisher.Mono;

@HttpExchange("/api/tenant/v1")
@LcAntServiceClient(service = "${lc-ant.services.tenant:tenant-service}", qualifier = "tenantServiceRestClientV1")
public interface TenantServiceClientV1 extends TenantService {

	@Override
	@PostExchange("/_search")
	Mono<PageResponse<Tenant>> search(@RequestBody Optional<Condition> criteria, PageRequest pageRequest);
	
	@Override
	@GetExchange("/{id}")
	default Mono<Tenant> findById(@PathVariable("id") String id) {
		return TenantService.super.findById(id);
	}

	@Override
	@PostExchange
	Mono<Tenant> create(@RequestBody Tenant tenant);

	@Override
	@PutExchange
	Mono<Tenant> update(@RequestBody Tenant tenant);

	@Override
	@DeleteExchange("/{id}")
	Mono<Void> delete(@PathVariable("id") String id);
	
	@PostExchange("/{id}/configs/get")
	 Mono<Map<String, Serializable>> getConfigs(
		@PathVariable("id") String tenantId,
		@RequestBody List<String> requestedConfigurations);
}
