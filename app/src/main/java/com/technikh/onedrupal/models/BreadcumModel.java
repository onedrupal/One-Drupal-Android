package com.technikh.onedrupal.models;


public class BreadcumModel {

    public String title,VocabId,TitleId;


    public BreadcumModel(String title, String VocabId, String TitleId) {
        this.title = title;
        this.VocabId = VocabId;
        this.TitleId = TitleId;
    }

    public String getXYZ() {
        return title;
    }

    public void setXYZ(String t) {
        this.title = t;
    }

    public String getVocabId() {
        return VocabId;
    }

    public void setVocabId(String vocabid) {
        this.VocabId = vocabid;
    }

    public String getTitleId() {
        return TitleId;
    }

    public void setTitleId(String titleid) {
        this.TitleId = titleid;
    }
}