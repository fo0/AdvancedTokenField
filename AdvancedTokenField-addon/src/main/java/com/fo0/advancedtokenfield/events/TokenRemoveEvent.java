package com.fo0.advancedtokenfield.events;

import com.fo0.advancedtokenfield.main.Token;
import com.vaadin.ui.Component;

public class TokenRemoveEvent {

	private Component component;
	private Token token;

	public TokenRemoveEvent(Component component, Token token) {
		super();
		this.component = component;
		this.token = token;
	}

	public Component getComponent() {
		return component;
	}

	public void setComponent(Component component) {
		this.component = component;
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
		result = prime * result + ((component == null) ? 0 : component.hashCode());
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
		if (component == null) {
			if (other.component != null)
				return false;
		} else if (!component.equals(other.component))
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
		return "TokenRemoveEvent [" + (component != null ? "component=" + component + ", " : "")
				+ (token != null ? "token=" + token : "") + "]";
	}

}
