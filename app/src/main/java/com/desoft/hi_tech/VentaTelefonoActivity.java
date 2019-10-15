package com.desoft.hi_tech;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class VentaTelefonoActivity extends AppCompatActivity {

    private ImageButton codigoqr, buscarIDTel;
    private Button venderTel, listarTel;
    private IntentIntegrator qrscan;
    private TextView producto;
    private EditText precioVentaTel;
    private ProgressDialog progressDialog;
    private Spinner spEmpleado;
    String cedula_U;
    private ListView listaTelefonos;
    String tienda;

    ArrayAdapter<String> adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_venta_telefono);

        progressDialog = new ProgressDialog(getApplicationContext());
        cargarPreferencias();

        codigoqr = (ImageButton) findViewById(R.id.btnVenQRTel);
        producto = (TextView) findViewById(R.id.txtBuscarProductoVenTel);
        precioVentaTel = (EditText) findViewById(R.id.txtPrecioVenTel);
        spEmpleado = (Spinner) findViewById(R.id.spEmpleadoVenTel);
        buscarIDTel = (ImageButton) findViewById(R.id.btnBuscaProductoVenTel);
        venderTel = (Button) findViewById(R.id.btnVenderTel);
        listarTel = (Button) findViewById(R.id.btnListarVentaTel);
        listaTelefonos = (ListView) findViewById(R.id.listaTelefonos);

        //CODIGO PARA VALIDAR SI EL DISPOSITIVO ESTA CONECTADO A INTERNET
        ConnectivityManager con = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = con.getActiveNetworkInfo();
        if(networkInfo != null && networkInfo.isConnected()) {
            Thread thread = new Thread() {
                @Override
                public void run() {
                    final String resultado1 = recibirDatosEmpleadosGET();
                    final String resultado = cargarDatosGET();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            cargarSpinner(listaEmpleados(resultado1));
                            cargarLista(listaProductos(resultado));
                            adapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1, listaProductos(resultado));
                            listaTelefonos.setAdapter(adapter);
                            producto.addTextChangedListener(new TextWatcher() {
                                @Override
                                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                                    //adapter.getFilter().filter(s);
                                }

                                @Override
                                public void onTextChanged(CharSequence s, int start, int before, int count) {
                                    adapter.getFilter().filter(s);
                                }

                                @Override
                                public void afterTextChanged(Editable s) {
                                    //adapter.getFilter().filter(s);
                                }
                            });
                            progressDialog.hide();
                            listaTelefonos.setClickable(true);
                            listaTelefonos.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                    Object o = listaTelefonos.getItemAtPosition(position);
                                    String str = (String) o;//As you are using Default String Adapter
                                    producto.setText(str);
                                }
                            });
                        }
                    });
                }
            };
            thread.start();
        }else {
            Toast.makeText(getApplicationContext(), "¡Verifique su conexión a internet!",Toast.LENGTH_SHORT).show();
            progressDialog.dismiss();
        }

        listarTel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listarVenta();
            }
        });

        buscarIDTel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buscarProductoId();
            }
        });

        venderTel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registrarVenta();
            }
        });

        venderTel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vistaVenderTelefono();
            }
        });
    }

    private void registrarVenta(){
        if (producto.getText().toString().isEmpty() || precioVentaTel.getText().toString().isEmpty()) {
            Toast.makeText(getApplicationContext(), "¡Complete los campos!", Toast.LENGTH_LONG).show();
        } else {
            String cedulaEmpleado = "";
            String[] emp = spEmpleado.getSelectedItem().toString().split(" - ");
            for (int i=0; i < emp.length; i++){
                cedulaEmpleado = emp[1].toString();
            }

            String id_imei = "";
            String[] imei = producto.getText().toString().split(" - ");
            for (int i=0; i < imei.length; i++) {
                id_imei = imei[0].toString();
            }

            final int precioTotal = Integer.parseInt(precioVentaTel.getText().toString());

            //agregas un mensaje en el ProgressDialog
            progressDialog.setMessage("Cargando...");
            //muestras el ProgressDialog
            progressDialog.show();
            //CODIGO PARA VALIDAR SI EL DISPOSITIVO ESTA CONECTADO A INTERNET
            ConnectivityManager con = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = con.getActiveNetworkInfo();
            if (networkInfo != null && networkInfo.isConnected()) {
                final String finalCedulaEmpleado = cedulaEmpleado;
                final String finalId_producto = id_imei;
                Thread thread = new Thread() {
                    @Override
                    public void run() {
                        final String resultado = (String) enviarDatosVentaGET(finalId_producto, precioTotal, finalCedulaEmpleado);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                int r = obtenerDatosJSON(resultado);
                                if (r > 0) {
                                    progressDialog.dismiss();
                                    producto.setText("");
                                    precioVentaTel.setText("");
                                    Toast.makeText(getApplicationContext(), "Se ha registrado la venta del telefono.", Toast.LENGTH_SHORT).show();
                                    //Toast.makeText(getContext(), "->" + resultado, Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(getApplicationContext(), "¡Error al registrar!", Toast.LENGTH_SHORT).show();
                                    progressDialog.hide();
                                }
                                progressDialog.hide();
                            }
                        });
                    }
                };
                thread.start();
            } else {
                Toast.makeText(this.getApplicationContext(), "¡Verifique su conexión a internet!", Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
            }
        }
    }

    /*
     * METODO DE MOSTRAR LOS DATOS LEYENDO POR MEDIO DEL LECTOR DE CCOIGO QR
     * */
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        IntentResult intentResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (intentResult != null) {
            if (intentResult.getContents() == null) {
                Toast.makeText(this.getApplicationContext(), "Resultado no encontrado", Toast.LENGTH_SHORT).show();
            } else {
                try {
                    JSONObject obj = new JSONObject(intentResult.getContents());
                } catch (JSONException ex) {
                    ex.printStackTrace();
                    //agregas un mensaje en el ProgressDialog
                    progressDialog.setMessage("Cargando...");
                    //muestras el ProgressDialog
                    progressDialog.show();
                    //CODIGO PARA VALIDAR SI EL DISPOSITIVO ESTA CONECTADO A INTERNET
                    ConnectivityManager con = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                    NetworkInfo networkInfo = con.getActiveNetworkInfo();
                    if (networkInfo != null && networkInfo.isConnected()) {
                        //final String finalCedulaEmpleado = ;
                        final String id = intentResult.getContents();
                        Thread thread = new Thread() {
                            @Override
                            public void run() {
                                final String resultado = recibirProductoQRGET(id,tienda);
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        progressDialog.hide();
                                        listaProductosQR(resultado);
                                        //Toast.makeText(getContext(), "Producto: " + resultado, Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        };
                        thread.start();
                    } else {
                        Toast.makeText(this.getApplicationContext(), "Verifique su conexión a internet", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void listarVenta(){
        Intent intent = new Intent(this.getApplicationContext(), ListarVentaActivity.class);
        startActivity(intent);
    }

    private void buscarProductoId(){
        //agregas un mensaje en el ProgressDialog
        progressDialog.setMessage("Cargando...");
        //muestras el ProgressDialog
        progressDialog.show();
        if (!producto.getText().toString().isEmpty()) {
            Thread thread = new Thread() {
                @Override
                public void run() {
                    String[] prod = producto.getText().toString().split(" - ");
                    String id = "";
                    for (int i = 0; i < prod.length; i++) {
                        id = prod[0].toString();
                    }
                    final String resultado = cargarDatosProdIDGET(id);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            progressDialog.hide();
                            cargarDatos(resultado);
                        }
                    });
                }
            };
            thread.start();
        } else {
            progressDialog.hide();
            Toast.makeText(this.getApplicationContext(),"¡Complete los campos!",Toast.LENGTH_SHORT).show();
        }
    }

    private void vistaVenderTelefono(){
        Intent intent = new Intent(this.getApplicationContext(), VentaTelefonoActivity.class);
        startActivity(intent);
    }

    /*
     * METODO PARA OBTENER LOS DATOS DE LOS PRODUCTOS
     * */
    public String cargarDatosGET(){
        URL url = null;
        String linea = "";
        int respuesta = 0;
        StringBuilder resul = null;
        String url_local = "http://192.168.56.1/ServiciosWeb/cargarProductosBD.php?cedula=" + cedula_U;
        String url_aws = "http://52.67.38.127/hitech/cargarTelefonos.php";

        try{
            //LA IP SE CAMBIA CON RESPECTO O EN BASE A LA MAQUINA EN LA CUAL SE ESTA EJECUTANDO YA QUE NO TODAS LAS IP SON LAS MISMAS EN LOS EQUIPOS
            url = new URL(url_aws);
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

    /*
     * METODO PARA ENVIAR LOS DATOS DE LA VENTA AL SERVIDOR POR WEB SERVICES
     * */
    public String enviarDatosVentaGET(String imei, int precio, String cedula){
        URL url = null;
        String linea = "";
        int respuesta = 0;
        StringBuilder resul = null;
        String url_local = "http://192.168.56.1/ServiciosWeb/registrarVentaBD.php";
        String url_aws = "http://52.67.38.127/hitech/registrarVentaTelefono.php";

        try{
            //LA IP SE CAMBIA CON RESPECTO O EN BASE A LA MAQUINA EN LA CUAL SE ESTA EJECUTANDO YA QUE NO TODAS LAS IP SON LAS MISMAS EN LOS EQUIPOS
            url = new URL(url_aws + "?producto=" + imei + "&precio=" + precio + "&empleado=" + cedula);
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

    /*
     * METODO PARA RECIBIR LOS EMPLEADOS REGISTRADOS
     * */
    public String recibirDatosEmpleadosGET(){
        URL url = null;
        String linea = "";
        int respuesta = 0;
        StringBuilder resul = null;
        String url_aws = "http://52.67.38.127/hitech/empleados.php";
        String url_local = "http://192.168.56.1/ServiciosWeb/empleadosBD.php";

        try{
            //LA IP SE CAMBIA CON RESPECTO O EN BASE A LA MAQUINA EN LA CUAL SE ESTA EJECUTANDO YA QUE NO TODAS LAS IP SON LAS MISMAS EN LOS EQUIPOS
            url = new URL(url_aws);
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

    /*
     * METODO PARA RECIBIR LOS DATOS DEL PRODUCTO
     * POR MEDIO DEL CODIGO QR
     * */
    public String recibirProductoQRGET(String producto, String tienda){
        URL url = null;
        String linea = "";
        int respuesta = 0;
        StringBuilder resul = null;

        //DDIRECCION DEL NUEVO SERVICIO DE LA NUEVA BD
        String url_aws = "http://52.67.38.127/hitech/buscarProductoQR.php?";

        try{
            //LA IP SE CAMBIA CON RESPECTO O EN BASE A LA MAQUINA EN LA CUAL SE ESTA EJECUTANDO YA QUE NO TODAS LAS IP SON LAS MISMAS EN LOS EQUIPOS
            url = new URL(url_aws + "producto=" + producto + "&tienda=" + tienda);
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

    /*
     * METODO PARAA CARGAR LOS DATOS DEL PRODUCTO RECIBIENDO
     * RECIBIENDO COMO PARAMETRO EL ID DEL PRODUCTO
     * */
    public String cargarDatosProdIDGET(String producto) {
        URL url = null;
        String linea = "";
        int respuesta = 0;
        StringBuilder resul = null;
        String url_aws = "http://52.67.38.127/hitech/buscarProductoID.php?";
        String url_local = "http://192.168.56.1/ServiciosWeb/buscarProducto.php?";

        try {
            //LA IP SE CAMBIA CON RESPECTO O EN BASE A LA MAQUINA EN LA CUAL SE ESTA EJECUTANDO YA QUE NO TODAS LAS IP SON LAS MISMAS EN LOS EQUIPOS
            url = new URL(url_aws + "producto=" + producto);
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

    /*
     * METODO PARA MOSTRAR LOS DATOS RECIBIDOS EN EL JSON EN LOS DIFERENTES CAMPOS
     * */
    public void cargarDatos(String response) {
        JSONArray jsonArray = null;
        try {
            jsonArray = new JSONArray(response);
            for (int i = 0; i < jsonArray.length(); i++) {
                precioVentaTel.setText(jsonArray.getJSONObject(i).getString("precioVenta"));
            }
        } catch (Exception ex) {
            Toast.makeText(getApplicationContext(), "Error: " + ex, Toast.LENGTH_SHORT).show();
            for (int i = 0; i < jsonArray.length(); i++) {
                try {
                    precioVentaTel.setText(jsonArray.getJSONObject(i).getString("precioVenta"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /* SECTOR DE CODIGO QUE PEERMITE CARRGAR LOS PRODUCTOS EN UN ARREGLO
     * PARA LUEGO CARGARLOS EN UN LISTVIEW
     * */
    public ArrayList<String> listaProductosQR(String response){
        ArrayList<String> listado = new ArrayList<String>();
        try{
            JSONArray jsonArray = new JSONArray(response);
            String texto = "";
            for (int i = 0; i < jsonArray.length(); i++){
                precioVentaTel.setText(jsonArray.getJSONObject(i).getString("precioVenta"));
                producto.setText(jsonArray.getJSONObject(i).getString("id_prodtienda") + " - "
                        + jsonArray.getJSONObject(i).getString("nombre") + " - "
                        + jsonArray.getJSONObject(i).getString("modelo"));
                listado.add(texto);
            }
        }catch (Exception e){}
        return listado;
    }

    /* SECTOR DE CODIGO QUE PEERMITE CARRGAR LOS EMPLEADOS
     *  EN UN ARREGLO PARA LUEGO CARGARLOS EN UN LISTVIEW
     * */
    public ArrayList<String> listaEmpleados(String response){
        ArrayList<String> listado = new ArrayList<String>();
        try{
            JSONArray jsonArray = new JSONArray(response);
            String texto = "";
            for (int i = 0;i<jsonArray.length();i++){
                texto = jsonArray.getJSONObject(i).getString("nombre") + " - " + jsonArray.getJSONObject(i).getString("cedula");
                listado.add(texto);
            }
        }catch (Exception e){}
        return listado;
    }

    /* SECTOR DE CODIGO QUE PEERMITE CARRGAR LOS PRODUCTOS EN UN ARREGLO
     * PARA LUEGO CARGARLOS EN UN LISTVIEW
     * */
    public ArrayList<String> listaProductos(String response){
        ArrayList<String> listado = new ArrayList<String>();
        try{
            JSONArray jsonArray = new JSONArray(response);
            String texto = "";
            for (int i = 0; i < jsonArray.length(); i++){
                texto = jsonArray.getJSONObject(i).getString("imei") + " - "
                        + jsonArray.getJSONObject(i).getString("marca") + " - "
                        + jsonArray.getJSONObject(i).getString("modelo");
                listado.add(texto);
            }
        }catch (Exception e){}
        return listado;
    }

    /* METODO QUE PERMITE OBTENER EL JSON
     * Y RECORRERLO Y SABER SI RECIBIO O NO DATOS
     * */
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

    /* METODO PARA CARGAR EL ARRAYLIST
     *  DE EMPLEADOS EN EL SPINNER
     * */
    public void cargarSpinner(ArrayList<String> empleado){
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this.getApplicationContext(), android.R.layout.simple_list_item_1, empleado);
        spEmpleado = (Spinner) findViewById(R.id.spEmpleadoVenTel);
        spEmpleado.setAdapter(adapter);
    }

    /* METODO QUE PERMITE CARGAR EL ARRAYLIST DE
     *  PRODUCTOS EN EL LISTVIEW
     * */
    public void cargarLista(ArrayList<String> listaProd) {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this.getApplicationContext(), android.R.layout.simple_list_item_1, listaProd);
        listaTelefonos = (ListView) findViewById(R.id.listaTelefonos);
        listaTelefonos.setAdapter(adapter);
    }

    private void cargarPreferencias(){
        SharedPreferences preferences = getApplicationContext().getSharedPreferences("Preferences", Context.MODE_PRIVATE);
        cedula_U = preferences.getString("cedula","");
        //tienda = preferences.getString("tienda","");
    }

    //****************** ================= FIN CODIGO ================= ******************//
}
