package com.prominentdev.blog.models;

import com.google.gson.annotations.SerializedName;

public class FilesizeItem{

	@SerializedName("value")
	private int value;

	public void setValue(int value){
		this.value = value;
	}

	public int getValue(){
		return value;
	}

	@Override
 	public String toString(){
		return 
			"FilesizeItem{" + 
			"value = '" + value + '\'' + 
			"}";
		}
}