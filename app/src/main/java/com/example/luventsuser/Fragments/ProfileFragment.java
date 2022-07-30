package com.example.luventsuser.Fragments;


import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.luventsuser.R;
import com.example.luventsuser.activities.ProfileEditActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileFragment extends Fragment {

    private CircleImageView circleImageView;
    private TextView editProfileTv,profileNameTv,emailTv,universityNameTv,contactTv;
    ProgressBar progressBar;

    FirebaseFirestore firebaseFirestore;
    FirebaseAuth mAuth;
    String profileImgUri,profileName,email,universityName="",contact="";

    public ProfileFragment() {
        // Required empty public constructor
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_profile, container, false);

        circleImageView=view.findViewById(R.id.profileFragCircleImageId);
        editProfileTv=view.findViewById(R.id.profileFragEditTvId);
        profileNameTv=view.findViewById(R.id.profileFragUserNameId);
        emailTv=view.findViewById(R.id.profileFragEmailTvId);
        universityNameTv=view.findViewById(R.id.profileFragUniversityTvId);
        contactTv=view.findViewById(R.id.profileFragContactTv);
        progressBar=view.findViewById(R.id.profileFragProgressBar);

        progressBar.setVisibility(View.VISIBLE);

        firebaseFirestore=FirebaseFirestore.getInstance();
        mAuth=FirebaseAuth.getInstance();

        firebaseFirestore.collection("UserInfo")
                .document(mAuth.getCurrentUser().getUid())
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                if (task.getResult()!=null)
                {
                    profileImgUri=task.getResult().getString("profileUri");
                    profileName=task.getResult().getString("userName");
                    email=task.getResult().getString("email");
                    universityName=task.getResult().getString("universityName");
                    contact=task.getResult().getString("contactNumber");

                    if (profileImgUri.equals("No Image")){
                        circleImageView.setImageResource(R.drawable.proico);
                        progressBar.setVisibility(View.GONE);
                    }
                    else
                    {
                        Picasso.get().load(profileImgUri).fit().into(circleImageView);
                        progressBar.setVisibility(View.GONE);
                    }

                    profileNameTv.setText(profileName);
                    emailTv.setText(email);

                    if (!universityName.equals("")) universityNameTv.setText(universityName);
                    if (!contact.equals("")) contactTv.setText(contact);

                }

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });

        editProfileTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getContext(), ProfileEditActivity.class);
                intent.putExtra("imgUri",profileImgUri);
                intent.putExtra("profileName",profileName);
                intent.putExtra("email",email);
                intent.putExtra("universityName",universityName);
                intent.putExtra("contactNumber",contact);
                startActivity(intent);
            }
        });




        return view;
    }

}
