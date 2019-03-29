package com.technikh.onedrupal.models;

/*
 * Copyright (c) 2019. Nikhil Dubbaka from TechNikh.com under GNU AFFERO GENERAL PUBLIC LICENSE
 * Copyright and license notices must be preserved.
 * When a modified version is used to provide a service over a network, the complete source code of the modified version must be made available.
 */

import com.google.gson.annotations.SerializedName;

public class UriItem{

	@SerializedName("value")
	private String value;

	@SerializedName("url")
	private String url;

	public void setValue(String value){
		this.value = value;
	}

	public String getValue(){
		return value;
	}

	public void setUrl(String url){
		this.url = url;
	}

	public String getUrl(){
		return url;
	}

	@Override
 	public String toString(){
		return 
			"UriItem{" + 
			"value = '" + value + '\'' + 
			",url = '" + url + '\'' + 
			"}";
		}
}