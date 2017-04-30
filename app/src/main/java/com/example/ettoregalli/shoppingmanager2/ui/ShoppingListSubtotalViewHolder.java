package com.example.ettoregalli.shoppingmanager2.ui;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.example.ettoregalli.shoppingmanager2.R;

public class ShoppingListSubtotalViewHolder extends RecyclerView.ViewHolder {

    protected TextView finalDestination;
    protected TextView total;

    protected CardView item;

    public ShoppingListSubtotalViewHolder(final View itemView) {
        super(itemView);
        item = (CardView) itemView.findViewById(R.id.subtotal_card_view);
        finalDestination = (TextView) itemView.findViewById(R.id.finalDestination);
        total = (TextView) itemView.findViewById(R.id.total);

    }
}
