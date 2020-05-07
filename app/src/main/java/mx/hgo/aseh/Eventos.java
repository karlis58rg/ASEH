package mx.hgo.aseh;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

public class Eventos extends AppCompatActivity {
    ImageView btnActividad,btnRevision,btnPanico,btnHome;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_eventos);

        btnActividad = findViewById(R.id.imgActividades);
        btnRevision = findViewById(R.id.imgRevision);
        btnPanico = findViewById(R.id.imgPanico);
        btnHome = findViewById(R.id.imgHeader);

        btnActividad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Eventos.this,RegistroActividad.class);
                startActivity(i);
            }
        });

        btnRevision.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Eventos.this,Revision.class);
                startActivity(i);
            }
        });

        btnPanico.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(),"ESTAMOS TRABAJANDO EN LA FUNCIONALIDAD, LAMENTAMOS EL INCONVENIENTE",Toast.LENGTH_SHORT).show();

            }
        });

        btnHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Eventos.this,Baner.class);
                startActivity(i);
            }
        });
    }
}
