package com.example.luventsuser.Fragments;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.borjabravo.readmoretextview.ReadMoreTextView;
import com.example.luventsuser.Adapters.ScheduleAdapter;
import com.example.luventsuser.Models.ScheduleModel;
import com.example.luventsuser.R;
import com.example.luventsuser.activities.SingleScheduleActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class AboutFragment extends Fragment {


    View view;
    private ReadMoreTextView aboutDescriptionTv;
    private TextView aboutGoingTv;
    String going,description;
    Bundle bundle;
    TextView noScheduleTv;

    RecyclerView recyclerView;
    ScheduleAdapter scheduleAdapter;
    List<ScheduleModel> dataList=new ArrayList<>();

    //Firebase

    private FirebaseFirestore firebaseFirestore;
    private Query query;

    private int eventYear,eventMonth,eventDate,eventHour,eventMin,eventEndYear,eventEndMonth,eventEndDate;
    String eventId,scheduleTitle,scheduleDesc;

    public AboutFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        bundle=this.getArguments();
        view= inflater.inflate(R.layout.fragment_about, container, false);



        //findingview
        aboutGoingTv=view.findViewById(R.id.aboutGoingId);
        aboutDescriptionTv=view.findViewById(R.id.aboutDescriptionId);
        recyclerView=view.findViewById(R.id.aboutfragRecyclerView);
        noScheduleTv=view.findViewById(R.id.aboutFragNoScheduleid);

        SharedPreferences sharedPreferences=getActivity().getSharedPreferences("UserEvent", Context.MODE_PRIVATE);


        firebaseFirestore=FirebaseFirestore.getInstance();
        //firebase

        description = sharedPreferences.getString("description","");
        going=sharedPreferences.getString("going","");
        eventYear=sharedPreferences.getInt("startYear",0);
        eventMonth=sharedPreferences.getInt("startMonth",0);
        eventDate=sharedPreferences.getInt("startDate",0);
        eventHour=sharedPreferences.getInt("startHour",0);
        eventMin=sharedPreferences.getInt("startMin",0);
        eventEndYear=sharedPreferences.getInt("endYear",0);
        eventEndYear=sharedPreferences.getInt("endYear",0);
        eventEndMonth=sharedPreferences.getInt("endMonth",0);
        eventEndDate=sharedPreferences.getInt("endDate",0);
        eventHour=sharedPreferences.getInt("startHour",0);
        eventMin=sharedPreferences.getInt("startMin",0);
        eventId=sharedPreferences.getString("eventId","");
        setRecycler();

        String mGoing;
        if (going==null)mGoing="0 going";
        else mGoing=going+" going";

        aboutDescriptionTv.setText(description);
        aboutGoingTv.setText(mGoing);



        return view;
    }

    private void setRecycler() {

        query=firebaseFirestore.collection("Events").document(eventId).collection("Schedules")
                .orderBy("create_at", Query.Direction.ASCENDING);

        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful())
                {
                    dataList.clear();
                    if (task.getResult()!=null)
                    {
                        for (DocumentSnapshot documentSnapshot: task.getResult())
                        {
                            ScheduleModel scheduleModel=documentSnapshot.toObject(ScheduleModel.class);
                            dataList.add(scheduleModel);
                        }

                        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(getActivity());
                        scheduleAdapter=new ScheduleAdapter(dataList,getContext());

                        if (dataList.isEmpty())noScheduleTv.setVisibility(View.VISIBLE);
                        else noScheduleTv.setVisibility(View.GONE);

                        setOnItemClickListener(scheduleAdapter);
                        setOnItemLongClickListener(scheduleAdapter);

                        recyclerView.setLayoutManager(linearLayoutManager);
                        recyclerView.setHasFixedSize(true);
                        recyclerView.setAdapter(scheduleAdapter);
                    }
                }

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });
    }

    private void setOnItemClickListener(ScheduleAdapter scheduleAdapter) {

        scheduleAdapter.setOnItemClickListener(new ScheduleAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position, ScheduleModel scheduleModel) {
                Intent intent=new Intent(getActivity(), SingleScheduleActivity.class);
                intent.putExtra("eventId",eventId);
                intent.putExtra("scheduleTitle",scheduleModel.getScheduleTitle());
                intent.putExtra("scheduleDate",scheduleModel.getScheduleDate());
                intent.putExtra("scheduleMonth",scheduleModel.getScheduleMonth());
                intent.putExtra("scheduleId",scheduleModel.getScheduleItemId());
                intent.putExtra("scheduleYear",eventYear);
                intent.putExtra("scheduleDesc",scheduleModel.getScheduleDescription());
                intent.putExtra("scheduleHour",scheduleModel.getScheduleHour());
                intent.putExtra("scheduleBusHour",scheduleModel.getBusHour());
                intent.putExtra("scheduleBusMin",scheduleModel.getBusMin());
                startActivity(intent);
                getActivity().finish();
            }
        });
    }

    private void setOnItemLongClickListener(ScheduleAdapter scheduleAdapter) {
    }



}
