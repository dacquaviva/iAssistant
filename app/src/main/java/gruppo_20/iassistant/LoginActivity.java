package gruppo_20.iassistant;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    private EditText emailView;
    private EditText pswView;
    private Button logButton;
    private FirebaseAuth mAuth;

    private boolean animationEnded = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        final TextView appName = (TextView) findViewById(R.id.appName);
        final LinearLayout loginLayout = (LinearLayout) findViewById(R.id.login_layout);
        ImageView cuore = (ImageView) findViewById(R.id.cuore);
        ImageView ombra = (ImageView) findViewById(R.id.ombra);
        ImageView mano = (ImageView)findViewById(R.id.mano);

        mAuth = FirebaseAuth.getInstance();
        emailView = (EditText) findViewById(R.id.email);
        pswView = (EditText) findViewById(R.id.password);
        logButton = (Button) findViewById(R.id.email_sign_in_button);

        if (mAuth.getCurrentUser() != null) {
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
            finish();
        }

        if (savedInstanceState == null || !savedInstanceState.getBoolean("animation ended") ) {
            //esegui l'animazione
            Animation rotate = AnimationUtils.loadAnimation(LoginActivity.this, R.anim.rotate);
            Animation fadeIn = AnimationUtils.loadAnimation(LoginActivity.this, R.anim.fade_in);
            Animation fadeOut = AnimationUtils.loadAnimation(LoginActivity.this, R.anim.fade_out);

            final ValueAnimator animator = ValueAnimator.ofInt(appName.getPaddingTop(), 120);
            animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                                           @Override
                                           public void onAnimationUpdate(ValueAnimator valueAnimator){
                                               appName.setPadding(0, (Integer) valueAnimator.getAnimatedValue(), 0, 0);
                                           }
                                       }
            );
            animator.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {

                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    loginLayout.setVisibility(View.VISIBLE);
                    animationEnded = true;
                }

                @Override
                public void onAnimationCancel(Animator animation) {

                }

                @Override
                public void onAnimationRepeat(Animator animation) {

                }
            });
            animator.setDuration(500);

            AnimationSet manoAnimation = new AnimationSet(false);
            AnimationSet animation = new AnimationSet(false);

            manoAnimation.addAnimation(rotate);
            manoAnimation.addAnimation(fadeOut);

            animation.addAnimation(fadeIn);
            animation.addAnimation(fadeOut);

            manoAnimation.setFillAfter(true);
            animation.setFillAfter(true);

            mano.startAnimation(manoAnimation);
            cuore.startAnimation(animation);
            ombra.startAnimation(animation);
            appName.startAnimation(fadeIn);

            fadeOut.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    animator.start();
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });

        } else {
            //mostrare solo login
            cuore.setVisibility(View.GONE);
            mano.setVisibility(View.GONE);
            ombra.setVisibility(View.GONE);
            appName.setPadding(0, 120, 0, 0);
            loginLayout.setVisibility(View.VISIBLE);
            animationEnded = true;
        }

        logButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emailView.getText().toString();
                final String password = pswView.getText().toString();

                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(getApplicationContext(), "Inserire Email!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(password)) {
                    Toast.makeText(getApplicationContext(), "Inserire password!", Toast.LENGTH_SHORT).show();
                    return;
                }


                //authenticate user
                mAuth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                // If sign in fails, display a message to the user. If sign in succeeds
                                // the auth state listener will be notified and logic to handle the
                                // signed in user can be handled in the listener.

                                if (!task.isSuccessful()) {
                                    // there was an error

                                        Toast.makeText(LoginActivity.this, "Autenticazione fallita", Toast.LENGTH_LONG).show();

                                } else {
                                    FirebaseUser user = mAuth.getCurrentUser();
                                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                    startActivity(intent);
                                    finish();
                                }
                            }
                        });
            }
        });
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("animation ended",animationEnded);
    }
}
