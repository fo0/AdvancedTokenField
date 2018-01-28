package com.fo0.advancedtokenfield.main;

import java.awt.color.ICC_ColorSpace;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

import com.fo0.advancedtokenfield.interceptor.TokenAddInterceptor;
import com.fo0.advancedtokenfield.interceptor.TokenNewItemInterceptor;
import com.fo0.advancedtokenfield.interceptor.TokenRemoveInterceptor;
import com.fo0.advancedtokenfield.listener.OnEnterListener;
import com.fo0.advancedtokenfield.listener.TokenAddListener;
import com.fo0.advancedtokenfield.listener.TokenNewItemListener;
import com.fo0.advancedtokenfield.listener.TokenRemoveListener;
import com.fo0.advancedtokenfield.model.TokenLayout;
import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.event.dd.DragAndDropEvent;
import com.vaadin.event.dd.DropHandler;
import com.vaadin.shared.Registration;
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

	/**
	 * Interceptors
	 */
	private TokenRemoveInterceptor tokenRemoveInterceptor;
	private TokenAddInterceptor tokenAddInterceptor;
	private TokenNewItemInterceptor tokenNewItemInterceptor;

	/**
	 * Listener
	 */
	private TokenRemoveListener tokenRemoveListener;
	private TokenAddListener tokenAddListener;
	private TokenNewItemListener tokenNewItemListener;

	private OnEnterListener enterListener;

	private static final String BASE_STYLE = "advancedtokenfield-layouttokens";

	public AdvancedTokenField(List<Token> tokens) {
		this.tokensOfField.addAll(tokens);
		init();
	}

	public AdvancedTokenField(boolean inputFieldVisible) {
		inputField.setVisible(inputFieldVisible);
		init();
	}

	public AdvancedTokenField(List<Token> tokens, boolean inputFieldVisible) {
		this.tokensOfField.addAll(tokens);
		inputField.setVisible(inputFieldVisible);
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

		inputField.setItems(tokensOfField);
		inputField.setEmptySelectionAllowed(true);
		inputField.setItemCaptionGenerator(e -> e.getValue());

		inputField.addShortcutListener(new ShortcutListener("Enter", KeyCode.ENTER, null) {

			private static final long serialVersionUID = -3010772759203328514L;

			@Override
			public void handleAction(Object sender, Object target) {
				Token tokenValue = inputField.getValue();

				if (tokenValue != null && !tokenValue.getValue().isEmpty()) {
					addToken(tokenValue);
				}

				if (enterListener != null) {
					enterListener.action(tokenValue);
				}
			}
		});

		tokenAddInterceptor = new TokenAddInterceptor() {

			@Override
			public Token action(Token token) {
				return token;
			}
		};

		tokenRemoveInterceptor = new TokenRemoveInterceptor() {

			@Override
			public Token action(Token event) {
				return event;
			}
		};

		tokenNewItemInterceptor = new TokenNewItemInterceptor() {

			@Override
			public Token action(Token token) {
				return token;
			}
		};
	}

	public void setAllowNewItems(boolean allow) {
		if (allow) {
			inputField.setNewItemHandler(e -> {
				Token token = new Token(e);
				addToken(tokenAddInterceptor.action(tokenNewItemInterceptor.action(token)));
				inputField.clear();
				if (tokenNewItemListener != null)
					tokenNewItemListener.action(token);
			});
		} else {
			inputField.setNewItemHandler(null);
		}
	}

	@Override
	public Registration addComponentAttachListener(ComponentAttachListener listener) {
		System.out.println("add detecting class attach");
		return super.addComponentAttachListener(listener);
	}

	@Override
	public void removeComponent(Component c) {
		System.out.println("remove detecting class: " + c.getClass());
		if (c instanceof TokenLayout) {
			// detect the drag and drop from layout
			removeToken(((TokenLayout) c).getToken());
			return;
		}
		super.removeComponent(c);
	}

	@Override
	public void addComponent(Component c) {
		System.out.println("add detecting class: " + c.getClass());
		if (c instanceof TokenLayout) {
			// detect the drag and drop from layout
			addToken(((TokenLayout) c).getToken());
			return;
		}

		super.addComponent(c);
	}

	@Override
	public void addComponent(Component c, int index) {
		System.out.println("add detecting class: " + c.getClass() + " at index: " + index);
		if (c instanceof TokenLayout) {
			// detect the drag and drop from layout
			addToken(((TokenLayout) c).getToken());
			return;
		}

		super.addComponent(c, index);
	}

	public void removeToken(Token token) {
		Token tokenData = tokenRemoveInterceptor.action(token);

		if (tokenData == null) {
			// prevent remove if interceptor not allow
			return;
		}

		// search in layout and remove if found
		TokenLayout tl = null;
		for (Iterator<Component> iterator = iterator(); iterator.hasNext();) {
			Component component = (Component) iterator.next();
			if (component instanceof TokenLayout) {
				tl = (TokenLayout) component;
			}
		}

		if (tl != null && tl.getToken() != null && tl.getToken().equals(tokenData)) {
			super.removeComponent(tl);
		}

		if (tokenRemoveListener != null && tl != null) {
			tokenRemoveListener.action(tl);
		}

		tokensOfField.remove(tokenData);
	}

	public void addToken(Token token, int idx) {
		Token tokenData = tokenAddInterceptor.action(token);
		if (tokenData == null) {
			// filter empty tokens
			return;
		}

		TokenLayout tokenLayout = new TokenLayout(tokenData);
		tokenLayout.getBtn().addClickListener(e -> {
			removeToken(tokenLayout.getToken());
		});

		addTokenToInputField(tokenData);

		if (idx > -1) {
			super.addComponent(tokenLayout, idx);
		} else {
			super.addComponent(tokenLayout, getComponentCount() - 1);

		}

		if (tokenAddListener != null)
			tokenAddListener.action(tokenData);
	}

	public void addToken(Token token) {
		addToken(token, -1);
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

	public void clearAll() {
		clearTokens();
		tokensOfField.clear();
	}

	/**
	 * Listener
	 */

	public void addTokenRemoveListener(TokenRemoveListener listener) {
		this.tokenRemoveListener = listener;
	}

	public void addTokenAddListener(TokenAddListener listener) {
		this.tokenAddListener = listener;
	}

	public void addTokenAddNewItemListener(TokenNewItemListener listener) {
		this.tokenNewItemListener = listener;
	}

	public void addOnEnterListener(OnEnterListener listener) {
		enterListener = listener;
	}

	/**
	 * Interceptors
	 */

	public void addTokenRemoveInterceptor(TokenRemoveInterceptor interceptor) {
		this.tokenRemoveInterceptor = interceptor;
	}

	public void addTokenAddInterceptor(TokenAddInterceptor interceptor) {
		this.tokenAddInterceptor = interceptor;
	}

	public void addTokenAddNewItemInterceptor(TokenNewItemInterceptor interceptor) {
		this.tokenNewItemInterceptor = interceptor;
	}

}
