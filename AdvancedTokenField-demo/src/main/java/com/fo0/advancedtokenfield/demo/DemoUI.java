package com.fo0.advancedtokenfield.demo;

import java.util.Arrays;

import javax.servlet.annotation.WebServlet;

import com.fo0.advancedtokenfield.interceptor.TokenNewItemInterceptor;
import com.fo0.advancedtokenfield.interceptor.TokenRemoveInterceptor;
import com.fo0.advancedtokenfield.main.AdvancedTokenField;
import com.fo0.advancedtokenfield.model.Token;
import com.vaadin.annotations.Push;
import com.vaadin.annotations.Theme;
import com.vaadin.annotations.Title;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.shared.communication.PushMode;
import com.vaadin.ui.Button;
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
	private VerticalLayout debugLayout;

	@Override
	protected void init(VaadinRequest request) {
		tokenField = new AdvancedTokenField();
		tokenField.setQuerySuggestionInputMinLength(0);
		// allow new items to be added to layout-tokens and combobox
		tokenField.setAllowNewItems(true);

		Button btn = new Button("Toggle Token close button");
		btn.addClickListener(e -> {
			tokenField.setTokenCloseButton(!tokenField.getTokenCloseButton());
			Notification.show("Tokens closable: " + tokenField.getTokenCloseButton(), Type.TRAY_NOTIFICATION);
		});

		root = new VerticalLayout(btn);
		root.addComponent(tokenField);
		setContent(root);

		// clearing visible token in layout
		tokenField.clearTokens();
		// clearing tokens in combobox
		tokenField.getTokensOfInputField().clear();

		// adding tokens to combobox
		tokenField.addTokenToInputField(new Token("Token1"));
		tokenField.addTokensToInputField(Arrays.asList(new Token[] { new Token("Token2"), new Token("Token3") }));

		// adding tokens to layout directly (adding to combobox cache too, if
		// not existing)
		tokenField.addToken(new Token("token4", "green"));
		tokenField.addTokens(Arrays.asList(new Token[] { new Token("Token5"), new Token("Token6") }));

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

		debugLayout = new VerticalLayout();
		root.addComponent(debugLayout);

		tokenField.addTokenAddListener(add -> {
			addDebugLog();
		});

		tokenField.addTokenRemoveListener(remove -> {
			addDebugLog();
		});

		tokenField.addTokenClickListener(e -> {
			Notification.show("Clicked on Token: " + e, Type.HUMANIZED_MESSAGE);
		});

		addDebugLog();
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
