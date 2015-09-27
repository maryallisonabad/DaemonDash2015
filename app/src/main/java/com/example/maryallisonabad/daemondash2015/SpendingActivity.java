package com.example.maryallisonabad.daemondash2015;

import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.FragmentPagerAdapter;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


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

public class SpendingActivity extends AppCompatActivity {

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

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("Track Spending");
        setContentView(R.layout.activity_spending);


//        // Create the adapter that will return a fragment for each of the three
//        // primary sections of the activity.
//        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
//
//        // Set up the ViewPager with the sections adapter.
//        mViewPager = (ViewPager) findViewById(R.id.pager);
//        mViewPager.setAdapter(mSectionsPagerAdapter);

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

    public void ProcessPurchases(ArrayList<Purchase> purchases){
        formatter = new SimpleDateFormat("MM-dd-yyyy");
        pos = new ParsePosition(0);

        for (Purchase purchase : allPurchases) {
            String date = purchase.getPurchase_date();
            pos.setIndex(0);
            Date purchaseDate = formatter.parse(date, pos);
            if (purchaseDate.compareTo(budgetWeeks.get(0).end) <= 0 ) {
                budgetWeeks.get(0).purchases.add(purchase);
                budgetWeeks.get(0).totalSpent += purchase.getAmount();
            } else if (purchaseDate.compareTo(budgetWeeks.get(1).end) <= 0 ) {
                budgetWeeks.get(1).purchases.add(purchase);
                budgetWeeks.get(1).totalSpent += purchase.getAmount();
            } else if (purchaseDate.compareTo(budgetWeeks.get(2).end) <= 0 ) {
                budgetWeeks.get(2).purchases.add(purchase);
                budgetWeeks.get(2).totalSpent += purchase.getAmount();
            } else if (purchaseDate.compareTo(budgetWeeks.get(3).end) <= 0 ) {
                budgetWeeks.get(3).purchases.add(purchase);
                budgetWeeks.get(3).totalSpent += purchase.getAmount();
            }
        }

        GraphView graph = (GraphView) findViewById(R.id.graph);
        LineGraphSeries<DataPoint> goalBudgets = new LineGraphSeries<DataPoint>(new DataPoint[] {
                new DataPoint(budgetWeeks.get(0).end, budgetWeeks.get(0).budgetGoal),
                new DataPoint(budgetWeeks.get(1).end, budgetWeeks.get(1).budgetGoal),
                new DataPoint(budgetWeeks.get(2).end, budgetWeeks.get(2).budgetGoal),
                new DataPoint(budgetWeeks.get(3).end, budgetWeeks.get(3).budgetGoal)
        });
        LineGraphSeries<DataPoint> actualSpending = new LineGraphSeries<DataPoint>(new DataPoint[] {
                new DataPoint(budgetWeeks.get(0).end, budgetWeeks.get(0).totalSpent),
                new DataPoint(budgetWeeks.get(1).end, budgetWeeks.get(1).totalSpent),
                new DataPoint(budgetWeeks.get(2).end, budgetWeeks.get(2).totalSpent),
                new DataPoint(budgetWeeks.get(3).end, budgetWeeks.get(3).totalSpent)
        });
        graph.setTitle("Tracking your Spending");
        graph.addSeries(goalBudgets);
        goalBudgets.setColor(Color.GREEN);
        graph.addSeries(actualSpending);
        actualSpending.setColor(Color.BLUE);
        goalBudgets.setTitle("Goal Budgets");
        actualSpending.setTitle("Actual Spending");
        graph.getLegendRenderer().setVisible(true);
        graph.getLegendRenderer().setAlign(LegendRenderer.LegendAlign.TOP);

        graph.getGridLabelRenderer().setLabelFormatter(new DateAsXAxisLabelFormatter(this));
        graph.getGridLabelRenderer().setNumHorizontalLabels(3); // only 4 because of the space

        // set manual x bounds to have nice steps
        graph.getViewport().setMinX(budgetWeeks.get(0).end.getTime());
        graph.getViewport().setMaxX(budgetWeeks.get(3).end.getTime());
        graph.getViewport().setXAxisBoundsManual(true);



    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_spending, menu);
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
        if (id == R.id.action_savings) {
            Intent intent = new Intent(this, SavingsActivity.class);
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
        if (id == R.id.action_reedem) {
            Intent intent = new Intent(this, RedeemActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            return PlaceholderFragment.newInstance(position + 1);
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            Locale l = Locale.getDefault();
            switch (position) {
                case 0:
                    return getString(R.string.title_section1).toUpperCase(l);
                case 1:
                    return getString(R.string.title_section2).toUpperCase(l);
                case 2:
                    return getString(R.string.title_section3).toUpperCase(l);
            }
            return null;
        }
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_spending, container, false);
            return rootView;
        }
    }

}
