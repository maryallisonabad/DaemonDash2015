package com.example.maryallisonabad.daemondash2015;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.FileOutputStream;

public class BudgetActivity extends AppCompatActivity {

    int budgetGoal;
    SharedPreferences settings;
    private String budgetScreen;

    private TextView budgetAmount;
    private EditText editBudget;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("Manage Budget");
        setContentView(R.layout.activity_budget);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_budget, menu);
        return true;
    }




    @Override
    public void onStart(){
        super.onStart();
        settings = getPreferences(0);
        budgetGoal = settings.getInt("budgetGoal", 100);
        budgetAmount = (TextView) findViewById(R.id.budText);
        budgetAmount.setText("$" + budgetGoal);


        //budgetScreen = "$" + budgetGoal;

    }

    /*
    Read in a new budget goal, then write back to preferences

        SharedPreferences.Editor editor = settings.edit();
        editor.putInt("budgetGoal", budgetGoal)
     */



    public void buttonOnClick(View view) {
        Button button = (Button) view;


        //change the budget on screen
        //String string = getString(R.string.curBudNum);

        budgetScreen = budgetAmount.getText().toString();
        System.out.println(budgetScreen);

        //budgetAmount.setText("$"+budgetScreen);
        int newBudget = Integer.parseInt(budgetScreen.substring(1, budgetScreen.length()));
        System.out.println(newBudget);

        //Read in a new budget goal, then write back to preferences
        SharedPreferences.Editor editor = settings.edit();
        editor.putInt("budgetGoal", newBudget).apply();
    }

    //Two methods used to save new Budget on screen
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        String stateSaved = savedInstanceState.getString("save_state");

        if(stateSaved == null) {
            Toast.makeText(BudgetActivity.this,
                    "onRestoreInstanceState:\nNO state saved!",
                    Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(BudgetActivity.this,
                    "onRestoreInstanceState:\nIT WORKS!!!!",
                    Toast.LENGTH_LONG).show();

            budgetAmount.setText(stateSaved);
            editBudget.setText(stateSaved);

        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        String stateToSave = editBudget.getText().toString();
        outState.putString("saved_state", stateToSave);
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
