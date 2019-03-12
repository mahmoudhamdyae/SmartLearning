package com.project.csed.smartlearning;

public class Quiz {

    private int number;
    private String date;

    public Quiz(int number, String date) {
        this.number = number;
        this.date = date;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
