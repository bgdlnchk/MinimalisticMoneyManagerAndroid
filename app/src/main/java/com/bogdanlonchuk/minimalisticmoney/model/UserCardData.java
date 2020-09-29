package com.bogdanlonchuk.minimalisticmoney.model;

public class UserCardData {
    private int userAmount;
    private String amountType;
    private String amountNote;
    private String amountID;
    private String amountDate;

    //Generate Constructor for user's item
    public UserCardData(int amount, String amountType, String note, String amountID, String amountDate) {
        this.userAmount = amount;
        this.amountType = amountType;
        this.amountNote = note;
        this.amountID = amountID;
        this.amountDate = amountDate;
    }

    public UserCardData(){

    }

    //Getters and Setters
    public int getUserAmount() {
        return userAmount;
    }

    public void setUserAmount(int userAmount) {
        this.userAmount = userAmount;
    }

    public String getAmountType() {
        return amountType;
    }

    public void setAmountType(String amountType) {
        this.amountType = amountType;
    }

    public String getAmountNote() {
        return amountNote;
    }

    public void setAmountNote(String amountNote) {
        this.amountNote = amountNote;
    }

    public String getAmountID() {
        return amountID;
    }

    public void setAmountID(String amountID) {
        this.amountID = amountID;
    }

    public String getAmountDate() {
        return amountDate;
    }

    public void setAmountDate(String amountDate) {
        this.amountDate = amountDate;
    }
}
