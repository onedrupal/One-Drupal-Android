package com.technikh.onedrupal.util;

/*
 * Copyright (c) 2019. Nikhil Dubbaka from TechNikh.com under GNU AFFERO GENERAL PUBLIC LICENSE
 * Copyright and license notices must be preserved.
 * When a modified version is used to provide a service over a network, the complete source code of the modified version must be made available.
 */

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;

import com.technikh.onedrupal.provider.IServerAuthenticator;
import com.technikh.onedrupal.provider.MyServerAuthenticator;

public class AccountUtils {

	public static final String ACCOUNT_TYPE = "com.technikh.onedrupal";
	public static final String AUTH_TOKEN_TYPE = "com.technikh.onedrupal.sitetoken";
	
	public static IServerAuthenticator mServerAuthenticator = new MyServerAuthenticator();
	
	public static Account getAccount(Context context, String accountName) {
		AccountManager accountManager = AccountManager.get(context);
		Account[] accounts = accountManager.getAccountsByType(ACCOUNT_TYPE);
		for (Account account : accounts) {
			if (account.name.equalsIgnoreCase(accountName)) {
				return account;
			}
		}
		return null;
	}
	
}
