package com.example.ettoregalli.shoppingmanager2.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.example.ettoregalli.shoppingmanager2.R;
import com.example.ettoregalli.shoppingmanager2.database.dao.ShoppingListDAO;
import com.example.ettoregalli.shoppingmanager2.database.model.ListItem;
import com.example.ettoregalli.shoppingmanager2.database.utils.InputOutputUtils;
import com.example.ettoregalli.shoppingmanager2.database.utils.ListItemInputOutputUtils;

import java.util.ArrayList;
import java.util.List;

public class MainListEditActivity extends AppCompatActivity {

    // Lista gestita
    protected final int LISTA_GESTITA = 999;

    // Motore accesso al DB
    ShoppingListDAO shoppingListDAO;

    // Elenco voci in lista
    List<ListItem> displayList;

    // Costruzione lista voci
    RecyclerView shoppingListRecyclerView;
    RecyclerView.LayoutManager slLayout;
    ShoppingListAdapter slAdapter;

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

        basicTest();
        viewrefresh();
    }

    /**
     * Refresh visualizzazione
     */
    private void viewrefresh() {
        /* Lista dettaglio */
        this.displayList.clear();
        this.displayList.addAll(shoppingListDAO.getListItemList(LISTA_GESTITA, 0));
        this.slAdapter.notifyDataSetChanged();
    }

    private void basicTest() {

        InputOutputUtils iou = new InputOutputUtils();

        String msg;
        ListItem li = new ListItem();
        try {
            li = (new ListItemInputOutputUtils()).getListItemFromInputFields("0", "0", "Articolo 1,", "", "1", "3.14", "");
            shoppingListDAO.addListItem(LISTA_GESTITA, li);
            li = (new ListItemInputOutputUtils()).getListItemFromInputFields("0", "0", "Articolo 2,", "", "1", "2.71", "");
            shoppingListDAO.addListItem(LISTA_GESTITA, li);
        } catch (Exception e) {
            e.printStackTrace();
        }
        msg = "sld.getNextItemId(1)=" + shoppingListDAO.getNextItemId(LISTA_GESTITA);
        System.out.println(msg);

        List<ListItem> lst = shoppingListDAO.getListItemList(LISTA_GESTITA, 0);
        for (ListItem el : lst) {
            System.out.println(el.getDescription() + " " + el.getQuantity().toString() + " " + iou.getBigDecimalStringOutput(el.getUnitPrice()));

        }
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
}
