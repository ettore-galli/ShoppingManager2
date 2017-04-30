package com.example.ettoregalli.shoppingmanager2.ui;

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
import com.example.ettoregalli.shoppingmanager2.database.utils.InputOutputUtils;
import com.example.ettoregalli.shoppingmanager2.database.utils.ListItemInputOutputUtils;

import java.util.ArrayList;
import java.util.List;

public class MainListEditActivity extends AppCompatActivity {

    // Lista gestita
    protected final int listId = 999;

    // Motore accesso al DB
    ShoppingListDAO shoppingListDAO;

    // Elenco voci in lista
    List<ListItem> displayList;

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



        /* Caricamento dati iniziale */
        loadItemList();
    }

    /**
     * Caricamento lista
     */
    public void loadItemList() {
        (new ItemListAsyncLoader()).execute(this);
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
    public int getFinalDestinationColor(int finalDestinationVisualIndex) {
        int fdColor = R.color.color_fd_any;

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
            default:
                fdColor = R.color.color_fd_any;
                break;
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
    private class ItemListAsyncLoader extends AsyncTask<MainListEditActivity, Void, Void> {

        @Override
        protected Void doInBackground(MainListEditActivity... mainListEditActivities) {
            for (MainListEditActivity ma : mainListEditActivities) {

                /* Lista dettaglio */
                ma.displayList.clear();
                ma.displayList.addAll(shoppingListDAO.getListItemList(listId, 0));
                ma.slAdapter.notifyDataSetChanged();

                /* Lista subtotali */
                ma.subtotalList.clear();
                ma.subtotalList.addAll(shoppingListDAO.getListSubtotalList(listId, 0));
                ma.totAdapter.notifyDataSetChanged();

            }
            return null;
        }

    } // class ItemListAsyncLoader
} // class MainListEditActivity
