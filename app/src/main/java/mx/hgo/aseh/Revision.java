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
import android.graphics.Color;
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
import android.view.KeyEvent;
import android.view.SurfaceHolder;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;

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

public class Revision extends AppCompatActivity {
    ImageView btnHome,btnOcr;
    Button btnConsultaRS,btnAgregaRS,btnGuardaRS;
    EditText txtPlacaRS,txtObservacionesRS,txtFolio;
    Spinner spinRevisionRS;
    TextView lblInformacionRS,latitud,longitud,direccion,retornos,vehiculoAsegurado,recomendaciones,vehiculoActiEsen,vehiOficial,vehiculoExento;
    String placaRS,hora,fecha,cargarInfoRFC,observaciones,infraccion,mensaje1,mensaje2,folioRS;
    int valorExento = 0;
    /*********** SHARE PREFERENCE ************/
    SharedPreferences share;
    SharedPreferences.Editor editor;
    private LocationManager locationManager;
    private Context context;
    public static String direc,municipio,estado;
    public  static String direccionTurno;
    public static Double lat,lon;
    private static final int CODIGO_SOLICITUD_PERMISO = 1;
    private static final int requestPermissionID = 101;
    private static final String TAG = "RevisionSanitaria";
    private Activity activity;
    int acceso = 0;
    AlertDialog alert = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_revision);
        cargarDatos();

        txtPlacaRS = findViewById(R.id.txtPlacaRevision);
        btnConsultaRS = findViewById(R.id.btnConsultarRevision);
        lblInformacionRS = findViewById(R.id.lblInformacionRevision);
        btnAgregaRS = findViewById(R.id.btnGenerarRevision);
        btnGuardaRS = findViewById(R.id.btnGuardarRevision);
        txtObservacionesRS = findViewById(R.id.txtObservaciones);
        spinRevisionRS = findViewById(R.id.spinRevision);
        btnHome = findViewById(R.id.imgHeader);
        btnOcr = findViewById(R.id.imgOcr);

        latitud = findViewById(R.id.lblLatitudR);
        longitud = findViewById(R.id.lblLongitudR);
        direccion = findViewById(R.id.lblDireccionR);

        retornos = findViewById(R.id.lblRetorno);
        recomendaciones = findViewById(R.id.lblRecomendacion);
        vehiculoAsegurado = findViewById(R.id.lblMulta);
        vehiculoActiEsen = findViewById(R.id.lblPermiso);
        vehiOficial = findViewById(R.id.lblVehiculoOficial);
        vehiculoExento = findViewById(R.id.lblVehiculoExento);

        txtFolio = findViewById(R.id.txtFolioMulta);
        txtFolio.setVisibility(View.GONE);


        context = getApplicationContext();
        activity = this;
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        if(acceso == 0){
            solicitarPermisoLocalizacion();
        } else {
            Toast.makeText(getApplicationContext()," **EL GPS** ES OBLIGATORIO PARA EL CORRECTO FUNCIONAMIENTO DEL APLICATIVO",Toast.LENGTH_LONG).show();
        }
        locationStart();

        btnConsultaRS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lblInformacionRS.setVisibility(View.VISIBLE);
                btnAgregaRS.setVisibility(View.VISIBLE);
                btnConsultaRS.setVisibility(View.INVISIBLE);
                txtPlacaRS.setEnabled(false);
            }
        });

        btnAgregaRS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                spinRevisionRS.setVisibility(View.VISIBLE);
                txtObservacionesRS.setVisibility(View.VISIBLE);
                btnGuardaRS.setVisibility(View.VISIBLE);
                btnAgregaRS.setVisibility(View.INVISIBLE);
            }
        });

        btnGuardaRS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(txtPlacaRS.getText().toString().isEmpty()){
                    Toast.makeText(getApplicationContext(),"LA PLACA ES OBLIGATORIA",Toast.LENGTH_SHORT).show();
                }else if(txtPlacaRS.getText().length() < 3){
                    Toast.makeText(getApplicationContext(), " LA PLACA NO PUEDE SER MENOR A TRES LETRAS.", Toast.LENGTH_LONG).show();
                }else if(spinRevisionRS.getSelectedItem().equals("--Seleccione--")){
                    Toast.makeText(getApplicationContext(), " DEBE SELECCIONAR UN MOTIVO POR EL CUAL EL AUTOMOVILISTA ES DETENIDO", Toast.LENGTH_LONG).show();
                }else if(txtObservacionesRS.toString().isEmpty()){
                    Toast.makeText(getApplicationContext(), " DEBE AGREGAR ALGÚN COMENTARIO", Toast.LENGTH_LONG).show();
                }else{
                    insertRevisionSanitaria();
                }
            }
        });

        btnHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Revision.this,GuiaDeUso.class);
                startActivity(i);
            }
        });

        btnOcr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "ESTAMOS TRABAJANDO EN ESTA FUNCIONALIDAD, LAMENTAMOS LAS MOLESTIAS.", Toast.LENGTH_LONG).show();
            }
        });

        txtPlacaRS.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    if(event.isShiftPressed()){
                        //Toast.makeText(getApplicationContext(), "se pudo", Toast.LENGTH_LONG).show();
                        return true;
                    }else {
                        getRetorno();
                        getRecomendacion();
                        getVehiculoAsegurado();
                        getActividadEsencial();
                        getVehiculoOficial();
                        getVehiculoExento();
                        //Toast.makeText(getApplicationContext(), "no", Toast.LENGTH_LONG).show();
                    }
                return false;
            }
        });

        spinRevisionRS.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if(spinRevisionRS.getSelectedItem().equals("Multa")){
                    txtFolio.setVisibility(View.VISIBLE);
                }else {
                    txtFolio.setText("");
                    txtFolio.setVisibility(View.GONE);
                }
            }
            public void onNothingSelected(AdapterView<?> adapterView) {
                return;
            }
        });
    }

    /******************GET A LA BD***********************************/
    public void getRetorno() {
        placaRS = txtPlacaRS.getText().toString().toUpperCase();
        final OkHttpClient client = new OkHttpClient();
        final Request request = new Request.Builder()
                .url("http://187.174.102.131:92/api/RevisionSanitaria?placa="+placaRS+"&retorno=Retorno por no acreditar ingreso")
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
                    final String myResponse = response.body().string();
                    Revision.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            //Looper.prepare(); // to be able to make toast
                            String resp = myResponse;
                            resp = resp.replace('"',' '); //REEMPLAZA LOS CARACTERES POR NUEVOS
                            resp = resp.trim();//QUITA LOS ESPACIOS AL INICIO Y AL FINAL DE LA ORACIÒN
                            System.out.println(resp);
                            retornos.setText("Retorno por no acreditar ingreso:"+" "+resp);
                            //Looper.loop();
                        }
                    });
                }
            }

        });
    }
    /******************GET A LA BD***********************************/
    public void getRecomendacion() {
        placaRS = txtPlacaRS.getText().toString().toUpperCase();
        final OkHttpClient client = new OkHttpClient();
        final Request request = new Request.Builder()
                .url("http://187.174.102.131:92/api/RevisionSanitaria?placa="+placaRS+"&recomendacion=Recomendación por medida sanitaria")
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
                    final String myResponse = response.body().string();
                    Revision.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            //Looper.prepare(); // to be able to make toast
                            String resp = myResponse;
                            resp = resp.replace('"',' '); //REEMPLAZA LOS CARACTERES POR NUEVOS
                            resp = resp.trim();//QUITA LOS ESPACIOS AL INICIO Y AL FINAL DE LA ORACIÒN
                            System.out.println(resp);
                            recomendaciones.setText("Recomendación por medida sanitaria:"+" "+resp);
                            //Looper.loop();
                        }
                    });
                }
            }

        });
    }

    /******************GET A LA BD***********************************/
    public void getVehiculoAsegurado() {
        placaRS = txtPlacaRS.getText().toString().toUpperCase();
        final OkHttpClient client = new OkHttpClient();
        final Request request = new Request.Builder()
                .url("http://187.174.102.131:92/api/RevisionSanitaria?placa="+placaRS+"&vehiculoAsegurado=Vehículo asegurado")
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
                    final String myResponse = response.body().string();
                    Revision.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            //Looper.prepare(); // to be able to make toast
                            String resp = myResponse;
                            resp = resp.replace('"',' '); //REEMPLAZA LOS CARACTERES POR NUEVOS
                            resp = resp.trim();//QUITA LOS ESPACIOS AL INICIO Y AL FINAL DE LA ORACIÒN
                            System.out.println(resp);
                            vehiculoAsegurado.setText("Vehículo asegurado:"+" "+resp);
                            //Looper.loop();
                        }
                    });
                }
            }

        });
    }


    /******************GET A LA BD***********************************/
    public void getActividadEsencial() {
        placaRS = txtPlacaRS.getText().toString().toUpperCase();
        final OkHttpClient client = new OkHttpClient();
        final Request request = new Request.Builder()
                .url("http://187.174.102.131:92/api/RevisionSanitaria?placa="+placaRS+"&actividadEsencial=Vehículo con actividad esencial")
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
                    final String myResponse = response.body().string();
                    Revision.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            //Looper.prepare(); // to be able to make toast
                            String resp = myResponse;
                            resp = resp.replace('"',' '); //REEMPLAZA LOS CARACTERES POR NUEVOS
                            resp = resp.trim();//QUITA LOS ESPACIOS AL INICIO Y AL FINAL DE LA ORACIÒN
                            System.out.println(resp);
                            vehiculoActiEsen.setText("Vehículo con actividad esencial:"+" "+resp);
                            //Looper.loop();
                        }
                    });
                }
            }

        });
    }

    /******************GET A LA BD***********************************/
    public void getVehiculoOficial() {
        placaRS = txtPlacaRS.getText().toString().toUpperCase();
        final OkHttpClient client = new OkHttpClient();
        final Request request = new Request.Builder()
                .url("http://187.174.102.131:92/api/RevisionSanitaria?placa="+placaRS+"&vehiculoOficial=Vehículo Oficial")
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
                    final String myResponse = response.body().string();
                    Revision.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            //Looper.prepare(); // to be able to make toast
                            String resp = myResponse;
                            resp = resp.replace('"',' '); //REEMPLAZA LOS CARACTERES POR NUEVOS
                            resp = resp.trim();//QUITA LOS ESPACIOS AL INICIO Y AL FINAL DE LA ORACIÒN
                            System.out.println(resp);
                            vehiOficial.setText("Vehículo Oficial:"+" "+resp);
                            //Looper.loop();
                        }
                    });
                }
            }

        });
    }

    /******************GET A LA BD***********************************/
    public void getVehiculoExento() {
        placaRS = txtPlacaRS.getText().toString().toUpperCase();
        final OkHttpClient client = new OkHttpClient();
        final Request request = new Request.Builder()
                .url("http://187.174.102.131:92/api/RevisionSanitaria?placa="+placaRS+"&vehiculoExento=Vehículo Exento")
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
                    final String myResponse = response.body().string();
                    Revision.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            //Looper.prepare(); // to be able to make toast
                            String resp = myResponse;
                            resp = resp.replace('"',' '); //REEMPLAZA LOS CARACTERES POR NUEVOS
                            resp = resp.trim();//QUITA LOS ESPACIOS AL INICIO Y AL FINAL DE LA ORACIÒN
                            System.out.println(resp);
                            vehiculoExento.setText("Vehículo Exento:"+" "+resp);
                            vehiculoExento.setBackgroundColor(Color.parseColor("#FF4848"));
                            valorExento = Integer.parseInt(resp) ;
                            getVehiculoPlacaExento();
                        }
                    });
                }
            }

        });
    }

    /******************GET A LA BD***********************************/
    public void getVehiculoPlacaExento() {
        placaRS = txtPlacaRS.getText().toString().toUpperCase();
        final OkHttpClient client = new OkHttpClient();
        final Request request = new Request.Builder()
                .url("http://187.174.102.131:92/api/RevisionSanitaria?placaVExento="+placaRS)
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
                    final String myResponse = response.body().string();
                    Revision.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            //Looper.prepare(); // to be able to make toast
                            String resp = myResponse;
                            resp = resp.replace('"',' '); //REEMPLAZA LOS CARACTERES POR NUEVOS
                            resp = resp.trim();//QUITA LOS ESPACIOS AL INICIO Y AL FINAL DE LA ORACIÒN
                            System.out.println(resp);
                            if(valorExento >= 1){
                                txtObservacionesRS.setText(resp);
                            }else {
                                txtObservacionesRS.setText("");
                                vehiculoExento.setBackgroundColor(Color.TRANSPARENT);
                            }
                            //Looper.loop();
                        }
                    });
                }
            }

        });
    }

    /******************INSERT A LA BD***********************************/
    private void insertRevisionSanitaria(){
        placaRS = txtPlacaRS.getText().toString().toUpperCase();
        folioRS = txtFolio.getText().toString();
        observaciones = txtObservacionesRS.getText().toString().toUpperCase();
        infraccion = (String) spinRevisionRS.getSelectedItem();
        infraccion.toUpperCase();
        //*************** FECHA **********************//
        Date date = new Date();
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
        fecha = dateFormat.format(date);

        //*************** HORA **********************//
        Date time = new Date();
        DateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
        hora = timeFormat.format(time);

        if(txtFolio.getText().toString().isEmpty()){
            folioRS = "N/A";
        }

        OkHttpClient client = new OkHttpClient();
        RequestBody body = new FormBody.Builder()
                .add("RFC",cargarInfoRFC)
                .add("Placa",placaRS)
                .add("Folio",folioRS)
                .add("Infraccion",infraccion)
                .add("Comentario",observaciones)
                .add("Fecha",fecha)
                .add("Hora",hora)
                .add("Latitud",lat.toString())
                .add("Longitud", lon.toString())
                .add("Direccion",direccionTurno)
                .add("Municipio",municipio)
                .build();
        Request request = new Request.Builder()
                .url("http://187.174.102.131:92/api/RevisionSanitaria/")
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
                    Revision.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            System.out.println(resp);
                            txtPlacaRS.setText("");
                            spinRevisionRS.setSelection(0);
                            txtObservacionesRS.setText("");
                            retornos.setText("");
                            vehiculoAsegurado.setText("");
                            recomendaciones.setText("");
                            vehiculoActiEsen.setText("");
                            vehiOficial.setText("");
                            txtFolio.setText("");
                            Toast.makeText(getApplicationContext(), "INFORMACIÓN GUARDADA CON EXITO", Toast.LENGTH_SHORT).show();

                        }
                    });
                }

            }
        });
    }

    /************************************ PERMISO DE GPS ***********************************************/
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

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

    //************************************ PERMISOS GPS ***********************************************//

    public void solicitarPermisoLocalizacion(){
        if(ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.ACCESS_FINE_LOCATION)){
            Toast.makeText(Revision.this, "PERMISOS ACTIVADOS", Toast.LENGTH_SHORT).show();
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
        Revision.Localizacion Local = new Revision.Localizacion();
        Local.setRevision(this);
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
        Revision revision;
        public Revision getRevision() {
            return revision;
        }
        public void setRevision(Revision revision1) {
            this.revision = revision1;
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
            this.revision.setLocation(loc);
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

    /******************* PREFERENCIAS DEL SISTEMA **********************************/

    private void cargarDatos(){
        share = getSharedPreferences("main",MODE_PRIVATE);
        cargarInfoRFC = share.getString("RFC","");
    }
}
