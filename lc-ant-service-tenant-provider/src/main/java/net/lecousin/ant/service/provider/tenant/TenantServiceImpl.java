package net.lecousin.ant.service.provider.tenant;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Primary;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.lecousin.ant.connector.database.DatabaseConnector;
import net.lecousin.ant.core.api.PageRequest;
import net.lecousin.ant.core.api.PageResponse;
import net.lecousin.ant.core.expression.Expression;
import net.lecousin.ant.core.expression.impl.ConditionAnd;
import net.lecousin.ant.core.expression.impl.ConditionOr;
import net.lecousin.ant.core.mapping.Mappers;
import net.lecousin.ant.core.security.NodePermissionDeclaration;
import net.lecousin.ant.core.security.PermissionDeclaration;
import net.lecousin.ant.core.security.RequiredPermissions;
import net.lecousin.ant.core.security.Root;
import net.lecousin.ant.core.springboot.aop.Trace;
import net.lecousin.ant.core.springboot.aop.Valid;
import net.lecousin.ant.core.springboot.connector.ConnectorService;
import net.lecousin.ant.core.springboot.messaging.ApiDataChangeEventSenderService;
import net.lecousin.ant.core.springboot.service.provider.LcAntServiceProvider;
import net.lecousin.ant.core.validation.ValidationContext;
import net.lecousin.ant.service.provider.tenant.db.TenantEntity;
import net.lecousin.ant.service.provider.tenant.db.TenantServiceConfigEntity;
import net.lecousin.ant.service.provider.tenant.exceptions.RootTenantReservedException;
import net.lecousin.ant.service.provider.tenant.exceptions.TenantNotFoundException;
import net.lecousin.ant.service.tenant.TenantPublicService;
import net.lecousin.ant.service.tenant.TenantService;
import net.lecousin.ant.service.tenant.dto.Tenant;
import reactor.core.publisher.Mono;

@Service("tenantServiceProvider")
@Primary
@RequiredArgsConstructor
@Slf4j
public class TenantServiceImpl implements TenantService, TenantPublicService, LcAntServiceProvider, InitializingBean {

	public static final String SERVICE_NAME = "tenant";
	
	private final ConnectorService connectorService;
	private final ApiDataChangeEventSenderService eventSender;
	
	private Function<Tenant, TenantEntity> dtoToEntity;
	private Function<TenantEntity, Tenant> entityToDto;
	
	private static final int MAX_PAGE_SIZE = 100;
	
	@Override
	public void afterPropertiesSet() throws Exception {
		this.dtoToEntity = Mappers.createMapper(Tenant.class, TenantEntity.class);
		this.entityToDto = Mappers.createMapper(TenantEntity.class, Tenant.class);
	}
	
	@Override
	public String getServiceName() {
		return SERVICE_NAME;
	}
	
	@Override
	public List<PermissionDeclaration> getServicePermissions() {
		return TenantPermissions.ALL;
	}
	
	@Override
	public List<NodePermissionDeclaration> getServiceNodePermissions() {
		return Collections.emptyList();
	}
	
	@Override
	public List<Object> getDependencies() {
		return List.of();
	}
	
	private Mono<DatabaseConnector> db() {
		return connectorService.getConnector(DatabaseConnector.class);
	}
	
	@Override
	@Trace(service = SERVICE_NAME)
	public Mono<Tenant> findById(String id) {
		return db().flatMap(db -> db.findById(TenantEntity.class, id))
			.map(entityToDto)
			.switchIfEmpty(Mono.error(new TenantNotFoundException(id)));
	}
	
	@Override
	@Trace(service = SERVICE_NAME)
	public Mono<Tenant> findByPublicId(String publicId) {
		return db().flatMap(db -> db.findOne(TenantEntity.class, TenantEntity.FIELD_PUBLIC_ID.is(publicId)))
			.map(entityToDto)
			.switchIfEmpty(Mono.error(new TenantNotFoundException(publicId)));
	}
	
	@Override
	@PreAuthorize("hasAuthority('per:tenant:read')")
	@Trace(service = SERVICE_NAME)
	public Mono<PageResponse<Tenant>> search(Expression<Boolean> criteria, PageRequest pageRequest) {
		pageRequest.forcePaging(MAX_PAGE_SIZE);
		return db().flatMap(db -> db.find(TenantEntity.class).where(criteria).paging(pageRequest).execute())
			.map(response -> response.map(entityToDto));
	}
	
	@Override
	public Mono<PageResponse<Tenant>> textSearch(String text, PageRequest pageRequest) {
		pageRequest.forcePaging(MAX_PAGE_SIZE);
		return db().flatMap(db -> db.textSearch(TenantEntity.class, text, Optional.of(pageRequest)))
			.map(response -> response.map(entityToDto));
	}

