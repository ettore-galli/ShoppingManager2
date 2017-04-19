package com.example.ettoregalli.shoppingmanager2.ui;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.example.ettoregalli.shoppingmanager2.R;
import com.example.ettoregalli.shoppingmanager2.database.dao.ShoppingListDAO;
import com.example.ettoregalli.shoppingmanager2.database.model.ListItem;
import com.example.ettoregalli.shoppingmanager2.database.utils.InputOutputUtils;
import com.example.ettoregalli.shoppingmanager2.database.utils.ListItemInputOutputUtils;

import java.util.List;

public class MainListEditActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_list_edit);
        basicTest();
    }

    private void basicTest() {
        // Context of the app under test.
        Context appContext = getApplicationContext();

        final int test_list_id = 999;
        InputOutputUtils iou = new InputOutputUtils();

        ShoppingListDAO sld = new ShoppingListDAO(appContext);
        String msg;
        ListItem li = new ListItem();
        try {
            li = (new ListItemInputOutputUtils()).getListItemFromInputFields("0", "0", "Articolo 1,", "", "1", "3.14", "");
            sld.addListItem(test_list_id, li);
            li = (new ListItemInputOutputUtils()).getListItemFromInputFields("0", "0", "Articolo 2,", "", "1", "2.71", "");
            sld.addListItem(test_list_id, li);
        } catch (Exception e) {
            e.printStackTrace();
        }
        msg = "sld.getNextItemId(1)=" + sld.getNextItemId(test_list_id);
        System.out.println(msg);

        List<ListItem> lst = sld.getListItemList(test_list_id, 0);
        for (ListItem el : lst) {
            System.out.println(el.getDescription() + " " + el.getQuantity().toString() + " " + iou.getBigDecimalStringOutput(el.getUnitPrice()));

        }
    }
}
