package com.desoft.hi_tech;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONArray;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class RegistrarTelefonoActivity extends AppCompatActivity {

    Button registrar;
    EditText imei, marca, modelo, costo, venta;
    private ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registrar_telefono);

        imei = (EditText) findViewById(R.id.txtImei);
        marca = (EditText) findViewById(R.id.txtMarca);
        modelo = (EditText) findViewById(R.id.txtModelo);
        costo = (EditText) findViewById(R.id.txtPrecioCostoTel);
        venta = (EditText) findViewById(R.id.txtPrecioVentaTel);
        registrar = (Button) findViewById(R.id.btnRegistrarTelefono);

        registrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registrarTelefono();
            }
        });
    }

    private void registrarTelefono(){
        //CODIGO PARA VALIDAR SI EL DISPOSITIVO ESTA CONECTADO A INTERNET
        ConnectivityManager con = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = con.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            if (imei.getText().toString().isEmpty() || marca.getText().toString().isEmpty() || modelo.getText().toString().isEmpty()
                    || costo.getText().toString().isEmpty() || modelo.getText().toString().isEmpty()) {
                Toast.makeText(getApplicationContext(), "¡Complete los campos!", Toast.LENGTH_SHORT).show();
            } else {
                //agregas un mensaje en el ProgressDialog
                progressDialog.setMessage("Cargando...");
                //muestras el ProgressDialog
                progressDialog.show();
                //CODIGO PARA VALIDAR SI EL DISPOSITIVO ESTA CONECTADO A INTERNET
                Thread thread = new Thread() {
                    @Override
                    public void run() {
                        final String resultado = registrarDatosGET(imei.toString(), marca.toString(), modelo.getText().toString(), Integer.parseInt(costo.toString()),
                                Integer.parseInt(venta.toString()));
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                //cargarWebServices();
                                int r = obtenerDatosJSON(resultado);
                                //Condición para validar si los campos estan llenos
                                if (r == 0) {
                                    progressDialog.hide();
                                    Toast.makeText(getApplicationContext(), "¡Algo malo ocurrio!", Toast.LENGTH_SHORT).show();
                                    //Toast.makeText(getApplicationContext(), resultado, Toast.LENGTH_LONG).show();
                                } else {
                                    progressDialog.dismiss();
                                    Toast.makeText(getApplicationContext(), "Se ha registrado el telefono exitosamente", Toast.LENGTH_SHORT).show();
                                    modelo.setText("");
                                    imei.setText("");
                                    marca.setText("");
                                    modelo.setText("");
                                    costo.setText("");
                                    venta.setText("");
                                }
                                progressDialog.hide();
                            }
                        });
                    }
                };
                thread.start();
            }
            progressDialog.hide();
        }
    }

    //----INICIO CODIGO---->
    //METODO PARA OBTENER LOS DATOS DE LOS PRODUCTOS
    public String registrarDatosGET(String imei, String marca, String modelo, int precioCosto, int precioVenta){
        URL url = null;
        String linea = "";
        int respuesta = 0;
        StringBuilder resul = null;
        String url_local = "http://192.168.1.23/ServiciosWeb/registrarProducto.php?";
        String url_aws = "http://52.67.38.127/hitech/hitech/registrarTelefono.php?";
        String mar = marca.replace(" ", "%20");
        String mod = modelo.replace(" ", "%20");

        try{
            //LA IP SE CAMBIA CON RESPECTO O EN BASE A LA MAQUINA EN LA CUAL SE ESTA EJECUTANDO YA QUE NO TODAS LAS IP SON LAS MISMAS EN LOS EQUIPOS
            url = new URL(url_aws + "imei=" + imei + "&marca="+ mar + "&modelo="+ mod + "&precioCosto=" + precioCosto + "&precioVenta=" + precioVenta);
            HttpURLConnection conection = (HttpURLConnection) url.openConnection();
            respuesta = conection.getResponseCode();
            resul = new StringBuilder();
            if (respuesta == HttpURLConnection.HTTP_OK){
                InputStream inputStream = new BufferedInputStream(conection.getInputStream());
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                while ((linea = reader.readLine()) != null){
                    resul.append(linea);
                }
            }
        }catch (Exception e){
            return e.getMessage();
        }
        return resul.toString();
    }

    //METODO QUE PERMITE OBTENER EL JSON Y RECORRERLO Y SABER SI RECIBIO O NO DATOS
    public int obtenerDatosJSON(String response){
        int res = 0;
        try {
            JSONArray jsonArray = new JSONArray(response);
            if (jsonArray.length() > 0 ){
                res = 1;
            }
        }catch (Exception e){}
        return res;
    }
}
