package com.prominentdev.blog.models;


import com.google.gson.annotations.SerializedName;

public class CreatedItem{

	@SerializedName("format")
	private String format;

	@SerializedName("value")
	private String value;

	public void setFormat(String format){
		this.format = format;
	}

	public String getFormat(){
		return format;
	}

	public void setValue(String value){
		this.value = value;
	}

	public String getValue(){
		return value;
	}

	@Override
 	public String toString(){
		return 
			"CreatedItem{" + 
			"format = '" + format + '\'' + 
			",value = '" + value + '\'' + 
			"}";
		}
}