package com.example.quicksos;

public class Contact {
    private String nameEs;
    private String nameEn;
    private String namePt;
    private String nameFr;
    private String nameIt;
    private String nameDe;
    private String nameGl;
    private String nameCa;
    private String nameEu;
    private String number;
    private String url;

    public Contact() {

    }

    public Contact(String nameEs, String nameEn, String namePt, String nameFr, String nameIt, String nameDe,
                   String nameGl, String nameCa, String nameEu, String number, String url) {
        this.nameEs = nameEs;
        this.nameEn = nameEn;
        this.namePt = namePt;
        this.nameFr = nameFr;
        this.nameIt = nameIt;
        this.nameDe = nameDe;
        this.nameGl = nameGl;
        this.nameCa = nameCa;
        this.nameEu = nameEu;
        this.number = number;
        this.url = url;
    }

    // Getters y Setters
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

    public String getNamePt() {
        return namePt;
    }

    public void setNamePt(String namePt) {
        this.namePt = namePt;
    }

    public String getNameFr() {
        return nameFr;
    }

    public void setNameFr(String nameFr) {
        this.nameFr = nameFr;
    }

    public String getNameIt() {
        return nameIt;
    }

    public void setNameIt(String nameIt) {
        this.nameIt = nameIt;
    }

    public String getNameDe() {
        return nameDe;
    }

    public void setNameDe(String nameDe) {
        this.nameDe = nameDe;
    }

    public String getNameGl() {
        return nameGl;
    }

    public void setNameGl(String nameGl) {
        this.nameGl = nameGl;
    }

    public String getNameCa() {
        return nameCa;
    }

    public void setNameCa(String nameCa) {
        this.nameCa = nameCa;
    }

    public String getNameEu() {
        return nameEu;
    }

    public void setNameEu(String nameEu) {
        this.nameEu = nameEu;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}