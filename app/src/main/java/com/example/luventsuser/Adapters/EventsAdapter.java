package com.example.luventsuser.Adapters;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.luventsuser.Classes.AlarmReceiver;
import com.example.luventsuser.Models.ShowEventsModel;
import com.example.luventsuser.Models.UserInfoModel;
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
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class EventsAdapter extends RecyclerView.Adapter<EventsAdapter.MyViewHolder> {

    List<ShowEventsModel> dataList;
    Context context;
    String strMonth,interestedPeople;
    FirebaseAuth mAuth=FirebaseAuth.getInstance();
    FirebaseFirestore firebaseFirestore=FirebaseFirestore.getInstance();
    CollectionReference collectionReference=firebaseFirestore.collection("Bookmarks").document(mAuth.getCurrentUser().getUid()).collection("Posts");
    DatabaseReference databaseGoing= FirebaseDatabase.getInstance().getReference("Events").child("Going");
    DatabaseReference databaseBookmarks= FirebaseDatabase.getInstance().getReference("Events").child("Bookmarks");
    boolean going;
    boolean bookmarked=false;
    long notificationid=1;

    public EventsAdapter(List<ShowEventsModel> dataList, Context context) {
        this.dataList = dataList;
        this.context = context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.events_data_list,parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        final ShowEventsModel showEventsModel=dataList.get(position);

        firebaseFirestore.collection("Events").document(showEventsModel.getEventId())
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                if (task.isSuccessful())
                {
                    if (!task.getResult().exists())
                    {
                        firebaseFirestore.collection("Bookmarks").document(mAuth.getCurrentUser().getUid())
                                .collection("Posts").document(showEventsModel.getEventId())
                                .delete();
                    }
                }

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });

        setGoingBackground(showEventsModel,holder);
        setBookMarksBackground(showEventsModel,holder);

        if(showEventsModel.getImageUri().equals("No Image"))
        {
            holder.imageView.setImageResource(R.drawable.ic_image_darkblue_24dp);
        }
        else
        Picasso.get().load(showEventsModel.getImageUri()).fit().into(holder.imageView);

        monthGetter(showEventsModel);
        String date=String.valueOf(showEventsModel.getDate());
        String Alldate=strMonth+" "+date;


        holder.monthHead.setText(strMonth);
        holder.dateHead.setText(date);
        holder.eventname.setText(showEventsModel.getEventName());
        holder.allDate.setText(Alldate);
        holder.host.setText(showEventsModel.getHostedBy());

        firebaseDataGetter(showEventsModel);

       holder.timeAgo.setText(TimeAgo.from(showEventsModel.getCreate_at()));

       Bookmarks(holder,showEventsModel);
       goingPeople(holder,showEventsModel);



    }

    UserInfoModel userInfoModel;
    private void goingPeople(final MyViewHolder holder, final ShowEventsModel showEventsModel) {

        holder.goingTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                going=true;


                    databaseGoing.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                            if (going)
                            {
                                if (dataSnapshot.child(showEventsModel.getEventId()).hasChild(mAuth.getCurrentUser().getUid()))
                                {
                                    databaseGoing.child(showEventsModel.getEventId()).child(mAuth.getCurrentUser().getUid()).removeValue();
                                    setGoingBackground(showEventsModel,holder);
                                    going=false;

                                    int x=1;
                                    alarmNotifier(showEventsModel,x);
                                }
                                else
                                {
                                    setGoingBackground(showEventsModel,holder);

                                    firebaseFirestore.collection("UserInfo").document(mAuth.getCurrentUser().getUid()).get()
                                            .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                @Override
                                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                                                    userInfoModel=task.getResult().toObject(UserInfoModel.class);


                                                    int x=0;
                                                    alarmNotifier(showEventsModel,x);




                                                }
                                            });
                                    databaseGoing.child(showEventsModel.getEventId()).child(mAuth.getCurrentUser().getUid()).setValue(userInfoModel);

                                    going=false;

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

    private void alarmNotifier(ShowEventsModel showEventsModel,int x) {


        if (x==1)
        {
            Calendar startTime=Calendar.getInstance();
            startTime.setTimeInMillis(System.currentTimeMillis());
            startTime.clear();

            int hour=showEventsModel.getHour();
            int min=showEventsModel.getMin();
            int year=showEventsModel.getYear();
            int date=showEventsModel.getDate();
            int month=showEventsModel.getMonth();

            startTime.set(year,month,date);

            long alarmStartTime=startTime.getTimeInMillis();

            Intent intent=new Intent(context, AlarmReceiver.class);
            intent.putExtra("notificationId",notificationid);
            intent.putExtra("message1","The event is near");
            intent.putExtra("message2","The event is started");
            intent.putExtra("timemillis",alarmStartTime);



            PendingIntent alarmIntent= PendingIntent.getBroadcast(context,0,intent,PendingIntent.FLAG_CANCEL_CURRENT);

            AlarmManager alarmManager=(AlarmManager)context.getSystemService(Context.ALARM_SERVICE);



            alarmManager.set(AlarmManager.RTC_WAKEUP,alarmStartTime,alarmIntent);

        }

        if (x==0) {
            Calendar startTime = Calendar.getInstance();
            int hour = showEventsModel.getHour();
            int min = showEventsModel.getMin();
//            startTime.set(Calendar.YEAR,showEventsModel.getYear());
//            startTime.set(Calendar.MONTH,showEventsModel.getMonth());
            startTime.set(Calendar.HOUR_OF_DAY, hour);
            startTime.set(Calendar.MINUTE, min);
            startTime.set(Calendar.SECOND, 0);

            notificationid=System.currentTimeMillis();

            long alarmStartTime = startTime.getTimeInMillis();

            Intent intent = new Intent(context, AlarmReceiver.class);
            intent.putExtra("notificationId", notificationid);
            intent.putExtra("message1", showEventsModel.getEventName());
            intent.putExtra("message2", "The event is started");
            intent.putExtra("timemillis", alarmStartTime);


            if (alarmStartTime > System.currentTimeMillis()) {


                PendingIntent alarmIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);

                AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);


                alarmManager.set(AlarmManager.RTC_WAKEUP, alarmStartTime, alarmIntent);
            }
            else
            {
                PendingIntent pi = PendingIntent.getService(context, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
                AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);


                alarmManager.cancel(pi);

            }


        }




    }

    private void setGoingBackground(final ShowEventsModel showEventsModel, final MyViewHolder holder) {

        databaseGoing.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (mAuth.getCurrentUser()!=null)
                {
                    if (dataSnapshot.child(showEventsModel.getEventId()).hasChild(mAuth.getCurrentUser().getUid()))
                    {
                        holder.goingTv.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_star_red_24dp,0,0,0);

                        final int sdk = android.os.Build.VERSION.SDK_INT;
                        if(sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                            holder.goingTv.setBackgroundDrawable(ContextCompat.getDrawable(context, R.drawable.bg_circle_shape_blue) );
                        } else {
                            holder.goingTv.setBackground(ContextCompat.getDrawable(context, R.drawable.bg_circle_shape_blue));
                        }
                        holder.goingTv.setTextColor(context.getResources().getColor(R.color.white));
                    }
                    else
                    {

                        holder.goingTv.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_star_border_black_24dp,0,0,0);
                        holder.goingTv.setBackground(context.getDrawable(R.drawable.bg_circle_shape));
                        holder.goingTv.setTextColor(context.getResources().getColor(R.color.colorPrimaryDark));


                    }
                }

                long totalGoing=dataSnapshot.child(showEventsModel.getEventId()).getChildrenCount();

                if (totalGoing<1)
                {
                    holder.goingPeople.setText("0 people going");
                }
                else
                {
                    String x=totalGoing+" people going";
                    holder.goingPeople.setText(x);
                }

                firebaseFirestore.collection("Events").document(showEventsModel.getEventId())
                        .update("interested",totalGoing);




            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void Bookmarks(final MyViewHolder holder, final ShowEventsModel showEventsModel) {

        
        holder.bookmarkImageview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final DocumentReference documentReference=collectionReference.document(showEventsModel.getEventId());

//                final ShowEventsModel showEventsModel2=new ShowEventsModel(showEventsModel.getHostedBy(),showEventsModel.getEventName(),showEventsModel.getAddress(),showEventsModel.getDescription(),showEventsModel.getImageUri(),showEventsModel.getEventId(),showEventsModel.getYear(),showEventsModel.getMonth()+1,showEventsModel.getDate(),showEventsModel.geteYear(),showEventsModel.geteMonth()+1,showEventsModel.geteDate(),showEventsModel.getHour(),showEventsModel.getMin(),showEventsModel.geteHour(),showEventsModel.geteMin(),System.currentTimeMillis(),showEventsModel.getInterested());

                documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        
                        if (task.getResult().exists())
                        {
                            documentReference.delete();

                        }
                        else
                        {
                            documentReference.set(showEventsModel).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {



                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    
                                }
                            });
                        }
                        
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        
                    }
                });




                //realtime
                bookmarked=true;
                databaseBookmarks.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (mAuth.getCurrentUser()!=null)
                        {

                            if (bookmarked) {


                                if (dataSnapshot.child(mAuth.getCurrentUser().getUid()).hasChild(showEventsModel.getEventId())) {
                                    databaseBookmarks.child(mAuth.getCurrentUser().getUid()).child(showEventsModel.getEventId()).removeValue();
                                    setBookMarksBackground(showEventsModel, holder);
                                    bookmarked=false;

                                } else {
                                    databaseBookmarks.child(mAuth.getCurrentUser().getUid()).child(showEventsModel.getEventId()).setValue("Bookmarks");
                                    setBookMarksBackground(showEventsModel, holder);
                                    bookmarked=false;
                                }
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

    private void setBookMarksBackground(final ShowEventsModel showEventsModel, final MyViewHolder holder) {


        databaseBookmarks.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if(mAuth.getCurrentUser()!=null)
                {
                    if (dataSnapshot.child(mAuth.getCurrentUser().getUid()).hasChild(showEventsModel.getEventId()))
                    {
                        holder.bookmarkImageview.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_bookmark_darkblue_24dp,0,0,0);
                    }
                    else
                    {
                        holder.bookmarkImageview.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_bookmark_border_darkblue_24dp,0,0,0);

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
        return dataList.size();
    }



    public class MyViewHolder extends RecyclerView.ViewHolder {

        ImageView imageView;
        TextView monthHead,dateHead,eventname,allDate,host, goingPeople,timeAgo,goingTv,bookmarkImageview;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            imageView=itemView.findViewById(R.id.list_imageViewId);
            monthHead=itemView.findViewById(R.id.list_monthId);
            dateHead=itemView.findViewById(R.id.list_dateId);
            eventname=itemView.findViewById(R.id.list_eventNameId);
            allDate=itemView.findViewById(R.id.list_allDateId);
            host=itemView.findViewById(R.id.hostedbyId);
            goingPeople =itemView.findViewById(R.id.interestedPeopleid);
            timeAgo=itemView.findViewById(R.id.timeAgoId);
            bookmarkImageview=itemView.findViewById(R.id.bookmark_item);
            goingTv=itemView.findViewById(R.id.interestedTvId);



            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(clickListener!=null)
                    {
                        int position=getAdapterPosition();
                        if(position!= RecyclerView.NO_POSITION)
                        {
                            clickListener.onItemClick(view,position,dataList.get(getAdapterPosition()));
                        }
                    }
                }
            });

            //

            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {

                    if(clickListener2!=null)
                    {
                        int position=getAdapterPosition();
                        if(position!= RecyclerView.NO_POSITION)
                        {
                            clickListener2.onItemLongClick(view,position,dataList.get(getAdapterPosition()));
                        }
                    }

                    return false;
                }
            });

        }
    }

    public void  monthGetter(ShowEventsModel showEventsModel)
    {
        if(showEventsModel.getMonth()==1) strMonth="JAN";
        if(showEventsModel.getMonth()==2) strMonth="FEB";
        if(showEventsModel.getMonth()==3) strMonth="MAR";
        if(showEventsModel.getMonth()==4) strMonth="APR";
        if(showEventsModel.getMonth()==5) strMonth="MAY";
        if(showEventsModel.getMonth()==6) strMonth="JUN";
        if(showEventsModel.getMonth()==7) strMonth="JUL";
        if(showEventsModel.getMonth()==8) strMonth="AUG";
        if(showEventsModel.getMonth()==9) strMonth="SEP";
        if(showEventsModel.getMonth()==10) strMonth="OCT";
        if(showEventsModel.getMonth()==11) strMonth="NOV";
        if(showEventsModel.getMonth()==12) strMonth="DEC";


    }

    //firebase
    public void firebaseDataGetter(ShowEventsModel showEventsModel)
    {

        long interest=showEventsModel.getInterested();

        interestedPeople=String.valueOf(interest);
    }

    //searchfilter
    public void setFilter(List<ShowEventsModel> mList) {
        dataList=new ArrayList<>();
        dataList.addAll(mList);
        notifyDataSetChanged();
    }

    //ClickListener
    private OnItemClickListener clickListener;
    public interface OnItemClickListener{
        void onItemClick(View view, int position, ShowEventsModel showEventsModel);
    }

    public void setOnItemClickListener(OnItemClickListener listener)
    {
        clickListener=listener;
    }

    //longClickItem
    private OnItemLongClickListener clickListener2;
    public interface OnItemLongClickListener{
        void onItemLongClick(View view, int position, ShowEventsModel showEventsModel);
    }

    public void setOnItemLongClickListener(OnItemLongClickListener listener)
    {
        clickListener2=listener;
    }
}
