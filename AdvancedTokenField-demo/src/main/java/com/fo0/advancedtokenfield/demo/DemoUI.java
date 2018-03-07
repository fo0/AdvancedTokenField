package com.fo0.advancedtokenfield.demo;

import java.util.Arrays;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.servlet.annotation.WebServlet;

import com.fo0.advancedtokenfield.interceptor.TokenNewItemInterceptor;
import com.fo0.advancedtokenfield.interceptor.TokenRemoveInterceptor;
import com.fo0.advancedtokenfield.main.AdvancedTokenField;
import com.fo0.advancedtokenfield.model.Token;
import com.vaadin.annotations.Push;
import com.vaadin.annotations.Theme;
import com.vaadin.annotations.Title;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.data.Binder;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.shared.communication.PushMode;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

@Theme("demo")
@Title("AdvancedTokenField Add-on Demo")
@SuppressWarnings("serial")
@Push(PushMode.AUTOMATIC)
public class DemoUI extends UI {

	@WebServlet(value = "/*", asyncSupported = true)
	@VaadinServletConfiguration(productionMode = false, ui = DemoUI.class)
	public static class Servlet extends VaadinServlet {
	}

	private VerticalLayout root;
	private AdvancedTokenField tokenField;
	private AdvancedTokenField dragField;
	private AdvancedTokenField binderField;
	private VerticalLayout debugLayout;
	private Binder<BeanToken> binder = null;

	@Override
	protected void init(VaadinRequest request) {
		dragField = createDragTokenField();
		tokenField = createMainTokenField();
		binderField = createTokenFieldWithBinder();

		root = new VerticalLayout();

		root.addComponent(dragField);
		root.addComponent(tokenField);
		root.addComponent(binderField);

		setContent(root);

		debugLayout = new VerticalLayout();
		root.addComponent(debugLayout);
		addDebugLog();
	}

	public AdvancedTokenField createDragTokenField() {
		AdvancedTokenField ft = new AdvancedTokenField(
				Stream.of(Token.builder().value("DragMe").build()).collect(Collectors.toList()), false);
		ft.addToken(Token.builder().value("DragMe").build());

		// clearing visible token in layout
		ft.clearTokens();
		// clearing tokens in combobox
		ft.clearTokenList();

		ft.addToken(Token.builder().value("DragMe").build());
		return ft;
	}

	public AdvancedTokenField createMainTokenField() {
		AdvancedTokenField tokenField = new AdvancedTokenField(
				Stream.of(Token.builder().value("FirstToken").build()).collect(Collectors.toList()));
		// allow new items to be added to layout-tokens and combobox
		tokenField.setAllowNewItems(true);

		// adding tokens to combobox
		tokenField.addTokenToInputField(Token.builder().value("Token1").build());
		tokenField.addTokensToInputField(Arrays.asList(
				new Token[] { Token.builder().value("Token2").build(), Token.builder().value("Token3").build() }));

		// adding tokens to layout directly (adding to combobox cache too, if
		// not existing)
		tokenField.addToken(Token.builder().value("token4").style("green").build());
		tokenField.addTokens(Arrays.asList(
				new Token[] { Token.builder().value("Token5").build(), Token.builder().value("Token6").build() }));

		// to override defaults
		tokenField.addTokenAddInterceptor(token -> {
			Notification.show(token.getClass().getSimpleName(), "Adding Token: " + token, Type.HUMANIZED_MESSAGE);
			return token;
		});

		// to override defaults
		tokenField.addTokenAddNewItemInterceptor((TokenNewItemInterceptor) value -> {
			Notification.show(value.getClass().getSimpleName(), "Add New Token: " + value, Type.TRAY_NOTIFICATION);
			return value;
		});

		// to override defaults
		tokenField.addTokenRemoveInterceptor((TokenRemoveInterceptor) removeEvent -> {
			Notification.show(removeEvent.getClass().getSimpleName(), "Removing Token: " + removeEvent,
					Type.HUMANIZED_MESSAGE);
			return removeEvent;
		});

		tokenField.addTokenAddNewItemListener(add -> {
			addDebugLog();
		});

		tokenField.addTokenAddListener(add -> {
			binder.getBean().getTokens().add(Token.builder().value(add.getValue()).build());
			addDebugLog();
		});

		tokenField.addTokenRemoveListener(remove -> {
			addDebugLog();
		});
		return tokenField;
	}

	public AdvancedTokenField createTokenFieldWithBinder() {
		AdvancedTokenField withBinder = new AdvancedTokenField(true);
		withBinder.setAllowNewItems(true);
		withBinder.getLayout().setCaption("With Binder");
		binder = new Binder<BeanToken>(BeanToken.class);
		binder.setReadOnly(true);
		binder.setBean(BeanToken.builder().build().random());
		binder.addValueChangeListener(e -> {
			System.out.println("value changed: " + e);
		});
		binder.forField(withBinder).bind(BeanToken::getTokens, BeanToken::setTokens);
		return withBinder;
	}

	public void addDebugLog() {
		debugLayout.removeAllComponents();
		debugLayout.addComponent(new Label("------added-------"));

		// output of visible _added_ tokens
		tokenField.getTokens().forEach(e -> debugLayout.addComponent(new Label(e.toString())));
		debugLayout.addComponent(new Label());
		debugLayout.addComponent(new Label("------inputfield-------"));

		// output of available tokens in inputfield
		tokenField.getTokensOfInputField().forEach(e -> debugLayout.addComponent(new Label(e.toString())));
	}
}
