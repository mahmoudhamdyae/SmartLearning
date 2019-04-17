package com.project.csed.smartlearning;

public class PrivateMessagePOJO {
    String senderName,message,date;



    public PrivateMessagePOJO(String senderName, String message, String date) {
        this.senderName = senderName;
        this.message = message;
        this.date = date;
    }

    public PrivateMessagePOJO() {
    }

    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
