package mx.hgo.aseh;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;

public class Splash extends AppCompatActivity {
    String cargarInfoRFC;
    int cargarInfoStatus;
    /*********** SHARE PREFERENCE ************/
    SharedPreferences share;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        cargarDatos();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if(cargarInfoRFC.isEmpty() != false){
                    Intent i = new Intent(Splash.this,Registro.class);
                    startActivity(i);
                    finish();
                }else if(cargarInfoStatus != 0) {
                    Intent i = new Intent(Splash.this,GuiaDeUso.class);
                    startActivity(i);
                    finish();
                }else {
                    Intent i = new Intent(Splash.this,GuiaDeUso.class);
                    startActivity(i);
                    finish();
                }
            }
        },2000);
    }

    public void cargarDatos(){
      share = getSharedPreferences("main",MODE_PRIVATE);
      cargarInfoRFC = share.getString("RFC","");
      cargarInfoStatus = share.getInt("STATUS",0);
    }
}
