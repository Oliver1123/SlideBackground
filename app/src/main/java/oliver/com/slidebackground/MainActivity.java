package oliver.com.slidebackground;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final SlideBackgroundView slideBackgroundView = (SlideBackgroundView) findViewById(R.id.sbv_background_slides);
        slideBackgroundView.setAnimatedImages(
                R.drawable.bg1_test,
                R.drawable.bg2_test,
                R.drawable.bg3,
                R.drawable.bg4,
                R.drawable.bg5,
                R.drawable.bg6,
                R.drawable.bg7,
                R.drawable.bg8
        );
        slideBackgroundView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (slideBackgroundView.isAnimationStarted()) {
                    slideBackgroundView.pauseAnimation();
                } else {
                    slideBackgroundView.startAnimation(false);
                }
            }
        });
        slideBackgroundView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                slideBackgroundView.startAnimation(true);
                return true;
            }
        });
    }
}
