package com.fo0.advancedtokenfield.demo;

import java.util.Arrays;

import javax.servlet.annotation.WebServlet;

import com.fo0.advancedtokenfield.listeners.TokenNewItemListener;
import com.fo0.advancedtokenfield.listeners.TokenRemoveListener;
import com.fo0.advancedtokenfield.main.AdvancedTokenField;
import com.fo0.advancedtokenfield.main.Token;
import com.vaadin.annotations.Theme;
import com.vaadin.annotations.Title;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

@Theme("demo")
@Title("AdvancedTokenField Add-on Demo")
@SuppressWarnings("serial")
public class DemoUI extends UI {

	@WebServlet(value = "/*", asyncSupported = true)
	@VaadinServletConfiguration(productionMode = false, ui = DemoUI.class)
	public static class Servlet extends VaadinServlet {
	}

	@Override
	protected void init(VaadinRequest request) {
		AdvancedTokenField tokenField = new AdvancedTokenField();
		// allow new items to be added to layout-tokens and combobox
		tokenField.setAllowNewItems(true);

		VerticalLayout root = new VerticalLayout();
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
		tokenField.addTokenAddListener(token -> {
			Notification.show(token.getClass().getSimpleName(), "Adding Token: " + token, Type.HUMANIZED_MESSAGE);
			return token;
		});

		// to override defaults
		tokenField.addTokenAddNewItemListener((TokenNewItemListener) value -> {
			Token token = new Token(value);
			Notification.show(value.getClass().getSimpleName(), "Add New Token: " + token, Type.TRAY_NOTIFICATION);
			return token;
		});

		// to override defaults
		tokenField.addTokenRemoveListener((TokenRemoveListener) removeEvent -> {
			Notification.show(removeEvent.getClass().getSimpleName(), "Removing Token: " + removeEvent.getToken(),
					Type.HUMANIZED_MESSAGE);
			return removeEvent;
		});

		root.addComponent(new Label("------added-------"));

		// output of visible _added_ tokens
		tokenField.getTokens().forEach(e -> root.addComponent(new Label(e.toString())));
		root.addComponent(new Label());
		root.addComponent(new Label("------inputfield-------"));

		// output of available tokens in inputfield
		tokenField.getTokensOfInputField().forEach(e -> root.addComponent(new Label(e.toString())));
	}
}
