package com.prominentdev.blog.models;

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