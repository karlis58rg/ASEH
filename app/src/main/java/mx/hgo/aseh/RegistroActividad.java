package mx.hgo.aseh;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;
import android.util.SparseArray;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import pl.droidsonroids.gif.GifImageView;


public class RegistroActividad extends AppCompatActivity {


    ImageView btnHome;
    Button btnCapturaTextoRA,btnTerminarTurnoRA;
    String placa,hora,fecha,cargarInfoRFC,placaTemp,mensaje1,mensaje2,token;
    int cargarInfoStatus;
    String status = "1";
    int bandera;
    GifImageView gifRojo;
    /*********** SHARE PREFERENCE ************/
    SharedPreferences share;
    SharedPreferences.Editor editor;

    /************* OCR **************/
    SurfaceView mCameraView;
    TextView mTextView,latitud,longitud,direccion;
    CameraSource mCameraSource;
    private static final String TAG = "RegistroActividad";
    private static final int requestPermissionID = 101;
    private Activity activity;
    int acceso = 0;
    AlertDialog alert = null;
    private static final int CODIGO_SOLICITUD_PERMISO = 1;
    private LocationManager locationManager;
    private Context context;
    public static String direc,municipio,estado;
    public  static String direccionTurno;
    public static Double lat,lon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro_actividad);
        cargarDatos();
        mCameraView = findViewById(R.id.scanPlaca);
        mTextView = findViewById(R.id.lblPlacaScan);
        btnCapturaTextoRA = findViewById(R.id.btnCapturarTexto);
        btnTerminarTurnoRA = findViewById(R.id.btnTerminarTurno);
        gifRojo = findViewById(R.id.gifSirena);
        latitud = findViewById(R.id.lblLatitud);
        longitud = findViewById(R.id.lblLongitud);
        direccion = findViewById(R.id.lblDireccion);
        btnHome = findViewById(R.id.imgHeader);

        context = getApplicationContext();
        activity = this;
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        if(acceso == 0){
            solicitarPermisoLocalizacion();
        } else {
            Toast.makeText(getApplicationContext()," **EL GPS** ES OBLIGATORIO PARA EL CORRECTO FUNCIONAMIENTO DEL APLICATIVO",Toast.LENGTH_LONG).show();
        }
        locationStart();
        getToken();

        if(cargarInfoStatus == 1){
            gifRojo.setVisibility(View.VISIBLE);
            btnTerminarTurnoRA.setVisibility(View.VISIBLE);
            mCameraView.setVisibility(View.INVISIBLE);
            mTextView.setVisibility(View.INVISIBLE);
            btnCapturaTextoRA.setVisibility(View.INVISIBLE);
        }else{
            startCameraSource();
            gifRojo.setVisibility(View.INVISIBLE);
            btnTerminarTurnoRA.setVisibility(View.INVISIBLE);
        }

        btnCapturaTextoRA.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                insertTempPlaca();
            }
        });
        btnTerminarTurnoRA.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getTerminaTurno();
            }
        });
        btnHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(RegistroActividad.this,GuiaDeUso.class);
                startActivity(i);
            }
        });
    }
    /******************INSERT A LA BD***********************************/
    private void insertTempPlaca(){
        placa = mTextView.getText().toString().toUpperCase();
        placa = placa.replace('-',' '); //REEMPLAZA LOS CARACTERES POR NUEVOS
        placa = placa.trim();//QUITA LOS ESPACIOS AL INICIO Y AL FINAL DE LA ORACIÒN
        placa = placa.replaceAll("\\s",""); //ELIMINA ESPACIOS,TABULADORES,RETORNOS

        OkHttpClient client = new OkHttpClient();
        RequestBody body = new FormBody.Builder()
                .add("RFC",cargarInfoRFC)
                .add("Placa",placa)
                .build();
        Request request = new Request.Builder()
                .url("http://187.174.102.131:92/api/TempPlaca/")
                .post(body)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                Looper.prepare();
                Toast.makeText(getApplicationContext(),"ERROR AL GUARDAR SU REGISTRO, POR FAVOR, VERIFIQUE SU CONEXIÓN A INTERNET",Toast.LENGTH_SHORT).show();
                Looper.loop();
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                if(response.isSuccessful()){
                    String myResponse = response.body().string();
                    myResponse = myResponse.replace('"',' ');
                    String resp = myResponse;
                    String valorRfc = "false";
                    if(resp.equals(valorRfc)){
                        Looper.prepare(); // to be able to make toast
                        Toast.makeText(getApplicationContext(), "LO SENTIMOS, ESTA PLACA NO SE ENCUENTRA EN LAS UNIDADES REGISTRADAS ", Toast.LENGTH_SHORT).show();
                        Looper.loop();
                    }else{
                        Looper.prepare(); // to be able to make toast
                        Toast.makeText(getApplicationContext(), "UN MOMENTO POR FAVOR", Toast.LENGTH_SHORT).show();
                        getPlacaTemp();
                        Looper.loop();
                    }
                    Log.i("HERE", resp);
                }
            }
        });
    }

    /******************GET A LA BD***********************************/
    public void getPlacaTemp() {
        final OkHttpClient client = new OkHttpClient();
        final Request request = new Request.Builder()
                .url("http://187.174.102.131:92/api/TempPlaca?rfc=" + cargarInfoRFC)
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
                    Looper.prepare(); // to be able to make toast
                    Toast.makeText(getApplicationContext(), "UN MOMENTO POR FAVOR", Toast.LENGTH_SHORT).show();
                    placaTemp = resp;
                    placaTemp = placaTemp.trim();
                    insertRegistroActividad();
                    Looper.loop();
                }
            }

        });
    }
    /******************INSERT A LA BD***********************************/
    private void insertRegistroActividad(){
        //*************** FECHA **********************//
        Date date = new Date();
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
        fecha = dateFormat.format(date);

        //*************** HORA **********************//
        Date time = new Date();
        DateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
        hora = timeFormat.format(time);


        OkHttpClient client = new OkHttpClient();
        RequestBody body = new FormBody.Builder()
                .add("Placa",placaTemp)
                .add("FechaIngreso",fecha)
                .add("HoraIngreso",hora)
                .add("IdStatus", status)
                .add("RFC",cargarInfoRFC)
                .add("LatitudInicio",lat.toString())
                .add("LongitudInicio", lon.toString())
                .add("DireccionInicio",direccionTurno)
                .build();
        Request request = new Request.Builder()
                .url("http://187.174.102.131:92/api/Registros/")
                .post(body)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                Looper.prepare();
                Toast.makeText(getApplicationContext(),"ERROR AL GUARDAR SU REGISTRO, POR FAVOR, VERIFIQUE SU CONEXIÓN A INTERNET",Toast.LENGTH_SHORT).show();
                Looper.loop();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if(response.isSuccessful()){
                    String myResponse = response.body().toString();
                    final String resp = myResponse;
                    RegistroActividad.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            System.out.println(resp);
                            bandera = 1;
                            guardarDatos();
                            Toast.makeText(getApplicationContext(),"SU TURNO A COMENZADO",Toast.LENGTH_SHORT).show();
                            mCameraView.setVisibility(View.INVISIBLE);
                            mTextView.setVisibility(View.INVISIBLE);
                            btnCapturaTextoRA.setVisibility(View.INVISIBLE);
                            gifRojo.setVisibility(View.VISIBLE);
                            btnTerminarTurnoRA.setVisibility(View.VISIBLE);
                        }
                    });
                }

            }
        });
    }

    /******************GET A LA BD***********************************/
    public void getTerminaTurno() {
        final OkHttpClient client = new OkHttpClient();
        final Request request = new Request.Builder()
                .url("http://187.174.102.131:92/api/Registros?rfc="+cargarInfoRFC+"&lat="+lat+"&lon="+lon+"&direc="+direccionTurno)
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
                        Toast.makeText(getApplicationContext(), "TURNO FINALIZADO", Toast.LENGTH_SHORT).show();
                        bandera = 0;
                        eliminarDatos();
                        Intent i = new Intent(RegistroActividad.this,Eventos.class);
                        startActivity(i);
                        finish();
                        Looper.loop();
                    }else{
                        Looper.prepare(); // to be able to make toast
                        Toast.makeText(getApplicationContext(), "USTED YA NO SE ENCUENTRA EN TURNO", Toast.LENGTH_SHORT).show();
                        Looper.loop();
                    }
                    Log.i("HERE", resp);
                }

            }

        });
    }

    /************************************ PERMISO DE OCR Y GPS ***********************************************/
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode != requestPermissionID) {
            Log.d(TAG, "SIN PERMISOS: " + requestCode);
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
            return;
        }
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            try {
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                mCameraSource.start(mCameraView.getHolder());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        switch (requestCode){
            case CODIGO_SOLICITUD_PERMISO :
                int resultado = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION);

                if(checarStatusPermiso(resultado)) {
                    if (!locationManager.isProviderEnabled( LocationManager.GPS_PROVIDER)) {
                        alertaGPS();
                    }
                } else {
                    Toast.makeText(activity, "EL PERMISO DE GPS NO ESTA ACTIVO", Toast.LENGTH_SHORT).show();
                }

        }
    }

    private void startCameraSource() {

        //Create the TextRecognizer
        final TextRecognizer textRecognizer = new TextRecognizer.Builder(getApplicationContext()).build();

        if (!textRecognizer.isOperational()) {
            Log.w(TAG, "Errr");
        } else {


            mCameraSource = new CameraSource.Builder(getApplicationContext(), textRecognizer)
                    .setFacing(CameraSource.CAMERA_FACING_BACK)
                    .setRequestedPreviewSize(1280, 1024)
                    .setAutoFocusEnabled(true)
                    .setRequestedFps(2.0f)
                    .build();


            mCameraView.getHolder().addCallback(new SurfaceHolder.Callback() {
                @Override
                public void surfaceCreated(SurfaceHolder holder) {
                    try {

                        if (ActivityCompat.checkSelfPermission(getApplicationContext(),
                                Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {

                            ActivityCompat.requestPermissions(RegistroActividad.this,
                                    new String[]{Manifest.permission.CAMERA},
                                    requestPermissionID);
                            return;
                        }
                        mCameraSource.start(mCameraView.getHolder());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
                }

                @Override
                public void surfaceDestroyed(SurfaceHolder holder) {
                    mCameraSource.stop();
                }
            });

            //Set the TextRecognizer's Processor.
            textRecognizer.setProcessor(new Detector.Processor<TextBlock>() {
                @Override
                public void release() {
                }


                @Override
                public void receiveDetections(Detector.Detections<TextBlock> detections) {
                    final SparseArray<TextBlock> items = detections.getDetectedItems();
                    if (items.size() != 0 ){

                        mTextView.post(new Runnable() {
                            @Override
                            public void run() {
                                StringBuilder stringBuilder = new StringBuilder();
                                for(int i=0;i<items.size();i++){
                                    TextBlock item = items.valueAt(i);
                                    stringBuilder.append(item.getValue());
                                    stringBuilder.append("\n");
                                }
                                mTextView.setText(stringBuilder.toString());
                            }
                        });
                    }
                }
            });
        }
    }

    //************************************ PERMISOS GPS ***********************************************//

    public void solicitarPermisoLocalizacion(){
        if(ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.ACCESS_FINE_LOCATION)){
            Toast.makeText(RegistroActividad.this, "PERMISOS ACTIVADOS", Toast.LENGTH_SHORT).show();
        } else {
            ActivityCompat.requestPermissions(activity, new String[] {
                    Manifest.permission.ACCESS_FINE_LOCATION
            }, CODIGO_SOLICITUD_PERMISO);
        }
    }

    private void alertaGPS(){
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("EL SISTEMA DE GPS ESTA DESACTIVADO, ¿DESEA ACTIVARLO?")
                .setCancelable(false)
                .setPositiveButton("SÍ", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        acceso = 1;
                        startActivity(new Intent( Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                })
                .setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        acceso = 1;
                        dialogInterface.cancel();
                    }
                });
        alert = builder.create();
        alert.show();
    }

    public boolean checarStatusPermiso(int resultado){
        if(resultado == PackageManager.PERMISSION_GRANTED){
            return true;
        } else {
            return false;
        }
    }

    /***********************************************************************************************************************/
    //Apartir de aqui empezamos a obtener la direciones y coordenadas
    public void locationStart() {
        LocationManager mlocManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        RegistroActividad.Localizacion Local = new RegistroActividad.Localizacion();
        Local.setRegistroActividad(this);
        final boolean gpsEnabled = mlocManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if (!gpsEnabled) {
            Intent settingsIntent = new Intent( Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(settingsIntent);
        }
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.ACCESS_FINE_LOCATION,}, 1000);
            return;
        }
        mlocManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, (LocationListener) Local);
        mlocManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, (LocationListener) Local);
        mensaje1 = "LOCALIZACIÓN AGREGADA";
        mensaje2 = "";
        Log.i("HERE", mensaje1);
    }

    public void setLocation(Location loc) {
        //Obtener la direccion de la calle a partir de la latitud y la longitud
        if (loc.getLatitude() != 0.0 && loc.getLongitude() != 0.0) {
            try {
                Geocoder geocoder = new Geocoder(this, Locale.getDefault());
                List<Address> list = geocoder.getFromLocation(
                        loc.getLatitude(), loc.getLongitude(), 1);
                if (!list.isEmpty()) {
                    Address DirCalle = list.get(0);
                    direc = DirCalle.getAddressLine(0);
                    municipio = DirCalle.getLocality();
                    estado = DirCalle.getAdminArea();
                    if(municipio != null) {
                        municipio = DirCalle.getLocality();
                    }else{
                        municipio = "SIN INFORMACION";
                    }
                    direccion.setText(direc +" " +municipio +" "+estado);
                    direccionTurno = direc +" " +municipio +" "+estado;
                    Log.i("HERE", "dir" + direc + "mun"+ municipio + "est"+ estado);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    /* Aqui empieza la Clase Localizacion */
    public class Localizacion implements LocationListener {
        RegistroActividad registroActividad;
        public RegistroActividad getRegistroActividad() {
            return registroActividad;
        }
        public void setRegistroActividad(RegistroActividad registroActividad1) {
            this.registroActividad = registroActividad1;
        }
        @Override
        public void onLocationChanged(Location loc) {
            // Este metodo se ejecuta cada vez que el GPS recibe nuevas coordenadas
            // debido a la deteccion de un cambio de ubicacion
            loc.getLatitude();
            loc.getLongitude();
            lat = loc.getLatitude();
            lon = loc.getLongitude();
            String Text = "Lat = "+ loc.getLatitude() + "\n Long = " + loc.getLongitude();
            mensaje1 = Text;
            latitud.setText(lat.toString());
            longitud.setText(lon.toString());
            Log.i("HERE", mensaje1);
            this.registroActividad.setLocation(loc);
        }
        @Override
        public void onProviderDisabled(String provider) {
            // Este metodo se ejecuta cuando el GPS es desactivado
            mensaje1 = "GPS DESACTIVADO";
        }
        @Override
        public void onProviderEnabled(String provider) {
            // Este metodo se ejecuta cuando el GPS es activado
            mensaje1 = "GPS ACTIVADO";
        }
        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            switch (status) {
                case LocationProvider.AVAILABLE:
                    Log.d("debug", "LocationProvider.AVAILABLE");
                    break;
                case LocationProvider.OUT_OF_SERVICE:
                    Log.d("debug", "LocationProvider.OUT_OF_SERVICE");
                    break;
                case LocationProvider.TEMPORARILY_UNAVAILABLE:
                    Log.d("debug", "LocationProvider.TEMPORARILY_UNAVAILABLE");
                    break;
            }
        }
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

    /******************* PREFERENCIAS DEL SISTEMA **********************************/

    private void cargarDatos(){
        share = getSharedPreferences("main",MODE_PRIVATE);
        cargarInfoRFC = share.getString("RFC","");
        cargarInfoStatus = share.getInt("STATUS",0);
    }

    private void guardarDatos(){
        share = getSharedPreferences("main",MODE_PRIVATE);
        editor = share.edit();
        editor.putInt("STATUS",bandera);
        editor.commit();
    }

    private void eliminarDatos(){
        share = getSharedPreferences("main",MODE_PRIVATE);
        editor = share.edit();
        editor.remove("STATUS").commit();
    }

}
