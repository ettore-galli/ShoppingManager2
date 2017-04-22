package com.example.ettoregalli.shoppingmanager2;

import android.content.ContentValues;
import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.example.ettoregalli.shoppingmanager2.database.dao.ShoppingListDAO;
import com.example.ettoregalli.shoppingmanager2.database.model.ListItem;
import com.example.ettoregalli.shoppingmanager2.database.utils.InputOutputException;
import com.example.ettoregalli.shoppingmanager2.database.utils.InputOutputUtils;
import com.example.ettoregalli.shoppingmanager2.database.utils.ListItemInputOutputUtils;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

/**
 * Instrumentation test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ShoppingListDAOBasicTest {
    @Test
    public void useAppContext() throws Exception {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();
        //assertEquals("com.example.ettoregalli.shoppingmanager2", appContext.getPackageName());

        final int test_list_id = 999;
        InputOutputUtils iou = new InputOutputUtils();

        ShoppingListDAO sld = new ShoppingListDAO(appContext);

        sld.scratchDelete();

        String msg;

        ListItem li = new ListItem();

        li = (new ListItemInputOutputUtils()).getListItemFromInputFields((new Integer(test_list_id)).toString(), "1", "Articolo 1,", "", "1", "3.14", "");
        sld.addListItem(test_list_id, li);
        li = (new ListItemInputOutputUtils()).getListItemFromInputFields((new Integer(test_list_id)).toString(), "2", "Articolo 2,", "", "1", "2.71", "");
        sld.addListItem(test_list_id, li);

        List<ListItem> lst = sld.getListItemList(test_list_id, 0);
        for (ListItem el : lst) {
            System.out.println(el.getDescription() + " " + el.getQuantity().toString() + " " + iou.getBigDecimalStringOutput(el.getUnitPrice()));

        }

        li = (new ListItemInputOutputUtils()).getListItemFromInputFields("0", "0", "Articolo II,", "", "1", "4.71", "");
        sld.updListItem(test_list_id, 2, li);

        lst = sld.getListItemList(test_list_id, 0);
        for (ListItem el : lst) {
            System.out.println(el.getDescription() + " " + el.getQuantity().toString() + " " + iou.getBigDecimalStringOutput(el.getUnitPrice()));

        }
    }

    @Test
    public void contentValuesTest() throws InputOutputException {
        ContentValues cv = new ContentValues();
        cv.put("c1", "Valore uno");
        cv.put("c2", 2);
        for (String k : cv.keySet()) {
            System.out.println(k + " " + cv.get(k).getClass().toString());
        }
    }
}
