package com.fo0.advancedtokenfield.model;

import java.util.regex.Pattern;

import org.vaadin.alump.searchdropdown.HighlighedSearchSuggestion;

public class TokenSuggestion extends HighlighedSearchSuggestion<Token> {

	private static final long serialVersionUID = 2275566933673919804L;

	private Token token;

	public TokenSuggestion(Token token, Pattern pattern) {
		setQuery(pattern);
		this.token = token;
	}

	@Override
	public String getPlainText() {
		return token.getValue();
	}

	@Override
	public Token getValue() {
		return token;
	}

}
