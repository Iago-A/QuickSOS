package com.example.quicksos;

public class Contact {
    private String nameEs;
    private String nameEn;
    private String number;
    private String url;

    public Contact() {

    }

    public Contact(String nameEs, String nameEn, String number, String url) {
        this.nameEs = nameEs;
        this.nameEn = nameEn;
        this.number = number;
        this.url = url;
    }

    public String getNameEs() {
        return nameEs;
    }

    public void setNameEs(String nameEs) {
        this.nameEs = nameEs;
    }

    public String getNameEn() {
        return nameEn;
    }

    public void setNameEn(String nameEn) {
        this.nameEn = nameEn;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber (String number) {
        this.number = number;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl (String url) {
        this.url = url;
    }
}