	@Override
	@PreAuthorize("hasAuthority('per:tenant:create')")
	@Trace(service = SERVICE_NAME)
	public Mono<Tenant> create(@Valid(ValidationContext.CREATION) Tenant tenant) {
		tenant.setPublicId(tenant.getPublicId().toLowerCase());
		if (tenant.getPublicId().equals(Root.AUTHORITY.toLowerCase()))
			return Mono.error(new RootTenantReservedException());
		return db().flatMap(db -> db.create(dtoToEntity.apply(tenant)))
			.map(entityToDto)
			.doOnSuccess(dto -> eventSender.sendNewData(dto.getId(), dto, RequiredPermissions.RootPermission.of(TenantPermissions.READ)));
	}

	@Override
	@PreAuthorize("hasAuthority('per:tenant:update')")
	@Trace(service = SERVICE_NAME)
	public Mono<Tenant> update(@Valid(ValidationContext.UPDATE) Tenant tenant) {
		return db().flatMap(
		db -> db.findById(TenantEntity.class, tenant.getId())
			.switchIfEmpty(Mono.error(new TenantNotFoundException(tenant.getId())))
			.flatMap(entity -> {
				if (entity.getPublicId().equals(Root.AUTHORITY.toLowerCase()))
					return Mono.error(new RootTenantReservedException());
				tenant.setPublicId(Optional.ofNullable(tenant.getPublicId()).map(String::toLowerCase).orElse(null));
				return db.update(tenant, TenantEntity.class);
			})
			.map(entityToDto)
			.doOnSuccess(dto -> eventSender.sendNewData(dto.getId(), dto, RequiredPermissions.RootPermission.of(TenantPermissions.READ)))
		);
	}

	@Override
	@PreAuthorize("hasAuthority('per:tenant:delete')")
	@Trace(service = SERVICE_NAME)
	public Mono<Void> delete(String id) {
		return db().flatMap(
		db -> db.findById(TenantEntity.class, id)
			.switchIfEmpty(Mono.error(new TenantNotFoundException(id)))
			.flatMap(entity -> {
				if (entity.getPublicId().equals(Root.AUTHORITY.toLowerCase()))
					return Mono.error(new RootTenantReservedException());
				return db.delete(TenantEntity.class, TenantEntity.FIELD_ID.is(id));
			})
		)
		.doOnSuccess(dto -> eventSender.sendDeletedData(id, Tenant.class, id, RequiredPermissions.RootPermission.of(TenantPermissions.READ)));
	}
	
	@Override
	@PreAuthorize("(principal == 'service:' + #service) || hasAuthority('root') || (hasAuthority('ten:' + #tenantId) && hasAuthority('per:tenant:config'))")
	@Trace(service = SERVICE_NAME)
	public Mono<Map<String, Serializable>> getServiceConfigurations(@P("service") String service, @P("tenantId") String tenantId, List<String> requestedConfigurations) {
		return db()
		.flatMap(db -> db
			.find(TenantServiceConfigEntity.class)
			.where(new ConditionAnd(
				TenantServiceConfigEntity.FIELD_TENANT_ID.is(tenantId),
				TenantServiceConfigEntity.FIELD_CONFIG_KEY.in(requestedConfigurations),
				new ConditionOr(
					TenantServiceConfigEntity.FIELD_SERVICE.is(service),
					TenantServiceConfigEntity.FIELD_SERVICE.isNull()
				)
			))
			.execute()
		).map(page -> {
			Map<String, Serializable> result = new HashMap<>();
			Map<String, Serializable> defaultConfig = new HashMap<>();
			page.getData().forEach(entity -> {
				if (entity.getService().isPresent())
					result.put(entity.getConfigKey(), entity.getConfigValue());
				else
					defaultConfig.put(entity.getConfigKey(), entity.getConfigValue());
			});
			defaultConfig.entrySet().forEach(entry -> {
				if (!result.containsKey(entry.getKey()))
					result.put(entry.getKey(), entry.getValue());
			});
			return result;
		});
	}
	
	
	@Override
	public Mono<Void> init(ConfigurableApplicationContext applicationContext) {
		return createRootTenantIfMissing();
	}
	
	private Mono<Void> createRootTenantIfMissing() {
		return db().flatMap(db ->
			db.find(TenantEntity.class).where(TenantEntity.FIELD_PUBLIC_ID.is(Root.AUTHORITY)).executeSingle()
			.switchIfEmpty(Mono.defer(() -> {
				log.info("Startup: root tenant does not exist yet => create it");
				TenantEntity entity = new TenantEntity();
				entity.setPublicId(Root.AUTHORITY.toLowerCase());
				entity.setValidityStart(Optional.of(LocalDate.now()));
				entity.setDisplayName("root");
				return db.create(entity)
					.doOnSuccess(created -> {
						log.info("Startup: root tenant created with id {}", created.getId());
						Tenant dto = entityToDto.apply(created);
						eventSender.sendNewData(dto.getId(), dto, RequiredPermissions.RootPermission.of(TenantPermissions.READ));
					});
			}))
			.then()
		);
	}
	
	@Override
	public Mono<Void> stop(ConfigurableApplicationContext applicationContext) {
		return Mono.empty();
	}

}
