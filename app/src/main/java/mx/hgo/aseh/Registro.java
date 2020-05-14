package mx.hgo.aseh;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import java.io.IOException;
import java.util.ArrayList;

import mx.hgo.aseh.SqLite.DataHelper;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class Registro extends AppCompatActivity {
    Button btnGuardarR;
    EditText txtNombreR,txtApaternoR,txtAmaternoR,txtTelefonoR;
    String nombre,apaterno,amaterno,telefono,cargo,token;
    String respRfc;
    Spinner spinCargoR;
    /*********** NOTIFICACIÓN ****************/
    private static final String CHANNEL_ID ="NOTIFICACIÓN" ;
    private NotificationManagerCompat notificationManager;
    private NotificationCompat.Builder builder;
    /*********** SHARE PREFERENCE ************/
    SharedPreferences share;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);

        txtNombreR = findViewById(R.id.txtNombre);
        txtApaternoR = findViewById(R.id.txtApaterno);
        txtAmaternoR = findViewById(R.id.txtAmaterno);
        txtTelefonoR = findViewById(R.id.txtTelefono);
        spinCargoR = findViewById(R.id.spinCargo);
        btnGuardarR = findViewById(R.id.btnGuardar);
        solicitarPermisosCamera();
        /************ CREACIÓN DEL CANAL *****************/
        createNotificationChannel();
        /************* TOKEN FIREBASE *******************/
        getToken();
        spinner();
        btnGuardarR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(txtNombreR.getText().toString().isEmpty()){
                    Toast.makeText(getApplicationContext(),"SU NOMBRE ES NECESARIO PARA VERIFICAR SU IDENTIDAD",Toast.LENGTH_SHORT).show();
                }else if(txtApaternoR.getText().toString().isEmpty()) {
                    Toast.makeText(getApplicationContext(), " SU APELLIDO PATERNO ES NECESARIO PARA VERIFICAR SU IDENTIDAD.", Toast.LENGTH_LONG).show();
                }else if (txtAmaternoR.getText().toString().isEmpty()){
                    Toast.makeText(getApplicationContext(), " SU APELLIDO MATERNO ES NECESARIO PARA VERIFICAR SU IDENTIDAD.", Toast.LENGTH_LONG).show();
                }else if(txtTelefonoR.getText().length() < 10){
                    Toast.makeText(getApplicationContext(), " SU NÚMERO TELEFÓNICO NO PUEDE SER MENOR A 10 DIGITOS.", Toast.LENGTH_LONG).show();
                }else if(txtNombreR.getText().length() < 3){
                    Toast.makeText(getApplicationContext(), " SU NOMBRE NO PUEDE SER MENOR A TRES LETRAS.", Toast.LENGTH_LONG).show();
                }else if(txtApaternoR.getText().length() < 3){
                    Toast.makeText(getApplicationContext(), " SU APELLIDO PATERNO NO PUEDE SER MENOR A TRES LETRAS.", Toast.LENGTH_LONG).show();
                }else if(txtAmaternoR.getText().length() < 3){
                    Toast.makeText(getApplicationContext(), " SU APELLIDO MATERNO NO PUEDE SER MENOR A TRES LETRAS.", Toast.LENGTH_LONG).show();
                }else {
                    Toast.makeText(getApplicationContext(), "UN MOMENTO POR FAVOR, ESTAMOS PROCESANDO SU SOLICITUD", Toast.LENGTH_SHORT).show();
                    getRfc();
                }
            }
        });
    }
    /******************GET A LA BD***********************************/
    public void getRfc() {
        nombre = txtNombreR.getText().toString().toUpperCase();
        apaterno = txtApaternoR.getText().toString().toUpperCase();
        amaterno = txtAmaternoR.getText().toString().toUpperCase();

        nombre = nombre.trim();
        apaterno = apaterno.trim();
        amaterno = amaterno.trim();

        final OkHttpClient client = new OkHttpClient();
        final Request request = new Request.Builder()
                .url("http://187.174.102.131:92/api/Usuarios?nombres="+nombre+"&apaterno="+apaterno+"&amaterno="+amaterno+"")
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
                    myResponse = myResponse.replace('"',' ');
                    String resp = myResponse;
                    String valorRfc = " USTED NO ESTA REGISTRADO EN SU COORPORACIÓN ";
                    if(resp.equals(valorRfc)){
                        Looper.prepare(); // to be able to make toast
                        Toast.makeText(getApplicationContext(), "LO SENTIMOS " + resp, Toast.LENGTH_SHORT).show();
                        Looper.loop();

                    }else{
                        resp = resp.trim();
                        respRfc = resp;
                        Looper.prepare(); // to be able to make toast
                        Toast.makeText(getApplicationContext(), "ESTAMOS PROCESANDO SU SOLICITUD", Toast.LENGTH_SHORT).show();
                        guardarDatos();
                        getUsuarioRegistrado();
                        Looper.loop();
                    }
                    Log.i("HERE", resp);
                }

            }

        });
    }

    /******************GET A LA BD***********************************/
    public void getUsuarioRegistrado() {
        nombre = txtNombreR.getText().toString().toUpperCase();
        apaterno = txtApaternoR.getText().toString().toUpperCase();
        amaterno = txtAmaternoR.getText().toString().toUpperCase();
        telefono = txtTelefonoR.getText().toString();

        nombre = nombre.trim();
        apaterno = apaterno.trim();
        amaterno = amaterno.trim();

        final OkHttpClient client = new OkHttpClient();
        final Request request = new Request.Builder()
                .url("http://187.174.102.131:92/api/Usuarios?nombres="+nombre+"&apaterno="+apaterno+"&amaterno="+amaterno+""+"&telefono="+telefono+"&token="+token)
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
                    myResponse = myResponse.replace('"',' ');
                    String resp = myResponse;
                    String valorRfc = "true";
                    if(resp.equals(valorRfc)){
                        Looper.prepare(); // to be able to make toast
                        sendNotification();
                        notificationManager.notify(1, builder.build());
                        txtNombreR.setText("");
                        txtApaternoR.setText("");
                        txtAmaternoR.setText("");
                        txtTelefonoR.setText("");
                        Toast.makeText(getApplicationContext(), "USTED YA SE ENCUENTRA REGISTRADO", Toast.LENGTH_SHORT).show();
                        Intent i = new Intent(Registro.this,GuiaDeUso.class);
                        startActivity(i);
                        finish();
                        Looper.loop();

                    }else{
                        Looper.prepare(); // to be able to make toast
                        Toast.makeText(getApplicationContext(), "ESTAMOS PROCESANDO SU SOLICITUD", Toast.LENGTH_SHORT).show();
                        insertRegistro();
                        Looper.loop();
                    }
                    Log.i("HERE", resp);
                }
            }

        });
    }

    /******************INSERT A LA BD***********************************/
    private void insertRegistro(){
        nombre = txtNombreR.getText().toString().toUpperCase();
        apaterno = txtApaternoR.getText().toString().toUpperCase();
        amaterno = txtAmaternoR.getText().toString().toUpperCase();
        telefono = txtTelefonoR.getText().toString();
        cargo = (String) spinCargoR.getSelectedItem();

        nombre = nombre.trim();
        apaterno = apaterno.trim();
        amaterno = amaterno.trim();

        OkHttpClient client = new OkHttpClient();
        RequestBody body = new FormBody.Builder()
                .add("Nombres",nombre)
                .add("ApellidoPat",apaterno)
                .add("ApellidoMat",amaterno)
                .add("Cargo",cargo)
                .add("Telefono",telefono)
                .add("Token",token)
                .add("RFC",respRfc)
                .build();
        Request request = new Request.Builder()
                .url("http://187.174.102.131:92/api/Usuarios/")
                .post(body)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                Looper.prepare();
                Toast.makeText(getApplicationContext(),"ERROR AL GUARDAR SU INFORMACIÓN, POR FAVOR VERIFIQUE SU CONEXIÓN A INTERNET",Toast.LENGTH_SHORT).show();
                Looper.loop();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if(response.isSuccessful()){
                    final String myResponse = response.body().toString();
                    Registro.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            sendNotification();
                            notificationManager.notify(1, builder.build());
                            Toast.makeText(getApplicationContext(),"SU REGISTRO SE HA GUARDADO CORRECTAMENTE",Toast.LENGTH_SHORT).show();
                            txtNombreR.setText("");
                            txtApaternoR.setText("");
                            txtAmaternoR.setText("");
                            txtTelefonoR.setText("");
                            Intent i = new Intent(Registro.this,GuiaDeUso.class);
                            startActivity(i);
                            finish();
                    }
                  });
                }

            }
        });
    }

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.channel_name);
            String description = getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private void sendNotification(){
        /******************* GENERA LA NOTIFICACIÓN ****************************************/
        // Create an explicit intent for an Activity in your app
        Intent intent = new Intent(this, Eventos.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);

        builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle("REGISTRO EXITOSO")
                .setContentText("USTED YA PUEDE HACER USO DE ESTA APLICACIÓN")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                // Set the intent that will fire when the user taps the notification
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        notificationManager = NotificationManagerCompat.from(this);
    }

    /****************** OBTIENE EL TOKEN DE FIREBASE ***********************************/
    private void getToken()
    {
        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (!task.isSuccessful()) {
                            token = "NO SE PUDO OBTENER EL TOKEN DEL DISPOSITIVO";
                            Log.i("HEY!", "NO SE PUDO OBTENER EL TOKEN DEL DISPOSITIVO", task.getException());
                            return;
                        }
                        // Get new Instance ID token
                        token = task.getResult().getToken();
                        // Log and toast
                        //Toast.makeText(Registro.this, token, Toast.LENGTH_SHORT).show();
                        Log.i("HEY!",token);
                    }
                });
    }


    private void spinner(){
        DataHelper dataHelper = new DataHelper(this);
        dataHelper.insertCargo("COMISARIO GENERAL");
        dataHelper.insertCargo("COORDINADOR DE SEGURIDAD REGIONAL");
        dataHelper.insertCargo("DIRECTOR DE AREA");
        dataHelper.insertCargo("DIRECTOR DEL H. CUERPO DE BOMBEROS");
        dataHelper.insertCargo("ESCOLTA");
        dataHelper.insertCargo("ESCOLTA A");
        dataHelper.insertCargo("ESCOLTA B");
        dataHelper.insertCargo("INSPECTOR");
        dataHelper.insertCargo("OFICIAL");
        dataHelper.insertCargo("POLICIA");
        dataHelper.insertCargo("POLICIA PRIMERO");
        dataHelper.insertCargo("POLICIA SEGUNDO");
        dataHelper.insertCargo("POLICIA TERCERO");
        dataHelper.insertCargo("SUB DIRECTOR DE AREA");
        dataHelper.insertCargo("SUBINSPECTOR");
        dataHelper.insertCargo("SUBOFICIAL");
        dataHelper.insertCargo("SECRETARIO SSPH");

        ArrayList<String> list = dataHelper.getAllCargo();
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,R.layout.spinner_layout,R.id.txt,list);
        spinCargoR.setAdapter(adapter);
    }

    private void guardarDatos(){
        share = getSharedPreferences("main",MODE_PRIVATE);
        editor = share.edit();
        editor.putString("RFC",respRfc);
        editor.commit();
    }

    //***************************** SE OPTIENEN TODOS LOS PERMISOS AL INICIAR LA APLICACIÓN *********************************//
    public void solicitarPermisosCamera() {
        if (ContextCompat.checkSelfPermission(Registro.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(Registro.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(Registro.this, new String[]{Manifest.permission.CAMERA, Manifest.permission.ACCESS_FINE_LOCATION}, 1000);
        }
    }

}
