package com.example.myjpyaporfavor;


import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.example.myslash.BreakingAPI.BreakingFrases;
import com.example.myslash.BreakingAPI.BreakingQuotes;
import com.example.myslash.BreakingAPI.BreakingapiService;
import com.example.myslash.BreakingAPI.MyAdapterBreaking;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class WebApi extends AppCompatActivity {

    private static final String TAG = "BREAKINGBAD";

    private ListView listView;
    private Button buttonRefresh;
    private Button buttonReturn;

    private Retrofit retrofit;

    private List<BreakingFrases> list;
    private int[] imagenUser = {R.drawable.user, R.drawable.user1, R.drawable.user2, R.drawable.user3};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_api);

        listView = (ListView) findViewById(R.id.listViewWA1);
        buttonRefresh = (Button) findViewById(R.id.buttonWARefresh);
        buttonReturn = (Button) findViewById(R.id.buttonWAReturn);

        retrofit = new Retrofit.Builder()
                .baseUrl("https://api.breakingbadquotes.xyz/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        obtenerDatos();

    }

    private void obtenerDatos() {
        BreakingapiService service = retrofit.create(BreakingapiService.class);
        Call<List<BreakingQuotes>> breakingRespuestaCall = service.obtenerListaFrases();

        breakingRespuestaCall.enqueue(new Callback<List<BreakingQuotes>>() {
            @Override
            public void onResponse(Call<List<BreakingQuotes>> call, Response<List<BreakingQuotes>> response) {
                if (response.isSuccessful()) {

                    List<BreakingQuotes> breakingRespuesta = response.body();

                    list = new ArrayList<BreakingFrases>();

                    for (BreakingQuotes p : breakingRespuesta) {

                        BreakingFrases breakingFrases = new BreakingFrases();

                        breakingFrases.setFrase(p.getQuote());
                        breakingFrases.setAutor(p.getAuthor());
                        if (p.getAuthor().equals("Saul Goodman") || p.getAuthor().equals("Walter White") || p.getAuthor().equals("Jesse Pinkman")) {
                            if (p.getAuthor().equals("Saul Goodman")) {
                                breakingFrases.setImagen(imagenUser[1]);
                            }
                            if (p.getAuthor().equals("Walter White")) {
                                breakingFrases.setImagen(imagenUser[2]);
                            }
                            if (p.getAuthor().equals("Jesse Pinkman")) {
                                breakingFrases.setImagen(imagenUser[3]);
                            }
                        } else {
                            breakingFrases.setImagen(imagenUser[0]);
                        }
                        list.add(breakingFrases);
                    }

                    MyAdapterBreaking myAdapter = new MyAdapterBreaking(list, getBaseContext());
                    listView.setAdapter(myAdapter);
                    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                            toast(i);
                        }
                    });

                } else {
                    Log.e(TAG, " onResponse: " + response.errorBody());
                }
            }

            @Override
            public void onFailure(Call<List<BreakingQuotes>> call, Throwable t) {
                Log.e(TAG, " onFailure: " + t.getMessage());
            }
        });
    }

    private void toast(int i) {
        Toast.makeText(getBaseContext(), "Yo Soy El Breaking Bad", Toast.LENGTH_SHORT).show();
    }

    public void Recargar(View v) {
        int numArchivo = getIntent().getExtras().getInt("numArchivo");
        int numLista = getIntent().getExtras().getInt("numLista");
        Intent intent = new Intent(WebApi.this, WebApi.class);
        intent.putExtra("numArchivo", numArchivo);
        intent.putExtra("numLista", numLista);
        startActivity(intent);

    }

    public void Regresar(View v) {
        int numArchivo = getIntent().getExtras().getInt("numArchivo");
        int numLista = getIntent().getExtras().getInt("numLista");
        Intent intent = new Intent(WebApi.this, ListMain.class);
        intent.putExtra("numArchivo", numArchivo);
        intent.putExtra("numLista", numLista);
        startActivity(intent);
    }
}
