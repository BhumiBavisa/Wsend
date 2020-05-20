package com.websatva.wsend.activities;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ShareActionProvider;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.websatva.filepicker.activity.FilePickerActivity;
import com.websatva.filepicker.config.Configurations;
import com.websatva.filepicker.model.MediaFile;
import com.websatva.wsend.BuildConfig;
import com.websatva.wsend.CSVFile;
import com.websatva.wsend.R;
import com.websatva.wsend.database.DatabaseHelper;
import com.websatva.wsend.model.Model;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import pub.devrel.easypermissions.EasyPermissions;

public class SelectOption extends AppCompatActivity implements EasyPermissions.PermissionCallbacks {

    InputStream inst;
    String[] data;
    Button btnupload,btnuploded;
    private final static int FILE_REQUEST_CODE = 1;
    private ArrayList<MediaFile> mediaFiles = new ArrayList<>();
    private static final int READ_CALL_LOG_CODE = 200;

    private static final String TAG = SelectOption.class.getSimpleName();




    // Keep reference to the ShareActionProvider from the menu
    private ShareActionProvider mShareActionProvider;
    Button single;
    Button multiple;

    TextView fileName;
    Uri fileUri;
    private File file;
    private AdView mAdView;



    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select);

        // Initialize the Mobile Ads SDK.
        MobileAds.initialize(this, getResources().getString(R.string.ad_app_id));

        //================Displaying Add on Bottom of Page ============

        mAdView = (AdView) findViewById(R.id.adView);

        // Create an ad request. Check logcat output for the hashed device ID to
        // get test ads on a physical device.

          AdRequest adRequest = new AdRequest.Builder().build();


        // Start loading the ad in the background.
        mAdView.loadAd(adRequest);


        //  boolean isTestDevice = adRequest.isTestDevice(this);
        // Log.v(TAG, "is Admob Test Device ? "+deviceId+" "+isTestDevice); //to confirm it worked


        mAdView.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded()
            {
            }

            @Override
            public void onAdClosed()
            {
                Toast.makeText(getApplicationContext(), "Ad is closed!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAdFailedToLoad(int errorCode)
            {
                Toast.makeText(getApplicationContext(), "Ad failed to load! error code: " + errorCode, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAdLeftApplication()

            {
                Toast.makeText(getApplicationContext(), "Ad left application!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAdOpened() {
                super.onAdOpened();
            }
        });





        //======assign all id=====
        single = findViewById(R.id.btn_single);
        multiple = findViewById(R.id.btn_multiple);

        single.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (EasyPermissions.hasPermissions(SelectOption.this, Manifest.permission.READ_CALL_LOG)) {


                    Intent io=new Intent(getApplicationContext(),SingleMessage.class);
                    startActivity(io);

                }
                else
                {
                    EasyPermissions.requestPermissions(SelectOption.this, "READ FILE", READ_CALL_LOG_CODE, Manifest.permission.READ_CALL_LOG);

                }

            }
        });

        multiple.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent in=new Intent(getApplicationContext(),MainActivity.class);
                startActivity(in);
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
        if (item.getItemId() == R.id.action_share)
        {

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


            return true;
        }
        return super.onOptionsItemSelected(item);
    }

//============RUntime permission for read external storage==========

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults)
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions,
                grantResults, SelectOption.this);
    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {
        Intent io=new Intent(getApplicationContext(),SingleMessage.class);
        startActivity(io);
    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {
        Log.d(TAG, "Permission has been denied");
    }




    @Override
    public void onPause() {
        if (mAdView != null) {
            mAdView.pause();
        }
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mAdView != null) {
            mAdView.resume();
        }
    }

    @Override
    public void onDestroy() {
        if (mAdView != null) {
            mAdView.destroy();
        }
        super.onDestroy();
    }



}
