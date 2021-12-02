package com.example.bloodbank;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private ImageView logo;
    private TextView title, slogan;
    Animation topanimation, bottomanimation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);

        logo = findViewById(R.id.imageview);
        title = findViewById(R.id.title);
        slogan = findViewById(R.id.slogan);

        topanimation = AnimationUtils.loadAnimation(this, R.anim.top_animation);
        bottomanimation = AnimationUtils.loadAnimation(this, R.anim.bottom_animation);

        logo.setAnimation(topanimation);
        title.setAnimation(bottomanimation);
        slogan.setAnimation(bottomanimation);

        int SPLASH_SCREEN = 4300;

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(MainActivity.this, SelectRegistrationActivity.class );
                startActivity(intent);
                finish();
            }
        }, SPLASH_SCREEN);

    }
}