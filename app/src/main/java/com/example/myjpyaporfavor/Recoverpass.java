package com.example.myjpyaporfavor;


import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import com.example.myslash.Encriptación.Sha1;
import com.example.myslash.Json.Info;
import com.example.myslash.Json.Json;
import com.example.myslash.MySQLite.DbInfo;

public class Recoverpass extends AppCompatActivity {

    private static final String TAG = "MyPaginaWeb";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recoverpass);
    }

    public void RestablecerContraseña (View v){

        EditText Pass = (EditText) findViewById(R.id.editTextRPpass);
        EditText newpass1 = (EditText) findViewById(R.id.editTextRPnewpass1);
        EditText newpass2 = (EditText) findViewById(R.id.editTextRPnewpass2);

        String mensaje = "";

        int numArchivo = getIntent().getExtras().getInt("numArchivo");
        String valorPass = getIntent().getExtras().getString("valorPass");

        if("".equals(Pass.getText().toString()) || "".equals(newpass1.getText().toString()) || "".equals(newpass2.getText().toString()))
        {
            mensaje = "Falta un Parametro";
        }
        else {
            if( !(newpass1.getText().toString()).equals(newpass2.getText().toString()) || !(Pass.getText().toString()).equals(valorPass)){
                mensaje = "Parametro Erroneo";
                if(!(newpass1.getText().toString()).equals(newpass2.getText().toString())){mensaje = "La Nueva Contraseña no Coincide";}
                if(!(Pass.getText().toString()).equals(valorPass)){mensaje = "Codigo Erroneo";}
            }else{
                try {

                    DbInfo dbInfo = new DbInfo(Recoverpass.this);
                    String completoTexto = dbInfo.verInfo(numArchivo);

                    Json json = new Json();
                    Info datos = json.leerJson(completoTexto);
                    String valorName = datos.getUserName();

                    Sha1 digest = new Sha1();
                    byte[] txtByte = digest.createSha1(valorName + newpass1.getText().toString());
                    String Sha1Password = digest.bytesToHex(txtByte);

                    String textoJson = json.crearJson(datos.getName(), datos.getFirstName(), datos.getLastName(), datos.getUserName(),
                            datos.getMail(), datos.getAge(), datos.getNumber(), datos.isGender(), datos.isType(), Sha1Password);

                    dbInfo.editarInfo(numArchivo, textoJson);

                    mensaje = "Contraseña Restablecida";
                    Toast.makeText(Recoverpass.this, mensaje, Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent (Recoverpass.this, Login.class);
                    startActivity( intent );

                } catch (Exception e) {
                    mensaje = "Error al Restablecer Contraseña";
                    Log.e(TAG, " Exception: " + e.getMessage());
                }
            }
        }
        Toast.makeText(Recoverpass.this, mensaje, Toast.LENGTH_SHORT).show();
    }
}
