package com.example.ettoregalli.shoppingmanager2.ui;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;

import com.example.ettoregalli.shoppingmanager2.R;
import com.example.ettoregalli.shoppingmanager2.database.dao.ShoppingListDAO;
import com.example.ettoregalli.shoppingmanager2.database.model.ListItem;
import com.example.ettoregalli.shoppingmanager2.database.model.ListSubtotal;
import com.example.ettoregalli.shoppingmanager2.database.sqliteutilities.SQLCudQueryBuilder;
import com.example.ettoregalli.shoppingmanager2.database.utils.InputOutputUtils;
import com.example.ettoregalli.shoppingmanager2.database.utils.ListItemInputOutputUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class MainListEditActivity extends AppCompatActivity {

    // Lista gestita
    int listId;

    // Motore accesso al DB
    ShoppingListDAO shoppingListDAO;

    // Elenco voci in lista
    List<ListItem> displayList;
    String listOrderBy;

    // Costruzione lista voci
    RecyclerView shoppingListRecyclerView;
    RecyclerView.LayoutManager slLayout;
    ShoppingListAdapter slAdapter;

    // Elenco subtotali
    List<ListSubtotal> subtotalList;

    // Costruzione lista tutali per destinazione finale
    RecyclerView shoppingListTotalsView;
    RecyclerView.LayoutManager totLayout;
    ShoppingListTotalAdapter totAdapter;

    // Pulsante nuova voce
    ImageButton insertNewItem;

    // Pulsanti ordinamento
    ImageButton orderById;
    ImageButton orderByFinalDestination;

    // Nuova lista
    ImageButton createNewList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_list_edit);

        /* Motore di database */
        this.shoppingListDAO = new ShoppingListDAO(this.getApplicationContext());

        /* Lista e adapter */
        this.displayList = new ArrayList<ListItem>();
        this.shoppingListRecyclerView = (RecyclerView) findViewById(R.id.shoppingListRecyclerView);
        this.slLayout = new LinearLayoutManager(this);
        this.shoppingListRecyclerView.setLayoutManager(this.slLayout);
        this.slAdapter = new ShoppingListAdapter(this.displayList, this);
        this.shoppingListRecyclerView.setAdapter(this.slAdapter);

        /* Lista subtotali e adapter */
        this.subtotalList = new ArrayList<ListSubtotal>();
        this.shoppingListTotalsView = (RecyclerView) findViewById(R.id.shoppingListTotalsView);
        this.totLayout = new LinearLayoutManager(this);
        this.shoppingListTotalsView.setLayoutManager(this.totLayout);
        this.totAdapter = new ShoppingListTotalAdapter(this.subtotalList, this);
        this.shoppingListTotalsView.setAdapter(this.totAdapter);

        /* Inserimento nuova voce */
        insertNewItem = (ImageButton) findViewById(R.id.insertNewItem);
        insertNewItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent insert_item_intent = new Intent(getApplicationContext(), EditItemActivity.class);
                Bundle callPars = new Bundle();
                callPars.putString(ShoppingListDriverConstants.OPEN_EDIT_FUNCTION, ShoppingListDriverConstants.OPEN_EDIT_FOR_INSERT);
                callPars.putInt(ShoppingListDriverConstants.INTENT_PARAMETER_LIST_ID, listId);
                insert_item_intent.putExtras(callPars);
                startActivityForResult(insert_item_intent, ShoppingListDriverConstants.INTENT_RESULT_ANY);
            }
        });

        /* Ordinamento */
        orderById = (ImageButton) findViewById(R.id.orderById);
        orderByFinalDestination = (ImageButton) findViewById(R.id.orderByFinalDestination);
        orderById.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listOrderBy = ShoppingListDAO.ORDER_ITEMS_BY_ID;
                loadItemList();
            }
        });

        orderByFinalDestination.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listOrderBy = ShoppingListDAO.ORDER_ITEMS_BY_FINAL_DESTINATION;
                loadItemList();
            }
        });

        /* Creazione nuova lista */
        createNewList = (ImageButton) findViewById(R.id.createNewList);
        createNewList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 1. Instantiate an AlertDialog.Builder with its constructor
                AlertDialog.Builder builder = new AlertDialog.Builder(MainListEditActivity.this);

                // 2. Chain together various setter methods to set the dialog characteristics
                builder.setMessage(R.string.new_list_dialog_title)
                        .setTitle(R.string.new_list_dialog_message)
                ;
                builder.setPositiveButton(R.string.new_list_dialog_yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User clicked OK button
                        try {
                            shoppingListDAO.createNewList();
                        } catch (SQLCudQueryBuilder.ClassNotSupportedException e) {
                            e.printStackTrace();
                        }
                        loadItemList();
                    }
                });
                builder.setNegativeButton(R.string.new_list_dialog_no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog
                    }
                });
                // 3. Get the AlertDialog from create()
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });


        /* Long click: scelta liste precedenti */
        createNewList.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                // 1. Instantiate an AlertDialog.Builder with its constructor
                AlertDialog.Builder builder = new AlertDialog.Builder(MainListEditActivity.this);

                // 2. Chain together various setter methods to set the dialog characteristics
                builder.setMessage(R.string.select_list_dialog_title)
                        .setTitle(R.string.select_list_dialog_message)
                ;

                
                return true;
            }
        });

        /* Ordinamento di default */
        listOrderBy = ShoppingListDAO.ORDER_ITEMS_BY_ID;

        /* Caricamento dati iniziale */
        loadItemList();
    }

    /**
     * Caricamento lista
     */
    public void loadItemList() {
        setTitle(shoppingListDAO.getCurrentListDescription());
        /* Lista gestita */
        listId = shoppingListDAO.getCurrentListId();
        /* Lancio caricamento */
        (new ItemListAsyncLoader()).execute();
    }

    /**
     * Dispatch incoming result to the correct fragment.
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        loadItemList();

    }

    /**
     * Colore in base alla destinazione finale
     *
     * @param finalDestinationVisualIndex
     * @return
     */
    public int getFinalDestinationColor(int finalDestinationVisualIndex, BigDecimal total) {
        int fdColor = R.color.color_fd_any;
        if (!total.equals(BigDecimal.ZERO)) {
            switch (finalDestinationVisualIndex) {
                case 1:
                    fdColor = R.color.color_fd_A;
                    break;
                case 2:
                    fdColor = R.color.color_fd_B;
                    break;
                case 3:
                    fdColor = R.color.color_fd_C;
                    break;
                case 4:
                    fdColor = R.color.color_fd_D;
                    break;
                case 5:
                    fdColor = R.color.color_fd_E;
                    break;
                case ShoppingListDriverConstants.SUBTOTALS_GRAND_TOTAL_VISUAL_INDEX:
                    fdColor = R.color.color_fd_grand_total;
                    break;
                default:
                    fdColor = R.color.color_fd_any;
                    break;
            }
        } else {
            fdColor = R.color.color_fd_zero;
        }

        return fdColor;
    }

    /**
     * Dati di test
     */
    private void basicTest() {

        InputOutputUtils iou = new InputOutputUtils();

        String msg;
        ListItem li = new ListItem();
        shoppingListDAO.scratchDelete();
        try {
            li = (new ListItemInputOutputUtils()).getListItemFromInputFields("0", "0", "Articolo 1", "", "1", "3.14", "", "TIGLI");
            shoppingListDAO.addListItem(listId, li);
            li = (new ListItemInputOutputUtils()).getListItemFromInputFields("0", "0", "Articolo 2", "", "1", "2.71", "", "TIGLI");
            shoppingListDAO.addListItem(listId, li);
            li = (new ListItemInputOutputUtils()).getListItemFromInputFields("0", "0", "Articolo 3", "", "1", "5.16", "", "DRESANO");
            shoppingListDAO.addListItem(listId, li);
        } catch (Exception e) {
            e.printStackTrace();
        }
        msg = "sld.getNextItemId(1)=" + shoppingListDAO.getNextItemId(listId);
        System.out.println(msg);

        List<ListItem> lst = shoppingListDAO.getListItemList(listId, 0);
        for (ListItem el : lst) {
            System.out.println(el.getDescription() + " " + el.getQuantity().toString() + " " + iou.getBigDecimalStringOutput(el.getUnitPrice()));

        }
    }

    /**
     * Definizione task caricamento asincrono liste
     */
    private class ItemListAsyncLoader extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {

            /* Lista dettaglio */
            displayList.clear();
            displayList.addAll(shoppingListDAO.getListItemList(listId, 0, null, listOrderBy));

            /* Lista subtotali */
            subtotalList.clear();
            subtotalList.addAll(shoppingListDAO.getListSubtotalList(listId, 0));

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            slAdapter.notifyDataSetChanged();
            totAdapter.notifyDataSetChanged();
        }
    } // class ItemListAsyncLoader
} // class MainListEditActivity
