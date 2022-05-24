package com.example.styleconnect;

public class CodiItem {    // item_frame 저장 내용
    private String category;
    private String codi_url;
    private String desi_url;
    private String desi_ID;
    private int desi_like;

    public CodiItem(){

    }
    public CodiItem(String category, String codi_url, String desi_url, String desi_ID, int desi_like) {
        this.category = category;
        this.codi_url = codi_url;
        this.desi_url = desi_url;
        this.desi_ID = desi_ID;
        this.desi_like = desi_like;
    }

    public String getCategory() {return category;}

    public void setCategory(String category) {this.category = category;}

    public String getCodi_url() {return codi_url;}

    public void setCodi_url(String codi_url) {
        this.codi_url = codi_url;
    }

    public String getDesi_url() {return desi_url;}

    public void setDesi_url(String desi_url) {
        this.desi_url = desi_url;
    }

    public String getDesi_ID() {return desi_ID;}

    public void setDesi_ID(String desi_ID) {this.desi_ID = desi_ID;}

    public int getDesi_like() {return desi_like;}

    public void setDesi_like(int desi_like) {this.desi_like = desi_like;}
}