package com.example.luventsuser.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.borjabravo.readmoretextview.ReadMoreTextView;
import com.example.luventsuser.Classes.ImageHeightWidth;
import com.example.luventsuser.Models.LikeModel;
import com.example.luventsuser.Models.PostModel;
import com.example.luventsuser.R;
import com.github.marlonlom.utilities.timeago.TimeAgo;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.MyViewHolder> {

    List<PostModel> postList;
    Context context;
    ImageHeightWidth imageHeightWidth;
    FirebaseFirestore firebaseFirestore=FirebaseFirestore.getInstance();
    FirebaseAuth mAuth=FirebaseAuth.getInstance();
    DatabaseReference databaseLike= FirebaseDatabase.getInstance().getReference("Events").child("Likes");
    DatabaseReference databaseComment= FirebaseDatabase.getInstance().getReference("Events").child("Comments");
    String likes,comments;
    CollectionReference collectionReference,allCommentsReference;
    int LiketaskSize,commentTaskSize;
    long fireLike=0,fireComment=0;
    private boolean processLike=false;
    long totalComments;




    public PostAdapter() {
    }

    public PostAdapter(List<PostModel> postList, Context context) {
        this.postList = postList;
        this.context = context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.post_data_list,parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {




        PostModel postModel=postList.get(position);

        //longClickListner(holder,postModel,position);
        setLikeBtn(postModel,holder);
        totalCommentGetter(holder,postModel);

        //bindingView

        String pUri=postModel.getProfileUri();
        if(pUri.equals("No Image"))
        {
            holder.circleImageView.setImageResource(R.drawable.proico);
        }
        else
            Picasso.get().load(postModel.getProfileUri()).fit().into(holder.circleImageView);

        holder.postGiverTV.setText(postModel.getUserName());
        holder.timeAgoTv.setText(TimeAgo.from(postModel.getCreate_at()));

        if(postModel.getCaption().length()<1){
            holder.statusDescriptionTv.setWidth(0);
            holder.statusDescriptionTv.setHeight(0);
        }
        holder.statusDescriptionTv.setText(postModel.getCaption());

        if(!postModel.getImageUri().equals("No Image"))
        {
            // holder.postImageViewId.setMaxWidth(postModel.getImageHeightWidth().getImageWidth());
            // holder.postImageViewId.setMaxHeight(postModel.getImageHeightWidth().getImageHeight());

            Picasso.get().load(postModel.getImageUri()).into(holder.postImageViewId);
        }
        else {
            holder.postImageViewId.setMaxHeight(0);
            holder.postImageViewId.setMaxWidth(0);
        }


//        holder.postCommentClickTv.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent=new Intent(context,CommentsActivity.class);
//                context.startActivity(intent);
//               // context.startActivity(new Intent(context.getApplicationContext(), CommentsActivity.class));
//            }
//        });


        mLikeItemClick(holder,postModel);

    }



    private void totalCommentGetter(final MyViewHolder holder, final PostModel postModel) {

        databaseComment.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


                totalComments=dataSnapshot.child(postModel.getEventId()).child(postModel.getPostId()).getChildrenCount();


                if(totalComments<2)comments=totalComments+" comment";
                else comments=totalComments+" comments";
                if (totalComments>0)
                    holder.allCommentsTv.setText(comments);
                else
                    holder.allCommentsTv.setText("");

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }


    private void mLikeItemClick(final MyViewHolder holder, final PostModel postModel) {

        holder.postLikeClickTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                processLike=true;


                final LikeModel likeModel=new LikeModel(postModel.getUserName(),postModel.getPostId(),postModel.getUserId(),System.currentTimeMillis());

                databaseLike.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        if (processLike)
                        {

                            if (dataSnapshot.child(postModel.getEventId()).child(postModel.getPostId()).hasChild(mAuth.getCurrentUser().getUid())) {
                                databaseLike.child(postModel.getEventId()).child(postModel.getPostId()).child(mAuth.getCurrentUser().getUid()).removeValue();
                                setLikeBtn(postModel,holder);
                                processLike=false;

                            } else {
                                databaseLike.child(postModel.getEventId()).child(postModel.getPostId()).child(mAuth.getCurrentUser().getUid()).setValue(likeModel);
                                setLikeBtn(postModel,holder);

                                processLike=false;
                            }
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

            }
        });



    }

    private void setLikeBtn(final PostModel postModel, final MyViewHolder holder) {

        databaseLike.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                long total_like=dataSnapshot.child(postModel.getEventId()).child(postModel.getPostId()).getChildrenCount();

                if (total_like<1)
                {
                    holder.allLikesTv.setText("");
                }
                else
                {
                    if (total_like==1)likes=total_like+" like";
                    else likes=total_like+" likes";

                    holder.allLikesTv.setText(likes);
                }

                if (mAuth.getCurrentUser()!=null) {


                    if (dataSnapshot.child(postModel.getEventId()).child(postModel.getPostId()).hasChild(mAuth.getCurrentUser().getUid())) {
                        holder.postLikeClickTv.setCompoundDrawablesWithIntrinsicBounds(R.mipmap.green_like2, 0, 0, 0);
                        holder.postLikeClickTv.setTextColor(context.getResources().getColor(R.color.blue_active));
                    } else {
                        holder.postLikeClickTv.setCompoundDrawablesWithIntrinsicBounds(R.mipmap.white_like, 0, 0, 0);
                        holder.postLikeClickTv.setTextColor(context.getResources().getColor(R.color.black));
                    }
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }


    @Override
    public int getItemCount() {
        return postList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        CircleImageView circleImageView;
        TextView postGiverTV,timeAgoTv,allLikesTv,allCommentsTv,postLikeClickTv,postCommentClickTv;
        ReadMoreTextView statusDescriptionTv;
        ImageView postImageViewId;



        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            circleImageView=itemView.findViewById(R.id.postCircleViewListId);
            postGiverTV=itemView.findViewById(R.id.post_giver_name_Id);
            timeAgoTv=itemView.findViewById(R.id.post_time_ago_list_id);
            allLikesTv=itemView.findViewById(R.id.post_all_likes_list_id);
            allCommentsTv=itemView.findViewById(R.id.post_all_comments_list_id);
            postLikeClickTv=itemView.findViewById(R.id.post_like_list_click_tv_id);
            postCommentClickTv=itemView.findViewById(R.id.post_comment_list_click_tv_id);
            statusDescriptionTv=itemView.findViewById(R.id.post_caption_list_id);
            postImageViewId=itemView.findViewById(R.id.post_image_list_id);

            databaseLike=FirebaseDatabase.getInstance().getReference("Events").child("Likes");

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(clickListener!=null)
                    {
                        int position=getAdapterPosition();
                        if(position!=RecyclerView.NO_POSITION)
                        {
                            clickListener.onItemClick(view,position,postList.get(getAdapterPosition()));
                        }
                    }
                }
            });

            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {

                    if(clickListener2!=null)
                    {
                        int position=getAdapterPosition();
                        if(position!=RecyclerView.NO_POSITION)
                        {
                            clickListener2.onLongItemClick(view,position,postList.get(getAdapterPosition()));
                        }
                    }
                    return false;
                }
            });

        }
    }

    //clicklistner
    private PostAdapter.OnItemClickListener clickListener;
    public interface OnItemClickListener{
        void onItemClick(View view, int position, PostModel postModel);
    }
    public void setOnItemClickListener(PostAdapter.OnItemClickListener listener)
    {
        clickListener=listener;
    }

    //clicklistner
    private PostAdapter.OnLongItemClickListener clickListener2;
    public interface OnLongItemClickListener{
        void onLongItemClick(View view, int position, PostModel postModel);
    }
    public void setOnLongItemClickListener(PostAdapter.OnLongItemClickListener listener)
    {
        clickListener2=listener;
    }


//    int imageHeight,imageWidth;
//    //actual image size getting
//    private void getDropboxIMGSize(Uri uri){
//        BitmapFactory.Options options = new BitmapFactory.Options();
//        options.inJustDecodeBounds = true;
//        BitmapFactory.decodeFile(new File(uri.getPath()).getAbsolutePath(), options);
//        imageHeight = options.outHeight;
//        imageWidth = options.outWidth;
//
//    }
}
