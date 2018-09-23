package com.fo0.advancedtokenfield.model;

import java.io.Serializable;

public class Token implements Serializable {

	private static final long serialVersionUID = -7438343157114436699L;

	private String id;
	private String value;
	private String style;

	public Token(String value) {
		super();
		this.id = value;
		this.value = value;
	}

	public Token(String value, String style) {
		super();
		this.id = value;
		this.value = value;
		this.style = style;
	}

	public Token(String id, String value, String style) {
		super();
		this.id = id;
		this.value = value;
		this.style = style;
	}

	public Token(Builder builder) {
		this(builder.id, builder.value, builder.style);
	}

	public static Builder builder() {
		return new Builder();
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

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Token other = (Token) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Token [" + (id != null ? "id=" + id + ", " : "") + (value != null ? "value=" + value + ", " : "")
				+ (style != null ? "style=" + style : "") + "]";
	}

	public static class Builder {

		private String id;
		private String value;
		private String style;

		Builder() {

		}

		public Builder id(String id) {
			this.id = id;
			return this;
		}

		public Builder value(String value) {
			this.value = value;
			return this;
		}

		public Builder style(String style) {
			this.style = style;
			return this;
		}

		public Token build() {
			return new Token(this);
		}
	}

}
