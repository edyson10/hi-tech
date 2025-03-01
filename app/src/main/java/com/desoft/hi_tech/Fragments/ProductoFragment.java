package com.desoft.hi_tech.Fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.desoft.hi_tech.ProductoActivity;
import com.desoft.hi_tech.R;
import com.desoft.hi_tech.RegistrarProductoActivity;

import org.json.JSONArray;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class ProductoFragment extends Fragment {

    View view;
    EditText cantidad, producto;
    Button añadir, verProductos, registrar;
    ListView listaModelos;
    ArrayAdapter<String> adapter;
    String cedula_U;
    private ProgressDialog progressDialog;

    private OnFragmentInteractionListener mListener;

    public ProductoFragment() {
        // Required empty public constructor
    }

    public static ProductoFragment newInstance(String param1, String param2) {
        ProductoFragment fragment = new ProductoFragment();
        Bundle args = new Bundle();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_producto, container, false);

        progressDialog = new ProgressDialog(getContext());
        cargarPreferencias();

        producto = (EditText) view.findViewById(R.id.txtProductoAct);
        cantidad = (EditText) view.findViewById(R.id.txtCantidadAct);
        añadir = (Button) view.findViewById(R.id.btnAñadir);
        verProductos = (Button) view.findViewById(R.id.btnVerProd);
        registrar = (Button) view.findViewById(R.id.btnRegistrarPrdo);
        listaModelos = (ListView) view.findViewById(R.id.listModelos);

        //CODIGO PARA VALIDAR SI EL DISPOSITIVO ESTA CONECTADO A INTERNET
        ConnectivityManager con = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = con.getActiveNetworkInfo();
        if(networkInfo != null && networkInfo.isConnected()) {
            Thread thread = new Thread() {
                @Override
                public void run() {
                    final String resultado = cargarDatosGET();
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            cargarLista(listaProductos(resultado));
                            adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, listaProductos(resultado));
                            listaModelos.setAdapter(adapter);
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
                        }
                    });
                    listaModelos.setClickable(true);
                    listaModelos.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            Object o = listaModelos.getItemAtPosition(position);
                            String str = (String) o;//As you are using Default String Adapter
                            producto.setText(str);
                        }
                    });
                }
            };
            thread.start();
        }else {
            Toast.makeText(getContext(), "Verifique su conexión a internet",Toast.LENGTH_SHORT).show();
            progressDialog.dismiss();
        }

        añadir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                añadirProducto();
            }
        });

        registrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registrarProducto();
            }
        });

        verProductos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                verProducto();
            }
        });

        return view;
    }

    private void añadirProducto(){
        if (producto.getText().toString().isEmpty() || cantidad.getText().toString().isEmpty()) {
            Toast.makeText(getContext(), "¡Complete los campos!", Toast.LENGTH_LONG).show();
        } else {
            String[] prod = producto.getText().toString().split(" - ");
            String id = "";
            String art = "";
            String mod = "";
            for (int i = 0; i < prod.length; i++) {
                id = prod[0].toString();
                art = prod[1].toString();
                mod = prod[2].toString();
            }

            final String finalId = id;
            final String finalArt = art;
            final String finalMod = mod;
            final char[] cant = cantidad.getText().toString().toCharArray();
            //agregas un mensaje en el ProgressDialog
            progressDialog.setMessage("Cargando...");
            //muestras el ProgressDialog
            progressDialog.show();
            //CODIGO PARA VALIDAR SI EL DISPOSITIVO ESTA CONECTADO A INTERNET
            ConnectivityManager con = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = con.getActiveNetworkInfo();
            if (networkInfo != null && networkInfo.isConnected()) {
                Thread thread = new Thread() {
                    @Override
                    public void run() {
                        final String resultado = actualizarDatosGET(finalId, Integer.parseInt(cantidad.getText().toString()));
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                int r = obtenerDatosJSON(resultado);
                                //Condición para validar si los campos estan llenos
                                if (r > 0) {
                                    progressDialog.dismiss();
                                    Toast.makeText(getContext(), "Se ha añadido la cantidad exitosamente", Toast.LENGTH_SHORT).show();
                                    //Toast.makeText(getContext(), "Mensaje " + resultado, Toast.LENGTH_LONG).show();
                                    producto.setText("");
                                    cantidad.setText("");
                                } else {
                                    Toast.makeText(getContext(), "¡Error al actualizar!", Toast.LENGTH_SHORT).show();
                                    //Toast.makeText(getContext(), "Mensaje " + resultado, Toast.LENGTH_LONG).show();
                                    progressDialog.hide();
                                }
                                progressDialog.hide();
                            }
                        });
                    }
                };
                thread.start();
            } else {
                Toast.makeText(getContext(), "Verifique su conexión a internet", Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
            }
        }
    }

    private void registrarProducto(){
        Intent intent = new Intent(getContext(), ProductoActivity.class);
        startActivity(intent);
    }

    private void verProducto(){
        Uri url_aws = Uri.parse("http://52.67.38.127/hitech/listaProducto.php");
        Uri url_local = Uri.parse("http://192.168.1.6/pruebafinal/listaProducto2.php");
        Intent intent = new Intent(Intent.ACTION_VIEW, url_aws);
        startActivity(intent);
    }

    //===== INICIO CODIGO =====>
    //METODO PARA ACTUALIZAR LOS DATOS AL SERVIDOR LOCAL
    public String actualizarDatosGET(String id ,int cantidad){
        URL url = null;
        String linea = "";
        int respuesta = 0;
        StringBuilder resul = null;
        String url_aws = "http://52.67.38.127/hitech/actualizarCantidad.php?";
        String url_local = "http://192.168.1.6/ServiciosWeb/actualizarCantidad.php?";

        try{
            url = new URL(url_aws + "id=" + id + "&cantidad=" + cantidad);
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

    //METODO PARA OBTENER LOS DATOS DE LOS PRODUCTOS
    public String cargarDatosGET(){
        URL url = null;
        String linea = "";
        int respuesta = 0;
        StringBuilder resul = null;
        String url_local = "http://192.168.1.6/ServiciosWeb/cargarProductos.php?cedula=" + cedula_U;
        String url_aws = "http://52.67.38.127/hitech/cargarProductos.php?cedula=" + cedula_U;

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
    //===== FIN CODIGO DEL SERVIDOR ======

    //ARREGLO SPINNER
    public ArrayList<String> listaProductos(String response){
        ArrayList<String> listado = new ArrayList<String>();
        try{
            JSONArray jsonArray = new JSONArray(response);
            String texto = "";
            for (int i = 0;i<jsonArray.length();i++){
                texto = jsonArray.getJSONObject(i).getString("id_prodtienda") + " - "
                        + jsonArray.getJSONObject(i).getString("producto") + " - "
                        + jsonArray.getJSONObject(i).getString("tienda");
                listado.add(texto);
            }
        }catch (Exception e){}
        return listado;
    }

    //METODO QUE PERMITE CARGAR EL LISTVIEW
    public void cargarLista(ArrayList<String> listaProd) {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this.getContext(), android.R.layout.simple_list_item_1, listaProd);
        listaModelos = (ListView) view.findViewById(R.id.listModelos);
        listaModelos.setAdapter(adapter);
    }

    private void cargarPreferencias(){
        SharedPreferences preferences = getActivity().getSharedPreferences("Preferences", Context.MODE_PRIVATE);
        cedula_U = preferences.getString("cedula","");
    }

    //----FIN CODIGO----->

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
