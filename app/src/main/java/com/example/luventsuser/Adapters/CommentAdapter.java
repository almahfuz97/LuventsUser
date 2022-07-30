package com.example.luventsuser.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.borjabravo.readmoretextview.ReadMoreTextView;
import com.example.luventsuser.Models.CommentModel;
import com.example.luventsuser.R;
import com.github.marlonlom.utilities.timeago.TimeAgo;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.MyViewHolder> {

    List<CommentModel> commentData;
    Context context;

    public CommentAdapter(List<CommentModel> commentModels, Context context) {
        this.commentData = commentModels;
        this.context = context;
    }

    public CommentAdapter() {
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.comment_data_list,parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        CommentModel commentModel=commentData.get(position);


        if(commentModel.getProfileUri().equals("No Image"))
        {
            holder.circleImageView.setImageResource(R.drawable.proico);
        }
        else Picasso.get().load(commentModel.getProfileUri()).fit().into(holder.circleImageView);

        holder.userName.setText(commentModel.getUserName());
        holder.readMoreTextView.setText(commentModel.getComment());
        holder.timeAgo.setText(TimeAgo.from(commentModel.getCreate_at()));

    }

    @Override
    public int getItemCount() {
        return commentData.size();
    }



    public class MyViewHolder extends RecyclerView.ViewHolder {
        CircleImageView circleImageView;
        ReadMoreTextView readMoreTextView;
        TextView userName,timeAgo;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            circleImageView=itemView.findViewById(R.id.comment_list_circle_profileId);
            userName=itemView.findViewById(R.id.comment_list_UserNameId);
            readMoreTextView=itemView.findViewById(R.id.comment_list_comment_Tv_Id);
            timeAgo=itemView.findViewById(R.id.comment_list_timeAgoId);

            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {

                    if(clickListener2!=null)
                    {
                        int position=getAdapterPosition();
                        if(position!=RecyclerView.NO_POSITION)
                        {
                            clickListener2.onLongItemClick(view,position,commentData.get(getAdapterPosition()));
                        }
                    }
                    return false;
                }
            });

        }


    }

    private CommentAdapter.OnLongItemClickListener clickListener2;
    public interface OnLongItemClickListener{
        void onLongItemClick(View view, int position, CommentModel commentModel);
    }
    public void setOnLongItemClickListener(CommentAdapter.OnLongItemClickListener listener)
    {
        clickListener2=listener;
    }
}
