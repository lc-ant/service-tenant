package net.lecousin.ant.service.tenant.dto;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import net.lecousin.ant.core.api.ApiData;
import net.lecousin.ant.core.validation.annotations.GreaterThanField;
import net.lecousin.ant.core.validation.annotations.Mandatory;
import net.lecousin.ant.core.validation.annotations.StringConstraint;

@Data
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
// CHECKSTYLE DISABLE: MagicNumber
public class Tenant extends ApiData {

	private static final long serialVersionUID = 1L;
	
	@StringConstraint(minLength = 3, maxLength = 30)
	private String publicId;
	
	@StringConstraint(minLength = 3, maxLength = 100)
	private String displayName;
	
	@JsonFormat(pattern = "yyyy-MM-dd")
	@Mandatory(false)
	private LocalDate validityStart;
	
	@JsonFormat(pattern = "yyyy-MM-dd")
	@Mandatory(false)
	@GreaterThanField("validityStart")
	private LocalDate validityEnd;
	
	@JsonIgnore
	public boolean isActive() {
		if (validityStart == null) return false;
		LocalDate today = LocalDate.now();
		return validityStart.compareTo(today) <= 0 && (validityEnd == null || validityEnd.isAfter(today));
	}
}
