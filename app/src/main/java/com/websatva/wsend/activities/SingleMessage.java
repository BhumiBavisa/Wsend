package com.websatva.wsend.activities;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CallLog;
import android.provider.ContactsContract;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.websatva.filepicker.activity.FilePickerActivity;
import com.websatva.filepicker.model.MediaFile;
import com.websatva.wsend.BuildConfig;
import com.websatva.wsend.R;
import com.websatva.wsend.adapter.CustomAutoCompleteAdapter;
import com.websatva.wsend.model.CallLogModel;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;

import pub.devrel.easypermissions.EasyPermissions;

public class SingleMessage extends AppCompatActivity implements EasyPermissions.PermissionCallbacks {

    EditText  Message;
    AutoCompleteTextView mobileNumber;
    Button Send;
    String Base_url = "https://api.whatsapp.com/send?phone=91";
    String secondConstant = "&text=";
    private AdView mAdView,mAdView1;
    ImageView contacts;
    private final static int SELECT_PHONE_NUMBER=100;
    private static final int READ_CONTACTS_CODE = 300;
    List<CallLogModel> datamodel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_msg);

        // Initialize the Mobile Ads SDK.
        MobileAds.initialize(this, getResources().getString(R.string.ad_single_app_id));

        //================Displaying Add on Bottom of Page ============

         mAdView = findViewById(R.id.adv);
        mAdView1=findViewById(R.id.adview);
       // mAdView.setAdSize(AdSize.BANNER);
      //  mAdView.setAdUnitId(getResources().getString(R.string.ad_single_unit_id));
        AdRequest adRequest = new AdRequest.Builder().build();
        AdRequest adRequest1 = new AdRequest.Builder().build();

        mAdView.loadAd(adRequest);
        mAdView1.loadAd(adRequest1);


        contacts=(ImageView)findViewById(R.id.contacts);
        mobileNumber = (AutoCompleteTextView) findViewById(R.id.number);
        Message = (EditText) findViewById(R.id.msg);




        Intent intent = getIntent();
        String action = intent.getAction();
        String type = intent.getType();
        if ("android.intent.action.SEND".equals(action) && type != null && "text/plain".equals(type)) {

            Message.setText(intent.getStringExtra("android.intent.extra.TEXT"));
        }




                datamodel = new ArrayList<CallLogModel>();
                datamodel = getcalllogdata();
                Log.d("LIST IS...", datamodel.toString());
                CustomAutoCompleteAdapter adapter = new CustomAutoCompleteAdapter(this, datamodel);

                // CallLogAdapter adapter = new CallLogAdapter(this, datamodel);
               // mobileNumber.setThreshold(0);

                mobileNumber.setAdapter(adapter);

        mobileNumber.setOnTouchListener(new View.OnTouchListener(){
            @Override
            public boolean onTouch(View v, MotionEvent event)
            {
                mobileNumber.showDropDown();


                return false;
            }
        });

        mobileNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                //do nothing
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //do nothing
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.length() != 0) {
                    // mClear.setVisibility(View.VISIBLE);
                    mobileNumber.showDropDown();
                } else {
                    // mClear.setVisibility(View.GONE);
                }
            }
        });

        mobileNumber.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3)
            {

                String number=datamodel.get(arg2).getNumber();
                Log.d("DATAAAAA",datamodel.get(arg2).getNumber());

                String f= String.valueOf(number.charAt(0));
                // Toast.makeText(getApplicationContext(),"FIRST LATTER"+f,Toast.LENGTH_LONG).show();

                if(number.length() > 12)
                {
                    String n= number.substring(3);
                    String noSpaceStr = n.replaceAll("\\s", "");
                    Log.d("NUMBERS...", noSpaceStr);
                    mobileNumber.setText(noSpaceStr);


                }
                else if(f.contains("0"))
                {
                    String n= number.substring(1);
                    String noSpaceStr = n.replaceAll("\\s", "");
                    Log.d("NUMBERS...", noSpaceStr);
                    mobileNumber.setText(noSpaceStr);


                }
                else if(number.length() < 10)
                {
                    Toast.makeText(getApplicationContext(),"Please Select Valid Number...",Toast.LENGTH_LONG).show();
                }
                else
                {
                    //  Toast.makeText(getApplicationContext(),"Phone number is valid",Toast.LENGTH_SHORT).show();
                    String noSpaceStr = number.replaceAll("\\s", "");
                    Log.d("NUMBERS...", noSpaceStr);
                    mobileNumber.setText(noSpaceStr);

                }

            }
        });



        Send = (Button) findViewById(R.id.send);

        Send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String url = Base_url + mobileNumber.getText().toString() + secondConstant + Message.getText().toString();
                Log.d("URL IS",url);

                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                startActivity(browserIntent);
                mobileNumber.setText("");
            }
        });

        contacts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {




                //check if app has permission to access the external storage.
                if (EasyPermissions.hasPermissions(SingleMessage.this, Manifest.permission.READ_CONTACTS)) {

                   mobileNumber.setText("");
                    Intent i=new Intent(Intent.ACTION_PICK);
                    i.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE);
                    startActivityForResult(i, SELECT_PHONE_NUMBER);


                } else {
                    //If permission is not present request for the same.
                    EasyPermissions.requestPermissions(SingleMessage.this, getString(R.string.read_file), READ_CONTACTS_CODE, Manifest.permission.READ_CONTACTS);

                }


            }
        });
    }

    //==============GET CALLLOG HISTORY======

    public List<CallLogModel> getcalllogdata() {

        List<CallLogModel> data=new ArrayList<>();
        StringBuffer sb = new StringBuffer();
        Cursor managedCursor =  getContentResolver().query(CallLog.Calls.CONTENT_URI, null,
                null, null,CallLog.Calls.DATE + " DESC");

        int number = managedCursor.getColumnIndex(CallLog.Calls.NUMBER);
        int name=managedCursor.getColumnIndex(CallLog.Calls.CACHED_NAME);
        int type = managedCursor.getColumnIndex(CallLog.Calls.TYPE);
        int date = managedCursor.getColumnIndex(CallLog.Calls.DATE);
        int duration = managedCursor.getColumnIndex(CallLog.Calls.DURATION);
        sb.append("Call Details :");
        CallLogModel dataModel = null;
        while (managedCursor.moveToNext())
        {
            dataModel= new CallLogModel();
            String phNumber = managedCursor.getString(number);
            String nm=managedCursor.getString(name);
            String callType = managedCursor.getString(type);
            String callDate = managedCursor.getString(date);
            Date callDayTime = new Date(Long.valueOf(callDate));
            String callDuration = managedCursor.getString(duration);
            String dir = null;
            int dircode = Integer.parseInt(callType);
            switch (dircode) {
                case CallLog.Calls.OUTGOING_TYPE:
                    dir = "OUTGOING";
                    break;

                case CallLog.Calls.INCOMING_TYPE:
                    dir = "INCOMING";
                    break;

                case CallLog.Calls.MISSED_TYPE:
                    dir = "MISSED";
                    break;
            }
            sb.append("\nPhone Number:--- " + phNumber + " \nCall Type:--- "
                    + dir + " \nCall Date:--- " + nm
                    + " \nCall duration in sec :--- " + callDuration);
            sb.append("\n----------------------------------");
            dataModel.setName(nm);
            dataModel.setNumber(phNumber);
            Log.i("Hellomo",""+dir+phNumber+callDayTime);

            // stringBuffer.append(dataModel);
            // stringBuffer.append(dataModel);
            data.add(dataModel);

        }
        managedCursor.close();
        for (CallLogModel mo:data ) {

            //  Log.i("Hellomo",""+mo.getNumber());
        }


        return data;

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

    //============Getting Result From gallery which u selecting file in intent data======
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SELECT_PHONE_NUMBER && resultCode == RESULT_OK) {
            // Get the URI and query the content provider for the phone number
            Uri contactUri = data.getData();
            String[] projection = new String[]{ContactsContract.CommonDataKinds.Phone.NUMBER};
            Cursor cursor = getContentResolver().query(contactUri, projection,
                    null, null, null);

            // If the cursor returned is valid, get the phone number
            if (cursor != null && cursor.moveToFirst()) {
                int numberIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
                String number = cursor.getString(numberIndex);
                // Do something with the phone number

                String f= String.valueOf(number.charAt(0));
                // Toast.makeText(getApplicationContext(),"FIRST LATTER"+f,Toast.LENGTH_LONG).show();

                if(number.length() > 12)
                {
                    String n= number.substring(3);
                    String noSpaceStr = n.replaceAll("\\s", "");
                    Log.d("NUMBERS...", noSpaceStr);
                    mobileNumber.setText(noSpaceStr);


                }
                else if(f.contains("0"))
                {
                    String n= number.substring(1);
                    String noSpaceStr = n.replaceAll("\\s", "");
                    Log.d("NUMBERS...", noSpaceStr);
                    mobileNumber.setText(noSpaceStr);


                }
                else if(number.length() < 10)
                {
                    Toast.makeText(getApplicationContext(),"Please Select Valid Number...",Toast.LENGTH_LONG).show();
                }
                else
                {
                    //  Toast.makeText(getApplicationContext(),"Phone number is valid",Toast.LENGTH_SHORT).show();
                    String noSpaceStr = number.replaceAll("\\s", "");
                    Log.d("NUMBERS...", noSpaceStr);
                    mobileNumber.setText(noSpaceStr);

                }




            }

            cursor.close();

        }


    }

    //============RUntime permission for read external storage==========

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults)
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions,
                grantResults, SingleMessage.this);
    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {
        Intent i=new Intent(Intent.ACTION_PICK);
        i.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE);
        startActivityForResult(i, SELECT_PHONE_NUMBER);
    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {
        Log.d("", "Permission has been denied");
    }




}

