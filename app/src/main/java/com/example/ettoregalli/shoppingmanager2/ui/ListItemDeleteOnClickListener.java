package com.example.ettoregalli.shoppingmanager2.ui;

import android.view.View;

/**
 * Click tasto Edit
 */

public class ListItemDeleteOnClickListener implements View.OnClickListener {
    int listId;
    int itemId;
    View itemView;
    MainListEditActivity mainListEditActivity;

    public ListItemDeleteOnClickListener(View itemView, MainListEditActivity mainListEditActivity, int listId, int itemId) {
        this.itemView = itemView;
        this.mainListEditActivity = mainListEditActivity;
        this.listId = listId;
        this.itemId = itemId;

    }

    @Override
    public void onClick(View view) {
        //Toast.makeText(c, "", Toast.LENGTH_SHORT).show();
    }
}
