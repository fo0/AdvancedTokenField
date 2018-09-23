package com.fo0.advancedtokenfield.main;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.apache.commons.lang3.StringUtils;
import org.vaadin.alump.searchdropdown.SearchDropDown;

import com.fo0.advancedtokenfield.interceptor.TokenAddInterceptor;
import com.fo0.advancedtokenfield.interceptor.TokenNewItemInterceptor;
import com.fo0.advancedtokenfield.interceptor.TokenRemoveInterceptor;
import com.fo0.advancedtokenfield.listener.OnEnterListener;
import com.fo0.advancedtokenfield.listener.TokenAddListener;
import com.fo0.advancedtokenfield.listener.TokenClickListener;
import com.fo0.advancedtokenfield.listener.TokenRemoveListener;
import com.fo0.advancedtokenfield.model.Token;
import com.fo0.advancedtokenfield.model.TokenLayout;
import com.fo0.advancedtokenfield.model.TokenSuggestionProvider;
import com.fo0.advancedtokenfield.utils.CONSTANTS;
import com.vaadin.shared.Registration;
import com.vaadin.ui.Component;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.themes.ValoTheme;

import fi.jasoft.dragdroplayouts.DDCssLayout;
import fi.jasoft.dragdroplayouts.client.ui.LayoutDragMode;
import fi.jasoft.dragdroplayouts.drophandlers.DefaultCssLayoutDropHandler;
import fi.jasoft.dragdroplayouts.interfaces.DragFilter;

public class AdvancedTokenField extends DDCssLayout {

	private static final long serialVersionUID = 8139678186130686248L;

	private SearchDropDown<Token> inputField = null;
	private TokenSuggestionProvider suggestionProvider;

	private List<Token> tokensOfField = new ArrayList<Token>();
	private List<Token> initTokensOfField = new ArrayList<Token>();

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
	private TokenClickListener tokenClickListener;

	private OnEnterListener enterListener;

	private boolean allowNewTokens = false;
	private boolean allowEmptyValues = false;
	private boolean removeInitTokens = false;
	private boolean tokenCloseButton = true;
	private int querySuggestionInputMinLength = 2;

	private static final String BASE_STYLE = "advancedtokenfield-layouttokens";

	public AdvancedTokenField(List<Token> tokens) {
		this(tokens, true);
	}

	public AdvancedTokenField(boolean inputFieldVisible) {
		this(null, inputFieldVisible);
	}

	public AdvancedTokenField(List<Token> tokens, boolean inputFieldVisible) {
		if (tokens != null && !tokens.isEmpty())
			this.tokensOfField.addAll(tokens);
		init();
		inputField.setVisible(inputFieldVisible);
	}

	public AdvancedTokenField() {
		init();
	}

