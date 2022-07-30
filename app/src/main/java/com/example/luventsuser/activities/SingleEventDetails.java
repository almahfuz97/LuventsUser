package com.example.luventsuser.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.luventsuser.R;
import com.google.android.material.tabs.TabItem;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.util.GregorianCalendar;
import java.util.Objects;

public class SingleEventDetails extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;
    private int  STORAGE_PERMISSION_CODE = 1;
    private ImageView imageView;
    private TextView dateTimeTv,locationTv,singleEventName,singleHostedBy,singleGoing;

    ViewPager viewPager;
    TabLayout tabLayout;
    PagerAdapter pagerAdapter;
    TabItem aboutItem,discussionItem;
    ProgressDialog progressDialog;
    Toolbar toolbar;
    int x=0;
    Uri mUri;
    int  refresh=1;

    FirebaseFirestore firebaseFirestore=FirebaseFirestore.getInstance();


    Spinner spinner;
    private AlertDialog alertDialog,alertDialog2;
    private TextView editEventDateStartTv,editEventDateEndTv,editEventTimeStartTv,editEventTimeEndTv,editEventCancelTv;
    private Spinner editEventSpinner;
    private Button editEventSaveButton,editEventchoosePhotoButton;
    private ImageView editEventPostImageview;
    private EditText editEventEventNameEt,editEventAdressEt,editEventDescEt;
    Toolbar editEventToolbar;



    String eventName;
    String hostedBy;
    String description;
    String going;
    String eventId;
    int startDate1;
    int endDate1;
    int StartHour1;
    int EndHour1;
    int startMin1;
    int EndMin1;
    int startYear1;
    int endYear1;
    int startMonth1;
    int endMonth1;
    String address1,strMonth,timeFormat,mdayOfWeek;
    SharedPreferences.Editor editor;

    int tab=0;

    String imgUri;
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_event_details);

        tabLayout=findViewById(R.id.tabLayoutId);
        viewPager=findViewById(R.id.singleEventviewPagerId);
        aboutItem=findViewById(R.id.aboutId);
        discussionItem=findViewById(R.id.discussionId);
        toolbar=findViewById(R.id.customToolbarId);

        singleEventName=findViewById(R.id.singleEventNameId);
        singleHostedBy=findViewById(R.id.singleHostedById);
        imageView=findViewById(R.id.singleImageViewId);
        dateTimeTv=findViewById(R.id.singleDateTimeId);
        locationTv=findViewById(R.id.singleAddressId);

        pagerAdapter=new com.example.luventsuser.Adapters.PagerAdapter(getSupportFragmentManager(),tabLayout.getTabCount());
        viewPager.setAdapter(pagerAdapter);

        tabloutListener();

        sharedPreferences=getSharedPreferences("UserEvent", Context.MODE_PRIVATE);
        description = sharedPreferences.getString("description","");
        eventName=sharedPreferences.getString("eventName","");
        eventId=sharedPreferences.getString("eventId","");
        address1=sharedPreferences.getString("address","");
        startDate1=sharedPreferences.getInt("startDate",0);
        endDate1=sharedPreferences.getInt("endDate",0);
        StartHour1=sharedPreferences.getInt("startHour",0);
        EndHour1=sharedPreferences.getInt("endHour",0);
        startMin1=sharedPreferences.getInt("startMin",0);
        EndMin1=sharedPreferences.getInt("endMin",0);
        startMonth1=sharedPreferences.getInt("startMonth",0);
        endMonth1=sharedPreferences.getInt("endMonth",0);
        startYear1=sharedPreferences.getInt("startYear",0);
        endYear1=sharedPreferences.getInt("endYear",0);
        imgUri=sharedPreferences.getString("imageUri","");
        going=sharedPreferences.getString("going","");
        hostedBy=sharedPreferences.getString("hostedBy","");
        refresh=sharedPreferences.getInt("refresh2",0);

        monthGetter();

        String dateTime=mdayOfWeek+", "+strMonth+" "+startDate1+", "+startYear1+" at "+timeFormat;

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(eventName);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //settingToTextview
        String host="Hosted by "+hostedBy;
        singleEventName.setText(eventName);
        singleHostedBy.setText(host);
        if (imgUri.equals("No Image")){
            imageView.setImageResource(R.drawable.ic_image_darkblue_24dp);
            x=1;
        }
        else {
            Picasso.get().load(imgUri).fit().into(imageView);
            x=1;
        }
        // progressDialog.cancel();

        dateTimeTv.setText(dateTime);
        locationTv.setText(address1);

        Intent intent=getIntent();
        tab=intent.getIntExtra("singleEventTab",0);

        switchTab();

    }

    private void switchTab() {

        if (tab==0){
            viewPager.setCurrentItem(0);
        }
        if (tab==1)viewPager.setCurrentItem(1);


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater=getMenuInflater();
        inflater.inflate(R.menu.menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId())
        {
            case R.id.logoutid:
                FirebaseAuth.getInstance().signOut();
                Intent intent=new Intent(SingleEventDetails.this,MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
                break;


        }
        return super.onOptionsItemSelected(item);
    }

    public void choosePhoto()
    {
        if (ContextCompat.checkSelfPermission(Objects.requireNonNull(this),
                Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {

            Intent intent = new Intent().setType("image/*").setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(intent, PICK_IMAGE_REQUEST);

        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==PICK_IMAGE_REQUEST && resultCode== Activity.RESULT_OK && data!=null && data.getData()!=null)
        {
            mUri=data.getData();

            Picasso.get().load(mUri).fit().into(editEventPostImageview);
            x=1;

        }
    }




    private String getFileExtension(Uri uri) {


        ContentResolver resolver = this.getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(resolver.getType(uri));
    }

    @Override
    public boolean onSupportNavigateUp() {

        onBackPressed();
        finish();
        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(this,BottomNavigation.class));
        finish();
    }

    private void tabloutListener() {

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                //DiscussionFragment discussionFragment=new DiscussionFragment();


                viewPager.setCurrentItem(tab.getPosition());



            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
    }
    public void  monthGetter()
    {
        if(startMonth1==1) strMonth="January";
        if(startMonth1==2) strMonth="February";
        if(startMonth1==3) strMonth="March";
        if(startMonth1==4) strMonth="April";
        if(startMonth1==5) strMonth="May";
        if(startMonth1==6) strMonth="June";
        if(startMonth1==7) strMonth="July";
        if(startMonth1==8) strMonth="August";
        if(startMonth1==9) strMonth="September";
        if(startMonth1==10) strMonth="October";
        if(startMonth1==11) strMonth="November";
        if(startMonth1==12) strMonth="December";

        //timeformat

        if (StartHour1>12)
        {
            int x=StartHour1-12;

            if(startMin1<10)timeFormat=x+":0"+startMin1+" am";
            else timeFormat=x+":"+startMin1+" am";
        }
        else {
            if (StartHour1==0)StartHour1=12;

            if(startMin1<10)timeFormat=StartHour1+":0"+startMin1+" am";
            else timeFormat=StartHour1+":"+startMin1+" am";
        }

        //dayOfweek
        GregorianCalendar date2 = new GregorianCalendar(startYear1, startMonth1, startDate1-2);

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
