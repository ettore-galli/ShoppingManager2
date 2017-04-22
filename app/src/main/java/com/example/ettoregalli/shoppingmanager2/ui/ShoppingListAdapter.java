package com.example.ettoregalli.shoppingmanager2.ui;

import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.ettoregalli.shoppingmanager2.R;
import com.example.ettoregalli.shoppingmanager2.database.model.ListItem;
import com.example.ettoregalli.shoppingmanager2.database.utils.InputOutputUtils;

import java.util.List;

/**
 * Classe ponte per il caricamento della lista
 */

public class ShoppingListAdapter extends RecyclerView.Adapter<ShoppingListItemViewHolder> {
    private List<ListItem> list;
    private MainListEditActivity mainListEditActivity;

    private InputOutputUtils iou;

    /**
     * Istanzia un Adapter
     *
     * @param list
     * @param mainListEditActivity
     */
    public ShoppingListAdapter(List<ListItem> list, MainListEditActivity mainListEditActivity) {
        this.list = list;
        this.mainListEditActivity = mainListEditActivity;
        this.iou = new InputOutputUtils();

    }

    /**
     * @param parent   Il contenitore padre
     * @param viewType Il layout da usare
     * @return
     */
    @Override
    public ShoppingListItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_list_item, parent, false);
        return new ShoppingListItemViewHolder(itemView);
    }

    /**
     * Valorizza i campi dell'elemento
     *
     * @param holder
     * @param position
     */
    @Override
    public void onBindViewHolder(ShoppingListItemViewHolder holder, int position) {
        ListItem sle = list.get(position);
        int listId = sle.getListId();
        int itemId = sle.getItemId();

        holder.description.setText(sle.getDescription()); //TODO: Migliorare
        holder.totalPrice.setText(iou.getBigDecimalStringOutput(sle.getTotalPrice()));

        holder.edit.setOnClickListener(new ListItemEditOnClickListener(holder.itemView, mainListEditActivity, listId, itemId));
        holder.delete.setOnClickListener(new ListItemDeleteOnClickListener(holder.itemView, mainListEditActivity, listId, itemId));

        int fdColor = this.mainListEditActivity.getFinalDestinationColor(sle.getFinalDestinationVisualIndex());
        if (sle.getTotalPrice().doubleValue() == 0) {
            fdColor = R.color.color_fd_zero;
        }
        int bgColor = ContextCompat.getColor(holder.itemView.getContext(), fdColor);
        holder.item.setCardBackgroundColor(bgColor);

    }

    /**
     * Returns the total number of items in the data set held by the adapter.
     *
     * @return The total number of items in this adapter.
     */
    @Override
    public int getItemCount() {
        return this.list.size();
    }

}
