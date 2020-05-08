package mx.hgo.aseh.ui.tools;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.synnapps.carouselview.CarouselView;
import com.synnapps.carouselview.ImageListener;

import mx.hgo.aseh.R;
import mx.hgo.aseh.ui.gallery.GalleryViewModel;

public class ToolsFragment extends Fragment {

    private ToolsViewModel toolsViewModel;
    private GalleryViewModel galleryViewModel;

    CarouselView baner;
    int[] carrusel = {R.drawable.caso_uno,R.drawable.caso_dos};

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        toolsViewModel =
                ViewModelProviders.of(this).get(ToolsViewModel.class);
        View root = inflater.inflate(R.layout.fragment_tools, container, false);
        //*************************** BANER **********************************************/
        baner = (CarouselView) root.findViewById(R.id.banerLegal);
        baner.setPageCount(carrusel.length);
        baner.setImageListener(imageListener);


        /*********************************************************************************/
        toolsViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
            }
        });
        return root;
    }

    ImageListener imageListener = new ImageListener() {
        @Override
        public void setImageForPosition(int position, ImageView imageView) {
            imageView.setImageResource(carrusel[position]);

        }
    };
}