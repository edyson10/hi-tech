package com.desoft.hi_tech.Fragments;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.desoft.hi_tech.EliminarSalidaActivity;
import com.desoft.hi_tech.EliminarTelefonoActivity;
import com.desoft.hi_tech.EliminarVentaActivity;
import com.desoft.hi_tech.R;

public class EliminarFragment extends Fragment {

    View view;
    ImageView venta, salida, telefono;
    private OnFragmentInteractionListener mListener;

    public EliminarFragment() {
        // Required empty public constructor
    }

    public static EliminarFragment newInstance(String param1, String param2) {
        EliminarFragment fragment = new EliminarFragment();
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
        view = inflater.inflate(R.layout.fragment_eliminar, container, false);
        venta = (ImageView) view.findViewById(R.id.imagenVentas);
        salida = (ImageView) view.findViewById(R.id.imagenSalida);
        telefono = (ImageView) view.findViewById(R.id.imagenTelefono);

        venta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vistaEliminarVenta();
            }
        });

        salida.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vistaEliminarSalida();
            }
        });

        telefono.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vistaEliminarTelefono();
            }
        });
        return view;
    }

    private void vistaEliminarVenta(){
        Intent intent = new Intent(getContext(), EliminarVentaActivity.class);
        startActivity(intent);
    }

    private void vistaEliminarSalida(){
        Intent intent = new Intent(getContext(), EliminarSalidaActivity.class);
        startActivity(intent);
    }

    private void vistaEliminarTelefono(){
        Intent intent = new Intent(getContext(), EliminarTelefonoActivity.class);
        startActivity(intent);
    }

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
