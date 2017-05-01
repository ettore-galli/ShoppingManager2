package com.example.ettoregalli.shoppingmanager2.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ettoregalli.shoppingmanager2.R;
import com.example.ettoregalli.shoppingmanager2.database.dao.ShoppingListDAO;
import com.example.ettoregalli.shoppingmanager2.database.model.ListItem;
import com.example.ettoregalli.shoppingmanager2.database.utils.InputOutputException;
import com.example.ettoregalli.shoppingmanager2.database.utils.InputOutputUtils;
import com.example.ettoregalli.shoppingmanager2.database.utils.ListItemInputOutputUtils;

import java.math.BigDecimal;
import java.util.List;

public class EditItemActivity extends AppCompatActivity {

    // Motore accesso al DB
    ShoppingListDAO shoppingListDAO;

    // Utility
    InputOutputUtils iou;
    ListItemInputOutputUtils liUtils;

    // Campi
    TextView title;
    AutoCompleteTextView finalDestination;
    ArrayAdapter<String> finalDestinationAa;
    TextView itemDescription;
    TextView quantity;
    Spinner unit;
    ArrayAdapter<String> unitAa;
    TextView unitPrice;
    TextView amountAdded;

    // Pulsanti
    Button unitPriceMinus;
    Button amountAddedMinus;
    Button enter;
    Button cancel;

    /* Contesto voce in fase di gestione */
    int listId;
    int itemId;
    String openEditFunction;

