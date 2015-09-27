package com.example.maryallisonabad.daemondash2015;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.content.Intent;

import com.google.gson.*;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.LegendRenderer;
import com.jjoe64.graphview.ValueDependentColor;
import com.jjoe64.graphview.helper.DateAsXAxisLabelFormatter;
import com.jjoe64.graphview.series.BarGraphSeries;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.reimaginebanking.api.java.NessieClient;
import com.reimaginebanking.api.java.Adapters.BillTypeAdapter;
import com.reimaginebanking.api.java.Constants.AccountType;
import com.reimaginebanking.api.java.Constants.BillStatus;
import com.reimaginebanking.api.java.Constants.TransactionMedium;
import com.reimaginebanking.api.java.NessieException;
import com.reimaginebanking.api.java.NessieResultsListener;
import com.reimaginebanking.api.java.models.*;
import com.reimaginebanking.api.java.models.Account;
import com.reimaginebanking.api.java.models.Customer;
import com.reimaginebanking.api.java.models.RequestResponse;

import java.util.Calendar;
import java.util.ArrayList;
import java.util.HashMap;
import com.example.maryallisonabad.daemondash2015.BudgetWeek;
import java.text.SimpleDateFormat;
import java.text.ParsePosition;
import java.util.Date;


public class MainDisplayPageActivity extends AppCompatActivity {

    ArrayList<BudgetWeek> budgetWeeks = new ArrayList<BudgetWeek>();
    ArrayList<Purchase> allPurchases = new ArrayList<Purchase>();
    ArrayList<Account> accounts = new ArrayList<Account>();
    int totalSpentThisWeek = 0;
    NessieClient nessieClient;
    //create dummy budgetWeeks
    BudgetWeek budget;
    SimpleDateFormat formatter;
    ParsePosition pos;
    int budgetGoal;
    SharedPreferences settings;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("Home");
        setContentView(R.layout.activity_main_display_page);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main_display_page, menu);


        return true;
    }

    @Override
    protected void onStart(){
        super.onStart();

        settings = getPreferences(0);
        budget = new BudgetWeek();
        budget.budgetGoal = settings.getInt("budgetGoal", 100);

        String key = "0a5f4897eba437d37614b8e66ef996da";
        nessieClient = NessieClient.getInstance();

        nessieClient.setAPIKey(key);

        //Load data
        nessieClient.getCustomerAccounts("55e94a6af8d8770528e60b88", new NessieResultsListener() {
            @Override
            public void onSuccess(Object result, NessieException e) {
                if (e == null) {
                    accounts = (ArrayList<Account>) result;
                    System.out.println("Accounts " + accounts.size());
                    ProcessAccounts(accounts);
                } else {
                    System.out.println(e.toString());
                }
            }
        });
        }

    public void ProcessAccounts(ArrayList<Account> accounts){
        System.out.println("about to get purchases");
        for (Account account : accounts) {
            String accountId = account.get_id();
            System.out.println("gettting purchases for account " + accountId);
            nessieClient.getPurchases(accountId, new NessieResultsListener() {
                @Override
                public void onSuccess(Object result, NessieException e) {
                    if (e == null) {
                        allPurchases = (ArrayList<Purchase>) result;
                        System.out.println("Purchases " + allPurchases.size());
                        ProcessPurchases(allPurchases);
                    } else {
                        System.out.println(e.toString());
                    }
                }
            });

        }
    }

    public void ProcessPurchases(ArrayList<Purchase> purchases){
        formatter = new SimpleDateFormat ("MM-dd-yyyy");
        pos = new ParsePosition(0);

        for (Purchase purchase : allPurchases) {
            String date = purchase.getPurchase_date();
            pos.setIndex(0);
            Date purchaseDate = formatter.parse(date, pos);
            if (budget.start.compareTo(purchaseDate) <= 0 && budget.end.compareTo(purchaseDate) >= 0) {
                System.out.println(purchaseDate.toString());
                budget.purchases.add(budget.numPurchases++, purchase);
                totalSpentThisWeek += purchase.getAmount();
            }

        }
        System.out.println("Total Spent this week " + totalSpentThisWeek);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_budget) {
            Intent intent = new Intent(this, BudgetActivity.class);
            startActivity(intent);
            return true;
        }
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_savings) {
            Intent intent = new Intent(this, SavingsActivity.class);
            startActivity(intent);
            return true;
        }
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_spending) {
            Intent intent = new Intent(this, SpendingActivity.class);
            startActivity(intent);
            return true;
        }
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_reedem) {
            Intent intent = new Intent(this, RedeemActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
