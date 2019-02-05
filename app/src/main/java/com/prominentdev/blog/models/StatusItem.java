package com.prominentdev.blog.models;

import com.google.gson.annotations.SerializedName;

public class StatusItem{

	@SerializedName("value")
	private boolean value;

	public void setValue(boolean value){
		this.value = value;
	}

	public boolean isValue(){
		return value;
	}

	@Override
 	public String toString(){
		return 
			"StatusItem{" + 
			"value = '" + value + '\'' + 
			"}";
		}
}