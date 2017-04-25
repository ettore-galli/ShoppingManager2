package com.example.ettoregalli.shoppingmanager2.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

/**
 * Click tasto Edit
 */

public class ListItemEditOnClickListener implements View.OnClickListener {
    int listId;
    int itemId;
    View itemView;
    MainListEditActivity mainListEditActivity;

    public ListItemEditOnClickListener(View itemView, MainListEditActivity mainListEditActivity, int listId, int itemId) {
        this.itemView = itemView;
        this.mainListEditActivity = mainListEditActivity;
        this.listId = listId;
        this.itemId = itemId;

    }

    @Override
    public void onClick(View view) {
        Intent insert_item_intent = new Intent(mainListEditActivity.getApplicationContext(), EditItemActivity.class);
        Bundle callPars = new Bundle();
        callPars.putString(ShoppingListDriverConstants.OPEN_EDIT_FUNCTION, ShoppingListDriverConstants.OPEN_EDIT_FOR_UPDATE);
        callPars.putInt(ShoppingListDriverConstants.INTENT_PARAMETER_LIST_ID, listId);
        callPars.putInt(ShoppingListDriverConstants.INTENT_PARAMETER_ITEM_ID, itemId);
        insert_item_intent.putExtras(callPars);
        mainListEditActivity.startActivityForResult(insert_item_intent, ShoppingListDriverConstants.INTENT_RESULT_ANY);
    }
}
