package com.fo0.advancedtokenfield.main;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.stream.IntStream;

import com.fo0.advancedtokenfield.enums.TokenFieldLayout;
import com.fo0.advancedtokenfield.interceptor.TokenAddInterceptor;
import com.fo0.advancedtokenfield.interceptor.TokenNewItemInterceptor;
import com.fo0.advancedtokenfield.interceptor.TokenRemoveInterceptor;
import com.fo0.advancedtokenfield.listener.OnEnterListener;
import com.fo0.advancedtokenfield.listener.TokenAddListener;
import com.fo0.advancedtokenfield.listener.TokenNewItemListener;
import com.fo0.advancedtokenfield.listener.TokenRemoveListener;
import com.fo0.advancedtokenfield.model.Token;
import com.fo0.advancedtokenfield.model.TokenLayout;
import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.event.ShortcutListener;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.CssLayout;

import fi.jasoft.dragdroplayouts.DDCssLayout;
import fi.jasoft.dragdroplayouts.client.ui.LayoutDragMode;
import fi.jasoft.dragdroplayouts.drophandlers.DefaultCssLayoutDropHandler;

public class AdvancedTokenField extends DDCssLayout {

	private static final long serialVersionUID = -8339803297037565191L;

	public static boolean ALLOW_DUPLICATED_TOKEN_VALUES = true;

	private ComboBox<Token> inputField = new ComboBox<Token>();
	private Collection<Token> tokensOfField = new ArrayList<Token>();

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

	public AdvancedTokenField(Collection<Token> tokens) {
		this.tokensOfField = tokens;
		init();
	}

	public AdvancedTokenField(boolean inputFieldVisible) {
		inputField.setVisible(inputFieldVisible);
		init();
	}

	public AdvancedTokenField(Collection<Token> tokens, boolean inputFieldVisible) {
		this.tokensOfField = tokens;
		inputField.setVisible(inputFieldVisible);
		init();
	}

	public AdvancedTokenField() {
		init();
	}

	public void setTokenFieldLayout(TokenFieldLayout layout) {
		switch (layout) {
		case Vertical:

			break;

		case Horizontal:

			break;
		}
	}

	private void init() {
		this.addStyleName(BASE_STYLE);
		addComponent(inputField);
		this.setDragMode(LayoutDragMode.CLONE);

		// Enable dropping
		this.setDropHandler(new DefaultCssLayoutDropHandler());

		// Only allow draggin tokens
		this.setDragFilter(component -> {
			return component instanceof TokenLayout;
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

		tokenAddInterceptor = token -> {
			return token;
		};

		tokenRemoveInterceptor = token -> {
			return token;
		};

		tokenNewItemInterceptor = token -> {
			return token;
		};
	}

	public DDCssLayout getLayout() {
		return this;
	}

	public void setAllowNewItems(boolean allow) {
		if (allow) {
			inputField.setNewItemHandler(e -> {
				Token token = Token.builder().value(e).build();
				addToken(tokenAddInterceptor.action(tokenNewItemInterceptor.action(token)));
				if (tokenNewItemListener != null)
					tokenNewItemListener.action(token);
				inputField.clear();
			});
		} else {
			inputField.setNewItemHandler(null);
		}
	}

	public AdvancedTokenField withAllowDuplicatedTokens(boolean allow) {
		ALLOW_DUPLICATED_TOKEN_VALUES = allow;
		return this;
	}

	public boolean isAllowDuplicatedTokens() {
		return ALLOW_DUPLICATED_TOKEN_VALUES;
	}

	@Override
	public void removeComponent(Component c) {
		if (c instanceof TokenLayout) {
			// detect the drag and drop from layout
			removeToken(((TokenLayout) c).getToken());
			return;
		}
		super.removeComponent(c);
	}

	@Override
	public void addComponentAsFirst(Component c) {
		if (c instanceof TokenLayout) {
			// detect the drag and drop from layout
			addToken(((TokenLayout) c).getToken(), this.getComponentCount());
			return;
		}

		super.addComponent(c);
	}

	@Override
	public void addComponent(Component c) {
		if (c instanceof TokenLayout) {
			// detect the drag and drop from layout
			addToken(((TokenLayout) c).getToken(), -1);
			return;
		}

		super.addComponent(c);
	}

	@Override
	public void addComponent(Component c, int index) {
		if (c instanceof TokenLayout) {
			// detect the drag and drop from layout
			addToken(((TokenLayout) c).getToken(), index);
			return;
		}

		this.addComponent(c, index);
	}

	public void removeToken(Token token) {
		Token tokenData = tokenRemoveInterceptor.action(token);

		if (tokenData == null) {
			// prevent remove if interceptor not allow
			return;
		}

		// search in layout and remove if found
		TokenLayout tl = null;
		for (Iterator<Component> iterator = this.iterator(); iterator.hasNext();) {
			Component component = (Component) iterator.next();
			if (component instanceof TokenLayout) {
				if (((TokenLayout) component).getToken().equals(token)) {
					tl = (TokenLayout) component;
					break;
				}
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
			super.addComponent(tokenLayout, this.getComponentCount() - 1);

		}

		if (tokenAddListener != null) {
			tokenAddListener.action(tokenData);
		}
		System.out.println("added token");
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

	/**
	 * Replaced by getValue()
	 * 
	 * @return
	 */
	@Deprecated
	public Collection<Token> getTokensOfInputField() {
		return tokensOfField;
	}

	public List<Token> getTokens() {
		List<Token> list = new ArrayList<Token>();
		for (int i = 0; i < this.getComponentCount(); i++) {
			if (this.getComponent(i) instanceof CssLayout) {
				CssLayout c = (CssLayout) this.getComponent(i);
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
		List<Component> componentsToRemove = new ArrayList<Component>();

		IntStream.range(0, this.getComponentCount()).forEach(e -> {
			if (this.getComponent(e) instanceof CssLayout) {
				componentsToRemove.add(this.getComponent(e));
			}
		});

		componentsToRemove.stream().forEach(e -> {
			removeComponent(e);
		});
	}

	public void clearAll() {
		clearTokens();
		tokensOfField.clear();
	}

	public void clearTokenList() {
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
