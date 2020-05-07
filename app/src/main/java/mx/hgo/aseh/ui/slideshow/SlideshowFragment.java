package mx.hgo.aseh.ui.slideshow;

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

public class SlideshowFragment extends Fragment {

    private SlideshowViewModel slideshowViewModel;
    CarouselView baner;
    int[] carrusel = {R.drawable.auto_uno,R.drawable.auto_dos,R.drawable.auto_tres,R.drawable.auto_cuatro,R.drawable.auto_cinco,R.drawable.auto_seis};

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        slideshowViewModel =
                ViewModelProviders.of(this).get(SlideshowViewModel.class);
        View root = inflater.inflate(R.layout.fragment_slideshow, container, false);
        /***************************** CARRUSEL ******************************************/
        baner = (CarouselView) root.findViewById(R.id.banerControl);
        baner.setPageCount(carrusel.length);
        baner.setImageListener(imageListener);

        /************************************************************************************/

        slideshowViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
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