package net.lecousin.ant.service.provider.tenant;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.annotation.Primary;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import net.lecousin.ant.connector.database.DatabaseConnector;
import net.lecousin.ant.core.api.PageRequest;
import net.lecousin.ant.core.api.PageResponse;
import net.lecousin.ant.core.condition.Condition;
import net.lecousin.ant.core.mapping.Mappers;
import net.lecousin.ant.core.springboot.aop.Valid;
import net.lecousin.ant.core.springboot.connector.ConnectorService;
import net.lecousin.ant.core.validation.ValidationContext;
import net.lecousin.ant.service.provider.tenant.db.TenantConfigEntity;
import net.lecousin.ant.service.provider.tenant.db.TenantEntity;
import net.lecousin.ant.service.tenant.TenantService;
import net.lecousin.ant.service.tenant.dto.Tenant;
import reactor.core.publisher.Mono;

@Service("tenantServiceProvider")
@Primary
@RequiredArgsConstructor
public class TenantServiceImpl implements TenantService, InitializingBean {

	private final ConnectorService connectorService;
	
	private Function<Tenant, TenantEntity> dtoToEntity;
	private Function<TenantEntity, Tenant> entityToDto;
	
	private static final int MAX_PAGE_SIZE = 100;
	
	@Override
	public void afterPropertiesSet() throws Exception {
		this.dtoToEntity = Mappers.createMapper(Tenant.class, TenantEntity.class);
		this.entityToDto = Mappers.createMapper(TenantEntity.class, Tenant.class);
	}
	
	private Mono<DatabaseConnector> db() {
		return connectorService.getConnector(DatabaseConnector.class);
	}
	
	@Override
	@PreAuthorize("hasAuthority('per:tenant:read')")
	public Mono<PageResponse<Tenant>> search(Optional<Condition> criteria, PageRequest pageRequest) {
		pageRequest.forcePaging(MAX_PAGE_SIZE);
		return db().flatMap(db -> db.find(TenantEntity.class).where(criteria).paging(pageRequest).execute())
			.map(response -> response.map(entityToDto));
	}

	@Override
	@PreAuthorize("hasAuthority('per:tenant:create')")
	public Mono<Tenant> create(@Valid(ValidationContext.CREATION) Tenant tenant) {
		return db().flatMap(db -> db.create(dtoToEntity.apply(tenant)))
			.map(entityToDto);
	}

	@Override
	@PreAuthorize("hasAuthority('per:tenant:update')")
	public Mono<Tenant> update(@Valid(ValidationContext.UPDATE) Tenant tenant) {
		return db().flatMap(db -> db.update(tenant, TenantEntity.class))
			.map(entityToDto);
	}

	@Override
	@PreAuthorize("hasAuthority('per:tenant:delete')")
	public Mono<Void> delete(String id) {
		return db().flatMap(db -> db.delete(TenantEntity.class, Condition.field("id").is(id)));
	}
	
	@Override
	public Mono<Map<String, Serializable>> getConfigs(String tenantId, List<String> requestedConfigurations) {
		return db()
		.flatMap(db -> db
			.find(TenantConfigEntity.class)
			.where(Condition.and(Condition.field("tenantId").is(tenantId), Condition.field("configKey").in(requestedConfigurations)))
			.execute()
		).map(page -> {
			Map<String, Serializable> result = new HashMap<>();
			page.getData().forEach(entity -> result.put(entity.getConfigKey(), entity.getConfigValue()));
			return result;
		});
	}

}
