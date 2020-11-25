package com.healthpack.myhealthassistant;

public class Journal {

    private String note;
    private String date;


    public Journal() {
        //default constructor
    }
    public Journal(String note, String date) {

        this.note = note;
        this.date = date;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
