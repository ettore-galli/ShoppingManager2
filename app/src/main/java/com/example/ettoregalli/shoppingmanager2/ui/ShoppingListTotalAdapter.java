package com.example.ettoregalli.shoppingmanager2.ui;

import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.ettoregalli.shoppingmanager2.R;
import com.example.ettoregalli.shoppingmanager2.database.model.ListSubtotal;
import com.example.ettoregalli.shoppingmanager2.database.utils.InputOutputUtils;

import java.util.List;

/**
 * Classe ponte per il caricamento della lista
 */

public class ShoppingListTotalAdapter extends RecyclerView.Adapter<ShoppingListSubtotalViewHolder> {
    private List<ListSubtotal> subtotalList;
    private MainListEditActivity mainListEditActivity;

    private InputOutputUtils iou;

    /**
     * Istanzia un Adapter
     *
     * @param subtotalList
     * @param mainListEditActivity
     */
    public ShoppingListTotalAdapter(List<ListSubtotal> subtotalList, MainListEditActivity mainListEditActivity) {
        this.subtotalList = subtotalList;
        this.mainListEditActivity = mainListEditActivity;
        this.iou = new InputOutputUtils();

    }

    /**
     * @param parent   Il contenitore padre
     * @param viewType Il layout da usare
     * @return
     */
    @Override
    public ShoppingListSubtotalViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.final_destination_totals_list_item, parent, false);
        return new ShoppingListSubtotalViewHolder(itemView);
    }

    /**
     * Valorizza i campi dell'elemento
     *
     * @param holder
     * @param position
     */
    @Override
    public void onBindViewHolder(ShoppingListSubtotalViewHolder holder, int position) {
        ListSubtotal listSubtotal = subtotalList.get(position);

        holder.finalDestination.setText(listSubtotal.getFinalDestination());
        holder.total.setText(iou.getBigDecimalStringOutput(listSubtotal.getTotalPrice()));

        int fdColor = this.mainListEditActivity.getFinalDestinationColor(listSubtotal.getFinalDestinationVisualIndex(), listSubtotal.getTotalPrice());

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
        return this.subtotalList.size();
    }

}
