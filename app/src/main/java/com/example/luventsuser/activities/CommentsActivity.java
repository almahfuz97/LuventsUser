package com.example.luventsuser.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bestsoft32.tt_fancy_gif_dialog_lib.TTFancyGifDialog;
import com.bestsoft32.tt_fancy_gif_dialog_lib.TTFancyGifDialogListener;
import com.borjabravo.readmoretextview.ReadMoreTextView;
import com.example.luventsuser.Adapters.CommentAdapter;
import com.example.luventsuser.Classes.ImageHeightWidth;
import com.example.luventsuser.Models.CommentModel;
import com.example.luventsuser.Models.LikeModel;
import com.example.luventsuser.R;
import com.github.marlonlom.utilities.timeago.TimeAgo;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class CommentsActivity extends AppCompatActivity implements View.OnClickListener {

    CircleImageView toolbarProfileView,commentProfileView;
    TextView toolbarUserNameTV,toolbarTimeAgoTV,allLikesTv,allCommentsTv,likeClickTv,commentClickTv,sendTv,backKeyTv;
    ReadMoreTextView captionTV;
    ImageView postImageView;
    EditText commentEditText;
    RecyclerView recyclerView;


    ImageHeightWidth imageHeightWidth;
    ProgressBar progressBar;


    List<CommentModel> commentData=new ArrayList<>();


    SharedPreferences.Editor editor;
    //firebase
    FirebaseFirestore firebaseFirestore;
    CollectionReference collectionReference,likeCollectionReference;
    DocumentReference likeDocReference;
    FirebaseAuth mAuth=FirebaseAuth.getInstance();
    DatabaseReference databaseComment,databaseLike;
    Query query;

    CommentAdapter commentAdapter;

    private String toolbarProfileImage,toolbarUserName,caption,postImage,create_at;
    private String postId,eventId,hostedId;
    Uri toolbarProfileUri,postImageUri;
    long timeAgo;
    int imageHeight,imageWidth,likeTaskSize,commentTaskSize;
    long totalLikes,totalComments;
    TextView commentsTv;
    Toolbar toolbar;


    boolean processLike=false;

     String userName,userProfileUri;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments);

        //findingView
        toolbarProfileView=findViewById(R.id.comments_toolbar_profileId);
        commentProfileView=findViewById(R.id.comment_side_comment_circleview_Id);
        toolbarUserNameTV=findViewById(R.id.comment_toolbar_userNameId);
        toolbarTimeAgoTV=findViewById(R.id.comment_toolbar_TimeagoId);
        allLikesTv=findViewById(R.id.comment_total_likes_id);

        commentsTv=findViewById(R.id.comment_total_cId);
        //commentsTv.setText("oirayna kne");

        likeClickTv=findViewById(R.id.comment_click_likeId);
        commentClickTv=findViewById(R.id.comment_click_commentId);
        captionTV=findViewById(R.id.comment_captionId);
        postImageView=findViewById(R.id.comment_imageView);
        commentEditText=findViewById(R.id.comment_comment_ET_ID);
        recyclerView=findViewById(R.id.comment_recyclerViewId);
        sendTv=findViewById(R.id.comment_sendId);
        progressBar=findViewById(R.id.comment_progressbarId);
        toolbar=findViewById(R.id.comment_toolbar_id);
        backKeyTv=findViewById(R.id.comment_back_keyId);



        setSupportActionBar(toolbar);

        progressBar.setVisibility(View.VISIBLE);



        //getting Intent Data
        Intent intent=getIntent();
        toolbarProfileImage=intent.getStringExtra("userProfileImage");
        toolbarUserName=intent.getStringExtra("userName");
        caption=intent.getStringExtra("caption");
        postImage=intent.getStringExtra("postImage");
        // allLikes=intent.getStringExtra("likes");
        // allComments=intent.getStringExtra("comments");
        create_at=intent.getStringExtra("create_at");
        postId=intent.getStringExtra("postId");
        eventId=intent.getStringExtra("eventId");
        hostedId=intent.getStringExtra("hostedId");





        //firebase
        firebaseFirestore=FirebaseFirestore.getInstance();
        collectionReference= firebaseFirestore.collection("Events").document(eventId).collection("AllPosts").document(postId).collection("AllComments");
        setRecycler();
        likeCollectionReference= firebaseFirestore.collection("Events").document(eventId).collection("AllPosts").document(postId).collection("Likes");

        likeDocReference= firebaseFirestore.collection("Events").document(eventId).collection("AllPosts").document(postId);

        databaseComment= FirebaseDatabase.getInstance().getReference("Events").child("Comments");
        databaseLike=FirebaseDatabase.getInstance().getReference("Events").child("Likes");


        toolbarProfileUri=Uri.parse(toolbarProfileImage);
        postImageUri=Uri.parse(postImage);
        timeAgo=Long.parseLong(create_at);


        settingToTheViews();


        commentAdapter=new CommentAdapter();


        sendTv.setOnClickListener(this);
        likeClickTv.setOnClickListener(this);
        commentClickTv.setOnClickListener(this);
        backKeyTv.setOnClickListener(this);

    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        finish();
        return true;
    }

    private void settingToTheViews() {

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
                            if (userProfileUri.equals("No Image"))commentProfileView.setImageResource(R.drawable.proico);
                            else
                                Picasso.get().load(userProfileUri).fit().into(commentProfileView);
                        }
                    }
                });



        if (toolbarProfileImage.equals("No Image")) toolbarProfileView.setImageResource(R.drawable.proico);
        else
            Picasso.get().load(toolbarProfileUri).centerCrop().fit().into(toolbarProfileView);

        if (postImage.equals("No Image")) {
            postImageView.setMaxWidth(0);
            postImageView.setMaxHeight(0);
        }
        else
        {
            imageHeightWidth=new ImageHeightWidth(postImageUri);

            //getDropboxIMGSize(postImageUri);
            imageHeight=imageHeightWidth.getImageHeight();

            postImageView.setMinimumHeight(imageHeight);
            Picasso.get().load(postImageUri).into(postImageView);
        }

        if(caption.isEmpty()){
            captionTV.setHeight(0);
            captionTV.setWidth(0);
        }
        captionTV.setText(caption);

        toolbarTimeAgoTV.setText(TimeAgo.from(timeAgo));
        toolbarUserNameTV.setText(toolbarUserName);

        //firebase
        //like
        databaseLike.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                totalLikes=dataSnapshot.child(eventId).child(postId).getChildrenCount();

                String likes;
                if (totalLikes<2)likes=totalLikes+" like";
                else likes=totalLikes+" likes";

                if (totalLikes>0)
                    allLikesTv.setText(likes);
                else
                {
                    allLikesTv.setText("");
                }


                if (dataSnapshot.child(eventId).child(postId).hasChild((mAuth.getCurrentUser().getUid())) )
                {
                    likeClickTv.setCompoundDrawablesWithIntrinsicBounds(R.mipmap.green_like2,0,0,0);
                    likeClickTv.setTextColor(getResources().getColor(R.color.blue_active));
                }
                else
                {
                    likeClickTv.setCompoundDrawablesWithIntrinsicBounds(R.mipmap.white_like,0,0,0);
                    likeClickTv.setTextColor(getResources().getColor(R.color.black));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        //comment
        databaseComment.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                totalComments=dataSnapshot.child(eventId).child(postId).getChildrenCount();

                String comments2;

                if (totalComments<2)comments2=totalComments+" comment";
                else comments2=totalComments+" comments";

                if (totalComments>0)
                    commentsTv.setText(comments2);
                else {
                    commentsTv.setText("");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }


    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.comment_sendId:
                uploadComment();
                break;
            case R.id.comment_click_likeId:
                like();
                break;
            case R.id.comment_click_commentId:
                commentEditText.requestFocus();
                break;
            case R.id.comment_back_keyId:
                //onBackPressed();
                Intent intent=new Intent(CommentsActivity.this,SingleEventDetails.class);
                intent.putExtra("singleEventTab",1);
                startActivity(intent);
                finish();
                break;

        }

    }

    private void like() {
        processLike=true;
        databaseLike.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


                if (processLike) {

                    if (dataSnapshot.child(eventId).child(postId).hasChild((mAuth.getCurrentUser().getUid()))) {
                        databaseLike.child(eventId).child(postId).child(mAuth.getCurrentUser().getUid()).removeValue();
                        settingToTheViews();
                        processLike=false;

                    } else {
                        LikeModel likeModel = new LikeModel(hostedId, postId, mAuth.getCurrentUser().getUid(), Long.parseLong(create_at));

                        databaseLike.child(eventId).child(postId).child(mAuth.getCurrentUser().getUid()).setValue(likeModel);
                        settingToTheViews();
                        processLike=false;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });




    }

    private void setLike() {

    }


    private void uploadComment() {



        final ProgressDialog commentingProgress=new ProgressDialog(this);
        commentingProgress.setMessage("Uploading comment...");

        final String comment=commentEditText.getText().toString().trim();

        if(comment.isEmpty())
        {

            return;
        }
        commentingProgress.show();




        final CommentModel commentModel=new CommentModel(userProfileUri,userName,comment,"",mAuth.getCurrentUser().getUid(),System.currentTimeMillis());

        final DatabaseReference databaseComment= FirebaseDatabase.getInstance().getReference("Events").child("Comments");

        collectionReference.add(commentModel).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
            @Override
            public void onComplete(@NonNull Task<DocumentReference> task) {
                progressBar.setVisibility(View.VISIBLE);

                commentingProgress.cancel();

                final CommentModel commentModel1=new CommentModel(userProfileUri,userName,comment,task.getResult().getId(),mAuth.getCurrentUser().getUid(),System.currentTimeMillis());

                //progressBar.setVisibility(View.GONE);
                    collectionReference.document(task.getResult().getId())
                            .set(commentModel1)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            Toast.makeText(CommentsActivity.this, "Comment is uploaded", Toast.LENGTH_SHORT).show();

                            databaseComment.child(eventId).child(postId).child(commentModel1.getCommentId()).setValue(commentModel1);
                            commentEditText.setText("");


                            overridePendingTransition(0, 0);
                            startActivity(getIntent());
                            overridePendingTransition(0, 0);

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressBar.setVisibility(View.GONE);

                        }
                    });
                }

        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(CommentsActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });



        settingToTheViews();

    }

    public void setRecycler()
    {
        query=collectionReference.orderBy("create_at", Query.Direction.DESCENDING);

        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                progressBar.setVisibility(View.GONE);

                if(task.isSuccessful())
                {
                    commentData.clear();

                    for(DocumentSnapshot doc: task.getResult())
                    {
                        CommentModel commentModel=doc.toObject(CommentModel.class);
                        commentData.add(commentModel);
                    }

                    commentAdapter=new CommentAdapter(commentData,getApplicationContext());

                    longItemClickListener(commentAdapter);

                    LinearLayoutManager linearLayoutManager=new LinearLayoutManager(getApplicationContext());
                    recyclerView.setLayoutManager(linearLayoutManager);
                    recyclerView.setHasFixedSize(true);
                    recyclerView.setAdapter(commentAdapter);
                }


            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(CommentsActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });

    }

    CommentModel  commentModel2;
    private void longItemClickListener(final CommentAdapter commentAdapter) {

        final ProgressDialog progressDeleteCom=new ProgressDialog(this);
        progressDeleteCom.setMessage("Deleting....");


        commentAdapter.setOnLongItemClickListener(new CommentAdapter.OnLongItemClickListener() {
            @Override
            public void onLongItemClick(View view, int position, final CommentModel commentModel) {

                if (mAuth.getCurrentUser().getUid().equals(commentModel.getUserId())) {


                    commentModel2 = commentModel;
                    new TTFancyGifDialog.Builder(CommentsActivity.this)
                            .setTitle("Delete!")
                            .setMessage("Do you want to delete the comment?")
                            .setPositiveBtnText("Yes")
                            .setPositiveBtnBackground("#22b573")
                            .setNegativeBtnText("Cancel")
                            .setNegativeBtnBackground("#c1272d")
                            .setGifResource(R.drawable.update)      //pass your gif, png or jpg
                            .isCancellable(true)
                            .OnPositiveClicked(new TTFancyGifDialogListener() {
                                @Override
                                public void OnClick() {
                                    //progressDialog.show();
                                    progressDeleteCom.show();

                                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Events").child("Comments");
                                    reference.child(eventId).child(postId).child(commentModel.getCommentId()).removeValue();

                                    firebaseFirestore.collection("Events").document(eventId)
                                            .collection("AllPosts").document(postId).collection("AllComments")
                                            .document(commentModel2.getCommentId()).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            progressDeleteCom.cancel();
                                            Toast.makeText(getApplicationContext(), "Deleted", Toast.LENGTH_LONG).show();
                                            recreate();
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            progressDeleteCom.cancel();
                                            Toast.makeText(CommentsActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    });

                                }
                            })
                            .OnNegativeClicked(new TTFancyGifDialogListener() {
                                @Override
                                public void OnClick() {


                                }
                            })
                            .build();


                }
            }
        });
    }

}
