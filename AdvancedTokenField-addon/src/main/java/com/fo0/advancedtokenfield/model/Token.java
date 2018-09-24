package com.fo0.advancedtokenfield.model;

import java.io.Serializable;

import com.vaadin.shared.ui.ContentMode;

public class Token implements Serializable {

	private static final long serialVersionUID = -7438343157114436699L;

	private String id;
	private String value;
	private String description;
	private String style;

	private ContentMode descriptionContentMode;

	/**
	 * deprecated as of release 4.0, replaced by {@link Builder}
	 */
	@Deprecated
	public Token(String value) {
		this(value, null);
	}

	/**
	 * deprecated as of release 4.0, replaced by {@link Builder}
	 */
	@Deprecated
	public Token(String value, String style) {
		this(value, value, null);
	}

	/**
	 * deprecated as of release 4.0, replaced by {@link Builder}
	 */
	@Deprecated
	public Token(String id, String value, String style) {
		this(id, value, null, style);
	}

	public Token(String id, String value, String description, String style) {
		this(id, value, description, null, style);
	}

	public Token(String id, String value, String description, ContentMode contentMode, String style) {
		super();
		this.id = id;
		this.value = value;
		this.description = description;

		if (contentMode != null)
			this.descriptionContentMode = contentMode;
		else
			descriptionContentMode = ContentMode.PREFORMATTED;

		this.style = style;
	}

	public Token(Builder builder) {
		this(builder.id, builder.value, builder.description, builder.descriptionContentMode, builder.style);
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

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public ContentMode getDescriptionContentMode() {
		return descriptionContentMode;
	}

	public void setDescriptionContentMode(ContentMode descriptionContentMode) {
		this.descriptionContentMode = descriptionContentMode;
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
				+ (description != null ? "description=" + description + ", " : "")
				+ (style != null ? "style=" + style + ", " : "")
				+ (descriptionContentMode != null ? "descriptionContentMode=" + descriptionContentMode : "") + "]";
	}

	public static class Builder implements Serializable {

		private static final long serialVersionUID = -6785815196882768136L;

		private String id;
		private String value;
		private String description;
		private String style;

		private ContentMode descriptionContentMode;

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

		/**
		 * adds description with default ContentMode: ContentMode.PREFORMATTED
		 * 
		 * @param description
		 * @return
		 */
		public Builder description(String description) {
			this.description = description;
			this.descriptionContentMode = ContentMode.PREFORMATTED;
			return this;
		}

		public Builder descriptio(String description, ContentMode contentMode) {
			this.description = description;
			this.descriptionContentMode = contentMode;
			return this;
		}

		public Builder idAndValue(String idAndValue) {
			this.id = idAndValue;
			this.value = idAndValue;
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
