package net.lecousin.ant.service.provider.tenant.rest;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import net.lecousin.ant.core.api.PageRequest;
import net.lecousin.ant.core.api.PageResponse;
import net.lecousin.ant.core.expression.Expression;
import net.lecousin.ant.service.provider.tenant.TenantServiceImpl;
import net.lecousin.ant.service.tenant.TenantService;
import net.lecousin.ant.service.tenant.dto.Tenant;
import reactor.core.publisher.Mono;

@RestController("tenantServiceRestControllerV1")
@RequestMapping("/api/tenant/v1")
@RequiredArgsConstructor
public class TenantRestControllerV1 implements TenantService {

	@Qualifier("tenantServiceProvider")
	private final TenantServiceImpl service;
	
	@Override
	@PostMapping("/_search")
	public Mono<PageResponse<Tenant>> search(@RequestBody(required = false) Expression<Boolean> criteria, PageRequest pageRequest) {
		return service.search(criteria, pageRequest);
	}
	
	@Override
	@GetMapping("/_textSearch")
	public Mono<PageResponse<Tenant>> textSearch(@RequestParam("text") String text, PageRequest pageRequest) {
		return service.textSearch(text, pageRequest);
	}
	
	@Override
	@GetMapping("/{id}")
	public Mono<Tenant> findById(@PathVariable("id") String id) {
		return TenantService.super.findById(id);
	}

	@Override
	@PostMapping
	public Mono<Tenant> create(@RequestBody Tenant tenant) {
		return service.create(tenant);
	}

	@Override
	@PutMapping
	public Mono<Tenant> update(@RequestBody Tenant tenant) {
		return service.update(tenant);
	}

	@Override
	@DeleteMapping("/{id}")
	public Mono<Void> delete(@PathVariable("id") String id) {
		return service.delete(id);
	}
	
	@Override
	@PostMapping("/{id}/configurations/service/{serviceName}/get")
	public Mono<Map<String, Serializable>> getServiceConfigurations(
		@PathVariable("serviceName") String serviceName,
		@PathVariable("id") String tenantId,
		@RequestBody List<String> requestedConfigurations) {
		return service.getServiceConfigurations(serviceName, tenantId, requestedConfigurations);
	}

}
