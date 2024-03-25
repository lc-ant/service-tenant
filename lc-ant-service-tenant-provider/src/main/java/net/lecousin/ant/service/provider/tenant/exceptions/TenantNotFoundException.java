package net.lecousin.ant.service.provider.tenant.exceptions;

import net.lecousin.ant.core.api.exceptions.NotFoundException;
import net.lecousin.commons.io.text.i18n.TranslatedString;

public class TenantNotFoundException extends NotFoundException {

	private static final long serialVersionUID = 1L;

	public TenantNotFoundException(String id) {
		super(new TranslatedString("service-tenant", "tenant not found {}", id), "tenant");
	}
	
}
