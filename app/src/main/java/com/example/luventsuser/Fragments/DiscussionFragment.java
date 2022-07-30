package com.example.luventsuser.Fragments;


import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.provider.FontRequest;
import androidx.emoji.text.EmojiCompat;
import androidx.emoji.text.FontRequestEmojiCompatConfig;
import androidx.emoji.widget.EmojiEditText;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.borjabravo.readmoretextview.ReadMoreTextView;
import com.example.luventsuser.Adapters.PostAdapter;
import com.example.luventsuser.Classes.ImageHeightWidth;
import com.example.luventsuser.Models.PostModel;
import com.example.luventsuser.R;
import com.example.luventsuser.activities.CommentsActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * A simple {@link Fragment} subclass.
 */
public class DiscussionFragment extends Fragment implements View.OnClickListener{


    private static final int CERTIFICATES = 2;
    private static final int PICK_IMAGE_REQUEST = 2;
    private int  STORAGE_PERMISSION_CODE = 2;
    public static int gotoDiscussion=0;
    private int adapterPosition;
    private ProgressDialog progressDialog,progressDialogueSave,progressDialogLoading;
    private static int choosephotoFlag=0;

    public DiscussionFragment() {
        // Required empty public constructor
    }


    RecyclerView recyclerView;
    ReadMoreTextView readMoreTextView;
    private EmojiEditText emojiEditText;
    private TextView addPhotoTv;
    private View view;
    private Button postBtn;
    private ImageView postImageView;
    private CircleImageView circleImageViewProfile;
    private ProgressBar progressBar;
    private Uri mUri;
    private String eventId,imageUri,hostedBy,mCaption;
    private PostAdapter postAdapter;

    private ImageHeightWidth imageHeightWidth;

    private List<PostModel> postData,postData2;
    //firebase
    private CollectionReference collectionReference;
    Query query;

    String userName,userProfileUri;

    private AlertDialog alertDialog,alertDialog2;
    private TextView editTv,deleteTv,cancelTv;

    private Button saveBtn,chooseButton;
    private ImageView editPostImageview;
    private EditText editPostEditTextId;

    String uid;
    PostModel postModel2;
    int position;


    //firebase
    private FirebaseFirestore firebaseFirestore=FirebaseFirestore.getInstance();
    private StorageReference storageReference= FirebaseStorage.getInstance().getReference("PostImages");
    private FirebaseAuth mAuth=FirebaseAuth.getInstance();



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        FontRequest fontRequest = new FontRequest(
                "com.example.fontprovider",
                "com.example",
                "emoji compat Font Query",
                CERTIFICATES);
        EmojiCompat.Config config=new FontRequestEmojiCompatConfig(getContext(),fontRequest);
        EmojiCompat.init(config);
        view= inflater.inflate(R.layout.fragment_discussion, container, false);

        //findingview
        emojiEditText=view.findViewById(R.id.writeSomethingId);
        addPhotoTv=view.findViewById(R.id.postAddPhotoId);
        postBtn=view.findViewById(R.id.postBtnId);
        postImageView=view.findViewById(R.id.postImageId);
        circleImageViewProfile=view.findViewById(R.id.circleImageIdDiscussion);
        recyclerView=view.findViewById(R.id.discussionRecyclerViewId);
        progressBar=view.findViewById(R.id.discussionProgressbarId);


        //sharedPref
        SharedPreferences sharedPreferences=getActivity().getSharedPreferences("UserEvent", Context.MODE_PRIVATE);
        eventId = sharedPreferences.getString("eventId","");
        hostedBy = sharedPreferences.getString("hostedBy","");


        //firebase
        postData=new ArrayList<>();
        postData2=new ArrayList<>();
        postAdapter=new PostAdapter(postData,getContext());
        collectionReference=firebaseFirestore.collection("Events").document(eventId).collection("AllPosts");

        setRecycler();


        progressDialog =new ProgressDialog(getContext());
        progressDialog.setMessage("Deleting...");
        progressDialogueSave =new ProgressDialog(getContext());
        progressDialogueSave.setMessage("Saving...");
        progressDialogLoading =new ProgressDialog(getContext());
        progressDialogLoading.setMessage("Uploading...");

        addPhotoTv.setOnClickListener(this);
        postBtn.setOnClickListener(this);


