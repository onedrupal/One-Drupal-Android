package com.prominentdev.blog.models;

import com.google.gson.annotations.SerializedName;

public class UidItem{

	@SerializedName("target_type")
	private String targetType;

	@SerializedName("target_uuid")
	private String targetUuid;

	@SerializedName("target_id")
	private int targetId;

	@SerializedName("url")
	private String url;

	public void setTargetType(String targetType){
		this.targetType = targetType;
	}

	public String getTargetType(){
		return targetType;
	}

	public void setTargetUuid(String targetUuid){
		this.targetUuid = targetUuid;
	}

	public String getTargetUuid(){
		return targetUuid;
	}

	public void setTargetId(int targetId){
		this.targetId = targetId;
	}

	public int getTargetId(){
		return targetId;
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
			"UidItem{" + 
			"target_type = '" + targetType + '\'' + 
			",target_uuid = '" + targetUuid + '\'' + 
			",target_id = '" + targetId + '\'' + 
			",url = '" + url + '\'' + 
			"}";
		}
}