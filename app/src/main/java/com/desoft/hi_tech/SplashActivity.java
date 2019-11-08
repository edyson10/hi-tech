package com.desoft.hi_tech;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Bundle;
import android.widget.Toast;

public class SplashActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
                finish();
            }
        }, 3000);

        //time time = new time();
        //time.execute();

    }

    /*
    public void hilo(){
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e){
            e.printStackTrace();
        }
    }

    public void ejecutar(){
        time time = new time();
        time.execute();
    }

    public class time extends AsyncTask<Void, Integer, Boolean>{

        @Override
        protected Boolean doInBackground(Void... voids) {
            hilo();
            return null;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            ejecutar();
            Toast.makeText(SplashActivity.this, "Se cerro sesión perro :v", Toast.LENGTH_SHORT).show();
        }
    }
     */
}
