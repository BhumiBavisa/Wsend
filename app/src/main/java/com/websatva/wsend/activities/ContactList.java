package com.websatva.wsend.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;


import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;


import com.websatva.wsend.BuildConfig;
import com.websatva.wsend.R;
import com.websatva.wsend.adapter.RecycleAdapter;
import com.websatva.wsend.database.DatabaseHelper;
import com.websatva.wsend.model.Model;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class ContactList extends AppCompatActivity
{
    public static String newString;
    RecyclerView mRecylerview;

    InputStream inst;
    String[] data;
    private ArrayList<Model> mylist;

    DatabaseHelper database;

    RecycleAdapter recycler;
    List<Model> datamodel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.contact_list);

        mRecylerview=(RecyclerView)findViewById(R.id.recycler);

        //==========get database data using helperclass in array which show in list==========
        database = new DatabaseHelper(this);
        datamodel=new ArrayList<Model>();


            datamodel = database.getdata();


        Log.d("LIST IS...",datamodel.toString());


        //=====get message from textmessage activity=====
        Bundle extras = getIntent().getExtras();
        newString= extras.getString("message");

        //Toast.makeText(getApplicationContext(),newString,Toast.LENGTH_LONG).show();

        //=====get all data in recylerview=====
        recycler = new RecycleAdapter(this,datamodel);
        mRecylerview.setAdapter(recycler);
        //mRecylerview.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false));

    }


    //=================Option menu for displaying menu==========

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_share) {

            try {
                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("text/plain");
                shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Wsend");
                String shareMessage= "\nLet me recommend you this application\n\n";
                shareMessage = shareMessage + "https://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID +"\n\n";
                shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage);
                startActivity(Intent.createChooser(shareIntent, "choose one"));

            } catch(Exception e) {
                //e.toString();
            }
        }
        return super.onOptionsItemSelected(item);
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            exitByBackKey();

            //moveTaskToBack(false);

            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    protected void exitByBackKey() {

        AlertDialog alertbox = new AlertDialog.Builder(this)
                .setMessage("Are You Sure To Leave This Screen?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {

                    // do something when the button is clicked
                    public void onClick(DialogInterface arg0, int arg1) {

                        finish();
                        //close();


                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {

                    // do something when the button is clicked
                    public void onClick(DialogInterface arg0, int arg1) {
                        arg0.cancel();
                    }
                })
                .show();

    }

}