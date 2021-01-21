package com.sasa.mskrpoject;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class ScoreActivity extends AppCompatActivity {
    private TextView textViewYourScore;
    private TextView textView2;
    private TextView textView3;
    private TextView textView4;
    private Button game;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_score);
        textView2 = findViewById(R.id.textView2);
        textViewYourScore = findViewById(R.id.textViewYourScore);
        textView3 = findViewById(R.id.textView3);
        textView4 = findViewById(R.id.textView4);
        game = findViewById(R.id.button);
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        int max = preferences.getInt("max", 0);
        int result = preferences.getInt("result", 0);
        textViewYourScore.setText(R.string.your);
        textView3.setText(R.string.max);
        textView2.setText(String.valueOf(result));
        textView4.setText(String.valueOf(max));
    }

    public void onclicknewgame(View view) {
        Intent intent = new Intent(ScoreActivity.this, ClassActivity.class);
        startActivity(intent);
    }


}