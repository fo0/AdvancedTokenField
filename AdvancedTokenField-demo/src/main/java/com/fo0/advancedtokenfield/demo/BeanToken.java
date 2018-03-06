package com.fo0.advancedtokenfield.demo;

import java.util.ArrayList;
import java.util.Collection;
import java.util.stream.IntStream;

import org.apache.commons.lang3.RandomStringUtils;

import com.fo0.advancedtokenfield.model.Token;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BeanToken {

	@Builder.Default
	private Collection<Token> tokens = new ArrayList<Token>();

	public BeanToken random() {
		IntStream.range(0, 5)
				.forEach(e -> tokens.add(Token.builder().value(RandomStringUtils.randomAlphanumeric(5)).build()));
		return this;
	}

}
