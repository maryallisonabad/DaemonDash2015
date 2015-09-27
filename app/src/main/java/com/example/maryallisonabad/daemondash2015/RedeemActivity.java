package com.example.maryallisonabad.daemondash2015;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.ArrayAdapter;
import android.widget.AdapterView.OnItemClickListener;
import android.view.View;
import android.widget.AdapterView;
public class RedeemActivity extends AppCompatActivity {

    private String[] couponArray = { "Chipotle", "Bobby's Burger Palace", "Rita's Italian Ice", "Panda Express"};

    private ListView couponsListView;
    private ArrayAdapter arrayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("Redeem Rewards");
        setContentView(R.layout.activity_redeem);
        couponsListView = (ListView) findViewById(R.id.listView);

        // this-The current activity context.
        // Second param is the resource Id for list layout row item
        // Third param is input array
        arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, couponArray);
        couponsListView.setAdapter(arrayAdapter);
        couponsListView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> a, View v, int position, long id) {
                Object o = couponsListView.getItemAtPosition(position);
                Intent intent;
                switch(position) {
                    case 0:
                        intent = new Intent(RedeemActivity.this, ChipotleActivity.class);
                        startActivity(intent);
                        break;
                    case 1:
                        intent = new Intent(RedeemActivity.this, BobbysActivity.class);
                        startActivity(intent);
                        break;
                    case 2:
                        intent = new Intent(RedeemActivity.this, RitasActivity.class);
                        startActivity(intent);
                        break;
                    case 3:
                        intent = new Intent(RedeemActivity.this, PandaExpressActivity.class);
                        startActivity(intent);
                        break;
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_redeem, menu);
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
        if (id == R.id.action_spending) {
            Intent intent = new Intent(this, SpendingActivity.class);
            startActivity(intent);
            return true;
        }
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_home) {
            Intent intent = new Intent(this, MainDisplayPageActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
