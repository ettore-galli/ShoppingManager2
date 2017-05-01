package com.example.ettoregalli.shoppingmanager2.ui;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.ettoregalli.shoppingmanager2.R;

public class ShoppingListItemViewHolder extends RecyclerView.ViewHolder {

    protected TextView finalDestination;
    protected TextView description;
    protected TextView totalPrice;

    protected ImageButton edit;
    protected ImageButton delete;

    protected CardView item;

    public ShoppingListItemViewHolder(final View itemView) {
        super(itemView);

        item = (CardView) itemView.findViewById(R.id.item_card_view);

        finalDestination = (TextView) itemView.findViewById(R.id.finalDestination);
        description = (TextView) itemView.findViewById(R.id.description);
        totalPrice = (TextView) itemView.findViewById(R.id.totalPrice);

        edit = (ImageButton) itemView.findViewById(R.id.edit);
        delete = (ImageButton) itemView.findViewById(R.id.delete);

    }
}
