package com.example.luventsuser.Fragments;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.luventsuser.Adapters.EventsAdapter;
import com.example.luventsuser.Models.ShowEventsModel;
import com.example.luventsuser.R;
import com.example.luventsuser.activities.SingleEventDetails;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment {


    public HomeFragment() {
        // Required empty public constructor
    }

    private FirebaseFirestore firestore;
    private CollectionReference collectionReference;
    private Query query;

    String strMonth,timeFormat,mdayOfWeek;

     List<ShowEventsModel> showEventsModelList;
    private List<ShowEventsModel> eventDatas;

    //views
    private Spinner spinner;
    private EditText searchbar;


    private RecyclerView recyclerView;
    ProgressBar progressBar;
    EventsAdapter adapter;

    SwipeRefreshLayout swipeRefreshLayout;


    View view;

    TextView textView;

    ProgressDialog progressDialog;
    String refresh="NR";
    SharedPreferences.Editor editor;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_home, container, false);

        //finding Views
        recyclerView=view.findViewById(R.id.eventsrecyclerviewid);
        progressBar=view.findViewById(R.id.recyprogragbarId);
        spinner=view.findViewById(R.id.sorted_spinner_id);
        searchbar=view.findViewById(R.id.HomesearchId);
        textView=view.findViewById(R.id.homefrag_notItem);

        searching();

        eventDatas=new ArrayList<>();
        showEventsModelList=new ArrayList<>();

        //firebase
        firestore=FirebaseFirestore.getInstance();
        adapter=new EventsAdapter(eventDatas,getContext());
        collectionReference=firestore.collection("Events");

        //progressDialogue
        progressDialog=new ProgressDialog(getActivity());
        progressDialog.setMessage("Loading...");
        query=collectionReference.orderBy("create_at", Query.Direction.DESCENDING);
        setRecycler();


        //spinnerOnitem click
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String item=spinner.getItemAtPosition(i).toString();

                if(item.equals("All"))
                {
                    query=collectionReference.orderBy("create_at", Query.Direction.DESCENDING);
                    setRecycler();

                }
                if(item.equals("LUCC"))
                {
                    query=collectionReference.whereEqualTo("hostedBy", item);
                    setRecycler();

                }
                if(item.equals("LUPS"))
                {
                    query=collectionReference.whereEqualTo("hostedBy", item);
                    setRecycler();
                }
                if(item.equals("LUCuC"))
                {
                    query=collectionReference.whereEqualTo("hostedBy", item);
                    setRecycler();
                }
                if(item.equals("LUSSC"))
                {
                    query=collectionReference.whereEqualTo("hostedBy", item);
                    setRecycler();
                }
                if(item.equals("LUDC"))
                {
                    query=collectionReference.whereEqualTo("hostedBy", item);
                    setRecycler();
                }
                if(item.equals("Orpheus"))
                {
                    query=collectionReference.whereEqualTo("hostedBy", item);
                    setRecycler();

                }
                if(item.equals("BC"))
                {
                    item="Banned Community";
                    query=collectionReference.whereEqualTo("hostedBy", item);
                    setRecycler();

                }
                if(item.equals("Recent"))
                {
                    query=collectionReference.orderBy("create_at",Query.Direction.DESCENDING).limit(5);
                    setRecycler();

                }
                if(item.equals("Going"))
                {
                    query=collectionReference.orderBy("interested",Query.Direction.DESCENDING).limit(10);
                    setRecycler();

                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        swipeRefreshLayout=view.findViewById(R.id.swipeRefreshId);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                setRecycler();
                adapter.notifyDataSetChanged();
                swipeRefreshLayout.setRefreshing(false);

            }
        });


        return view;


    }

    private void setRecycler() {

        progressBar.setVisibility(View.VISIBLE);

        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                progressBar.setVisibility(View.GONE);
                if (task.isSuccessful())
                {
                    eventDatas.clear();
                    for (DocumentSnapshot doc: task.getResult())
                    {

                        ShowEventsModel data=doc.toObject(ShowEventsModel.class);
                        eventDatas.add(data);

                    }
                    adapter=new EventsAdapter(eventDatas,getActivity());
                    onItemClick(adapter);
                    //onItemLongClick(adapter);

                    LinearLayoutManager linearLayoutManager=new LinearLayoutManager(getActivity());


                    recyclerView.setLayoutManager(linearLayoutManager);
                    recyclerView.setHasFixedSize(true);
                    recyclerView.setAdapter(adapter);

                    if (eventDatas.isEmpty())
                    {
                        textView.setVisibility(View.VISIBLE);
                        textView.setText("There's no event");
                    }
                    else
                    {
                        textView.setVisibility(View.GONE);
                    }
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressBar.setVisibility(View.GONE);
            }
        })
        ;
    }

    private void onItemClick(EventsAdapter adapter) {

        adapter.setOnItemClickListener(new EventsAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position, ShowEventsModel showEventsModel) {


                Intent intent=new Intent(getContext(), SingleEventDetails.class);
                SharedPreferences sharedPreferences = getActivity().getSharedPreferences("UserEvent", Context.MODE_PRIVATE);

                editor= sharedPreferences.edit();

                editor.putString("description", showEventsModel.getDescription());
                editor.putString("eventName", showEventsModel.getEventName());
                editor.putInt   ("startDate", showEventsModel.getDate());
                editor.putInt   ("endDate", showEventsModel.geteDate());
                editor.putInt   ("startHour", showEventsModel.getHour());
                editor.putInt   ("endHour", showEventsModel.geteHour());
                editor.putInt   ("startMin", showEventsModel.getMin());
                editor.putInt   ("endMin", showEventsModel.geteMin());
                editor.putInt   ("startMonth", showEventsModel.getMonth());
                editor.putInt   ("endMonth", showEventsModel.geteMonth());
                editor.putInt   ("startYear", showEventsModel.getYear());
                editor.putInt   ("endYear", showEventsModel.geteYear());
                editor.putString   ("address", showEventsModel.getAddress());
                editor.putString("imageUri",showEventsModel.getImageUri()+"");
                editor.putLong("createAt",showEventsModel.getCreate_at());

                // editor.putString("going", String.valueOf(showEventsModel.getInterested()));
                editor.putString("eventId",showEventsModel.getEventId());
                editor.putString("hostedBy",showEventsModel.getHostedBy());
                editor.putString("going",String.valueOf(showEventsModel.getInterested()));
                //

                refresh=sharedPreferences.getString("REFRESHED","");

                editor.apply();

                //intent.putExtra("img",showEventsModel.getImageUri());
                monthGetter(showEventsModel);

                startActivity(intent);

            }
        });
    }

    //searchingFilter
    public void searching()
    {
        searchbar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                try {

                    String mText = searchbar.getText().toString().trim();
                    List<ShowEventsModel> mList = filter(eventDatas, mText);
                    adapter.setFilter(mList);
                }
                catch (Exception e)
                {

                }



            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    private List<ShowEventsModel> filter(List<ShowEventsModel> createModelList, String mText) {

        mText=mText.toLowerCase().trim();

        List<ShowEventsModel> searchString=new ArrayList<>();

        for(ShowEventsModel createModelData : createModelList)
        {
            String eventname=createModelData.getEventName().toLowerCase().trim();

            if(eventname.contains(mText))
            {
                searchString.add(createModelData);
            }
        }
        return  searchString;

    }


    public void  monthGetter(ShowEventsModel showEventsModel)
    {
        if(showEventsModel.getMonth()==1) strMonth="January";
        if(showEventsModel.getMonth()==2) strMonth="February";
        if(showEventsModel.getMonth()==3) strMonth="March";
        if(showEventsModel.getMonth()==4) strMonth="April";
        if(showEventsModel.getMonth()==5) strMonth="May";
        if(showEventsModel.getMonth()==6) strMonth="June";
        if(showEventsModel.getMonth()==7) strMonth="July";
        if(showEventsModel.getMonth()==8) strMonth="August";
        if(showEventsModel.getMonth()==9) strMonth="September";
        if(showEventsModel.getMonth()==10) strMonth="October";
        if(showEventsModel.getMonth()==11) strMonth="November";
        if(showEventsModel.getMonth()==12) strMonth="December";

        //timeformat

        if (showEventsModel.getHour()>12)
        {
            int x=showEventsModel.getHour()-12;

            timeFormat=x+" pm";
        }
        else timeFormat=showEventsModel.getHour()+" am";

        //dayOfweek
        GregorianCalendar date2 = new GregorianCalendar(showEventsModel.geteYear(), showEventsModel.geteMonth(), showEventsModel.getDate()-4);

        int dayOfWeek=date2.get(date2.DAY_OF_WEEK);
        if(dayOfWeek==1)mdayOfWeek="Monday";
        if(dayOfWeek==2)mdayOfWeek="Tuesday";
        if(dayOfWeek==3)mdayOfWeek="Wednesday";
        if(dayOfWeek==4)mdayOfWeek="Thursday";
        if(dayOfWeek==5)mdayOfWeek="Friday";
        if(dayOfWeek==6)mdayOfWeek="Saturday";
        if(dayOfWeek==7)mdayOfWeek="Sunday";



    }


}
