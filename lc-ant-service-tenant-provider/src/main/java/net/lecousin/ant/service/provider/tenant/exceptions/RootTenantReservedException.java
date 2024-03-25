package net.lecousin.ant.service.provider.tenant.exceptions;

import net.lecousin.ant.core.api.exceptions.ForbiddenException;
import net.lecousin.commons.io.text.i18n.TranslatedString;

public class RootTenantReservedException extends ForbiddenException {

	private static final long serialVersionUID = 1L;

	public RootTenantReservedException() {
		super(new TranslatedString("service-tenant", "root tenant is reserved"));
	}
	
}
