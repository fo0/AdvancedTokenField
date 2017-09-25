package com.fo0.advancedtokenfield.events;

import com.fo0.advancedtokenfield.main.Token;
import com.fo0.advancedtokenfield.model.TokenLayout;

public class TokenRemoveEvent {

	private TokenLayout TokenLayout;
	private Token token;

	public TokenRemoveEvent(TokenLayout TokenLayout, Token token) {
		super();
		this.TokenLayout = TokenLayout;
		this.token = token;
	}

	public TokenLayout getTokenLayout() {
		return TokenLayout;
	}

	public void setTokenLayout(TokenLayout TokenLayout) {
		this.TokenLayout = TokenLayout;
	}

	public Token getToken() {
		return token;
	}

	public void setToken(Token token) {
		this.token = token;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((TokenLayout == null) ? 0 : TokenLayout.hashCode());
		result = prime * result + ((token == null) ? 0 : token.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof TokenRemoveEvent))
			return false;
		TokenRemoveEvent other = (TokenRemoveEvent) obj;
		if (TokenLayout == null) {
			if (other.TokenLayout != null)
				return false;
		} else if (!TokenLayout.equals(other.TokenLayout))
			return false;
		if (token == null) {
			if (other.token != null)
				return false;
		} else if (!token.equals(other.token))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "TokenRemoveEvent [" + (TokenLayout != null ? "TokenLayout=" + TokenLayout + ", " : "")
				+ (token != null ? "token=" + token : "") + "]";
	}

}
