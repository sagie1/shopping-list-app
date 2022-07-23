package com.example.shoppinglist;

public class Data {

    String type;
    String note;
    String date;
    String id;
    double amount;

    public Data(){}

    public Data(String type, String note, String date, String id, double amount) {
        this.type = type;
        this.note = note;
        this.date = date;
        this.id = id;
        this.amount = amount;
    }

    public String getType() {
        return type;
    }

    public String getNote() {
        return note;
    }

    public String getDate() {
        return date;
    }

    public String getId() {
        return id;
    }

    public double getAmount() {
        return amount;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }
}
