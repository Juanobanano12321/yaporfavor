package com.example.myjpyaporfavor;


import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;
import android.widget.TextView;

import com.example.myslash.Json.Info;
import com.example.myslash.Json.Json;
import com.example.myslash.MySQLite.DbInfo;

public class Website extends AppCompatActivity {

    TextView textview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_website);
        textview = findViewById(R.id.textViewW1);

        try {

            int numArchivo = getIntent().getExtras().getInt("numArchivo");
            int numLista = getIntent().getExtras().getInt("numLista");
            Json json = new Json();

            DbInfo dbInfo = new DbInfo(Website.this);
            String completoTexto = dbInfo.verInfo(numArchivo);
            Info datos = json.leerJson(completoTexto);

            textview.setText("Welcome " + datos.getFirstName());
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN , WindowManager.LayoutParams.FLAG_FULLSCREEN);
            new Handler( ).postDelayed(new Runnable() {
                @Override
                public void run(){
                    Intent intent = new Intent( Website.this, ListMain.class);
                    intent.putExtra("numArchivo", numArchivo);
                    intent.putExtra("numLista", numLista);
                    startActivity( intent );
                }
            } , 4000 );
        }catch(Exception e){}
    }
}
