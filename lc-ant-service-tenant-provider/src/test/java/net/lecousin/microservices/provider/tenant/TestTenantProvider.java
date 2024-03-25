package net.lecousin.microservices.provider.tenant;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;

import org.junit.jupiter.api.TestTemplate;
import org.springframework.context.annotation.Import;

import net.lecousin.ant.core.api.PageRequest;
import net.lecousin.ant.core.security.Root;
import net.lecousin.ant.core.springboot.service.client.LcAntServiceClientConfiguration;
import net.lecousin.ant.core.springboot.test.LcAntServiceTest;
import net.lecousin.ant.core.springboot.test.TestWithBeans;
import net.lecousin.ant.core.springboot.test.WithMockInternalAuthentication;
import net.lecousin.ant.service.provider.tenant.TenantServiceConfiguration;
import net.lecousin.ant.service.tenant.TenantService;
import net.lecousin.ant.service.tenant.dto.Tenant;

@LcAntServiceTest(service = "tenant")
@Import({TenantServiceConfiguration.class, LcAntServiceClientConfiguration.class})
@TestWithBeans(value = TenantService.class, qualifiers = { "tenantServiceProvider", "tenantServiceRestControllerV1", "tenantServiceRestClientV1" })
class TestTenantProvider {

	@TestTemplate
	@WithMockInternalAuthentication(authorities = Root.AUTHORITY)
	void testCRUDTenant(TenantService service) {
		Tenant tenant = service.create(Tenant.builder().publicId("test").displayName("This is a test").validityStart(LocalDate.now()).build()).block();
		assertThat(tenant.getPublicId()).isEqualTo("test");
		assertThat(tenant.getDisplayName()).isEqualTo("This is a test");
		assertThat(tenant.getValidityStart()).isNotNull();
		assertThat(tenant.getValidityEnd()).isNull();
		assertThat(tenant.isActive()).isTrue();
		assertThat(tenant.getId()).isNotNull();
		assertThat(tenant.getVersion()).isEqualTo(1);
		
		var page = service.search(null, PageRequest.builder().withTotal(true).build()).block();
		assertThat(page).isNotNull();
		assertThat(page.getTotal()).isEqualTo(2L);
		assertThat(page.getData()).hasSize(2);
		tenant = page.getData().get(0);
		if ("root".equals(tenant.getPublicId())) {
			tenant = page.getData().get(1);
		}
		assertThat(tenant.getPublicId()).isEqualTo("test");
		assertThat(tenant.getDisplayName()).isEqualTo("This is a test");
		assertThat(tenant.getValidityStart()).isNotNull();
		assertThat(tenant.getValidityEnd()).isNull();
		assertThat(tenant.isActive()).isTrue();
		assertThat(tenant.getId()).isNotNull();
		assertThat(tenant.getVersion()).isEqualTo(1);
		
		service.delete(tenant.getId()).block();
		
		page = service.search(null, PageRequest.builder().withTotal(true).build()).block();
		assertThat(page).isNotNull();
		assertThat(page.getTotal()).isEqualTo(1L);
	}
	
}
