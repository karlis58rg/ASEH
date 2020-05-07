package mx.hgo.aseh.ui.home;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import mx.hgo.aseh.Baner;
import mx.hgo.aseh.Eventos;
import mx.hgo.aseh.R;
import mx.hgo.aseh.RegistroActividad;
import mx.hgo.aseh.Revision;

public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;
    private ImageView btnActividad,btnRevision,btnPanico;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                ViewModelProviders.of(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        /*************************** EVENTO DE LOS BOTONES *******************************/
        btnActividad = root.findViewById(R.id.imgActividades);
        btnRevision = root.findViewById(R.id.imgRevision);
        btnPanico = root.findViewById(R.id.imgPanico);

        btnActividad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), RegistroActividad.class);
                startActivity(i);
            }
        });

        btnRevision.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), Revision.class);
                startActivity(i);
            }
        });

        btnPanico.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(),"ESTAMOS TRABAJANDO EN LA FUNCIONALIDAD, LAMENTAMOS EL INCONVENIENTE",Toast.LENGTH_SHORT).show();

            }
        });
        /*************************************************************************/

        homeViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {

            }
        });
        return root;
    }
}