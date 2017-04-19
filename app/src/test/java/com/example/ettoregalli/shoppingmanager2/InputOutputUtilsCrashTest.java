package com.example.ettoregalli.shoppingmanager2;

import android.content.ContentValues;

import com.example.ettoregalli.shoppingmanager2.database.utils.InputOutputException;
import com.example.ettoregalli.shoppingmanager2.database.utils.InputOutputUtils;

import org.junit.Test;

import java.math.BigDecimal;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.fail;

public class InputOutputUtilsCrashTest {

    public void dsp(String s) {
        if (s != null) {
            System.out.println(s);
        } else {
            System.out.println("NULL");
        }
    }

    public void dsp(BigDecimal bd) {
        if (bd != null) {
            System.out.println(bd.toPlainString());
        } else {
            System.out.println("NULL");
        }
    }

    public void dsp(int s) {
        System.out.println(s);
    }



    @Test
    public void bigDecimalBasicTest() throws InputOutputException {

        InputOutputUtils iou = new InputOutputUtils();

        String[] bdBuoni = {"12345678.12", "-12345678.12", "1234567890", "-1234567890", ""};
        for (String ns : bdBuoni) {
            try {
                BigDecimal bx = iou.getBigDecimalFromInput(ns);
                dsp(ns + " ==> " + bx);
            } catch (Exception e) {
                fail(e.getMessage());
            }

        }
        String[] bdCattivi = {"*"};
        for (String ns : bdCattivi) {
            try {
                BigDecimal bx = iou.getBigDecimalFromInput(ns);
                fail("Attesa eccezione per " + ns);
            } catch (NumberFormatException e) {
                dsp(ns + " ==> OK ECCEZIONE: " + e.getMessage());
            } catch (InputOutputException ioe) {
                dsp(ns + " ==> OK ECCEZIONE: " + ioe.getMessage());
            }

        }

        String[] intBuoni = {"123", "-123", ""};
        String[] intCattivi = {"AAA", "*"};
        for (String ns : intBuoni) {
            int ix = iou.getInt(ns);
            dsp(ns + " ==> " + ix);
        }
        for (String ns : intCattivi) {
            try {
                int ix = iou.getInt(ns);
                dsp(ns + " ==> " + ix);
                fail("Attesa eccezione per " + ns);
            } catch (Exception e) {
                dsp(ns + " ==> OK ECCEZIONE: " + e.getMessage());
            }
        }

        BigDecimal zero = iou.getBigDecimalFromInput("0");
        assertEquals(BigDecimal.ZERO, zero);
    } // bigDecimalBasicTest

} // InputOutputUtilsCrashTest
