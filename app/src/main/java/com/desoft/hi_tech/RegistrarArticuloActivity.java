package com.desoft.hi_tech;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class RegistrarArticuloActivity extends AppCompatActivity {

    EditText articulo;
    Button btnRegArticulo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registrar_articulo);

        articulo = (EditText) findViewById(R.id.txtRegistrarArticulo);
        btnRegArticulo = (Button) findViewById(R.id.btnRegistrarArticulo);

        btnRegArticulo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registrarArticulo();
            }
        });
    }

    private void registrarArticulo(){

    }
}
