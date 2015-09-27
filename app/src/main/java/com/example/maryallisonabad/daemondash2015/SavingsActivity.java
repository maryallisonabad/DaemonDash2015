package com.example.maryallisonabad.daemondash2015;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.LegendRenderer;
import com.jjoe64.graphview.helper.DateAsXAxisLabelFormatter;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.reimaginebanking.api.java.NessieClient;
import com.reimaginebanking.api.java.NessieException;
import com.reimaginebanking.api.java.NessieResultsListener;
import com.reimaginebanking.api.java.models.Account;
import com.reimaginebanking.api.java.models.Purchase;

import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class SavingsActivity extends AppCompatActivity {

    ArrayList<BudgetWeek> budgetWeeks = new ArrayList<BudgetWeek>();
    int budgetWeeksIndex = 0;
    ArrayList<Purchase> allPurchases = new ArrayList<Purchase>();
    ArrayList<Account> accounts = new ArrayList<Account>();
    int totalSpentThisWeek = 0;
    NessieClient nessieClient;
    //create dummy budgetWeeks
    BudgetWeek budget = new BudgetWeek();
    SimpleDateFormat formatter;
    ParsePosition pos;
    SharedPreferences settings;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("See Savings");
        setContentView(R.layout.activity_savings);
    }

    @Override
    protected void onStart(){
        super.onStart();
        String key = "0a5f4897eba437d37614b8e66ef996da";
        nessieClient = NessieClient.getInstance();
        settings = getPreferences(0);
        int budgetGoal = settings.getInt("budgetGoal", 100);

        nessieClient.setAPIKey(key);
        budgetWeeks.add(0, new BudgetWeek("09-01-2015", "09-07-2015", 150, true));
        budgetWeeks.add(1, new BudgetWeek("09-08-2015", "09-14-2015", 80, true));
        budgetWeeks.add(2, new BudgetWeek("09-15-2015", "09-21-2015", 60, true));
        budgetWeeks.add(3, new BudgetWeek("09-22-2015", "09-28-2015", budgetGoal, true));

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
        for (Account account : accounts) {
            String accountId = account.get_id();
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

    public void ProcessPurchases(ArrayList<Purchase> purchases) {
        formatter = new SimpleDateFormat("MM-dd-yyyy");
        pos = new ParsePosition(0);

        for (Purchase purchase : allPurchases) {
            String date = purchase.getPurchase_date();
            pos.setIndex(0);
            Date purchaseDate = formatter.parse(date, pos);
            if (purchaseDate.compareTo(budgetWeeks.get(0).end) <= 0) {
                budgetWeeks.get(0).purchases.add(purchase);
                budgetWeeks.get(0).totalSpent += purchase.getAmount();
            } else if (purchaseDate.compareTo(budgetWeeks.get(1).end) <= 0) {
                budgetWeeks.get(1).purchases.add(purchase);
                budgetWeeks.get(1).totalSpent += purchase.getAmount();
            } else if (purchaseDate.compareTo(budgetWeeks.get(2).end) <= 0) {
                budgetWeeks.get(2).purchases.add(purchase);
                budgetWeeks.get(2).totalSpent += purchase.getAmount();
            } else if (purchaseDate.compareTo(budgetWeeks.get(3).end) <= 0) {
                budgetWeeks.get(3).purchases.add(purchase);
                budgetWeeks.get(3).totalSpent += purchase.getAmount();
            }
        }


        //Savings
        budgetWeeks.get(0).cumulativeSavings = budgetWeeks.get(0).getAmountUnderBudget();
        budgetWeeks.get(1).cumulativeSavings = budgetWeeks.get(0).cumulativeSavings + budgetWeeks.get(1).getAmountUnderBudget();
        budgetWeeks.get(2).cumulativeSavings = budgetWeeks.get(0).cumulativeSavings + budgetWeeks.get(2).getAmountUnderBudget();
        budgetWeeks.get(3).cumulativeSavings = budgetWeeks.get(0).cumulativeSavings + budgetWeeks.get(3).getAmountUnderBudget();

        GraphView savings = (GraphView) findViewById(R.id.graph2);
        LineGraphSeries<DataPoint> weeklyPerformance = new LineGraphSeries<DataPoint>(new DataPoint[]{
                new DataPoint(budgetWeeks.get(0).end, budgetWeeks.get(0).getAmountUnderBudget()),
                new DataPoint(budgetWeeks.get(1).end, budgetWeeks.get(1).getAmountUnderBudget()),
                new DataPoint(budgetWeeks.get(2).end, budgetWeeks.get(2).getAmountUnderBudget()),
                new DataPoint(budgetWeeks.get(3).end, budgetWeeks.get(3).getAmountUnderBudget())
        });
        LineGraphSeries<DataPoint> cumulativePerformance = new LineGraphSeries<DataPoint>(new DataPoint[]{
                new DataPoint(budgetWeeks.get(0).end, budgetWeeks.get(0).cumulativeSavings),
                new DataPoint(budgetWeeks.get(1).end, budgetWeeks.get(1).cumulativeSavings),
                new DataPoint(budgetWeeks.get(2).end, budgetWeeks.get(2).cumulativeSavings),
                new DataPoint(budgetWeeks.get(3).end, budgetWeeks.get(3).cumulativeSavings)
        });
        savings.setTitle("Tracking your Savings");
        savings.addSeries(weeklyPerformance);
        weeklyPerformance.setColor(Color.GREEN);
        savings.addSeries(cumulativePerformance);
        cumulativePerformance.setColor(Color.BLUE);
        cumulativePerformance.setTitle("Cumulative Savings");
        weeklyPerformance.setTitle("Weekly Savings");
        savings.getLegendRenderer().setVisible(true);
        savings.getLegendRenderer().setAlign(LegendRenderer.LegendAlign.TOP);

        savings.getGridLabelRenderer().setLabelFormatter(new DateAsXAxisLabelFormatter(this));
        savings.getGridLabelRenderer().setNumHorizontalLabels(3); // only 4 because of the space

        // set manual x bounds to have nice steps
        savings.getViewport().setMinX(budgetWeeks.get(0).end.getTime());
        savings.getViewport().setMaxX(budgetWeeks.get(3).end.getTime());
        savings.getViewport().setXAxisBoundsManual(true);
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_savings, menu);
        return true;
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
        if (id == R.id.action_home) {
            Intent intent = new Intent(this, MainDisplayPageActivity.class);
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
