package com.desoft.hi_tech;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import org.json.JSONArray;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class RegistrarProductoActivity extends AppCompatActivity {

    Spinner spArticulo;
    EditText modelo, precioUnitario, precioVenta, descripcion, cantidad;
    Button registrar;
    ImageView foto;
    ArrayAdapter<String> adapter;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registrar_producto);

        progressDialog = new ProgressDialog(RegistrarProductoActivity.this);
        spArticulo = (Spinner) findViewById(R.id.spArticuloReg);
        modelo = (EditText) findViewById(R.id.txtModeloRegistrar);
        precioUnitario = (EditText) findViewById(R.id.txtPrecioUnitarioReg);
        precioVenta = (EditText) findViewById(R.id.txtPrecioVentaReg);
        descripcion = (EditText) findViewById(R.id.txtDescripcionRegistrar);
        cantidad = (EditText) findViewById(R.id.txtCantidadRegistrar);
        registrar = (Button) findViewById(R.id.btnRegistrarProducto);

        registrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registrarProducto();
            }
        });

        final String articulo[] = {"Seleccione un articulo","Accesorios","Acuario","Agenda","Audifonos","Baterias","Bloques","Cables","Cargador","Fibras 3D","Fibras 4D",
                "Fibras 5D","Fibras Basicas","Fibras Basicos Chinos","Fibras Biselados","Forros 360","Forros 360 Magnetico","Forros Antishock Basicos",
                "Forros Antishock Boomper","Forros Antishock Chinos","Forros Silicone Case","Gomas basicas","Gomas diseno","Memorias","Repuestos","Tablet","Telefono"};

        //-----CODIGO PARA MOSTRAR LOS DATOS EN EN SPINNER O COMBOBOX
        //MOSTRAR EL LISTADO DE LOS ARTICULOS EN EL SPINNER ARTICULOS
        List<String> listaArticulo;
        final ArrayAdapter adapterSpinner;
        listaArticulo = new ArrayList<>();
        //Cargo articulo en listaArticulo
        Collections.addAll(listaArticulo, articulo);
        //Paso los valores a mi adapter
        adapterSpinner = new ArrayAdapter<String>(this.getApplicationContext(), R.layout.spinner_item_producto, listaArticulo);
        //Linea de código secundario sirve para asignar un layout a los ítems
        adapterSpinner.setDropDownViewResource(R.layout.spinner_item_producto);
        //Muestro los ítems en el spinner, obtenidos gracias al adapter
        spArticulo.setAdapter(adapterSpinner);
        //FIN CODIGO
    }

    private void registrarProducto() {
        //CODIGO PARA VALIDAR SI EL DISPOSITIVO ESTA CONECTADO A INTERNET
        ConnectivityManager con = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = con.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            if (modelo.getText().toString().isEmpty() || precioUnitario.getText().toString().isEmpty() || precioVenta.getText().toString().isEmpty()
                    || cantidad.getText().toString().isEmpty() || descripcion.getText().toString().isEmpty()) {
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
                        final String resultado = registrarDatosGET(spArticulo.getSelectedItem().toString(), modelo.getText().toString(),
                                Integer.parseInt(precioUnitario.getText().toString()), Integer.parseInt(precioVenta.getText().toString()),
                                Integer.parseInt(cantidad.getText().toString()), descripcion.getText().toString());
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                //cargarWebServices();
                                int r = obtenerDatosJSON(resultado);
                                //Condición para validar si los campos estan llenos
                                if (r == 0) {
                                    progressDialog.hide();
                                    Toast.makeText(getApplicationContext(), "¡Algo malo ocurrio!", Toast.LENGTH_SHORT).show();
                                    Toast.makeText(getApplicationContext(), resultado, Toast.LENGTH_LONG).show();
                                } else {
                                    progressDialog.dismiss();
                                    Toast.makeText(getApplicationContext(), "Se ha registrado el producto exitosamente", Toast.LENGTH_SHORT).show();
                                    modelo.setText("");
                                    precioUnitario.setText("");
                                    precioVenta.setText("");
                                    cantidad.setText("");
                                    descripcion.setText("");
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

    private void vistaAgregarArticulo(){
        Intent intent = new Intent(getApplicationContext(), RegistrarArticuloActivity.class);
        startActivity(intent);
    }

    //----INICIO CODIGO---->
    //METODO PARA OBTENER LOS DATOS DE LOS PRODUCTOS
    public String registrarDatosGET(String articulo, String modelo, int precioCosto, int precioVenta, int cantidad, String descrip){
        URL url = null;
        String linea = "";
        int respuesta = 0;
        StringBuilder resul = null;
        String url_local = "http://192.168.1.23/ServiciosWeb/registrarProducto.php?";
        String url_aws = "http://18.228.235.94/wifix/ServiciosWeb/registrarProducto.php?";
        String mar = articulo.replace(" ", "%20");
        String mod = modelo.replace(" ", "%20");
        String descri= descrip.replace(" ", "%20");

        //String imagen = convertirImagenString(bitmap);

        try{
            //LA IP SE CAMBIA CON RESPECTO O EN BASE A LA MAQUINA EN LA CUAL SE ESTA EJECUTANDO YA QUE NO TODAS LAS IP SON LAS MISMAS EN LOS EQUIPOS
            url = new URL(url_aws + "articulo=" + mar + "&modelo=" + mod + "&precioCosto=" + precioCosto + "&precioVenta=" + precioVenta
                    + "&cantidad=" + cantidad + "&descripcion=" + descri);
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
