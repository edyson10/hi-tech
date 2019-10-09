package com.desoft.hi_tech.Fragments;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import com.desoft.hi_tech.ListarSalidasActivity;
import com.desoft.hi_tech.R;

import java.util.Calendar;


public class ReporteFragment extends Fragment {

    View vista;
    TextView fecha;
    Calendar mCurrentDate;
    int dia, mes, anio;
    Button reporteDia, reporteMes, salida, utilidad;
    String recuperado = "";
    String tienda;
    private ProgressDialog progressDialog;

    private OnFragmentInteractionListener mListener;

    public ReporteFragment() {
        // Required empty public constructor
    }

    public static ReporteFragment newInstance(String param1, String param2) {
        ReporteFragment fragment = new ReporteFragment();
        Bundle args = new Bundle();

        fragment.setArguments(args);
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
        vista = inflater.inflate(R.layout.fragment_reporte, container, false);
        progressDialog = new ProgressDialog(getContext());
        cargarPreferencias();

        reporteDia = vista.findViewById(R.id.btnReporteDia);
        reporteMes = vista.findViewById(R.id.btnReporteMes);
        utilidad = vista.findViewById(R.id.btnUtilidad);
        salida = vista.findViewById(R.id.btnRealizarSalida);

        //CODIGO FECHA DE ENTREGA ESTIPULADA
        fecha = (TextView) vista.findViewById(R.id.fechaReporte);
        mCurrentDate = Calendar.getInstance();
        dia = mCurrentDate.get(Calendar.DAY_OF_MONTH);
        mes = mCurrentDate.get(Calendar.MONTH);
        anio = mCurrentDate.get(Calendar.YEAR);
        fecha.setText(anio + "-" + (mes + 1) + "-" + dia);
        fecha.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker , int year , int monthOfYear, int dayOfMonth) {
                        monthOfYear = monthOfYear + 1;
                        fecha.setText(year+"-"+monthOfYear+"-"+dayOfMonth);
                    }
                }, anio, mes, dia);
                datePickerDialog.show();
            }
        });

        reporteDia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reporteDiario();
            }
        });

        reporteMes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reporteMensual();
            }
        });

        utilidad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                verUtilidad();
            }
        });

        salida.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vistaSalida();
            }
        });

        return vista;
    }

    private void reporteDiario(){
        //CODIGO PARA VALIDAR SI EL DISPOSITIVO ESTA CONECTADO A INTERNET
        //REPORTE LOCAL PALACIO
        ConnectivityManager con = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = con.getActiveNetworkInfo();
        if(networkInfo != null && networkInfo.isConnected()) {
            reporteDia = (Button) vista.findViewById(R.id.btnReporteDia);
            reporteDia.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Uri url_aws = Uri.parse("http://52.67.38.127/hitech/indexpordia.php?fecha=" + fecha.getText().toString());
                    Intent intent = new Intent(Intent.ACTION_VIEW, url_aws);
                    startActivity(intent);
                }
            });
        } else{
            Toast.makeText(getContext(), "Verifique su conexión a internet",Toast.LENGTH_SHORT).show();
            progressDialog.dismiss();
        }
    }

    private void reporteMensual(){
        ConnectivityManager con = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = con.getActiveNetworkInfo();
        if(networkInfo != null && networkInfo.isConnected()) {
            reporteMes.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Uri url_aws = Uri.parse("http://52.67.38.127/hitech/indexpormes.php");
                    Intent intent = new Intent(Intent.ACTION_VIEW, url_aws);
                    startActivity(intent);
                }
            });
        }else {
            Toast.makeText(getContext(), "Verifique su conexión a internet",Toast.LENGTH_SHORT).show();
            progressDialog.dismiss();
        }
    }

    private void verUtilidad(){
        /*
        Intent intent = new Intent(getContext(), UtilidadActivity.class);
        startActivity(intent);
         */
    }

    private void vistaSalida(){
        Intent intent = new Intent(getContext(), ListarSalidasActivity.class);
        startActivity(intent);
    }

    private void cargarPreferencias(){
        SharedPreferences preferences = getActivity().getSharedPreferences("Preferences", Context.MODE_PRIVATE);
        tienda = preferences.getString("tienda","");
    }

    private void cargarTitulo(){
        if(tienda.equalsIgnoreCase("1")) {
            utilidad.setText("UTILIDAD ALEJANDRIA");
            utilidad.setTextColor(Color.RED);
        }
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    /*
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
    */

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
