package com.prominentdev.blog.models;


import com.google.gson.annotations.SerializedName;


public class FilemimeItem{

	@SerializedName("value")
	private String value;

	public void setValue(String value){
		this.value = value;
	}

	public String getValue(){
		return value;
	}

	@Override
 	public String toString(){
		return 
			"FilemimeItem{" + 
			"value = '" + value + '\'' + 
			"}";
		}
}