package com.sasa.mskrpoject;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Arrays;
import java.util.Locale;

public class ClassActivity extends AppCompatActivity {
    private TextView textViewQuestion;
    private TextView textViewTimer;
    private TextView textViewScore;
    private TextView opinion0;
    private TextView opinion1;
    private TextView opinion2;
    private TextView opinion3;
    private String rightAnswer;
    private int countQuestions = 0;
    private int countRightAnswers = 0;
    private boolean gameOver = false;
    SharedPreferences preferences;
    private int countOfRounds = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_class);
        textViewTimer = findViewById(R.id.textViewTimer);
        opinion0 = findViewById(R.id.textViewOpinion0);
        opinion1 = findViewById(R.id.textViewOpinion1);
        opinion2 = findViewById(R.id.textViewOpinion2);
        opinion3 = findViewById(R.id.textViewOpinion3);
        textViewQuestion = findViewById(R.id.textViewQuestion);
        textViewScore = findViewById(R.id.textViewScore);
        preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        timer();
        playRound0();
        bubble();
    }
    private void timer(){
        CountDownTimer timer = new CountDownTimer(5000,1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                textViewTimer.setText(getTime(millisUntilFinished));
                if (millisUntilFinished < 10000) {
                    textViewTimer.setTextColor(getResources().getColor(android.R.color.holo_red_light));
                }
            }

            @Override
            public void onFinish() {
                gameOver = true;
                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                int max = preferences.getInt("max", 0);
                preferences.edit().putInt("result", countRightAnswers).apply();
                if (countRightAnswers >= max) {
                    preferences.edit().putInt("max", countRightAnswers).apply();
                }
                Intent intent = new Intent(ClassActivity.this, ScoreActivity.class);
                intent.putExtra("result", countRightAnswers);
                startActivity(intent);
            }
        };
        timer.start();
    }
    private String getTime(long millis) {
        int seconds = (int) (millis / 1000);
        int minutes = seconds / 60;
        seconds = seconds % 60;
        return String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds);
    }
    private void playRound0() {
        textViewQuestion.setText("How many legs does a spider have?");
        opinion0.setText("9");
        opinion1.setText("4");
        opinion2.setText("3");
        opinion3.setText("8");
        rightAnswer = opinion3.getText().toString();
    }
    private void playRound1() {
        textViewQuestion.setText("What is the name of the toy cowboy in Toy Story");
        opinion0.setText("Woody");
        opinion1.setText("Hoody");
        opinion2.setText("Dima");
        opinion3.setText("Sasha");
        rightAnswer = opinion0.getText().toString();
    }
    private void playRound2() {
        textViewQuestion.setText("What is the color of an emerald?");
        opinion0.setText("Green");
        opinion1.setText("Blue");
        opinion2.setText("Red");
        opinion3.setText("Black");
        rightAnswer = opinion0.getText().toString();
    }
    private void playRound3() {
        textViewQuestion.setText("What is something you hit with a hammer?");
        opinion0.setText("A nail");
        opinion1.setText("Chain");
        opinion2.setText("Hair");
        opinion3.setText("Zoo");
        rightAnswer = opinion0.getText().toString();
    }
    private void playRound4() {
        textViewQuestion.setText("Whose nose grew longer every time he lied?");
        opinion0.setText("Pinocchio");
        opinion1.setText("Buratino");
        opinion2.setText("Em");
        opinion3.setText("Tiger");
        rightAnswer = opinion0.getText().toString();
    }
    private void playRound5() {
        textViewQuestion.setText("If you freeze water, what do you get?");
        opinion0.setText("Warm");
        opinion1.setText("Ice");
        opinion2.setText("Fire");
        opinion3.setText("Cold ice");
        rightAnswer = opinion1.getText().toString();
    }
    private void playRound6() {
        textViewQuestion.setText("How many planets are in our solar system?");
        opinion0.setText("9");
        opinion1.setText("4");
        opinion2.setText("3");
        opinion3.setText("8");
        rightAnswer = opinion3.getText().toString();
    }


    public void onClickAnswer(View view) {
        if (!gameOver){
            TextView textView = (TextView) view;
            String answer = textView.getText().toString();
            if (answer.equals(rightAnswer)) {
                Toast.makeText(this, "yes", Toast.LENGTH_SHORT).show();
                countRightAnswers++;
            } else {
                Toast.makeText(this, "no", Toast.LENGTH_SHORT).show();
            }
            countQuestions++;
            String score = String.format("%s / %s", countRightAnswers, countQuestions);
            textViewScore.setText(score);
            switch (countOfRounds){
                case 0:
                    playRound1();
                    countOfRounds = 1;
                    break;
                case 1:
                    playRound2();
                    countOfRounds = 2;
                    break;
                case 2:
                    playRound3();
                    countOfRounds = 3;
                    break;
                case 3:
                    playRound4();
                    countOfRounds = 4;
                    break;
                case 4:
                    playRound5();
                    countOfRounds++;
                    break;
                case 5:
                    playRound6();
                    countOfRounds++;
                    break;
                case 6:
                    playRound0();
                    countOfRounds = 0;
                    break;
            }
        }
    }
    public  String bubble() {
        int [] mas = {11, 3, 14, 16, 7};

        boolean isSorted = false;
        int buf;
        while(!isSorted) {
            isSorted = true;
            for (int i = 0; i < mas.length-1; i++) {
                if(mas[i] > mas[i+1]){
                    isSorted = false;

                    buf = mas[i];
                    mas[i] = mas[i+1];
                    mas[i+1] = buf;
                }
            }
        }
        return Arrays.toString(mas);
    }
}