package com.desoft.hi_tech.Clases;

import android.widget.Toast;

import java.util.Date;

import static com.desoft.hi_tech.LoginActivity.objeto;
import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;

public class TareaAutomatica implements Runnable {

    private Boolean correr = TRUE;

    public TareaAutomatica(){

    }

    public boolean isCorrer() {
        return correr;
    }

    public void setCorrer(boolean correr) {
        this.correr = correr;
    }

    @Override
    public void run() {
        try {
            while (correr){
                Thread.sleep(5000);
                Toast.makeText(objeto, "Prueba", Toast.LENGTH_SHORT).show();
                System.out.println("ejecutando hilo---->");
            }
        }catch (Exception e){
            System.out.println("NO ejecutando hilo---->");
        }
    }

    public void stop(){
        correr = FALSE;
    }


}
