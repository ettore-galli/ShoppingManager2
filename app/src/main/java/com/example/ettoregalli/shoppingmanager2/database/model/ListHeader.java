package com.example.ettoregalli.shoppingmanager2.database.model;

import java.util.Date;

/**
 * Intestazione lista
 */

public class ListHeader {
    private int listId;
    private String listTitle;
    private Date listDate;

    public ListHeader() {
    }

    public Date getListDate() {
        return listDate;
    }

    public void setListDate(Date listDate) {
        this.listDate = listDate;
    }

    public int getListId() {
        return listId;
    }

    public void setListId(int listId) {
        this.listId = listId;
    }

    public String getListTitle() {
        return listTitle;
    }

    public void setListTitle(String listTitle) {
        this.listTitle = listTitle;
    }
}
