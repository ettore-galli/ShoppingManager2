package com.example.ettoregalli.shoppingmanager2.database.utils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Scanner;

public class InputOutputUtils {

    private static final RoundingMode roundingMode = RoundingMode.HALF_UP;
    private static final int DefautDecimalDigits = 2;
    private int decimalDigits;
    private DecimalFormatSymbols dfs;
    private Scanner sc;
    private DecimalFormat df;

    /**
     * Crea una nuova istanza
     */
    public InputOutputUtils(int decimalDigits) {
        this.decimalDigits = decimalDigits;
        this.dfs = new DecimalFormatSymbols();

        this.df = new DecimalFormat();
        this.df.setRoundingMode(InputOutputUtils.roundingMode);
        this.df.setDecimalFormatSymbols(this.dfs);
        this.df.setGroupingUsed(false);
        this.df.setMaximumFractionDigits(this.decimalDigits);
        this.df.setMinimumFractionDigits(this.decimalDigits);
    }

    /**
     * Crea una nuova istanza
     */
    public InputOutputUtils() {
        this(DefautDecimalDigits);

    }

    /**
     * Ottiene un numero decimale da input testuale
     *
     * @param nInput
     * @return
     */
    public BigDecimal getBigDecimalFromInput(String nInput) throws InputOutputException {
        // Output:
        BigDecimal bdOut = null;

        // Cleanup input
        if (nInput.trim().length() > 0) {
            nInput = nInput.trim().replace(dfs.getGroupingSeparator(), dfs.getDecimalSeparator());
            nInput = nInput.replace(dfs.getCurrencySymbol(), "");
            try {
                bdOut = (new Scanner(nInput)).nextBigDecimal();
            } catch (NumberFormatException nfe) {
                throw nfe;
            } catch (Exception ge) {
                throw new InputOutputException(ge.getMessage());
            }
            if (bdOut != null) {
                bdOut.setScale(this.decimalDigits);
            }
        }
        return bdOut;
    }

    /**
     * Restituisce una rappresentazione consistente del numero decimale
     *
     * @param bdInput
     * @return
     */
    public String getBigDecimalStringOutput(BigDecimal bdInput) {
        String result;
        if (bdInput != null) {
            result = this.df.format(bdInput);
        } else {
            result = "";
        }
        return result;
    }

    public int getInt(String intInput) {
        int i = 0;
        intInput = intInput.trim();
        if (intInput.length() > 0) {
            try {
                i = (new Integer(intInput)).intValue();
            } catch (NumberFormatException nfe) {
                throw nfe;
            }
        }
        return i;
    }

}
