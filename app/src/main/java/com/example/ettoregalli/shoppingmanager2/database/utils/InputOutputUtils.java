package com.example.ettoregalli.shoppingmanager2.database.utils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Scanner;

public class InputOutputUtils {

    private static final int decimalDigits = 15;
    private static final RoundingMode roundingMode = RoundingMode.HALF_UP;

    private DecimalFormatSymbols dfs;
    private Scanner sc;
    private DecimalFormat df;

    public InputOutputUtils() {

        this.dfs = new DecimalFormatSymbols();

        this.df = new DecimalFormat();
        this.df.setRoundingMode(InputOutputUtils.roundingMode);
        this.df.setDecimalFormatSymbols(this.dfs);
        this.df.setGroupingUsed(false);
        this.df.setMaximumFractionDigits(InputOutputUtils.decimalDigits);
        this.df.setMinimumFractionDigits(InputOutputUtils.decimalDigits);
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
                bdOut.setScale(InputOutputUtils.decimalDigits);
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
