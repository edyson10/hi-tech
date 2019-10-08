package com.desoft.hi_tech;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.support.v4.view.GravityCompat;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.MenuItem;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.widget.Toast;

import com.desoft.hi_tech.Fragments.AcercaFragment;
import com.desoft.hi_tech.Fragments.BodegaFragment;
import com.desoft.hi_tech.Fragments.DanosFragment;
import com.desoft.hi_tech.Fragments.EditarFragment;
import com.desoft.hi_tech.Fragments.EliminarFragment;
import com.desoft.hi_tech.Fragments.ProductoFragment;
import com.desoft.hi_tech.Fragments.ReporteFragment;
import com.desoft.hi_tech.Fragments.SettingFragment;
import com.desoft.hi_tech.Fragments.VentaFragment;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, VentaFragment.OnFragmentInteractionListener,
        ReporteFragment.OnFragmentInteractionListener, ProductoFragment.OnFragmentInteractionListener,
        BodegaFragment.OnFragmentInteractionListener, EditarFragment.OnFragmentInteractionListener,
        DanosFragment.OnFragmentInteractionListener, EliminarFragment.OnFragmentInteractionListener,
        SettingFragment.OnFragmentInteractionListener, AcercaFragment.OnFragmentInteractionListener {

    private SharedPreferences preferences;
    private ProgressDialog progressDialog;
    View view = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        preferences = getSharedPreferences("Preferences", Context.MODE_PRIVATE);

        Fragment fragment = new VentaFragment();
        getSupportFragmentManager().beginTransaction().add(R.id.content_main, fragment).commit();
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        progressDialog = new ProgressDialog(MainActivity.this);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        LayoutInflater imagen_alert = LayoutInflater.from(MainActivity.this);
        // Se añade el logo de hitech para los alertidalog
        // view = imagen_alert.inflate(R.layout.imagen_alert, null);
    }

    @Override
    public void onBackPressed() {
        alertOneButton();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.logout) {
            alertOneButton();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        Fragment miFragment = null;
        boolean fragmentSeleccionado = false;

        if (id == R.id.nav_venta) {
            miFragment = new VentaFragment();
            fragmentSeleccionado = true;
        } else if (id == R.id.nav_reporte) {
            miFragment = new ReporteFragment();
            fragmentSeleccionado = true;
        } else if (id == R.id.nav_producto) {
            miFragment = new ProductoFragment();
            fragmentSeleccionado = true;
        } else if (id == R.id.nav_bodega) {
            miFragment = new BodegaFragment();
            fragmentSeleccionado = true;
        } else if (id == R.id.nav_editar) {
            miFragment = new EditarFragment();
            fragmentSeleccionado = true;
        } else if (id == R.id.nav_eliminar) {
            miFragment = new EliminarFragment();
            fragmentSeleccionado = true;
        } else if (id == R.id.nav_danos) {
            miFragment = new DanosFragment();
            fragmentSeleccionado = true;
        } else if (id == R.id.nav_settings) {
            Toast.makeText(getApplicationContext(), "¡Opcion aun no disponible!", Toast.LENGTH_SHORT).show();
            /*
            miFragment = new SettingFragment();
            fragmentSeleccionado = true;
            */
        } else if (id == R.id.nav_acerca) {
            miFragment = new AcercaFragment();
            fragmentSeleccionado = true;
        }

        //CODIGO AÑADIDO
        if (fragmentSeleccionado){
            //COLOCAR EN LAS OPCIONES SELECIONADAS LOS FRAGMENT QUE SELECCIONE EL USUARIO
            getSupportFragmentManager().beginTransaction().replace(R.id.content_main, miFragment).commit();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void alertOneButton() {
        new AlertDialog.Builder(MainActivity.this)
                //.setIcon(R.drawable.icono)
                .setTitle("Sesión")
                .setMessage("¿Desea cerrar sesión?")
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                })
                .setPositiveButton("Si", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                        removePreferences();
                        //startActivity(intent);
                        Toast.makeText(getApplicationContext(), "Se ha cerrado sesión", Toast.LENGTH_SHORT).show();
                    }
                }).show();
    }

    private void removePreferences() {
        preferences.edit().clear().apply();
        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
