package net.lecousin.ant.service.client.tenant;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;

import net.lecousin.ant.core.springboot.service.client.LcAntServiceClient;
import net.lecousin.ant.service.tenant.TenantPublicService;
import net.lecousin.ant.service.tenant.dto.Tenant;
import reactor.core.publisher.Mono;

@HttpExchange("/public-api/tenant/v1")
@LcAntServiceClient(serviceName = "tenant", serviceUrl = "${lc-ant.services.tenant:tenant-service}", qualifier = "tenantServicePublicRestClientV1")
public interface TenantPublicServiceClientV1 extends TenantPublicService {

	@Override
	@GetExchange("/{id}")
	Mono<Tenant> findById(@PathVariable("id") String id);

	@Override
	@GetExchange
	Mono<Tenant> findByPublicId(@RequestParam("publicId") String publicId);
	
}
