package com.fo0.advancedtokenfield.model;

import com.fo0.advancedtokenfield.main.Token;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.NativeButton;
import com.vaadin.ui.themes.ValoTheme;

public class TokenLayout extends CssLayout {

	private static final long serialVersionUID = 1818425531699295539L;
	
	private Label lbl = new Label();
	private NativeButton btn = new NativeButton();

	public TokenLayout(Token token) {
		super();
		lbl.setValue(token.getValue());

		setData(token);
		addStyleName("flat");
		addStyleName(ValoTheme.LAYOUT_COMPONENT_GROUP);
		btn.setIcon(VaadinIcons.CLOSE);
		
		if (token.getStyle() != null && !token.getStyle().isEmpty())
			addStyleName(token.getStyle());
		
		addComponents(lbl, btn);
	}

	public Label getLbl() {
		return lbl;
	}

	public void setLbl(Label lbl) {
		this.lbl = lbl;
	}

	public NativeButton getBtn() {
		return btn;
	}

	public void setBtn(NativeButton btn) {
		this.btn = btn;
	}

	@Override
	public String toString() {
		return "TokenLayout [" + (lbl != null ? "lbl=" + lbl + ", " : "") + (btn != null ? "btn=" + btn : "") + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((btn == null) ? 0 : btn.hashCode());
		result = prime * result + ((lbl == null) ? 0 : lbl.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (!(obj instanceof TokenLayout))
			return false;
		TokenLayout other = (TokenLayout) obj;
		if (btn == null) {
			if (other.btn != null)
				return false;
		} else if (!btn.equals(other.btn))
			return false;
		if (lbl == null) {
			if (other.lbl != null)
				return false;
		} else if (!lbl.equals(other.lbl))
			return false;
		return true;
	}

}
