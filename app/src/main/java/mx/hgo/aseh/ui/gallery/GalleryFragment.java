package mx.hgo.aseh.ui.gallery;

import android.os.Bundle;
import android.provider.DocumentsContract;
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

public class GalleryFragment extends Fragment {

    private GalleryViewModel galleryViewModel;

    CarouselView baner;
    int[] carrusel = {R.drawable.uno,R.drawable.dos,R.drawable.tres,R.drawable.cuatro,R.drawable.cinco};

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        galleryViewModel =
                ViewModelProviders.of(this).get(GalleryViewModel.class);
        View root = inflater.inflate(R.layout.fragment_gallery, container, false);
        //*************************** BANER **********************************************/
        baner = (CarouselView) root.findViewById(R.id.banerPunto);
        baner.setPageCount(carrusel.length);
        baner.setImageListener(imageListener);


        /*********************************************************************************/

        galleryViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
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