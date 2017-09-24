package com.fo0.advancedtokenfield.main;

import java.util.ArrayList;
import java.util.List;

import com.fo0.advancedtokenfield.events.TokenRemoveEvent;
import com.fo0.advancedtokenfield.listeners.TokenAddListener;
import com.fo0.advancedtokenfield.listeners.TokenNewItemListener;
import com.fo0.advancedtokenfield.listeners.TokenRemoveListener;
import com.vaadin.event.ShortcutAction;
import com.vaadin.event.ShortcutListener;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.NativeButton;
import com.vaadin.ui.themes.ValoTheme;

public class AdvancedTokenField extends CssLayout {

	private ComboBox<Token> inputField = new ComboBox<Token>();
	private List<Token> tokensOfField = new ArrayList<Token>();

	private TokenRemoveListener tokenRemoveListener;
	private TokenAddListener tokenAddListener;
	private TokenNewItemListener tokenNewItemListener;

	private static final String BASE_STYLE = "advancedtokenfield-layouttokens";

	public AdvancedTokenField(List<Token> tokens) {
		this.tokensOfField.addAll(tokens);
		init();
	}

	public AdvancedTokenField() {
		init();
	}

	private void init() {
		addStyleName(BASE_STYLE);
		addComponent(inputField);
		inputField.setItems(tokensOfField);
		inputField.setEmptySelectionAllowed(true);
		inputField.setItemCaptionGenerator(e -> e.getValue());

		inputField.addShortcutListener(new ShortcutListener(null, ShortcutAction.KeyCode.ENTER, null) {

			@Override
			public void handleAction(Object sender, Object target) {
				addToken(tokenAddListener.action(inputField.getValue()));
			}
		});

		tokenAddListener = new TokenAddListener() {

			@Override
			public Token action(Token token) {
				return token;
			}
		};

		tokenRemoveListener = new TokenRemoveListener() {

			@Override
			public TokenRemoveEvent action(TokenRemoveEvent event) {
				return event;
			}
		};

		tokenNewItemListener = new TokenNewItemListener() {

			@Override
			public Token action(String token) {
				return new Token(token);
			}
		};
	}

	public void setAllowNewItems(boolean allow) {
		if (allow) {
			inputField.setNewItemHandler(e -> {
				tokenAddListener.action(tokenNewItemListener.action(e));
			});
		} else {
			inputField.setNewItemHandler(null);
		}
	}

	public void addTokenRemoveListener(TokenRemoveListener listener) {
		this.tokenRemoveListener = listener;
	}

	public void addTokenAddListener(TokenAddListener listener) {
		this.tokenAddListener = listener;
	}

	public void addTokenAddNewItemListener(TokenNewItemListener listener) {
		this.tokenNewItemListener = listener;
	}

	public void removeTokenFromLayout(TokenRemoveEvent event) {
		removeComponent(event.getComponent());
	}

	public void addToken(Token token) {
		CssLayout field = new CssLayout();
		field.setData(token);
		field.addStyleName("flat");
		field.addStyleName(ValoTheme.LAYOUT_COMPONENT_GROUP);

		if (token.getStyle() != null && !token.getStyle().isEmpty())
			field.addStyleName(token.getStyle());

		Label lbl = new Label(token.getValue());
		NativeButton btn = new NativeButton();
		btn.setIcon(VaadinIcons.CLOSE);
		btn.addClickListener(e -> {
			removeTokenFromLayout(tokenRemoveListener.action(new TokenRemoveEvent(field, token)));
		});

		field.addComponents(lbl, btn);
		addComponent(field, getComponentCount() - 1);
		addTokenToInputField(token);
		inputField.setValue(null);
	}

	public void addTokens(List<Token> token) {
		token.stream().forEach(e -> addToken(e));
	}

	public ComboBox<Token> getInputField() {
		return inputField;
	}

	public void setInputField(ComboBox<Token> inputField) {
		this.inputField = inputField;
	}

	public List<Token> getTokensOfInputField() {
		return tokensOfField;
	}

	public List<Token> getTokens() {
		List<Token> list = new ArrayList<Token>();
		for (int i = 0; i < getComponentCount(); i++) {
			if (getComponent(i) instanceof CssLayout) {
				CssLayout c = (CssLayout) getComponent(i);
				Token t = (Token) c.getData();
				list.add(t);
			}
		}
		return list;
	}

	public void addTokensToInputField(List<Token> tokens) {
		tokens.stream().forEach(e -> addTokenToInputField(e));
	}

	public void addTokenToInputField(Token token) {
		if (!tokensOfField.contains(token)) {
			tokensOfField.add(token);
		}
	}

	public void clearTokens() {
		for (int i = 0; i < getComponentCount(); i++) {
			if (getComponent(i) instanceof CssLayout) {
				removeComponent(getComponent(i));
			}
		}
	}

}
