package com.desoft.hi_tech;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class ProductoActivity extends AppCompatActivity {

    Button regArticulo, regProducto, regTelefono, regProdTienda;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_producto);

        regArticulo = (Button) findViewById(R.id.btnRegistrarArt);
        regProducto = (Button) findViewById(R.id.btnRegistrarProd);
        regTelefono = (Button) findViewById(R.id.btnRegistrarTelefono);
        regProdTienda = (Button) findViewById(R.id.btnRegistrarProdTienda);

        regArticulo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registrarArticulo();
            }
        });

        regProducto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registrarProducto();
            }
        });

        regTelefono.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registrarTelefono();
            }
        });

        regProdTienda.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "Opción aún no disponible", Toast.LENGTH_SHORT).show();
                //registrarProductoTienda();
            }
        });
    }

    private void registrarArticulo(){
        Intent intent = new Intent(getApplicationContext(), RegistrarArticuloActivity.class);
        startActivity(intent);
    }

    private void registrarProducto(){
        Intent intent = new Intent(getApplicationContext(), RegistrarProductoActivity.class);
        startActivity(intent);
    }

    private void registrarTelefono(){
        Intent intent = new Intent(getApplicationContext(), RegistrarTelefonoActivity.class);
        startActivity(intent);
    }

    private void registrarProductoTienda(){
        Intent intent = new Intent(getApplicationContext(), RegistrarProductoTiendaActivity.class);
        startActivity(intent);
    }
}
