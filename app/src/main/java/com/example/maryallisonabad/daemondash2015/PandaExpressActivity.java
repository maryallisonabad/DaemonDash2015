package com.example.maryallisonabad.daemondash2015;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

public class PandaExpressActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_panda_express);

        final Button button = (Button)findViewById(R.id.button);

        button.setOnClickListener(
                new Button.OnClickListener() {
                    public void onClick(View v) {
                        button.setEnabled(false);
                        button.setText("Redeemed!");
                        ImageView iv = (ImageView) findViewById(R.id.imageView4);
                        iv.setVisibility(View.GONE);
                    }
                }
        );
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_panda_express, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_home) {
            Intent intent = new Intent(this, MainDisplayPageActivity.class);
            startActivity(intent);
            finish();
            return true;
        }
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_budget) {
            Intent intent = new Intent(this, BudgetActivity.class);
            startActivity(intent);
            finish();
            return true;
        }
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_savings) {
            Intent intent = new Intent(this, SavingsActivity.class);
            startActivity(intent);
            finish();
            return true;
        }
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_spending) {
            Intent intent = new Intent(this, SpendingActivity.class);
            startActivity(intent);
            finish();
            return true;
        }
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_reedem) {
            Intent intent = new Intent(this, RedeemActivity.class);
            startActivity(intent);
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
