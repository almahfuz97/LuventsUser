package com.example.luventsuser.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.example.luventsuser.Models.UserInfoModel;
import com.example.luventsuser.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.rengwuxian.materialedittext.MaterialEditText;

public class SignUpActivity extends AppCompatActivity implements View.OnClickListener{

    MaterialEditText nameEt,emailEt,passwordEt,confirmPasswordEt;
    Button signUpBtn;
    TextView gotoLogin;
    ProgressDialog progressDialog;


    //Firebase
    FirebaseAuth mAuth;
    FirebaseFirestore firebaseFirestore;

    //models
    UserInfoModel userInfoModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        //findingViews
        nameEt=findViewById(R.id.signUp_nameEtId);
        emailEt=findViewById(R.id.signUp_emailId);
        passwordEt=findViewById(R.id.signUp_passwordId);
        confirmPasswordEt=findViewById(R.id.signUP_confirmPassId);
        signUpBtn=findViewById(R.id.signUp_ButtonId);
        gotoLogin=findViewById(R.id.signUp_alreadyHaveAccountTvID);

        //firebase
        mAuth=FirebaseAuth.getInstance();
        firebaseFirestore=FirebaseFirestore.getInstance();

        //progressDialogue
        progressDialog=new ProgressDialog(this);
        progressDialog.setMessage("Please wait...");

        //clickListener
        signUpBtn.setOnClickListener(this);
        gotoLogin.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId())
        {
            case R.id.signUp_ButtonId:
                signingIn();
                break;
            case R.id.signUp_alreadyHaveAccountTvID:
                Animatoo.animateFade(SignUpActivity.this);
                startActivity(new Intent(SignUpActivity.this, MainActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK));

                finish();
                break;
        }
    }

    private void signingIn() {

       final String name=nameEt.getText().toString().toLowerCase().trim();
       final String email=emailEt.getText().toString().toLowerCase().trim();
       final String password=passwordEt.getText().toString();
       final String confirmPass=confirmPasswordEt.getText().toString();

        if (name.isEmpty())
        {
            nameEt.setError("Required name!");
            nameEt.requestFocus();
            return;
        }
        if (email.isEmpty())
        {
            emailEt.setError("Required email!");
            emailEt.requestFocus();
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches())
        {
            emailEt.setError("Invalid email!");
            emailEt.requestFocus();
            return;
        }
        if (password.length()<6)
        {
            passwordEt.setError("Min legnth should be 6!");
            passwordEt.requestFocus();
            return;
        }
        if (!password.equals(confirmPass))
        {
            confirmPasswordEt.setError("Password didn't match");
            confirmPasswordEt.requestFocus();
            return;
        }

        progressDialog.show();

        mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {


                if (task.isSuccessful())

                {
                    final FirebaseUser user=mAuth.getCurrentUser();
                    if (user!=null)
                    {
                        userInfoModel = new UserInfoModel(name, email,"","", user.getUid(), null, System.currentTimeMillis(),"No Image");

                        firebaseFirestore.collection("UserInfo")
                                .document(user.getUid()).set(userInfoModel).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {

                                if (task.isSuccessful())
                                {
                                    //email verification
                                    user.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            progressDialog.cancel();

                                            Toast.makeText(SignUpActivity.this, "Verification email sent!", Toast.LENGTH_SHORT).show();
                                            Intent intent = new Intent(SignUpActivity.this, MainActivity.class);
                                            Animatoo.animateFade(SignUpActivity.this);
                                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                            startActivity(intent);
                                            finish();

                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(SignUpActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();

                                        }
                                    });
                                }

                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(SignUpActivity.this,e.getMessage(),Toast.LENGTH_LONG).show();
                                return;

                            }
                        });


                    }
                }
                else
                {
                    if (task.getException() instanceof FirebaseAuthUserCollisionException) {
                        Toast.makeText(SignUpActivity.this, "You are already Registered ", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(SignUpActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override

            public void onFailure(@NonNull Exception e) {
                progressDialog.cancel();
                Toast.makeText(SignUpActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });




    }
}
