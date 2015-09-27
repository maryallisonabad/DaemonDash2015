package com.example.maryallisonabad.daemondash2015;

import java.util.Calendar;
import java.util.Date;
import java.util.ArrayList;
import com.reimaginebanking.api.java.models.Account;
import com.reimaginebanking.api.java.models.Purchase;
import java.text.SimpleDateFormat;
import java.text.ParsePosition;

/**
 * Created by Katura on 9/27/2015.
 */
public class BudgetWeek {
    Date start;
    Date end;
    int budgetGoal;
    int totalSpent;
    boolean weekComplete;
    ArrayList<Purchase> purchases = new ArrayList<Purchase>();
    int numPurchases;
    SimpleDateFormat formatter;
    ParsePosition pos;
    int cumulativeSavings = 0;


    public BudgetWeek(){
        formatter = new SimpleDateFormat ("MM-dd-yyyy");
        pos = new ParsePosition(0);
        start  = this.formatter.parse("09-22-2015", pos);
        pos.setIndex(0);
        end  = this.formatter.parse("09-28-2015", pos);

        budgetGoal = 100;
        totalSpent = 0;
        weekComplete = false;
        numPurchases = 0;

    }

    public BudgetWeek(String startS, String endS, int goal, boolean complete){
        formatter = new SimpleDateFormat ("MM-dd-yyyy");
        pos = new ParsePosition(0);
        start  = this.formatter.parse(startS, pos);
        pos.setIndex(0);
        end  = this.formatter.parse(endS, pos);

        budgetGoal = goal;
        totalSpent = 0;
        weekComplete = complete;
    }

    public int getAmountUnderBudget(){
        return budgetGoal - totalSpent;
    }


}
