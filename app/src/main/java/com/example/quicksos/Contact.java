package com.example.quicksos;

public class Contact {
    private String name;
    private String number;
    private String url;

    public Contact() {

    }

    public Contact(String name, String number, String url) {
        this.name = name;
        this.number = number;
        this.url = url;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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
