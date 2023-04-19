package com.example.myjpyaporfavor;


import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.myslash.Encriptación.Des;
import com.example.myslash.Encriptación.Sha1;
import com.example.myslash.Json.Info;
import com.example.myslash.Json.Json;
import com.example.myslash.MySQLite.DbInfo;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.Random;

public class Forgotpass extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgotpass);
    }

    public void RecuperarContraseña (View v){
        EditText userName = (EditText) findViewById(R.id.editTextFPName);
        EditText Mail = (EditText) findViewById(R.id.editTextFPMail);

        String mensaje = "";
        String DominioCorreo = "";

        if("".equals(userName.getText().toString()) || "".equals(Mail.getText().toString()))
        {
            mensaje = "Falta un Parametro";
        }else{
            boolean TipoCorreo = false;
            String Correo = "";
            for(int x = 0 ; x < Mail.length(); x++){
                if(Mail.getText().charAt(x) == '@'){
                    for(int y = x ; y < Mail.length(); y++){
                        Correo = Correo + Mail.getText().charAt(y);
                    }
                    if("@gmail.com".equals(Correo) || "@hotmail.com".equals(Correo) || "@outlook.com".equals(Correo)){
                        if("@gmail.com".equals(Correo)){DominioCorreo = "Gmail";}
                        if("@hotmail.com".equals(Correo)){DominioCorreo = "Hotmail";}
                        if("@outlook.com".equals(Correo)){DominioCorreo = "Outlook";}
                        TipoCorreo = true;
                    }
                    break;
                }
            }
            if(userName.length() > 20 || Mail.length() > 25 || TipoCorreo == false){
                mensaje = "Parametro Erroneo";
                if(userName.length() > 20){mensaje = "Nombre de Usuario Muy Largo";}
                if(Mail.length() > 25){mensaje = "Correo Muy Largo";}
                if(TipoCorreo == false){mensaje = "Correo Invalido";}
            }else {
                try {
                    Json json = new Json();
                    Des myDes = new Des();

                    String MailCorreo = "";
                    String HTMLCorreo = "";
                    String nombreUsuario = "";
                    String valorPass = "";
                    int numArchivo = 0;

                    boolean BucleArchivo = true;
                    int x = 1;
                    while (BucleArchivo) {
                        DbInfo dbInfo = new DbInfo(Forgotpass.this);
                        if(dbInfo.comprobarInfo(x)){
                            String completoTexto = dbInfo.verInfo(x);

                            Info datos = json.leerJson(completoTexto);
                            String valorName = datos.getUserName();
                            String valorMail = datos.getMail();

                            if (valorName.equals(userName.getText().toString()) & valorMail.equals(Mail.getText().toString())) {
                                mensaje = "Usuario Encontrado";
                                MailCorreo = valorMail;
                                nombreUsuario = valorName;
                                Random rand = new Random();
                                valorPass = String.format(String.valueOf(rand.nextInt((99999 - 10000) + 1) + 10000));
                                numArchivo = x;

                                String TAG = "MyPaginaWeb";
                                Log.i( TAG , valorPass);

                                BucleArchivo = false;
                            } else {
                                x = x + 1;
                            }
                        }else{
                            mensaje = "Usuario no Encontrado";
                            BucleArchivo = false;
                        }
                    }

                    if("Usuario Encontrado".equals(mensaje)){
                        HTMLCorreo = "<html><head><title> Correo Olvidar Contraseña </title></head><style>.div-1 {background-color: #2d2c35;}.div-2 {margin: 20px;border: 3px solid;background-color: #39383f;}</style><body><div class=\"div-1\"><table width=\"100%\" height=\"146\" border=\"0\"><tr><td width=\"31%\" height=\"142\"><div align=\"center\"><img src=\"https://upload.wikimedia.org/wikipedia/commons/thumb/f/f8/Logo_Instituto_Polit%C3%A9cnico_Nacional.png/347px-Logo_Instituto_Polit%C3%A9cnico_Nacional.png\" width=\"94\" height=\"130\"/></div></td><td width=\"41%\"><div align=\"center\"><p><Font color=black>INSTITUTO POLITECNICO NACIONAL</font></p><p><Font color=black>CENTRO DE ESTUDIOS CIENTIFICOS Y TECNOLOGICOS No.9</font></p><p><Font color=black>ANDROID STUDIO 2</font></p><p><Font color=black>MY PAGINA WEB</font></p></div></td><td width=\"28%\"><div align=\"center\"><img src=\"https://www.cecyt9.ipn.mx/assets/files/cecyt9/img/escudos/escudoCECyT9.png\" width=\"141\" height=\"134\"/></div></td></tr></table><div class=\"div-2\"><P align=left><Font size=8 color=black Face=Arial> Hola, " + nombreUsuario + " </font></p><P align=justify><Font size=8 color=black Face=Arial> Hemos recibido una solicitud para acceder a tu cuenta de " + DominioCorreo + ", " + MailCorreo + ", a través de tu dirección de correo electronico. Tu código de verificación de  " + DominioCorreo + " es:</font></p><Hr><br><P align=center><Font size=12 color=black Face=Arial><strong> " + valorPass + " </strong></font></p><br><Hr><P align=justify><Font size=8 color=black Face=Arial> Si no has solicitado este código, puede que alguien esté intentando acceder a la cuenta de " + DominioCorreo + " " + MailCorreo + ". <strong>No reenvíes este correo electronónico ni des el código a nadie.</strong></font></p><br><P align=left><Font size=8 color=black Face=Arial> Atentamente, </font></p><P align=left><Font size=8 color=black Face=Arial> El equipo de MyPaginaWeb </font></p></div><br></div></body></html>";
                        MailCorreo = myDes.cifrar(MailCorreo);
                        HTMLCorreo = myDes.cifrar(HTMLCorreo);

                        if( sendInfo( MailCorreo, HTMLCorreo) ) {
                            mensaje = "Se envío el Correo";
                            Intent intent = new Intent (Forgotpass.this, Recoverpass.class);
                            intent.putExtra("numArchivo", numArchivo);
                            intent.putExtra("valorPass", valorPass);
                            startActivity( intent );
                        }
                        else {
                            mensaje = "Error en el envío del Correo";
                        }
                    }

                } catch (Exception e) {
                    mensaje = "Error en el Archivo";
                }
            }
        }
        Toast.makeText(Forgotpass.this, mensaje, Toast.LENGTH_SHORT).show();
    }

    public void Volver (View v){
        Intent intent = new Intent (Forgotpass.this, Login.class);
        startActivity( intent );
    }

    public boolean sendInfo( String Correo , String HTML)
    {
        String TAG = "App";
        JsonObjectRequest jsonObjectRequest = null;
        JSONObject jsonObject = null;
        String url = "https://us-central1-nemidesarrollo.cloudfunctions.net/envio_correo";
        RequestQueue requestQueue = null;

        jsonObject = new JSONObject( );
        try {
            jsonObject.put("correo" , Correo);
            jsonObject.put("mensaje", HTML);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response)
            {
                Log.i(TAG, response.toString());
            }
        } , new  Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, error.toString());
            }
        } );
        requestQueue = Volley.newRequestQueue( getBaseContext() );
        requestQueue.add(jsonObjectRequest);

        return true;
    }
}
