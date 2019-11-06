package com.desoft.hi_tech.Fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.desoft.hi_tech.R;

import static android.app.Activity.RESULT_OK;

public class SettingFragment extends Fragment {

    EditText nombre;
    Button editar, guardar;
    View view;
    SharedPreferences preferences;
    ImageView mImage;
    private Uri mImageUri;

    private OnFragmentInteractionListener mListener;

    public SettingFragment() {
        // Required empty public constructor
    }

    public static SettingFragment newInstance(String param1, String param2) {
        SettingFragment fragment = new SettingFragment();
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

        view = inflater.inflate(R.layout.fragment_setting, container, false);
        preferences = getActivity().getSharedPreferences("Preferences", Context.MODE_PRIVATE);

        nombre = view.findViewById(R.id.txtNombre);
        editar = view.findViewById(R.id.btnEditar);
        guardar = view.findViewById(R.id.btnGuardar);
        mImage = view.findViewById(R.id.imagenPerfil);
        nombre.setEnabled(false);
        nombre.setClickable(false);
        nombre.setCursorVisible(false);
        cargarPreferencias();

        editar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editarDatos();
            }
        });

        guardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!nombre.getText().toString().isEmpty()){
                    guardarDatos(nombre.getText().toString());
                } else Toast.makeText(getContext(), "Debe de completar los campos.", Toast.LENGTH_SHORT).show();
            }
        });

        mImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cargarImagen();
            }
        });

        return view;
    }

    private void editarDatos(){
        nombre.setEnabled(true);
        nombre.setClickable(true);
        nombre.setCursorVisible(true);
    }

    private void guardarDatos(String nom){
        SharedPreferences preferences = getActivity().getSharedPreferences("Preferences", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("nombre", nom).apply();
        editor.commit();
        Toast.makeText(getContext(), "Se guardo correctamente.", Toast.LENGTH_SHORT).show();
        nombre.setEnabled(false);
        nombre.setClickable(false);
        nombre.setCursorVisible(false);
    }

    private void cargarImagen(){
        Intent intent;
        if (Build.VERSION.SDK_INT < 19) {
            intent = new Intent(Intent.ACTION_GET_CONTENT);
        } else {
            //intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            //intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        }
        intent.setType("image/*");
        startActivityForResult(Intent.createChooser(intent, "Seleccione la aplicación"), 10);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 10) {
            if (resultCode == RESULT_OK) {
                if (data != null) {
                    mImageUri = data.getData();
                    SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putString("image", String.valueOf(mImageUri));
                    editor.commit();
                    mImage.setImageURI(mImageUri);
                    mImage.invalidate();
                }
            }
        }
    }

    private void cargarPreferencias(){
        SharedPreferences preferences = getActivity().getSharedPreferences("Preferences", Context.MODE_PRIVATE);
        nombre.setText(preferences.getString("nombre",""));
        preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String mImageUri = preferences.getString("image", null);
        mImage.setImageURI(Uri.parse(mImageUri));
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
