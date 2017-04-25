package com.example.ettoregalli.shoppingmanager2.ui;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.view.View;

import com.example.ettoregalli.shoppingmanager2.R;
import com.example.ettoregalli.shoppingmanager2.database.sqliteutilities.SQLCudQueryBuilder;

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
        // 1. Instantiate an AlertDialog.Builder with its constructor
        AlertDialog.Builder builder = new AlertDialog.Builder(this.mainListEditActivity);

        // 2. Chain together various setter methods to set the dialog characteristics
        builder.setMessage(R.string.delete_dialog_title)
                .setTitle(R.string.delete_dialog_message)
        ;
        builder.setPositiveButton(R.string.delete_dialog_yes, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked OK button
                try {
                    mainListEditActivity.shoppingListDAO.dltListItem(listId, itemId);
                } catch (SQLCudQueryBuilder.ClassNotSupportedException e) {
                    e.printStackTrace();
                }
                mainListEditActivity.viewrefresh();
            }
        });
        builder.setNegativeButton(R.string.delete_dialog_no, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User cancelled the dialog
            }
        });
        // 3. Get the AlertDialog from create()
        AlertDialog dialog = builder.create();
        dialog.show();

    }
}

