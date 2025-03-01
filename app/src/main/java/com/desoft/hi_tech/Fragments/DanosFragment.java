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

import com.desoft.hi_tech.ListarDanoActivity;
import com.desoft.hi_tech.R;

import org.json.JSONArray;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class DanosFragment extends Fragment {

    View view;
    EditText cantidad, producto, observacion;
    Button baja, listarDanos;
    String cedula_U;
    private ListView listaProductos;
    ArrayAdapter<String> adapter;
    private ProgressDialog progressDialog;

    private OnFragmentInteractionListener mListener;

    public DanosFragment() {
        // Required empty public constructor
    }

    public static DanosFragment newInstance(String param1, String param2) {
        DanosFragment fragment = new DanosFragment();
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
        view = inflater.inflate(R.layout.fragment_danos, container, false);
        progressDialog = new ProgressDialog(getContext());
        cargarPreferencias();
        producto = (EditText) view.findViewById(R.id.txtProductoDano);
        cantidad = (EditText) view.findViewById(R.id.txtCantidadDano);
        observacion = (EditText) view.findViewById(R.id.txtObservacionDano);
        baja = (Button) view.findViewById(R.id.btnDano);
        listarDanos = (Button) view.findViewById(R.id.btnListarDanos);
        listaProductos = (ListView) view.findViewById(R.id.listaModelosDanos);

        //CODIGO PARA VALIDAR SI EL DISPOSITIVO ESTA CONECTADO A INTERNET
        ConnectivityManager con = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = con.getActiveNetworkInfo();
        if(networkInfo != null && networkInfo.isConnected()) {
            Thread thread = new Thread() {
                @Override
                public void run() {
                    final String resultado = obtenerDatosGET();
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            cargarLista(listaProductos(resultado));
                            adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, listaProductos(resultado));
                            listaProductos.setAdapter(adapter);
                            //Toast.makeText(getContext(),cedula_U, Toast.LENGTH_SHORT).show();
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
                    listaProductos.setClickable(true);
                    listaProductos.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            Object o = listaProductos.getItemAtPosition(position);
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

        baja.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registrarDano();
            }
        });

        listarDanos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listarDano();
            }
        });

        return  view;
    }

    private void registrarDano(){
        if (producto.getText().toString().isEmpty() || cantidad.getText().toString().isEmpty() || observacion.getText().toString().isEmpty()) {
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

            final String finalArt = art;
            final String finalMod = mod;
            final String finalId = id;
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
                        final String resultado = enviarDatosGET(cedula_U, finalId, finalArt, finalMod, Integer.parseInt(cantidad.getText().toString()),
                                observacion.getText().toString());
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                int r = obtenerDatosJSON(resultado);
                                //Condición para validar si los campos estan llenos
                                if (r > 0) {
                                    progressDialog.dismiss();
                                    producto.setText("");
                                    observacion.setText("");
                                    cantidad.setText("");
                                    Toast.makeText(getContext(), "Se ha registrado la salida del producto exitosamente", Toast.LENGTH_SHORT).show();
                                    //Toast.makeText(getContext(), finalCedulaEmpleado, Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(getContext(), "¡Algo malo ocurrio!", Toast.LENGTH_SHORT).show();
                                    //Toast.makeText(getContext(), resultado, Toast.LENGTH_LONG).show();
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

    private void listarDano() {
        Intent intent = new Intent(getContext(), ListarDanoActivity.class);
        startActivity(intent);
    }

    //METODO PARA ENVIAR LOS DATOS AL SERVIDOR LOCAL
    public String enviarDatosGET(String cedula, String id, String marca, String modelo, int cantidad, String observacion){
        URL url = null;
        String linea = "";
        int respuesta = 0;
        StringBuilder resul = null;
        String url_aws = "http://52.67.38.127/hitech/salidaProducto.php";
        String url_local = "http://18.228.235.94/wifix/ServiciosWeb/salidaProducto.php";
        String mod = modelo.replace(" ", "%20");
        String mar = marca.replace(" ", "%20");
        String obs = observacion.replace(" ", "%20");

        try{
            //LA IP SE CAMBIA CON RESPECTO O EN BASE A LA MAQUINA EN LA CUAL SE ESTA EJECUTANDO YA QUE NO TODAS LAS IP SON LAS MISMAS EN LOS EQUIPOS
            url = new URL(url_aws + "?empleado=" + cedula + "&id" + id + "&marca=" + mar + "&modelo=" + mod
                    + "&cantidad=" + cantidad + "&observacion=" + obs);
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
    private String obtenerDatosGET(){
        URL url = null;
        String linea = "";
        int respuesta = 0;
        StringBuilder resul = null;
        String url_aws = "http://52.67.38.127/hitech/cargarProductos.php?cedula=" + cedula_U;
        String url_local = "http://192.168.1.6/ServiciosWeb/cargarProductos.php?cedula=" + cedula_U;

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

    //METODO QUE PERMITE OBTENER EL JSON Y RECORRERLO Y SABER SI RECIBIO O NO DATOS
    private int obtenerDatosJSON(String response){
        int res = 0;
        try {
            JSONArray jsonArray = new JSONArray(response);
            if (jsonArray.length() > 0 ){
                res = 1;
            }
        }catch (Exception e){}
        return res;
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
                texto = jsonArray.getJSONObject(i).getString("id_prodtienda") + " - "
                        + jsonArray.getJSONObject(i).getString("nombre") + " - "
                        + jsonArray.getJSONObject(i).getString("modelo");
                listado.add(texto);
            }
        }catch (Exception e){}
        return listado;
    }

    /* METODO QUE PERMITE CARGAR EL ARRAYLIST DE
     *  PRODUCTOS EN EL LISTVIEW
     * */
    public void cargarLista(ArrayList<String> listaProd) {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this.getContext(), android.R.layout.simple_list_item_1, listaProd);
        listaProductos = (ListView) view.findViewById(R.id.listaModelosDanos);
        listaProductos.setAdapter(adapter);
    }

    private void cargarPreferencias(){
        SharedPreferences preferences = getActivity().getSharedPreferences("Preferences", Context.MODE_PRIVATE);
        cedula_U = preferences.getString("cedula","");
    }

    // ===== FIN DEL CODIGO =====

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
