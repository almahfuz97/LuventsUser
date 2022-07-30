package com.example.luventsuser.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.luventsuser.Models.RegisterFormModel;
import com.example.luventsuser.R;

import java.util.ArrayList;
import java.util.List;

public class RegisteredUserListAdapter extends RecyclerView.Adapter<RegisteredUserListAdapter.MyViewHolder> {

    List<RegisterFormModel> dataList;
    Context context;

    public RegisteredUserListAdapter(List<RegisterFormModel> dataList, Context context) {
        this.dataList = dataList;
        this.context = context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.registered_user_list,parent,false);

        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        RegisterFormModel registerFormModel=dataList.get(position);

        holder.nameTv.setText(registerFormModel.getUserName());
        holder.emailTv.setText(registerFormModel.getUserEmail());
        holder.statusTv.setText(registerFormModel.getStatus());

    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView nameTv,emailTv,statusTv;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            nameTv=itemView.findViewById(R.id.registered_user_nameListId);
            emailTv=itemView.findViewById(R.id.registered_user_emailListId);
            statusTv=itemView.findViewById(R.id.registered_user_statusListId);
        }
    }


    //searchfilter
    public void setFilter(List<RegisterFormModel> mList) {
        dataList=new ArrayList<>();
        dataList.addAll(mList);
        notifyDataSetChanged();
    }
}
