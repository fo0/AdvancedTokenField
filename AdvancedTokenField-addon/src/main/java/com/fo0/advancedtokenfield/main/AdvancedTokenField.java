package com.fo0.advancedtokenfield.main;

import java.util.ArrayList;
import java.util.List;

import com.fo0.advancedtokenfield.events.TokenRemoveEvent;
import com.fo0.advancedtokenfield.listeners.TokenAddListener;
import com.fo0.advancedtokenfield.listeners.TokenNewItemListener;
import com.fo0.advancedtokenfield.listeners.TokenRemoveListener;
import com.fo0.advancedtokenfield.model.TokenLayout;
import com.vaadin.event.ShortcutAction;
import com.vaadin.event.ShortcutListener;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.CssLayout;

import fi.jasoft.dragdroplayouts.DDCssLayout;
import fi.jasoft.dragdroplayouts.client.ui.LayoutDragMode;
import fi.jasoft.dragdroplayouts.drophandlers.DefaultCssLayoutDropHandler;
import fi.jasoft.dragdroplayouts.interfaces.DragFilter;

public class AdvancedTokenField extends DDCssLayout {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8139678186130686248L;

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
		setDragMode(LayoutDragMode.CLONE);

		// Enable dropping
		setDropHandler(new DefaultCssLayoutDropHandler());

		// Only allow draggin buttons
		setDragFilter(new DragFilter() {
			public boolean isDraggable(Component component) {
				return component instanceof TokenLayout;
			}
		});

		addComponentAttachListener(e -> {
			System.out.println("attached");
		});

		addComponentDetachListener(e -> {
			System.out.println("detached");
		});

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
				addToken(tokenAddListener.action(tokenNewItemListener.action(e)));
				inputField.clear();
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
		removeComponent(event.getTokenLayout());
	}

	public void addToken(Token token) {
		TokenLayout tokenLayout = new TokenLayout(token);
		tokenLayout.getBtn().addClickListener(e -> {
			removeTokenFromLayout(tokenRemoveListener.action(new TokenRemoveEvent(tokenLayout, token)));
		});
		
		addTokenToInputField(token);
		addComponent(tokenLayout, getComponentCount() - 1);
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
		inputField.clear();
	}

	public void clearTokens() {
		for (int i = 0; i < getComponentCount(); i++) {
			if (getComponent(i) instanceof CssLayout) {
				removeComponent(getComponent(i));
			}
		}
	}

}
