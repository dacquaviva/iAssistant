package gruppo_20.iassistant.ui;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.TextInputLayout;
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
import com.google.firebase.database.FirebaseDatabase;

import gruppo_20.iassistant.R;

public class LoginActivity extends AppCompatActivity {

    private EditText emailView;
    private EditText pswView;
    private Button logButton;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        FirebaseDatabase.getInstance().setPersistenceEnabled(true);

        final TextView appName = (TextView) findViewById(R.id.appName);
        final ConstraintLayout loginLayout = (ConstraintLayout) findViewById(R.id.login_layout);
        final ImageView cuore = (ImageView) findViewById(R.id.cuore);
        final ImageView ombra = (ImageView) findViewById(R.id.ombra);
        final ImageView mano = (ImageView)  findViewById(R.id.mano);
        final TextInputLayout inputLayoutEmail = (TextInputLayout) findViewById(R.id.textInputLayout);
        final TextInputLayout inputLayoutPsw = (TextInputLayout) findViewById(R.id.textInputLayout2);


        mAuth = FirebaseAuth.getInstance();
        emailView = (EditText) findViewById(R.id.email);
        pswView = (EditText) findViewById(R.id.password);
        logButton = (Button) findViewById(R.id.email_sign_in_button);

        if (mAuth.getCurrentUser() != null) {
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
            finish();
        }


        //esegui l'animazione
        Animation rotate = AnimationUtils.loadAnimation(LoginActivity.this, R.anim.rotate);
        Animation fadeIn = AnimationUtils.loadAnimation(LoginActivity.this, R.anim.fade_in);
        Animation fadeOut = AnimationUtils.loadAnimation(LoginActivity.this, R.anim.fade_out);

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

        fadeOut.setAnimationListener(new Animation.AnimationListener(){
            @Override
            public void onAnimationStart(Animation arg0) {
            }
            @Override
            public void onAnimationRepeat(Animation arg0) {
            }
            @Override
            public void onAnimationEnd(Animation arg0) {
                cuore.setVisibility(View.GONE);
                ombra.setVisibility(View.GONE);
                mano.setVisibility(View.GONE);
                inputLayoutEmail.setVisibility(View.VISIBLE);
                inputLayoutPsw.setVisibility(View.VISIBLE);
                logButton.setVisibility(View.VISIBLE);
            }
        });

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
}
