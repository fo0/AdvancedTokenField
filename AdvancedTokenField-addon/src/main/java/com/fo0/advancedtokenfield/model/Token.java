package com.fo0.advancedtokenfield.model;

import java.io.Serializable;

import org.apache.commons.lang3.RandomStringUtils;

import com.fo0.advancedtokenfield.main.AdvancedTokenField;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(of = { "id" })
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Token implements Serializable, Cloneable {

	private static final long serialVersionUID = -7438343157114436699L;

	@Builder.Default
	private String id = RandomStringUtils.randomAlphanumeric(10);

	@Builder.Default
	private String value;

	@Builder.Default
	private String style;

	public String getId() {
		if (AdvancedTokenField.ALLOW_DUPLICATED_TOKEN_VALUES) {
			return id = RandomStringUtils.randomAlphanumeric(10);
		}
		return id;
	}

}
