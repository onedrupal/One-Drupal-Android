package com.technikh.onedrupal.models;

/*
 * Copyright (c) 2019. Nikhil Dubbaka from TechNikh.com under GNU AFFERO GENERAL PUBLIC LICENSE
 * Copyright and license notices must be preserved.
 * When a modified version is used to provide a service over a network, the complete source code of the modified version must be made available.
 */

import java.util.List;
import com.google.gson.annotations.SerializedName;


public class ImageUploadResponse{

	@SerializedName("fid")
	private List<FidItem> fid;

	@SerializedName("langcode")
	private List<LangcodeItem> langcode;

	@SerializedName("uid")
	private List<UidItem> uid;

	@SerializedName("filename")
	private List<FilenameItem> filename;

	@SerializedName("filemime")
	private List<FilemimeItem> filemime;

	@SerializedName("created")
	private List<CreatedItem> created;

	@SerializedName("filesize")
	private List<FilesizeItem> filesize;

	@SerializedName("uuid")
	private List<UuidItem> uuid;

	@SerializedName("uri")
	private List<UriItem> uri;

	@SerializedName("status")
	private List<StatusItem> status;

	@SerializedName("changed")
	private List<ChangedItem> changed;

	public void setFid(List<FidItem> fid){
		this.fid = fid;
	}

	public List<FidItem> getFid(){
		return fid;
	}

	public void setLangcode(List<LangcodeItem> langcode){
		this.langcode = langcode;
	}

	public List<LangcodeItem> getLangcode(){
		return langcode;
	}

	public void setUid(List<UidItem> uid){
		this.uid = uid;
	}

	public List<UidItem> getUid(){
		return uid;
	}

	public void setFilename(List<FilenameItem> filename){
		this.filename = filename;
	}

	public List<FilenameItem> getFilename(){
		return filename;
	}

	public void setFilemime(List<FilemimeItem> filemime){
		this.filemime = filemime;
	}

	public List<FilemimeItem> getFilemime(){
		return filemime;
	}

	public void setCreated(List<CreatedItem> created){
		this.created = created;
	}

	public List<CreatedItem> getCreated(){
		return created;
	}

	public void setFilesize(List<FilesizeItem> filesize){
		this.filesize = filesize;
	}

	public List<FilesizeItem> getFilesize(){
		return filesize;
	}

	public void setUuid(List<UuidItem> uuid){
		this.uuid = uuid;
	}

	public List<UuidItem> getUuid(){
		return uuid;
	}

	public void setUri(List<UriItem> uri){
		this.uri = uri;
	}

	public List<UriItem> getUri(){
		return uri;
	}

	public void setStatus(List<StatusItem> status){
		this.status = status;
	}

	public List<StatusItem> getStatus(){
		return status;
	}

	public void setChanged(List<ChangedItem> changed){
		this.changed = changed;
	}

	public List<ChangedItem> getChanged(){
		return changed;
	}

	@Override
 	public String toString(){
		return 
			"ImageUploadResponse{" + 
			"fid = '" + fid + '\'' + 
			",langcode = '" + langcode + '\'' + 
			",uid = '" + uid + '\'' + 
			",filename = '" + filename + '\'' + 
			",filemime = '" + filemime + '\'' + 
			",created = '" + created + '\'' + 
			",filesize = '" + filesize + '\'' + 
			",uuid = '" + uuid + '\'' + 
			",uri = '" + uri + '\'' + 
			",status = '" + status + '\'' + 
			",changed = '" + changed + '\'' + 
			"}";
		}
}