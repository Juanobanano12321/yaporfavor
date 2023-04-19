package com.example.myjpyaporfavor;


import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationManager;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import java.io.File;
import java.io.IOException;

public class EditList extends AppCompatActivity {

    private EditText Name, Password;
    private RadioButton Opcion1, Opcion2, Opcion3, Opcion4, Opcion5;
    private int []imagenUser = { R.drawable.user,R.drawable.user1,R.drawable.user2,R.drawable.user3};

    private ImageView ivFoto;
    private Button btnTomarFoto, btnSeleccionarImagen;

    private Uri imagenUri;
    private Bitmap imageP;

    private int TOMAR_FOTO = 100;
    private int SELEC_IMAGEN = 200;

    private String CARPETA_RAIZ = "MyPaginaWebFotos";
    private String CARPETAS_IMAGENES = "imagenes";
    private String RUTA_IMAGEN = CARPETA_RAIZ + CARPETAS_IMAGENES;
    private String path;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_list);
        Name = (EditText) findViewById(R.id.editTextELName);
        Password = (EditText) findViewById(R.id.editTextELPassword);
        Opcion1 = (RadioButton) findViewById(R.id.radioButtonEL1);
        Opcion2 = (RadioButton) findViewById(R.id.radioButtonEL2);
        Opcion3 = (RadioButton) findViewById(R.id.radioButtonEL3);
        Opcion4 = (RadioButton) findViewById(R.id.radioButtonEL4);
        Opcion5 = (RadioButton) findViewById(R.id.radioButtonEL5);
        ivFoto = findViewById(R.id.imageViewELOp5);
        btnTomarFoto = findViewById(R.id.buttonElTake);
        btnSeleccionarImagen = findViewById(R.id.buttonElSelc);

        imageP = null;

        int numArchivo = getIntent().getExtras().getInt("numArchivo");
        int numContext = getIntent().getExtras().getInt("numContext");

        if(ContextCompat.checkSelfPermission(EditList.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(EditList.this,
                    new String[]{Manifest.permission.CAMERA,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
        }

        try {
            if (numContext == 2) {
                int numArchivoCuenta = getIntent().getExtras().getInt("numArchivoCuenta");
                DbCuenta dbCuenta = new DbCuenta(EditList.this);
                String completoTexto = dbCuenta.verCuenta(numArchivo, numArchivoCuenta);

                Json json = new Json();
                EncripBitMap EBM = new EncripBitMap();
                Cuenta datos = json.leerJsonCuenta(completoTexto);
                String valorAccountName = datos.getNameCuenta();
                String valorAccountPassword = datos.getPassCuenta();
                boolean valorAccountTipo = datos.isTipo();
                int valorAccountImage = datos.getImage();

                Name.setText(valorAccountName);
                Password.setText(valorAccountPassword);
                if(valorAccountTipo != true) {
                    if (valorAccountImage == imagenUser[0]) {
                        Opcion1.setChecked(true);
                    }
                    if (valorAccountImage == imagenUser[1]) {
                        Opcion2.setChecked(true);
                    }
                    if (valorAccountImage == imagenUser[2]) {
                        Opcion3.setChecked(true);
                    }
                    if (valorAccountImage == imagenUser[3]) {
                        Opcion4.setChecked(true);
                    }
                }else{
                    imageP = EBM.desCifrar(datos.getImageP());
                    Opcion5.setChecked(true);
                    ivFoto.setImageBitmap(imageP);
                }
            }
        }catch(Exception e){}

        Opcion1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cambiarBoton(1);
            }
        });

        Opcion2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cambiarBoton(2);
            }
        });

        Opcion3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cambiarBoton(3);
            }
        });

        Opcion4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cambiarBoton(4);
            }
        });

        Opcion5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cambiarBoton(5);
            }
        });

        btnTomarFoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tomarFoto();
            }
        });

        btnSeleccionarImagen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                seleccionarImagen();
            }
        });
    }

    public void Enviar (View v){
        int numArchivo = getIntent().getExtras().getInt("numArchivo");
        int numContext = getIntent().getExtras().getInt("numContext");
        int numLista = getIntent().getExtras().getInt("numLista");
        if((false == Opcion1.isChecked() & false == Opcion2.isChecked() &
                false == Opcion3.isChecked() & false == Opcion4.isChecked() & false == Opcion5.isChecked()) ||
                "".equals(Name.getText().toString()) || "".equals(Password.getText().toString())) {
            Toast.makeText(EditList.this, "Falta un parametro", Toast.LENGTH_SHORT).show();
        }else {
            if(Name.length() > 22 || Password.length() > 30){
                String mensaje = "Parametro Erroneo";
                if(Name.length() > 22){mensaje = "Nombre Muy Largo";}
                if(Password.length() > 30){mensaje = "Contraseña Muy Larga";}
                Toast.makeText(EditList.this, mensaje, Toast.LENGTH_SHORT).show();
            }else {
                Json json = new Json();
                DbCuenta dbCuenta = new DbCuenta(EditList.this);

                try {
                    String valorNombre = Name.getText().toString();
                    String valorPassword = Password.getText().toString();
                    Location valorLocation = obtenerUbAc(EditList.this);
                    if (valorLocation != null) {
                        int valorImage = imagenUser[0];
                        boolean valorTipo = false;
                        Bitmap valorImageP = null;
                        if (Opcion1.isChecked()) {
                            valorImage = imagenUser[0];
                        }
                        if (Opcion2.isChecked()) {
                            valorImage = imagenUser[1];
                        }
                        if (Opcion3.isChecked()) {
                            valorImage = imagenUser[2];
                        }
                        if (Opcion4.isChecked()) {
                            valorImage = imagenUser[3];
                        }
                        if (Opcion5.isChecked()) {
                            if(imageP != null) {
                                valorTipo = true;
                                valorImageP = imageP;
                            }
                        }

                        String textoJsonCuenta = json.crearJsonCuenta(valorNombre, valorPassword, valorLocation, valorTipo, valorImageP, valorImage);

                        if (numContext == 1) {
                            boolean BucleArchivo = true;
                            int x = 1;
                            while (BucleArchivo) {
                                if (dbCuenta.comprobarCuenta(numArchivo, x)) {
                                    x = x + 1;
                                } else {
                                    dbCuenta.insertarCuenta(numArchivo, x, textoJsonCuenta);
                                    while((numLista + 5) <= x){numLista += 5;}
                                    BucleArchivo = false;
                                }
                            }
                        }
                        if (numContext == 2) {
                            int numArchivoCuenta = getIntent().getExtras().getInt("numArchivoCuenta");
                            dbCuenta.editarCuenta(numArchivo, numArchivoCuenta, textoJsonCuenta);
                        }
                        Intent intent = new Intent(EditList.this, ListMain.class);
                        intent.putExtra("numArchivo", numArchivo);
                        intent.putExtra("numLista", numLista);
                        startActivity(intent);
                    }
                }catch(Exception e){
                    Toast.makeText(EditList.this, "" + e, Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    public Location obtenerUbAc(Context context) {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions( new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION} , 3);
            return null;
        } else {
            LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
            Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (location == null) {
                location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                if (location == null) {
                    location = locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);
                    if (location == null) {
                        location = new Location("");
                    }
                }
            }
            return location;
        }
    }

    public void tomarFoto() {

        String nombreImagen = "";
        File fileImagen = new File(Environment.getExternalStorageDirectory(), RUTA_IMAGEN);
        boolean isCreada = fileImagen.exists();

        if (isCreada == false) {
            isCreada = fileImagen.mkdirs();
        }

        if (isCreada == true) {
            nombreImagen = (System.currentTimeMillis() / 1000) + ".jpg";
        }

        path = Environment.getExternalStorageDirectory() + File.separator + RUTA_IMAGEN + File.separator + nombreImagen;
        File imagen = new File(path);

        Intent intent = null;
        intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            String authorities = this.getPackageName() + ".provider";
            Uri imageUri = FileProvider.getUriForFile(this, authorities, imagen);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        } else {
            intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(imagen));
        }

        startActivityForResult(intent, TOMAR_FOTO);
    }

    public void seleccionarImagen() {
        Intent galeria = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        startActivityForResult(galeria, SELEC_IMAGEN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        cambiarBoton(5);
        if(resultCode == RESULT_OK && requestCode == SELEC_IMAGEN) {
            imagenUri = data.getData();
            try {
                Bitmap imagen = MediaStore.Images.Media.getBitmap(getContentResolver(), imagenUri);
                imageP = imagen;
                ivFoto.setImageBitmap(imagen);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if(resultCode == RESULT_OK && requestCode == TOMAR_FOTO) {
            MediaScannerConnection.scanFile(EditList.this, new String[]{path}, null, new MediaScannerConnection.OnScanCompletedListener() {
                @Override
                public void onScanCompleted(String s, Uri uri) {

                }
            });

            Bitmap bitmap = BitmapFactory.decodeFile(path);
            imageP = bitmap;
            ivFoto.setImageBitmap(bitmap);
        }

    }

    public void cambiarBoton(int i) {
        Opcion1.setChecked(false);
        Opcion2.setChecked(false);
        Opcion3.setChecked(false);
        Opcion4.setChecked(false);
        Opcion5.setChecked(false);
        if(i == 1){Opcion1.setChecked(true);}
        if(i == 2){Opcion2.setChecked(true);}
        if(i == 3){Opcion3.setChecked(true);}
        if(i == 4){Opcion4.setChecked(true);}
        if(i == 5){Opcion5.setChecked(true);}
    }

    public void Volver (View v){
        int numArchivo = getIntent().getExtras().getInt("numArchivo");
        int numLista = getIntent().getExtras().getInt("numLista");
        Intent intent = new Intent (EditList.this, ListMain.class);
        intent.putExtra("numArchivo", numArchivo);
        intent.putExtra("numLista", numLista);
        startActivity( intent );
    }
}
