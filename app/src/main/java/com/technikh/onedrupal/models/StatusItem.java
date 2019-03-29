package com.technikh.onedrupal.models;

/*
 * Copyright (c) 2019. Nikhil Dubbaka from TechNikh.com under GNU AFFERO GENERAL PUBLIC LICENSE
 * Copyright and license notices must be preserved.
 * When a modified version is used to provide a service over a network, the complete source code of the modified version must be made available.
 */

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