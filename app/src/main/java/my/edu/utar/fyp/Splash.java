package my.edu.utar.fyp;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

public class Splash extends AppCompatActivity {

    ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        ActionBar actionBar=getSupportActionBar();

        anaEkraneGec();


    }

    private void anaEkraneGec(){
        Animation anim = AnimationUtils.loadAnimation(this,R.anim.fade);
        imageView=findViewById(R.id.splashLogoIV);
        anim.reset();
        imageView.clearAnimation();
        imageView.startAnimation(anim);

        anim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {

                Intent intent = new Intent(Splash.this,Login.class);
                startActivity(intent);

                Splash.this.finish();

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }
}