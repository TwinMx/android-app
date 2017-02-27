package fr.isen.twinmx.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import fr.isen.twinmx.R;

public class SplashActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_splash);

        final Intent shortcuts = getIntent();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                final Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                if (shortcuts.getAction() != null) {
                    intent.setAction(shortcuts.getAction());
                }
                startActivity(intent);
                finish();
            }
        }, 1000);
    }
}
