package com.technikh.onedrupal.provider;

/*
 * Copyright (c) 2019. Nikhil Dubbaka from TechNikh.com under GNU AFFERO GENERAL PUBLIC LICENSE
 * Copyright and license notices must be preserved.
 * When a modified version is used to provide a service over a network, the complete source code of the modified version must be made available.
 */

public interface IServerAuthenticator {

	/**
	 * Tells the server to create the new user and return its auth token.
	 * @param email
	 * @param username
	 * @param password
	 * @return Access token
	 */
	public String signUp (final String email, final String username, final String password);
	
	/**
	 * Logs the user in and returns its auth token.
	 * @param email
	 * @param password
	 * @return Access token
	 */
	public String signIn (final String site_domain, final String email, final String password);
	
}
