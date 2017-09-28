package com.fo0.advancedtokenfield.interceptor;

import com.fo0.advancedtokenfield.events.TokenRemoveEvent;

public interface TokenRemoveInterceptor {

	TokenRemoveEvent action(TokenRemoveEvent event);

}
