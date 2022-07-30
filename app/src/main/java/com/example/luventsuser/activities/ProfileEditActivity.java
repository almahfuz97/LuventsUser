package com.example.luventsuser.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.TextView;
import android.widget.Toast;

import com.example.luventsuser.Classes.ImageHeightWidth;
import com.example.luventsuser.Models.PostModel;
import com.example.luventsuser.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.squareup.picasso.Picasso;

import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileEditActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int PICK_IMAGE_REQUEST = 1;
    private static final int STORAGE_PERMISSION_CODE = 1;
    CircleImageView circleImageView;
    TextView saveTv, choosePhotoTv, emailTv,backTV;
    MaterialEditText profileNameTv, universityNameTv, contactTv;

    String profileName, email, universityName = "", contact = "", imgUri;

    private StorageReference storageReference = FirebaseStorage.getInstance().getReference("PostImages");

    ProgressDialog progressDialog;


    Uri mUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_edit);

        circleImageView = findViewById(R.id.editProfileCircleImgId);
        saveTv = findViewById(R.id.editProfileSaveId);
        choosePhotoTv = findViewById(R.id.editProfileChooseImageId);
        profileNameTv = findViewById(R.id.editProfileNameEditText);
        emailTv = findViewById(R.id.editProfileEmailEt);
        universityNameTv = findViewById(R.id.editProfileUniversityNameEt);
        contactTv = findViewById(R.id.editProfileContactEt);
        backTV=findViewById(R.id.editProfileBackId);

        progressDialog=new ProgressDialog(this);
        progressDialog.setMessage("please wait...");

        Intent intent = getIntent();

        profileName = intent.getStringExtra("profileName");
        email = intent.getStringExtra("email");
        universityName = intent.getStringExtra("universityName");
        contact = intent.getStringExtra("contactNumber");
        imgUri = intent.getStringExtra("imgUri");


        if (imgUri.equals("No Image")) {
            circleImageView.setImageResource(R.drawable.proico);
        } else {
            Picasso.get().load(imgUri).fit().into(circleImageView);
        }

        profileNameTv.setText(profileName);
        emailTv.setText(email);
        universityNameTv.setText(universityName);
        contactTv.setText(contact);

        saveTv.setOnClickListener(this);
        choosePhotoTv.setOnClickListener(this);
        backTV.setOnClickListener(this);


    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.editProfileChooseImageId:
                choosePhoto();
                break;
            case R.id.editProfileSaveId:
                uploadImage();
                break;
            case R.id.editProfileBackId:
                Intent intent=new Intent(this,BottomNavigation.class);
                intent.putExtra("tab",3);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
                break;
        }

    }

    private void choosePhoto() {

        if (ContextCompat.checkSelfPermission(Objects.requireNonNull(this),
                Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {

            Intent intent = new Intent().setType("image/*").setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(intent, PICK_IMAGE_REQUEST);


        } else {
            requestStoragePermission();


        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.getData() != null) {
            mUri = data.getData();

            Picasso.get().load(mUri).fit().into(circleImageView);


        }
    }

    public void requestStoragePermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(Objects.requireNonNull(this),
                Manifest.permission.READ_EXTERNAL_STORAGE)) {

            new AlertDialog.Builder(this)
                    .setTitle("Permission needed")
                    .setMessage("This permission is needed because of this and that")
                    .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions(ProfileEditActivity.this,
                                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, STORAGE_PERMISSION_CODE);


                        }
                    })
                    .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .create().show();

        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, STORAGE_PERMISSION_CODE);
        }


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == STORAGE_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permission GRANTED", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Permission DENIED", Toast.LENGTH_SHORT).show();
            }
        }
    }

    //image Extension getter
    private String getFileExtension(Uri uri) {


        ContentResolver resolver = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(resolver.getType(uri));
    }


    private void uploadImage() {

        universityName=universityNameTv.getText().toString().trim();
        contact=contactTv.getText().toString().trim();

        if (universityName.isEmpty())universityName="";
        if (contact.isEmpty())contact="";
        if (profileName.isEmpty()) {
            profileNameTv.setError("Write a name");
            profileNameTv.requestFocus();
            return;
        }

        progressDialog.show();



        if (mUri == null) {
            imgUri = "No Image";
            setPostToDatabase();
        } else {
            final StorageReference fileRef = storageReference.child(System.currentTimeMillis() + "." + getFileExtension(mUri));

            fileRef.putFile(mUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                            fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {

                                    imgUri = uri.toString();
                                    setPostToDatabase();

                                }
                            });
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                }
            });
        }
    }

    //Storing to Database
    private void setPostToDatabase()
    {
        FirebaseFirestore.getInstance()
                .collection("UserInfo")
                .document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .update(
                        "userName", profileName,
                        "universityName", universityName,
                        "contactNumber",contact,
                        "profileUri",imgUri
                ).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Toast.makeText(ProfileEditActivity.this, "Profile updated", Toast.LENGTH_SHORT).show();

                Intent intent=new Intent(getApplicationContext(),BottomNavigation.class);
                intent.putExtra("tab",3);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
                progressDialog.cancel();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(ProfileEditActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                progressDialog.cancel();
            }
        });
    }
}

