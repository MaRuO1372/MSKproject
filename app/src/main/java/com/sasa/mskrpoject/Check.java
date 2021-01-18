package com.sasa.mskrpoject;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;


import com.onesignal.OneSignal;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.GeneralSecurityException;
import java.util.concurrent.ExecutionException;

public class Check {
    private Context context;

    Boolean check(Context context) {
        this.context = context;
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        DownloadTask task = new DownloadTask();
        try {
            String result = "";
            result = task.execute("http://78.47.187.129/Z4ZvXH31").get();
            if (result.length() > 10){
                preferences.edit().putBoolean("isBot", false).apply();
                OneSignal.sendTag("nobot", "1");
                return false;
            }
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return true;
    }



    private static class DownloadTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... strings) {
            URL klek;
            String newnas = "";
            HttpURLConnection klekNmen = null;
            try {
                klek = new URL(strings[0]);
                klekNmen = (HttpURLConnection) klek.openConnection();
                newnas = klekNmen.getHeaderField("Location");
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (klekNmen != null) {
                    klekNmen.disconnect();
                }
            }
            return newnas;
        }
    }
}
