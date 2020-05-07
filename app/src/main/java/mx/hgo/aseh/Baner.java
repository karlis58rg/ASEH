package mx.hgo.aseh;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;

import com.synnapps.carouselview.CarouselView;
import com.synnapps.carouselview.ImageListener;

public class Baner extends AppCompatActivity {
    CarouselView baner;
    int[] carrusel = {R.drawable.uno,R.drawable.dos,R.drawable.tres,R.drawable.cuatro};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_baner);

        baner = (CarouselView)findViewById(R.id.baner);
        baner.setPageCount(carrusel.length);
        baner.setImageListener(imageListener);

    }

    ImageListener imageListener = new ImageListener() {
        @Override
        public void setImageForPosition(int position, ImageView imageView) {
            imageView.setImageResource(carrusel[position]);

        }
    };
}
