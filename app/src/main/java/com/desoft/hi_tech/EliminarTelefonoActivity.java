package com.desoft.hi_tech;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class EliminarTelefonoActivity extends AppCompatActivity {

    EditText telefono;
    ImageView buscarTelefono;
    ListView listaTelefono;
    private ProgressDialog progressDialog;
    ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_eliminar_telefono);

        progressDialog = new ProgressDialog(EliminarTelefonoActivity.this);
        telefono = (EditText) findViewById(R.id.txtBuscaImei);
        buscarTelefono = (ImageView) findViewById(R.id.btnBuscaImei);
        listaTelefono = (ListView) findViewById(R.id.listTelefono);

        buscarTelefono.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buscarImei(telefono.getText().toString());
            }
        });

        listaTelefono.setClickable(true);
        listaTelefono.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Object o = listaTelefono.getItemAtPosition(position);
                String str=(String)o;//As you are using Default String Adapter
                alertOneButton();
            }
        });
    }

    public void buscarImei(final String imei){
        //agregas un mensaje en el ProgressDialog
        progressDialog.setMessage("Cargando salida...");
        //muestras el ProgressDialog
        progressDialog.show();
        if (imei.isEmpty()){
            progressDialog.dismiss();
            Toast.makeText(getApplicationContext(), "Complete los campos", Toast.LENGTH_SHORT).show();
        }else {
            Thread thread = new Thread() {
                @Override
                public void run() {
                    final String resultado = buscarImeiGET(imei);
                    final int validar = validarDatosJSON(resultado);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (validar > 0) {
                                progressDialog.dismiss();
                                cargarLista(listarVenta((resultado)));
                                Toast.makeText(getApplicationContext(), "Se ha cargado el telefono exitosamente.", Toast.LENGTH_SHORT).show();
                                adapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1, listarVenta(resultado)) {
                                    //PERMITE CAMBIAR DE COLOR EL ISTVIEW EN UN ACTIVITY YA QE LO MUESTRA LAS LETRAS EN BLANCO
                                    @Override
                                    public View getView(int position, View convertView, ViewGroup parent) {
                                        View view = super.getView(position, convertView, parent);
                                        // Initialize a TextView for ListView each Item
                                        TextView tv = (TextView) view.findViewById(android.R.id.text1);
                                        // Set the text color of TextView (ListView Item)
                                        tv.setTextColor(Color.BLACK);
                                        return view;
                                    }
                                };
                                listaTelefono.setAdapter(adapter);
                            } else {
                                progressDialog.dismiss();
                                Toast.makeText(getApplicationContext(), "No hay ningún telefono registrado con ese IMEI.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            };
            thread.start();
        }
    }

    //METODO PARA MOSTRAR DIALOGO PARA QUE EL SERVICIO SE DE POR FINAIZADO
    public void alertOneButton() {
        new AlertDialog.Builder(EliminarTelefonoActivity.this)
                //.setIcon(R.drawable.icono)
                .setTitle("Eliminar Telefono")
                .setMessage("¿Seguro que deseas eliminar el telefono?")
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                })
                .setPositiveButton("Si", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Thread thread = new Thread() {
                            @Override
                            public void run() {
                                final String res = eliminarDatosGET(Integer.parseInt(telefono.getText().toString()));
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (res.equalsIgnoreCase("No eliminado")) {
                                            progressDialog.dismiss();
                                            Toast.makeText(getApplicationContext(), "¡Algo paso!", Toast.LENGTH_SHORT).show();
                                            progressDialog.hide();
                                        } else {
                                            adapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1, listarVenta(res)){
                                                //PERMITE CAMBIAR DE COLOR EL ISTVIEW EN UN ACTIVITY YA QE LO MUESTRA LAS LETRAS EN BLANCO
                                                @Override
                                                public View getView(int position, View convertView, ViewGroup parent) {
                                                    View view = super.getView(position, convertView, parent);
                                                    // Initialize a TextView for ListView each Item
                                                    TextView tv = (TextView) view.findViewById(android.R.id.text1);
                                                    // Set the text color of TextView (ListView Item)
                                                    tv.setTextColor(Color.BLACK);
                                                    return view;
                                                }
                                            };
                                            listaTelefono.setAdapter(adapter);
                                            Toast.makeText(getApplicationContext(), "Telefono eliminado.", Toast.LENGTH_SHORT).show();
                                            progressDialog.hide();
                                        }
                                    }
                                });
                            }
                        };
                        thread.start();
                    }
                }).show();
    }

    //===== INICIO CODIGO SERVIDOR Y JSON
    public String buscarImeiGET(String imei) {
        URL url = null;
        String linea = "";
        int respuesta = 0;
        StringBuilder resul = null;
        String url_local = "http://172.21.0.130/ServiciosWeb/buscarSalida.php?";
        String url_aws = "http://52.67.38.127/hitech/buscarImei.php?";

        try {
            url = new URL(url_aws + "imei=" + imei);
            HttpURLConnection conection = (HttpURLConnection) url.openConnection();
            respuesta = conection.getResponseCode();
            resul = new StringBuilder();
            if (respuesta == HttpURLConnection.HTTP_OK) {
                InputStream inputStream = new BufferedInputStream(conection.getInputStream());
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                while ((linea = reader.readLine()) != null) {
                    resul.append(linea);
                }
            }
        } catch (Exception e) {
            return e.getMessage();
        }
        return resul.toString();
    }

    //METODO PARA RECIBIR LOS DATOS DEL SERRVIDOR EN JSON
    public String eliminarDatosGET(int imei){
        URL url = null;
        String linea = "";
        int respuesta = 0;
        StringBuilder resul = null;
        String url_local = "http://172.21.0.130/ServiciosWeb/eliminarSalida.php?";
        String url_aws = "http://52.67.38.127/hitech/eliminarTelefono.php?";

        try{
            url = new URL(url_aws + "imei=" + imei);
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

    public void cargarLista(ArrayList<String> lista) {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this.getApplicationContext(), android.R.layout.simple_list_item_1, lista);
        listaTelefono = (ListView) findViewById(R.id.listTelefono);
        listaTelefono.setAdapter(adapter);
    }

    //CARGAR LA LOS DATOS RECIBIDO EN EL LISTVIEW DE BUSCAR VENTA
    public ArrayList<String> listarVenta(String response){
        ArrayList<String> listado = new ArrayList<String>();
        try{
            JSONArray jsonArray = new JSONArray(response);
            String texto = "";
            for (int i = 0;i<jsonArray.length();i++){
                texto = "Imei: " + jsonArray.getJSONObject(i).getString("imei") + "\n"
                        + "Marca: " + jsonArray.getJSONObject(i).getString("marca") + "\n"
                        + "Modelo: " + jsonArray.getJSONObject(i).getString("modelo") + "\n"
                        + "Precio costo: " + jsonArray.getJSONObject(i).getString("precioCosto") + "\n"
                        + "Precio venta: " + jsonArray.getJSONObject(i).getString("precioVenta") + "\n"
                        + "Fecha compra: " + jsonArray.getJSONObject(i).getString("fecha_compra") + "\n"
                        + "Bodega: " + jsonArray.getJSONObject(i).getString("bodega");
                listado.add(texto);
            }
        }catch (Exception e){}
        return listado;
    }

    public int validarDatosJSON(String response) {
        int res = 0;
        try {
            JSONArray jsonArray = new JSONArray(response);
            if (jsonArray.length() > 0) {
                res = 1;
            }
        } catch (Exception e) {
        }
        return res;
    }
}
