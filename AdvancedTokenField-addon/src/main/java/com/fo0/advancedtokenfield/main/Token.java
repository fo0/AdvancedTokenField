package com.fo0.advancedtokenfield.main;

public class Token {

	private String value;
	private String style;

	public Token(String value) {
		super();
		this.value = value;
	}

	public Token(String value, String style) {
		super();
		this.value = value;
		this.style = style;
	}

	public String getStyle() {
		return style;
	}

	public void setStyle(String style) {
		this.style = style;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((value == null) ? 0 : value.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof Token))
			return false;
		Token other = (Token) obj;
		if (value == null) {
			if (other.value != null)
				return false;
		} else if (!value.equals(other.value))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Token [" + (value != null ? "value=" + value + ", " : "") + (style != null ? "style=" + style : "")
				+ "]";
	}

}
