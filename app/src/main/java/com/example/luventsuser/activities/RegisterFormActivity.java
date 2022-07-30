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

import com.example.luventsuser.Models.RegisterFormModel;
import com.example.luventsuser.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.rengwuxian.materialedittext.MaterialEditText;

public class RegisterFormActivity extends AppCompatActivity implements View.OnClickListener {

    MaterialEditText nameEt,emailEt,universityNameEt,contactNumberEt,bkashTrxIdEt;
    Button registerButton;
    TextView alreadyRegisteredTv;

    String fullname,email,universityName,contactNumber,bkashTrxId,eventId,scheduleId;

    //firebase
    FirebaseFirestore firebaseFirestore;
    FirebaseAuth mAuth;

    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_form);

        //finding views
        nameEt=findViewById(R.id.register_form_nameId);
        emailEt=findViewById(R.id.register_form_emailId);
        universityNameEt=findViewById(R.id.register_form_UniversityId);
        contactNumberEt=findViewById(R.id.register_form_contactNumberId);
        bkashTrxIdEt=findViewById(R.id.register_form_transactionId);
        alreadyRegisteredTv=findViewById(R.id.register_form_alreadyRegisterId);
        registerButton=findViewById(R.id.register_form_registerBtnId);

        progressDialog=new ProgressDialog(this);
        progressDialog.setMessage("Wait...");



        Intent intent=getIntent();
        eventId=intent.getStringExtra("eventId");
        scheduleId=intent.getStringExtra("scheduleId");

        //firebase
        firebaseFirestore=FirebaseFirestore.getInstance();
        mAuth=FirebaseAuth.getInstance();

        alreadyRegisteredTv.setOnClickListener(this);
        registerButton.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {

        switch (view.getId())
        {
            case R.id.register_form_alreadyRegisterId:
                Intent intent=new Intent(this,SingleEventDetails.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
                break;
            case R.id.register_form_registerBtnId:
                saveForm();
                break;
        }

    }

    private void saveForm() {

        fullname=nameEt.getText().toString().trim();
        email=emailEt.getText().toString().trim();
        universityName=universityNameEt.getText().toString().trim();
        contactNumber=contactNumberEt.getText().toString().trim();
        bkashTrxId=bkashTrxIdEt.getText().toString().trim();

        if (fullname.isEmpty())
        {
            nameEt.setError("Name required!");
            nameEt.requestFocus();
            return;
        }
         if (email.isEmpty())
        {
            emailEt.setError("email required!");
            emailEt.requestFocus();
            return;
        }
         if (!Patterns.EMAIL_ADDRESS.matcher(email).matches())
        {
            emailEt.setError("Invalid email!");
            emailEt.requestFocus();
            return;
        }

         if (universityName.isEmpty())
        {
            universityNameEt.setError("University name required!");
            universityNameEt.requestFocus();
            return;
        }
         if (contactNumber.isEmpty())
        {
            contactNumberEt.setError("contact number required!");
            contactNumberEt.requestFocus();
            return;
        }
         if (bkashTrxId.isEmpty())
        {
            bkashTrxIdEt.setError("Name required!");
            bkashTrxIdEt.requestFocus();
            return;
        }
         progressDialog.show();


        RegisterFormModel registerFormModel=new RegisterFormModel(mAuth.getCurrentUser().getUid(),eventId,scheduleId,"",fullname,email,universityName,contactNumber,bkashTrxId,"Pending",System.currentTimeMillis());

         firebaseFirestore.collection("RegistrationInfo")
                 .document(eventId)
                 .collection("Schedules").document(scheduleId)
                 .collection("userFormInfo")
                 .add(registerFormModel).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
             @Override
             public void onComplete(@NonNull Task<DocumentReference> task) {

                 progressDialog.cancel();
                 if (task.isSuccessful())
                 {

                     RegisterFormModel registerFormModel2=new RegisterFormModel(mAuth.getCurrentUser().getUid(),eventId,scheduleId,task.getResult().getId(),fullname,email,universityName,contactNumber,bkashTrxId,"Pending",System.currentTimeMillis());

                     firebaseFirestore.collection("RegistrationInfo")
                             .document(eventId)
                             .collection("Schedules").document(scheduleId)
                             .collection("userFormInfo")
                             .document(task.getResult().getId())
                             .set(registerFormModel2).addOnCompleteListener(new OnCompleteListener<Void>() {
                         @Override
                         public void onComplete(@NonNull Task<Void> task) {

                             if (task.isSuccessful())
                             {
                                 Toast.makeText(RegisterFormActivity.this, "Form submitted", Toast.LENGTH_SHORT).show();

                                 Intent intent=new Intent(RegisterFormActivity.this,SingleEventDetails.class);
                                 intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                 intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                 startActivity(intent);
                                 finish();
                             }

                         }
                     }).addOnFailureListener(new OnFailureListener() {
                         @Override
                         public void onFailure(@NonNull Exception e) {
                             Toast.makeText(RegisterFormActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                             progressDialog.cancel();
                         }
                     });

                 }

             }
         }).addOnFailureListener(new OnFailureListener() {
             @Override
             public void onFailure(@NonNull Exception e) {
                 Toast.makeText(RegisterFormActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                 progressDialog.cancel();

             }
         });

    }
}
