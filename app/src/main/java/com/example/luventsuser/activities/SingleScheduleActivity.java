package com.example.luventsuser.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.luventsuser.Adapters.RegisteredUserListAdapter;
import com.example.luventsuser.Models.RegisterFormModel;
import com.example.luventsuser.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;

public class SingleScheduleActivity extends AppCompatActivity {

    private Toolbar toolbar;
    TextView timeTv,descriptionTv,bustimeTV,registerNowTv,toolbarTitleTv,noneRegisteredTv,scheduleTitleTv;
    RecyclerView recyclerView;

    String eventId,scheduleTitle,scheduleDescription,strMonth,timeFormat,mdayOfWeek,scheduleId;
    int scheduleDate,scheduleMonth,scheduleYear,scheduleHour,busHour,busMin;

    Spinner spinner;
    EditText searchEditText;
    String spinnerItem;

    FirebaseFirestore firebaseFirestore;
    Query query;
    List<RegisterFormModel> dataList=new ArrayList<>();
    RegisteredUserListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_schedule);


        //finding views
        toolbar=findViewById(R.id.singleScheduleToolbarId);
        timeTv=findViewById(R.id.singleScheduleTimeTvId);
        descriptionTv=findViewById(R.id.singleScheduleDescId);
        recyclerView=findViewById(R.id.singleScheduleRecylcerView);
        bustimeTV=findViewById(R.id.singleScheduleBusTimeId);
        registerNowTv=findViewById(R.id.single_schedule_registerNowId);
        toolbarTitleTv=findViewById(R.id.single_schedule_toolbarTitleTvID);
        searchEditText=findViewById(R.id.singleScheduleSearchEtId);
        spinner=findViewById(R.id.singleScheduleSpinnerId);
        noneRegisteredTv=findViewById(R.id.singleScheduleNoneRegisteredTvId);
        scheduleTitleTv=findViewById(R.id.singleScheduleTitleName);


        Intent intent=getIntent();
        eventId=intent.getStringExtra("eventId");
        scheduleTitle=intent.getStringExtra("scheduleTitle");
        scheduleDescription=intent.getStringExtra("scheduleDesc");
        scheduleId=intent.getStringExtra("scheduleId");
        scheduleDate=intent.getIntExtra("scheduleDate",0);
        scheduleMonth=intent.getIntExtra("scheduleMonth",0);
        scheduleYear=intent.getIntExtra("scheduleYear",0);
        scheduleHour=intent.getIntExtra("scheduleHour",0);
        busHour=intent.getIntExtra("scheduleBusHour",0);
        busMin=intent.getIntExtra("scheduleBusMin",0);


        searching();


        toolbarTitleTv.setText(scheduleTitle);


        monthGetter();

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);

        descriptionTv.setText(scheduleDescription);
        scheduleTitleTv.setText(scheduleTitle);

        String dateTime=mdayOfWeek+" "+strMonth+" ,"+" "+scheduleYear+" at "+timeFormat;
        timeTv.setText(dateTime);

        String busTimes=String.format("%02d:%02d",busHour,busMin);
        bustimeTV.setText(busTimes);

        registerNowTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gotoRegisterNow();
            }
        });

        firebaseFirestore=FirebaseFirestore.getInstance();


        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String item=spinner.getItemAtPosition(i).toString();


                if (item.equals("All"))
                {
                    query=firebaseFirestore.collection("RegistrationInfo").document(eventId).collection("Schedules")
                            .document(scheduleId).collection("userFormInfo").orderBy("create_at", Query.Direction.ASCENDING);
                    setRecycler();
                }
                if (item.equals("Completed"))
                {
                    query=firebaseFirestore.collection("RegistrationInfo").document(eventId).collection("Schedules")
                            .document(scheduleId).collection("userFormInfo").whereEqualTo("status","Completed");
                    setRecycler();
                }

                if (item.equals("Rejected"))
                {
                    query=firebaseFirestore.collection("RegistrationInfo").document(eventId).collection("Schedules")
                            .document(scheduleId).collection("userFormInfo").whereEqualTo("status","Rejected");
                    setRecycler();
                }
                if (item.equals("Pending"))
                {
                    query=firebaseFirestore.collection("RegistrationInfo").document(eventId).collection("Schedules")
                            .document(scheduleId).collection("userFormInfo").whereEqualTo("status","Pending");
                    setRecycler();
                }


            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

    }

    private void setRecycler() {

        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful())
                {
                    if (task.getResult()!=null) {

                        dataList.clear();
                        for (DocumentSnapshot documentSnapshot : task.getResult())
                        {

                            RegisterFormModel registerFormModel=documentSnapshot.toObject(RegisterFormModel.class);
                            dataList.add(registerFormModel);

                        }

                        adapter=new RegisteredUserListAdapter(dataList,getApplicationContext());

                        if (dataList.isEmpty()) noneRegisteredTv.setVisibility(View.VISIBLE);
                        else noneRegisteredTv.setVisibility(View.GONE);

                        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(getApplicationContext());
                        recyclerView.setLayoutManager(linearLayoutManager);
                        recyclerView.setHasFixedSize(true);
                        recyclerView.setAdapter(adapter);

                    }

                }

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                Toast.makeText(SingleScheduleActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });
    }


    private void gotoRegisterNow() {

        Intent intent=new Intent(SingleScheduleActivity.this,RegisterFormActivity.class);
        intent.putExtra("scheduleId",scheduleId);
        intent.putExtra("eventId",eventId);
        startActivity(intent);
        finish();

    }

    private void monthGetter() {

        if(scheduleMonth==0) strMonth="January";
        if(scheduleMonth==1) strMonth="February";
        if(scheduleMonth==2) strMonth="March";
        if(scheduleMonth==3) strMonth="April";
        if(scheduleMonth==4) strMonth="May";
        if(scheduleMonth==5) strMonth="June";
        if(scheduleMonth==6) strMonth="July";
        if(scheduleMonth==7) strMonth="August";
        if(scheduleMonth==8) strMonth="September";
        if(scheduleMonth==9) strMonth="October";
        if(scheduleMonth==10) strMonth="November";
        if(scheduleMonth==11) strMonth="December";

        //timeformat

        if (scheduleHour>12)
        {
            int x=scheduleHour-12;

            timeFormat=x+" pm";
        }
        else timeFormat=scheduleHour+" am";

        //dayOfweek
        GregorianCalendar date2 = new GregorianCalendar(scheduleYear, scheduleMonth, scheduleDate-4);

        int dayOfWeek=date2.get(date2.DAY_OF_WEEK);
        if(dayOfWeek==1)mdayOfWeek="Monday";
        if(dayOfWeek==2)mdayOfWeek="Tuesday";
        if(dayOfWeek==3)mdayOfWeek="Wednesday";
        if(dayOfWeek==4)mdayOfWeek="Thursday";
        if(dayOfWeek==5)mdayOfWeek="Friday";
        if(dayOfWeek==6)mdayOfWeek="Saturday";
        if(dayOfWeek==7)mdayOfWeek="Sunday";

    }


    //searchingFilter
    public void searching() {
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                try {

                    String mText = searchEditText.getText().toString().trim();
                    List<RegisterFormModel> mList = filter(dataList, mText);
                    adapter.setFilter(mList);
                } catch (Exception e) {

                }


            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    private List<RegisterFormModel> filter(List<RegisterFormModel> registerFormModels, String mText) {

        mText=mText.toLowerCase().trim();

        List<RegisterFormModel> searchString=new ArrayList<>();

        for(RegisterFormModel model : registerFormModels)
        {
            String username=model.getUserName().toLowerCase().trim();

            if(username.contains(mText))
            {
                searchString.add(model);
            }
        }
        return  searchString;

    }



    @Override
    public void onBackPressed() {
        startActivity(new Intent(this,SingleEventDetails.class));
        finish();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        finish();
        return true;
    }
}
