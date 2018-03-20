package com.fo0.advancedtokenfield.model;

import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.vaadin.alump.searchdropdown.HighlighedSearchSuggestion;
import org.vaadin.alump.searchdropdown.SearchSuggestionPresenter;
import org.vaadin.alump.searchdropdown.SearchSuggestionProvider;

import com.vaadin.ui.Notification;

public class TokenSuggestionProvider implements SearchSuggestionProvider<Token> {

	private static final long serialVersionUID = 1729866106642894144L;

	private List<Token> list = null;
	private int minLength = 2;

	public TokenSuggestionProvider(List<Token> tokens) {
		this(tokens, 2);
	}

	public TokenSuggestionProvider(List<Token> tokens, int minLength) {
		list = tokens;
		this.minLength = minLength;
	}

	public void setQuerySuggestionInputMinLength(int minLength) {
		this.minLength = minLength;
	}

	@Override
	public void provideSuggestions(String query, SearchSuggestionPresenter<Token> presenter) {

		String trimmed = query.trim();

		if (trimmed.length() <= minLength - 1) {
			presenter.showSuggestions(query, Collections.EMPTY_LIST, false);
			return;
		}

		Runnable runnable = () -> {
			Pattern pattern = HighlighedSearchSuggestion.createPattern(trimmed);
			List<HighlighedSearchSuggestion<Token>> suggestions = list.stream()
					.filter(e -> StringUtils.containsIgnoreCase(e.getValue(), trimmed))
					.map(s -> new TokenSuggestion(s, pattern)).collect(Collectors.toList());
			presenter.showSuggestions(trimmed, suggestions, false);
		};

		Thread thread = new Thread(runnable);
		thread.start();
	};

	@Override
	public void showMoreResults(String query) {
		Notification.show("Not implemented for this demo");

	}

}