	private void init() {
		addStyleName(BASE_STYLE);

		suggestionProvider = new TokenSuggestionProvider(tokensOfField, querySuggestionInputMinLength);
		inputField = new SearchDropDown<Token>(suggestionProvider);
		inputField.addStyleName(ValoTheme.TEXTFIELD_BORDERLESS);
		inputField.addSearchListener(search -> {
			String input = search.getSource().getValue().trim();
			if (StringUtils.isEmpty(input)) {
				return;
			}

			if (!allowNewTokens) {
				Token token = tokensOfField.stream().filter(e -> e.getValue().equals(input)).findFirst().orElse(null);

				if (token != null)
					addToken(token);
			} else {
				Token token = tokensOfField.stream().filter(e -> e.getValue().equals(input)).findFirst().orElse(null);

				if (token != null) {
					addToken(token);
				} else {
					token = tokenNewItemInterceptor.action(new Token(input));
					addToken(token);
				}
			}
		});

		addComponent(inputField);
		setDragMode(LayoutDragMode.CLONE);

		// Enable dropping
		setDropHandler(new DefaultCssLayoutDropHandler());

		// Only allow draggin buttons
		setDragFilter(new DragFilter() {
			private static final long serialVersionUID = 5221913366037820701L;

			public boolean isDraggable(Component component) {
				return component instanceof TokenLayout;
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

		copyInputfieldTokensToInitTokens();
	}

	protected void copyInputfieldTokensToInitTokens() {
		initTokensOfField.clear();
		initTokensOfField.addAll(tokensOfField);
	}

	public void setRemoveInitTokens(boolean remove) {
		this.removeInitTokens = remove;
	}

	public void setQuerySuggestionInputMinLength(int minLength) {
		this.querySuggestionInputMinLength = minLength;
		suggestionProvider.setQuerySuggestionInputMinLength(querySuggestionInputMinLength);
	}

	public boolean isAllowNewTokens() {
		return allowNewTokens;
	}

	public void setAllowNewTokens(boolean allowNewTokens) {
		this.allowNewTokens = allowNewTokens;
	}

	public boolean isAllowEmptyValues() {
		return allowEmptyValues;
	}

	public void setAllowEmptyValues(boolean allowEmptyValues) {
		this.allowEmptyValues = allowEmptyValues;
	}

	public void setTokenCloseButton(boolean tokenCloseButton) {
		this.tokenCloseButton = tokenCloseButton;
	}

	public boolean getTokenCloseButton() {
		return tokenCloseButton;
	}

	@Override
	public Registration addComponentAttachListener(ComponentAttachListener listener) {
		if (CONSTANTS.DEBUG)
			System.out.println("add detecting class attach");
		return super.addComponentAttachListener(listener);
	}

	@Override
	public void removeComponent(Component c) {
		if (CONSTANTS.DEBUG)
			System.out.println("remove detecting class: " + c.getClass());
		if (c instanceof TokenLayout) {
			// detect the drag and drop from layout
			removeToken(((TokenLayout) c).getToken());
			return;
		}
		super.removeComponent(c);
	}

	@Override
	public void addComponentAsFirst(Component c) {
		if (CONSTANTS.DEBUG)
			System.out.println("add detecting class: " + c.getClass());
		if (c instanceof TokenLayout) {
			// detect the drag and drop from layout
			addToken(((TokenLayout) c).getToken(), getComponentCount());
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

		if (removeInitTokens || !initTokensOfField.contains(tokenData)) {
			if (CONSTANTS.DEBUG) {
				System.out.println("remove init tokens: " + removeInitTokens);

				System.out.println("in  init token: " + initTokensOfField.stream().anyMatch(e -> e.equals(tokenData)));
				System.out.println("removing token: " + tokenData);
			}
			tokensOfField.remove(tokenData);
		}
	}

	public void addToken(Token token, int idx) {
		Token tokenData = tokenAddInterceptor.action(token);
		if (tokenData == null) {
			// filter empty tokens
			return;
		}

		TokenLayout tokenLayout = new TokenLayout(tokenData, tokenClickListener, tokenCloseButton);

		if (tokenCloseButton)
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

		inputField.clear();
	}

	public void addToken(Token token) {
		addToken(token, -1);
	}

	public void addTokens(List<Token> token) {
		token.stream().forEach(e -> addToken(e));
	}

	public SearchDropDown<Token> getInputField() {
		return inputField;
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
		if (tokens == null || tokens.isEmpty())
			return;

		addTokensToInputField(tokens.stream().toArray(Token[]::new));
	}

	public void addTokensToInputField(Token... tokens) {
		List<Token> list = Stream.of(tokens).distinct().filter(e -> !tokensOfField.contains(e))
				.collect(Collectors.toList());
		if (list == null || list.isEmpty())
			return;

		tokensOfField.addAll(list);

		copyInputfieldTokensToInitTokens();
	}

	public void addTokenToInputField(Token token) {
		addTokensToInputField(token);
	}

	public void clearTokens() {
		List<Component> componentsToRemove = new ArrayList<Component>();

		IntStream.range(0, getComponentCount()).forEach(e -> {
			if (getComponent(e) instanceof CssLayout) {
				componentsToRemove.add(getComponent(e));
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

	/**
	 * Listener
	 */

	public void addTokenRemoveListener(TokenRemoveListener listener) {
		this.tokenRemoveListener = listener;
	}

	public void addTokenAddListener(TokenAddListener listener) {
		this.tokenAddListener = listener;
	}

	public void addOnEnterListener(OnEnterListener listener) {
		enterListener = listener;
	}

	public void addTokenClickListener(TokenClickListener listener) {
		this.tokenClickListener = listener;
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
