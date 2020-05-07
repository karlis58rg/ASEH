package mx.hgo.aseh;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class Login extends AppCompatActivity {
    String usuarioL,contraseniaL,cargarInfoRFC;
    EditText txtUserL,txtPassL;
    Button btnIngresarL;
    /*********** SHARE PREFERENCE ************/
    SharedPreferences share;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        cargarDatos();

        txtUserL = findViewById(R.id.txtUsuario);
        txtPassL = findViewById(R.id.txtContrasenia);
        btnIngresarL = findViewById(R.id.btnIngresar);

        btnIngresarL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getRfc();
            }
        });
    }

    /******************GET A LA BD***********************************/
    public void getRfc() {
        usuarioL = txtUserL.getText().toString().toUpperCase();
        contraseniaL = txtPassL.getText().toString().toUpperCase();
        usuarioL = usuarioL.trim();

        if(cargarInfoRFC.equals(usuarioL)){
        final OkHttpClient client = new OkHttpClient();
        final Request request = new Request.Builder()
                .url("http://187.174.102.131:92/api/Usuarios?usuario="+usuarioL+"&contrasenia="+contraseniaL)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                Looper.prepare();
                Toast.makeText(getApplicationContext(),"ERROR AL OBTENER LA INFORMACIÓN, POR FAVOR VERIFIQUE SU CONEXIÓN A INTERNET",Toast.LENGTH_SHORT).show();
                Looper.loop();
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String myResponse = response.body().string();
                    final String resp = myResponse;
                    final String valor = "true";

                    if(resp.equals(valor)){
                        Looper.prepare(); // to be able to make toast
                        Toast.makeText(getApplicationContext(), "ACCESO CORRECTO", Toast.LENGTH_SHORT).show();
                        Intent i = new Intent(Login.this,RegistroActividad.class);
                        startActivity(i);
                        finish();
                        Looper.loop();

                    }else{
                        Looper.prepare(); // to be able to make toast
                        Toast.makeText(getApplicationContext(), "LO SENTIMOS, USUARIO O CONTRASEÑA INCORRECTOS", Toast.LENGTH_SHORT).show();
                        Looper.loop();
                    }
                    Log.i("HERE", resp);
                }

            }

        });
        }else{
            Toast.makeText(getApplicationContext(),"LO SENTIMOS, SU RFC NO COINCIDE CON EL REGISTRADO PREVIAMENTE",Toast.LENGTH_SHORT).show();
        }
    }

    private void cargarDatos(){
        share = getSharedPreferences("main",MODE_PRIVATE);
        cargarInfoRFC = share.getString("RFC","");
    }

}
