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
import net.lecousin.ant.core.validation.annotations.StringConstraint;

@Entity(domain = "tenant", name = "tenant")
@Data
@NoArgsConstructor
@AllArgsConstructor
// CHECKSTYLE DISABLE: MagicNumber
public class TenantEntity {

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
