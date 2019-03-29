package com.technikh.onedrupal.authenticator;

/*
 * Copyright (c) 2019. Nikhil Dubbaka from TechNikh.com under GNU AFFERO GENERAL PUBLIC LICENSE
 * Copyright and license notices must be preserved.
 * When a modified version is used to provide a service over a network, the complete source code of the modified version must be made available.
 */

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class AuthenticatorService extends Service {

	private static AccountAuthenticator sAccountAuthenticator;
	
	@Override
	public IBinder onBind(Intent intent) {
		IBinder binder = null;
		if (intent.getAction().equals(android.accounts.AccountManager.ACTION_AUTHENTICATOR_INTENT)) {
			binder = getAuthenticator().getIBinder();
		}
		return binder;
	}
	
	private AccountAuthenticator getAuthenticator() {
		if (null == AuthenticatorService.sAccountAuthenticator) {
			AuthenticatorService.sAccountAuthenticator = new AccountAuthenticator(this);
		}
		return AuthenticatorService.sAccountAuthenticator;
	}

}
