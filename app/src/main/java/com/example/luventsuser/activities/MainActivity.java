package com.example.luventsuser.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.PatternMatcher;
import android.util.Patterns;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.example.luventsuser.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.util.regex.Pattern;

import static android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    MaterialEditText emailET,passwordEt;
    TextView forgotPassTv,gotoSignUpTv,resendEmailTV;
    Button loginBtn;
    LinearLayout layout;

    //firebase
    FirebaseUser user;
    FirebaseFirestore firebaseFirestore;
    FirebaseAuth mAuth;

    ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //findingviews
        emailET=findViewById(R.id.login_email_id);
        passwordEt=findViewById(R.id.login_password_id);
        forgotPassTv=findViewById(R.id.forgotPassTvId);
        gotoSignUpTv=findViewById(R.id.login_goto_sign_upTvId);
        loginBtn=findViewById(R.id.login_id);
        resendEmailTV=findViewById(R.id.login_resendEmailId);

        progressDialog=new ProgressDialog(this);
        progressDialog.setMessage("Logging In");

        //firebase
        mAuth=FirebaseAuth.getInstance();
        //user=mAuth.getCurrentUser();

        //clickListener
        forgotPassTv.setOnClickListener(this);
        loginBtn.setOnClickListener(this);
        gotoSignUpTv.setOnClickListener(this);
        resendEmailTV.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId())
        {
            case R.id.forgotPassTvId:
                Animatoo.animateFade(MainActivity.this);
                startActivity(new Intent(getApplicationContext(),ForgotPasswordActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK).addFlags(FLAG_ACTIVITY_CLEAR_TOP));
                finish();
                break;
            case R.id.login_goto_sign_upTvId:
                Animatoo.animateFade(MainActivity.this);
                startActivity(new Intent(MainActivity.this,SignUpActivity.class));
                break;
            case R.id.login_id:
                loggingIn();
                break;
            case R.id.login_resendEmailId:
                resendEmail();
                break;
        }

    }

    private void resendEmail() {
        if (mAuth.getCurrentUser()!=null)
        {
            mAuth.getCurrentUser().sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful())
                    {
                        Toast.makeText(MainActivity.this, "Verification email is sent!", Toast.LENGTH_SHORT).show();
                        emailET.setError(null);
                        resendEmailTV.setVisibility(View.GONE);
                    }

                }
            });
        }
    }

    private void loggingIn() {
        final String email=emailET.getText().toString().toLowerCase().trim();
        String pass=passwordEt.getText().toString();


        if (email.isEmpty())
        {
            emailET.setError("Email required!");
            emailET.requestFocus();
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches())
        {
            emailET.setError("Invalid Email!");
            emailET.requestFocus();
            return;
        }
        if (pass.isEmpty())
        {
            passwordEt.setError("Pass can't be empty!");
            passwordEt.requestFocus();
            return;
        }

        progressDialog.show();

        mAuth.signInWithEmailAndPassword(email,pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                progressDialog.cancel();
                if (task.isSuccessful())
                {
                    if (mAuth.getCurrentUser() != null) {


                        if (mAuth.getCurrentUser().isEmailVerified()) {

                            Intent intent=new Intent(MainActivity.this,BottomNavigation.class);
                            Animatoo.animateFade(MainActivity.this);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                            finish();

                        } else {
                            emailET.setError("Verify your email first!");
                            emailET.requestFocus();
                            resendEmailTV.setVisibility(View.VISIBLE);

                        }
                    }
                }

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.cancel();
                Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (mAuth.getCurrentUser()!=null && mAuth.getCurrentUser().isEmailVerified())
        {

            Intent intent=new Intent(MainActivity.this,BottomNavigation.class);
            //Animatoo.animateFade(MainActivity.this);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        }
    }
}
