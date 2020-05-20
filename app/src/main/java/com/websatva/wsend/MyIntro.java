package com.websatva.wsend;

import android.content.Intent;
import android.os.Bundle;

import com.github.paolorotolo.appintro.AppIntro;
import com.websatva.wsend.activities.SelectOption;


/**
 * Created by HP on 10/23/2016.
 */
public class MyIntro extends AppIntro {
    // Please DO NOT override onCreate. Use init
    @Override
    public void init(Bundle savedInstanceState) {

        //adding the three slides for introduction app you can ad as many you needed
        addSlide(AppIntroSampleSlider.newInstance(R.layout.intro_screen));
        addSlide(AppIntroSampleSlider.newInstance(R.layout.intro_screen1));
        addSlide(AppIntroSampleSlider.newInstance(R.layout.intro_screen2));
        addSlide(AppIntroSampleSlider.newInstance(R.layout.intro_screen4));
        addSlide(AppIntroSampleSlider.newInstance(R.layout.intro_screen3));


        // Show and Hide Skip and Done buttons
        showStatusBar(false);
        showSkipButton(true);

        // Turn vibration on and set intensity
        // You will need to add VIBRATE permission in Manifest file
        setVibrate(true);
        setVibrateIntensity(30);

        //Add animation to the intro slider
        setDepthAnimation();
    }

    @Override
    public void onSkipPressed() {


            Intent intent11 = new Intent(MyIntro.this, SelectOption.class);
            intent11.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent11);
            finish();
        }


    @Override
    public void onNextPressed() {
        // Do something here when users click or tap on Next button.
    }

    @Override
    public void onDonePressed() {


            Intent intent112 = new Intent(MyIntro.this, SelectOption.class);
        intent112.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent112);
            finish();
        }




    @Override
    public void onSlideChanged() {
        // Do something here when slide is changed
    }
}