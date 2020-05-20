package com.websatva.wsend.activities;

import android.content.Intent;
import android.os.Bundle;

import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.websatva.wsend.BuildConfig;
import com.websatva.wsend.R;

public class TextMessage extends AppCompatActivity
{
    String mssg;
    Button send;
    EditText edtmsg;
    private InterstitialAd mInterstitialAd;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.text_msg);


        // Initialize the Mobile Ads SDK.
        MobileAds.initialize(this, getResources().getString(R.string.ad_app_id));

        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId("ca-app-pub-2391411119178891/9909799058");
        mInterstitialAd.loadAd(new AdRequest.Builder().build());
        edtmsg=(EditText)findViewById(R.id.msg);

        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                // Load the next interstitial.
              //  mInterstitialAd.loadAd(new AdRequest.Builder().build());
                mssg=edtmsg.getText().toString();
              //  Toast.makeText(getApplicationContext(),mssg,Toast.LENGTH_LONG).show();

                Intent in=new Intent(getApplicationContext(), ContactList.class);
                in.putExtra("message",mssg);
                startActivity(in);
            }

        });

        Toast.makeText(getApplicationContext(),"Upload Successfully....",Toast.LENGTH_LONG).show();

        //=== button on click going to list of contact activity with the comman message which u want to send to all the contact=====
        send=(Button)findViewById(R.id.button_send);
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (mInterstitialAd.isLoaded()) {
                    mInterstitialAd.show();
                }
                else
                    {
                    Log.d("TAG", "The interstitial wasn't loaded yet.");

                        mssg=edtmsg.getText().toString();
                    Intent in=new Intent(getApplicationContext(), ContactList.class);
                    in.putExtra("message",mssg);
                    startActivity(in);
                }




            }
        });

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



}
