package com.websatva.wsend;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.websatva.wsend.activities.MainActivity;
import com.websatva.wsend.activities.SelectOption;

public class Splashscreen extends Activity {

    Handler handler;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_screen);

        handler=new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent=new Intent(Splashscreen.this, SelectOption.class);
                startActivity(intent);
                finish();
            }
        },3000);

    }
}