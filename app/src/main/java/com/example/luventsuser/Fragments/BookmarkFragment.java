package com.example.luventsuser.Fragments;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.luventsuser.Adapters.EventsAdapter;
import com.example.luventsuser.Models.ShowEventsModel;
import com.example.luventsuser.R;
import com.example.luventsuser.activities.MainActivity;
import com.example.luventsuser.activities.SingleEventDetails;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class BookmarkFragment extends Fragment {


    RecyclerView recyclerView;
    Toolbar toolbar;
    EventsAdapter adapter;
    List<ShowEventsModel> eventsModelList;
    FirebaseFirestore firebaseFirestore;
    CollectionReference collectionReference;
    FirebaseAuth mAuth;
    Query query;
    ProgressBar progressBar;
    SwipeRefreshLayout swipeRefreshLayout;
    TextView textView;
    ShowEventsModel showEventsModel5;

    int bookmarkFlag;
    public BookmarkFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_bookmark, container, false);

        eventsModelList=new ArrayList<>();
        recyclerView=view.findViewById(R.id.bookmarks_recyclerView);
        toolbar=view.findViewById(R.id.bookmarks_toolbar);
        progressBar=view.findViewById(R.id.bookmarks_progressbar);
        progressBar.setVisibility(View.VISIBLE);
        textView=view.findViewById(R.id.bookmarks_noItemTvid);

        setHasOptionsMenu(true);
        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle("Bookmarks");



        //firebase
        mAuth=FirebaseAuth.getInstance();
        firebaseFirestore=FirebaseFirestore.getInstance();
        collectionReference=firebaseFirestore.collection("Bookmarks").document(mAuth.getCurrentUser().getUid()).collection("Posts");

        query=collectionReference.orderBy("create_at", Query.Direction.DESCENDING);

        setRecycler();

        swipeRefreshLayout=view.findViewById(R.id.BookmarSwipeRefreshId);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                query=collectionReference.orderBy("create_at", Query.Direction.DESCENDING);
                setRecycler();
                adapter.notifyDataSetChanged();
                swipeRefreshLayout.setRefreshing(false);

            }
        });



        return view;
    }

    private void setRecycler() {

        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                progressBar.setVisibility(View.GONE);
                if (task.isSuccessful())
                {
                    eventsModelList.clear();
                    for (DocumentSnapshot doc: task.getResult())
                    {
                         showEventsModel5=doc.toObject(ShowEventsModel.class);
                         eventsModelList.add(showEventsModel5);

                    }

                    adapter=new EventsAdapter(eventsModelList,getActivity());

                    if (eventsModelList.isEmpty())
                    {
                        textView.setVisibility(View.VISIBLE);
                        textView.setText("You have not bookmarked any event!");
                    }
                    else {
                        textView.setVisibility(View.GONE);
                    }

                    onItemClick(adapter);


                    LinearLayoutManager linearLayoutManager=new LinearLayoutManager(getActivity());
                    recyclerView.setHasFixedSize(true);
                    recyclerView.setLayoutManager(linearLayoutManager);
                    recyclerView.setAdapter(adapter);

                }

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(getContext(),e.getMessage(),Toast.LENGTH_LONG).show();


            }
        });
    }

    private void onItemClick(EventsAdapter adapter) {



        adapter.setOnItemClickListener(new EventsAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position, ShowEventsModel showEventsModel) {

                Intent intent=new Intent(getContext(), SingleEventDetails.class);
                SharedPreferences sharedPreferences = getActivity().getSharedPreferences("UserEvent", Context.MODE_PRIVATE);

                SharedPreferences.Editor editor= sharedPreferences.edit();

                editor.putString("description", showEventsModel.getDescription());
                editor.putString("eventName", showEventsModel.getEventName());
                editor.putInt   ("startDate", showEventsModel.getDate());
                editor.putInt   ("endDate", showEventsModel.geteDate());
                editor.putInt   ("startHour", showEventsModel.getHour());
                editor.putInt   ("endHour", showEventsModel.geteHour());
                editor.putInt   ("startMin", showEventsModel.getMin());
                editor.putInt   ("endMin", showEventsModel.geteMin());
                editor.putInt   ("startMonth", showEventsModel.getMonth()+1);
                editor.putInt   ("endMonth", showEventsModel.geteMonth()+1);
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


                editor.apply();

                //intent.putExtra("img",showEventsModel.getImageUri());
               // monthGetter(showEventsModel);

                startActivity(intent);
            }
        });
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu, menu);
        super.onCreateOptionsMenu(menu,inflater);
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId())
        {
            case R.id.logoutid:
                FirebaseAuth.getInstance().signOut();

                Intent intent=new Intent(getActivity(), MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                getActivity().finish();
                break;

        }
        return super.onOptionsItemSelected(item);
    }

}