    // Contenitore pulsanti destinazioni finali
    LinearLayout finalDestinationKeys;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_item);

        // Motore accesso al DB
        if (shoppingListDAO == null) {
            shoppingListDAO = new ShoppingListDAO(getApplicationContext());
        }

        // Utility
        iou = new InputOutputUtils();
        liUtils = new ListItemInputOutputUtils();

        // Ricezione parametri in ingresso
        Bundle callPars = getIntent().getExtras();
        openEditFunction = callPars.getString(ShoppingListDriverConstants.OPEN_EDIT_FUNCTION);
        listId = callPars.getInt(ShoppingListDriverConstants.INTENT_PARAMETER_LIST_ID);
        itemId = callPars.getInt(ShoppingListDriverConstants.INTENT_PARAMETER_ITEM_ID);

        /* Pulsanti */
        unitPriceMinus = (Button) findViewById(R.id.unitPriceMinus);
        amountAddedMinus = (Button) findViewById(R.id.amountAddedMinus);
        enter = (Button) findViewById(R.id.enter);
        cancel = (Button) findViewById(R.id.cancel);

        // unitPriceMinus.setOnClickListener(new EditMinusOnClickListener(this));

        // Implemento il cambio di segno per l'importo unitario
        unitPriceMinus.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        cambiaSegno(unitPrice);
                    }
                }
        );

        // Implemento il cambio di segno per l'importo aggiuntivo
        amountAddedMinus.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        cambiaSegno(amountAdded);
                    }
                }
        );

        enter.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        saveAndExit();
                    }
                }
        );

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                exitWithoutSaving();
                ;
            }
        });

        /* Campi edit */
        title = (TextView) findViewById(R.id.title);
        itemDescription = (TextView) findViewById(R.id.itemDescription);
        quantity = (TextView) findViewById(R.id.quantity);
        unitPrice = (TextView) findViewById(R.id.unitPrice);
        amountAdded = (TextView) findViewById(R.id.amountAdded);

        /* Drop down destinazioni finali */
        finalDestination = (AutoCompleteTextView) findViewById(R.id.finalDestination);
        finalDestinationAa = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, shoppingListDAO.getFinalDestinationsList(listId, itemId));
        finalDestination.setAdapter(finalDestinationAa);

        /* Drop down list unità di misura */
        unit = (Spinner) findViewById(R.id.unit);
        unitAa = new ArrayAdapter<String>(this, R.layout.um_spinner_item, shoppingListDAO.getUnitsList());
        unit.setAdapter(unitAa);


        /* Visualizzazione dati iniziale */
        initDisplay();

        /* Pulsanti destinazione finale */
        initFinalDestinationKeys();
    }

    /**
     * Cambia segno
     *
     * @param tw Campo contenente l'importo
     */
    private void cambiaSegno(TextView tw) {

        try {
            BigDecimal current = iou.getBigDecimalFromInput(tw.getText().toString());
            current = current.multiply(new BigDecimal(-1));
            tw.setText(iou.getBigDecimalStringOutput(current));
        } catch (Exception e) {
            error(e.getMessage());
        }

    }

    private void initDisplay() {
        /* Caricamento dati */
        switch (this.openEditFunction) {
            case ShoppingListDriverConstants.OPEN_EDIT_FOR_INSERT:
                /* Inserimento */
                itemId = 0;
                break;

            case ShoppingListDriverConstants.OPEN_EDIT_FOR_UPDATE:
                /* Modifica */
                ListItem liUpd = shoppingListDAO.getListItemList(listId, itemId).get(0);
                putListEntryOnDisplay(liUpd);
                break;

            case ShoppingListDriverConstants.OPEN_EDIT_FOR_DELETE:
                /* Cancellazione */
                break;
        }

    }

    /* Pulsanti destinazione finale */
    private void initFinalDestinationKeys() {
        finalDestinationKeys = (LinearLayout) findViewById(R.id.finalDestinationKeys);
        List<String> fdl = shoppingListDAO.getFinalDestinationsList(listId, 0);
        for (String fd : fdl) {
            Button b = new Button(this);
            b.setText(fd);
            b.setMinWidth(50);
            b.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try{
                        String finalDest = ((Button) view).getText().toString();
                        finalDestination.setText(finalDest);
                    } catch (Exception e){

                    }

                }
            });
            finalDestinationKeys.addView(b);
        }
    }

    private void putListEntryOnDisplay(ListItem li) {
        itemDescription.setText(li.getDescription());
        unit.setSelection(unitAa.getPosition(li.getUnit()));
        quantity.setText(iou.getBigDecimalStringOutput(li.getQuantity()));
        unitPrice.setText(iou.getBigDecimalStringOutput(li.getUnitPrice()));
        amountAdded.setText(iou.getBigDecimalStringOutput(li.getAmountAdded()));
        finalDestination.setText(li.getFinalDestination());

    }

    protected ListItem getListEntryFromDisplay() {
        ListItem li = null;
        try {
            li = liUtils.getListItemFromInputFields(
                    (new Integer(listId)).toString(),
                    (new Integer(itemId)).toString(),
                    itemDescription.getText().toString(),
                    unit.getSelectedItem().toString(),
                    quantity.getText().toString(),
                    unitPrice.getText().toString(),
                    amountAdded.getText().toString(),
                    finalDestination.getText().toString()
            );
        } catch (InputOutputException e) {
            error(e.getMessage());
        }
        return li;

    }

    protected void exitWithoutSaving() {
        setResult(0);
        finish();
    }

    protected void saveAndExit() {
        ListItem li = getListEntryFromDisplay();
        try {

            switch (this.openEditFunction) {
                case ShoppingListDriverConstants.OPEN_EDIT_FOR_INSERT:
                /* Inserimento */
                    shoppingListDAO.addListItem(listId, li);
                    break;

                case ShoppingListDriverConstants.OPEN_EDIT_FOR_UPDATE:
                /* Modifica */
                    shoppingListDAO.updListItem(listId, itemId, li);
                    break;

                case ShoppingListDriverConstants.OPEN_EDIT_FOR_DELETE:
                /* Cancellazione */
                    shoppingListDAO.dltListItem(listId, itemId);
                    break;
            }

            setResult(0);
            finish();
        } catch (Exception e) {
            error(e.getMessage());
        }
    }

    /**
     * Mostra un messaggio
     *
     * @param message
     */
    protected void error(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
    }
}
