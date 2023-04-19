package com.example.myjpyaporfavor;


import androidx.appcompat.app.AppCompatActivity;

import org.osmdroid.config.Configuration;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;

import android.content.Intent;
import android.graphics.Bitmap;
import android.location.Location;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.myslash.Encriptación.EncripBitMap;
import com.example.myslash.Json.Cuenta;
import com.example.myslash.Json.Json;
import com.example.myslash.MySQLite.DbCuenta;

public class MapList extends AppCompatActivity {

    private MapView mapView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_list);
        Configuration.getInstance().load(this, PreferenceManager.getDefaultSharedPreferences(this));

        mapView = findViewById(R.id.mapViewML);

        int numArchivo = getIntent().getExtras().getInt("numArchivo");

        try {
            int numArchivoCuenta = getIntent().getExtras().getInt("numArchivoCuenta");
            DbCuenta dbCuenta = new DbCuenta(MapList.this);
            String completoTexto = dbCuenta.verCuenta(numArchivo, numArchivoCuenta);

            Json json = new Json();
            Cuenta datos = json.leerJsonCuenta(completoTexto);
            double latitude = datos.getLocation().getLatitude();
            double longitude = datos.getLocation().getLongitude();

            GeoPoint startPoint = new GeoPoint(latitude, longitude);
            mapView.getController().setCenter(startPoint);
            mapView.getController().setZoom(10);

        } catch (Exception e) {
        }

        mapView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    double latitude = mapView.getMapCenter().getLatitude();
                    double longitude = mapView.getMapCenter().getLongitude();

                    Toast.makeText(MapList.this, "Latitud: " + latitude + ", Longitud: " + longitude, Toast.LENGTH_SHORT).show();

                    return true;
                }
                return false;
            }
        });
    }

    public void Actualizar(View v) {
        int numArchivo = getIntent().getExtras().getInt("numArchivo");
        int numLista = getIntent().getExtras().getInt("numLista");
        int numArchivoCuenta = getIntent().getExtras().getInt("numArchivoCuenta");

        Json json = new Json();
        DbCuenta dbCuenta = new DbCuenta(MapList.this);
        EncripBitMap EBM = new EncripBitMap();

        try {
            GeoPoint mapCenter = (GeoPoint) mapView.getMapCenter();
            double latitude = mapCenter.getLatitude();
            double longitude = mapCenter.getLongitude();

            String completoTexto = dbCuenta.verCuenta(numArchivo, numArchivoCuenta);

            Cuenta datos = json.leerJsonCuenta(completoTexto);

            String valorNombre = datos.getNameCuenta();
            String valorPassword = datos.getPassCuenta();
            Location valorLocation = new Location("");
            valorLocation.setLatitude(latitude);
            valorLocation.setLongitude(longitude);
            boolean valorTipo = datos.isTipo();
            Bitmap valorImageP = EBM.desCifrar(datos.getImageP());
            int valorImage = datos.getImage();

            String textoJsonCuenta = json.crearJsonCuenta(valorNombre, valorPassword, valorLocation, valorTipo, valorImageP, valorImage);

            dbCuenta.editarCuenta(numArchivo, numArchivoCuenta, textoJsonCuenta);

            Intent intent = new Intent(MapList.this, ListMain.class);
            intent.putExtra("numArchivo", numArchivo);
            intent.putExtra("numLista", numLista);
            startActivity(intent);

        } catch (Exception e) {
            Toast.makeText(MapList.this, "" + e, Toast.LENGTH_SHORT).show();
        }
    }

    public void Volver(View v) {
        int numArchivo = getIntent().getExtras().getInt("numArchivo");
        int numLista = getIntent().getExtras().getInt("numLista");
        Intent intent = new Intent(MapList.this, ListMain.class);
        intent.putExtra("numArchivo", numArchivo);
        intent.putExtra("numLista", numLista);
        startActivity(intent);
    }
}
