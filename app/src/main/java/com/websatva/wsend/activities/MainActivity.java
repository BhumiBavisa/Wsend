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
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;


import pub.devrel.easypermissions.EasyPermissions;

public class MainActivity extends AppCompatActivity implements EasyPermissions.PermissionCallbacks {

    InputStream inst;
    String[] data;
    Button btnupload,btnuploded;
    private final static int FILE_REQUEST_CODE = 1;
    private ArrayList<MediaFile> mediaFiles = new ArrayList<>();

    List<Model> myList;
    Uri path;
    String filepath;
    DatabaseHelper databaseHelper;
    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int REQUEST_FILE_CODE = 200;
    private static final int READ_REQUEST_CODE = 300;

    // Keep reference to the ShareActionProvider from the menu
    private ShareActionProvider mShareActionProvider;
    Button fileBrowseBtn;
    Button uploadBtn;

    TextView fileName;
    Uri fileUri;
    private File file;
    private AdView mAdView;



    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize the Mobile Ads SDK.
        MobileAds.initialize(this, getResources().getString(R.string.ad_app_id));

        //================Displaying Add on Bottom of Page ============

        mAdView = (AdView) findViewById(R.id.adView);

        // Create an ad request. Check logcat output for the hashed device ID to
        // get test ads on a physical device.

        AdRequest adRequest = new AdRequest.Builder().build();

        // Start loading the ad in the background.
        mAdView.loadAd(adRequest);



        databaseHelper = new DatabaseHelper(this);

        //======assign all id=====
        fileBrowseBtn = findViewById(R.id.btn_choose_file);
        uploadBtn = findViewById(R.id.btn_upload);
        fileName = findViewById(R.id.tv_file_name);
        //showFileChooser();


        //======Click on this filebrowser button open chooser activity for selecting file=======
        fileBrowseBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {

                //check if app has permission to access the external storage.
                if (EasyPermissions.hasPermissions(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    showFileChooserIntent();

                } else {
                    //If permission is not present request for the same.
                    EasyPermissions.requestPermissions(MainActivity.this, getString(R.string.read_file), READ_REQUEST_CODE, Manifest.permission.READ_EXTERNAL_STORAGE);
                }

            }
        });

        //====Upload button click is gettting file data upload on sqlite database====
        uploadBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                String extension = filepath.substring(filepath.lastIndexOf("."));
                Log.d("CSV...",extension);

                if (filepath != null && extension.equals(".csv"))
                {

                    //====this is doin background work which display only progress dialog====
                    UploadAsyncTask uploadAsyncTask = new UploadAsyncTask(MainActivity.this);
                    uploadAsyncTask.execute();


                } else {
                    Toast.makeText(getApplicationContext(),
                            "Please select a CSV file first", Toast.LENGTH_LONG).show();
                    showFileChooser();
                }

            }
        });




    }




    /**
     * Shows an intent which has options from which user can choose the file like File manager, Gallery etc
     */
    private void showFileChooserIntent()
    {

        //=========This is intent using by FILEPICKER module =========
        Intent intent = new Intent(MainActivity.this, FilePickerActivity.class);
        intent.putExtra(FilePickerActivity.CONFIGS, new Configurations.Builder()
                .setCheckPermission(true)
                .setSelectedMediaFiles(mediaFiles)
                .setShowFiles(true)
                .setShowImages(false)
                .setShowVideos(false)
                .setSingleChoiceMode(true)
                .setSkipZeroSizeFiles(true)
                .setSuffixes("csv")
                // .setRootPath(Environment.getExternalStorageDirectory().getPath() + "/Download")
                .build());
        startActivityForResult(intent, FILE_REQUEST_CODE);


    }


    //============Getting Result From gallery which u selecting file in intent data======
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == FILE_REQUEST_CODE
                && resultCode == RESULT_OK
                && data != null) {
            mediaFiles.clear();


            ArrayList<MediaFile> files = data.getParcelableArrayListExtra(FilePickerActivity.MEDIA_FILES);
            mediaFiles.addAll(files);
            MediaFile mediaFile = mediaFiles.get(0);

            //filepath contain path of file====
            Log.d(TAG, "Filename " + mediaFile.getPath());
            filepath=mediaFile.getPath();

            //======set filename on textview======
            fileName.setText("Filename : "+mediaFile.getName()+".csv");

            String extension = filepath.substring(filepath.lastIndexOf("."));
            Log.d("CSV...", extension);


            if (filepath != null && extension.equals(".csv")){

                hideFileChooser();
            }
            else
            {
                Toast.makeText(getApplicationContext(),
                        "Please select a CSV file first", Toast.LENGTH_LONG).show();
            }
        }
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



    /**
     * Background network task to handle file upload.
     */
    private class UploadAsyncTask extends AsyncTask<Void, Integer, String> {


        private Context context;
        private Exception exception;
        private ProgressDialog progressDialog;

        private UploadAsyncTask(Context context) {
            this.context = context;
        }

        @Override
        protected String doInBackground(Void... params)
        {
            String responseString = null;
            SQLiteDatabase db = databaseHelper.getWritableDatabase();
            String tableName = "mytable";
            db.execSQL("delete from " + tableName);

            try {

                Log.d("FILE PATH IS....", filepath);

                FileInputStream fileInputStream = new FileInputStream(filepath);


                CSVFile csvFile = new CSVFile(fileInputStream);
                List<String[]> datalist = csvFile.read();

                ContentValues contentValues = new ContentValues();
                // String[] line=null;

                db.beginTransaction();

                for(String[] Data:datalist )
                {
                    try {
                        //Name,Number
                        String Name = Data[0].toString();
                        String Number = Data[1].toString();

                        databaseHelper.insertdata(Name,Number);

                        Log.d("SUCCESFULLY UPDATED....", Name);

                    }catch (ArrayIndexOutOfBoundsException e)
                    {
                        e.printStackTrace();
                    }
                }
                db.setTransactionSuccessful();
                db.endTransaction();


            } catch (IOException e) {
                e.printStackTrace();
            }

            return responseString;
        }

        @Override
        protected void onPreExecute() {

            // Init and show dialog
            this.progressDialog = new ProgressDialog(this.context);
            this.progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            this.progressDialog.setCancelable(false);
            this.progressDialog.show();
        }

        @Override
        protected void onPostExecute(String result) {

            // Close dialog
            this.progressDialog.dismiss();

            // showFileChooser();
            myList= databaseHelper.getdata();
            if(myList.size() != 0) {
                Intent in = new Intent(getApplicationContext(), TextMessage.class);
                startActivity(in);

            }
            else {
                Toast.makeText(getApplicationContext(),
                        "There is no Data in this file!", Toast.LENGTH_LONG).show();
                showFileChooser();

            }
            showFileChooser();

        }

        @Override
        protected void onProgressUpdate(Integer... progress) {
            // Update process
            this.progressDialog.setProgress((int) progress[0]);
        }
    }


    /**
     * Hides the Choose file button and displays the file preview, file name and upload button
     */
    private void hideFileChooser() {
        fileBrowseBtn.setVisibility(View.GONE);
        uploadBtn.setVisibility(View.VISIBLE);
        fileName.setVisibility(View.VISIBLE);

    }

    /**
     *  Displays Choose file button and Hides the file preview, file name and upload button
     */
    private void showFileChooser() {
        fileBrowseBtn.setVisibility(View.VISIBLE);
        uploadBtn.setVisibility(View.GONE);
        fileName.setVisibility(View.GONE);


    }



    //============RUntime permission for read external storage==========

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults)
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions,
                grantResults, MainActivity.this);
    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {
        showFileChooserIntent();
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
