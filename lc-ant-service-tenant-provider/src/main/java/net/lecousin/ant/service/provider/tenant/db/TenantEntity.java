package net.lecousin.ant.service.provider.tenant.db;

import java.time.LocalDate;
import java.util.Optional;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.lecousin.ant.connector.database.annotations.Entity;
import net.lecousin.ant.connector.database.annotations.GeneratedValue;
import net.lecousin.ant.connector.database.annotations.Index;
import net.lecousin.ant.connector.database.model.IndexType;
import net.lecousin.ant.core.api.ApiData;
import net.lecousin.ant.core.expression.impl.NumberFieldReference;
import net.lecousin.ant.core.expression.impl.StringFieldReference;
import net.lecousin.ant.core.validation.annotations.StringConstraint;

@Entity(domain = "tenant")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Index(fields = { "publicId" }, type = IndexType.UNIQUE)
@Index(fields = { "publicId", "displayName" }, type = IndexType.TEXT)
// CHECKSTYLE DISABLE: MagicNumber
public class TenantEntity {
	
	public static final StringFieldReference FIELD_ID = ApiData.FIELD_ID;
	public static final NumberFieldReference<Long> FIELD_VERSION = ApiData.FIELD_VERSION;
	public static final StringFieldReference FIELD_PUBLIC_ID = new StringFieldReference("publicId");

	@Id
	@GeneratedValue
	private String id;
	@Version
	private long version;
	
	@StringConstraint(minLength = 3, maxLength = 30)
	private String publicId;
	@StringConstraint(minLength = 3, maxLength = 100)
	private String displayName;
	
	private Optional<LocalDate> validityStart;
	private Optional<LocalDate> validityEnd;

}