        firebaseFirestore.collection("UserInfo")
                .document(mAuth.getCurrentUser().getUid())
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.getResult()!=null)
                        {
                            userName=task.getResult().getString("userName");
                            userProfileUri=task.getResult().getString("profileUri");

                            if(userProfileUri.equals("No Image"))
                            {
                                circleImageViewProfile.setImageResource(R.drawable.proico);
                            }
                            else
                                Picasso.get().load(userProfileUri).fit().into(circleImageViewProfile);
                        }
                        else
                            circleImageViewProfile.setImageResource(R.drawable.proico);

                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                circleImageViewProfile.setImageResource(R.drawable.proico);

            }
        });



        return view;
    }

    private void setRecycler() {

        query=collectionReference.orderBy("create_at", Query.Direction.DESCENDING);

        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful())
                {
                    postData.clear();
                    for (DocumentSnapshot doc: task.getResult())
                    {
                        PostModel postModel=doc.toObject(PostModel.class);
                        postData.add(postModel);

                    }
                    postAdapter=new PostAdapter(postData,getActivity());

                    LinearLayoutManager linearLayoutManager=new LinearLayoutManager(getActivity());
                    onItemClick(postAdapter);
                    onLongItemClick(postAdapter);

                    recyclerView.setLayoutManager(linearLayoutManager);
                    recyclerView.setHasFixedSize(true);


                    //postData.remove(adapterPosition);

                    //postAdapter.notifyDataSetChanged();
                    recyclerView.setAdapter(postAdapter);

                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    //recycler Long click
    private void onLongItemClick(PostAdapter postAdapter) {

        postAdapter.setOnLongItemClickListener(new PostAdapter.OnLongItemClickListener() {
            @Override
            public void onLongItemClick(View view, int position, PostModel postModel) {
                longClickListner(position,postModel);
            }
        });
    }


    private void longClickListner(final int position, PostModel postModel) {


        postModel2=postModel;
        this.position=position;
        firebaseFirestore.collection("Events").document(postModel2.getEventId()).collection("AllPosts").document(postModel2.getPostId())
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful())
                {
                    uid=task.getResult().get("userId").toString();

                    if (uid.equals(mAuth.getCurrentUser().getUid()))
                    {
                        longClickAlertDialogue(postModel2,position);

                    }
                }

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });



    }


    //alert Dialogue for Long click on list
    private void longClickAlertDialogue(final PostModel postModel, final int position) {



        AlertDialog.Builder alert=new AlertDialog.Builder(getContext());

        //  View mView=LayoutInflater.from(getContext()).inflate(R.layout.edit_post_alert_dialogue,null);
        View mView=getLayoutInflater().inflate(R.layout.edit_post_alert_dialogue,null);

        alert.setView(mView);
        alertDialog=alert.create();
        alertDialog.show();

        editTv=mView.findViewById(R.id.edit_post_alert_dialogue_editId);
        deleteTv=mView.findViewById(R.id.edit_post_alert_dialogue_deleteId);
        cancelTv=mView.findViewById(R.id.edit_post_alert_dialogue_cancelId);

        editTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.cancel();

                AlertDialog.Builder alert2=new AlertDialog.Builder(getContext());

                //  View mView=LayoutInflater.from(getContext()).inflate(R.layout.edit_post_alert_dialogue,null);
                View mView2=getLayoutInflater().inflate(R.layout.edit_post,null);

                alert2.setView(mView2);
                alertDialog2=alert2.create();
                alertDialog2.show();

                saveBtn=mView2.findViewById(R.id.edit_post_saveBtnId);
                chooseButton=mView2.findViewById(R.id.edit_post_choosePhotoBtnId);
                editPostEditTextId=mView2.findViewById(R.id.edit_post_editTextId);
                editPostImageview=mView2.findViewById(R.id.edit_post_imageviewID);

                editPostEditTextId.setText(postModel.getCaption());

                if(!postModel.getImageUri().equals("No Image"))
                {
                    Picasso.get().load(postModel.getImageUri()).fit().into(editPostImageview);
                }


                chooseButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        choosephotoFlag=2;
                        choosePhoto();
                    }
                });

                saveBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        alertDialog2.cancel();
                        progressDialogueSave.show();
                        editPostUpdateImage();
                    }
                });


            }
        });
        deleteTv.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                progressDialog.show();
                alertDialog.cancel();
                firebaseFirestore.collection("Events").document(postModel.getEventId()).collection("AllPosts").document(postModel.getPostId())
                        .delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        progressDialogueSave.cancel();
                        Toast.makeText(getContext(), "Post Deleted Succesfully", Toast.LENGTH_SHORT).show();
                        postData.remove(position);
                        getActivity().recreate();

                        progressDialog.cancel();


                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                        alertDialog.cancel();
                        progressDialogueSave.cancel();


                    }
                });


            }
        });

        cancelTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.cancel();
            }
        });


    }

    //updateImage
    private void editPostUpdateImage() {



        String editTextCaption=editPostEditTextId.getText().toString().trim();
        if (editTextCaption.isEmpty() && mUri==null)
        {
            editPostEditTextId.setError("Can't be empty");
            editPostEditTextId.requestFocus();
            return;
        }

        if(mUri==null)
        {
            if (postModel2.getImageUri().equals("No Image"))
            {
                imageUri="No Image";
                editPostUpdateDatabase();
            }
            else
            {
                imageUri=postModel2.getImageUri();
                editPostUpdateDatabase();
            }

        }
        else
        {
            final StorageReference fileRef=storageReference.child(System.currentTimeMillis()+"."+getFileExtension(mUri));

            fileRef.putFile(mUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                            fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {

                                    imageUri=uri.toString();
                                    editPostUpdateDatabase();
                                    imageHeightWidth=new ImageHeightWidth(uri);
                                }
                            });
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getContext(),e.getMessage(),Toast.LENGTH_LONG).show();
                }
            });
        }
    }


    //updateDatabase
    private void editPostUpdateDatabase() {

        firebaseFirestore.collection("Events").document(postModel2.getEventId()).collection("AllPosts").document(postModel2.getPostId())
                .update("imageUri",imageUri,"caption",editPostEditTextId.getText().toString().trim()).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                progressDialogueSave.cancel();
                Toast.makeText(getContext(), "Updated succesfully", Toast.LENGTH_SHORT).show();
                getActivity().recreate();


            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialogueSave.cancel();
                Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });

    }

    //recycler list item click
    private void onItemClick(PostAdapter postAdapter) {

        postAdapter.setOnItemClickListener(new PostAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position, PostModel postModel) {

                Intent intent=new Intent(getContext(), CommentsActivity.class);
                intent.putExtra("userProfileImage",postModel.getProfileUri());
                intent.putExtra("userName",postModel.getUserName());
                intent.putExtra("caption",postModel.getCaption());
                intent.putExtra("postImage",postModel.getImageUri());
                intent.putExtra("likes",postModel.getLikes()+"");
                intent.putExtra("comments",postModel.getComments()+"");
                intent.putExtra("create_at",postModel.getCreate_at()+"");
                intent.putExtra("postId",postModel.getPostId()+"");
                intent.putExtra("eventId",eventId);
                intent.putExtra("hostedId",hostedBy);

                startActivity(intent);
                // getActivity().finish();
            }
        });
    }

    @Override
    public void onClick(View view) {

        switch (view.getId())
        {
            case R.id.postAddPhotoId:
                choosePhoto();
                break;
            case R.id.postBtnId:

                setImageToStorage();
                break;
        }

    }


    //choosing Photo
    private void choosePhoto() {


        if (ContextCompat.checkSelfPermission(Objects.requireNonNull(getActivity()),
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

        if(requestCode==PICK_IMAGE_REQUEST && resultCode== Activity.RESULT_OK && data!=null && data.getData()!=null)
        {
            mUri=data.getData();

                postImageView.setMinimumWidth(200);
                postImageView.setMinimumHeight(200);
                Picasso.get().load(mUri).fit().into(postImageView);
        }
    }

    public void requestStoragePermission()
    {
        if (ActivityCompat.shouldShowRequestPermissionRationale(Objects.requireNonNull(getActivity()),
                Manifest.permission.READ_EXTERNAL_STORAGE)) {

            new AlertDialog.Builder(getActivity())
                    .setTitle("Permission needed")
                    .setMessage("This permission is needed because of this and that")
                    .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions(getActivity(),
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
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, STORAGE_PERMISSION_CODE);
        }


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == STORAGE_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(getActivity(), "Permission GRANTED", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getActivity(), "Permission DENIED", Toast.LENGTH_SHORT).show();
            }
        }
    }

    //image Extension getter
    private String getFileExtension(Uri uri) {


        ContentResolver resolver = getActivity().getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(resolver.getType(uri));
    }


    //Storing Image
    private void setImageToStorage() {

        mCaption=emojiEditText.getText().toString().trim();

        if (mCaption.isEmpty() && mUri==null) {
            emojiEditText.setError("Post can't be empty!");
            emojiEditText.requestFocus();
            progressBar.setVisibility(View.GONE);
            return;
        }

        emojiEditText.setText("");
        progressDialogLoading.show();


        if (mUri==null)
        {
            imageUri="No Image";
            setPostToDatabase();
        }
        else
        {
            final StorageReference fileRef=storageReference.child(System.currentTimeMillis()+"."+getFileExtension(mUri));

            fileRef.putFile(mUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                            fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {

                                    imageUri=uri.toString();
                                    setPostToDatabase();
                                    imageHeightWidth=new ImageHeightWidth(uri);
                                }
                            });
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getContext(),e.getMessage(),Toast.LENGTH_LONG).show();
                }
            });
        }
    }

    //Storing to Database
    private void setPostToDatabase() {



        PostModel postModel=new PostModel(userName,mCaption,imageUri,mAuth.getUid(),"",userProfileUri,eventId,0,0,System.currentTimeMillis(),imageHeightWidth);

        firebaseFirestore.collection("Events").document(eventId).collection("AllPosts").add(postModel)
                .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentReference> task) {
                        if (task.isSuccessful()) {


                            progressDialogLoading.cancel();
                            PostModel postModel2 = new PostModel(userName, mCaption, imageUri, mAuth.getUid(), task.getResult().getId(), userProfileUri,eventId, 0, 0, System.currentTimeMillis(),imageHeightWidth);
                            firebaseFirestore.collection("Events").document(eventId).collection("AllPosts")
                                    .document(task.getResult().getId()).set(postModel2).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful())
                                    {
                                        Toast.makeText(getContext(), "Status is uploaded", Toast.LENGTH_SHORT).show();

                                        getActivity().recreate();


                                    }
                                    else
                                        Toast.makeText(getContext(), "Something went wrong!", Toast.LENGTH_SHORT).show();

                                }
                            });
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                        progressDialogLoading.cancel();
                    }
                });


    }


}
