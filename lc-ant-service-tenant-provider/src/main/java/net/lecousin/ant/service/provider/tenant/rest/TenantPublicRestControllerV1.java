package net.lecousin.ant.service.provider.tenant.rest;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import net.lecousin.ant.service.provider.tenant.TenantServiceImpl;
import net.lecousin.ant.service.tenant.TenantPublicService;
import net.lecousin.ant.service.tenant.dto.Tenant;
import reactor.core.publisher.Mono;

@RestController("tenantServicePublicRestControllerV1")
@RequestMapping("/public-api/tenant/v1")
@RequiredArgsConstructor
public class TenantPublicRestControllerV1 implements TenantPublicService {

	@Qualifier("tenantServiceProvider")
	private final TenantServiceImpl service;
	
	@Override
	@GetMapping("/{id}")
	public Mono<Tenant> findById(@PathVariable("id") String id) {
		return service.findById(id);
	}

	@Override
	@GetMapping
	public Mono<Tenant> findByPublicId(@RequestParam("publicId") String publicId) {
		return service.findByPublicId(publicId);
	}
}
