package com.example.heartbeatapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.format.Time;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
//import android.widget.Toolbar;

import com.ekn.gruzer.gaugelibrary.ArcGauge;
import com.ekn.gruzer.gaugelibrary.Range;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.IDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.Utils;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.Viewport;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.security.AccessController;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import static android.graphics.Color.BLUE;
import static android.graphics.Color.GRAY;
import static android.graphics.Color.RED;
import static android.graphics.Color.WHITE;
import static android.graphics.Color.YELLOW;
import static android.graphics.Color.red;

public class MainActivity extends AppCompatActivity {
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy:MM:dd-HH:mm:ss");
    Toolbar toolbar;
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    ListView listView;
    ArrayList<HistoryUser> arrayList;
    MenuAdapter menuAdapter;
    TextView tvBpm, tvSpo2, tvTB, tvTB2;
    ImageView imHeat;
    ArcGauge arcGauge1; ArcGauge arcGauge2;
    GraphView graphView;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference reference = database.getReference("Current");
    DatabaseReference reference1 = database.getReference("History");
    String bpm, spo2, text1, text2, text3;
    long timeFirebase,timeTem = 0;
    Button btnClick;
    boolean run = false;
    float bpmOut, spo2Out,notifOut=1;
  //  float mang[] = {0,0,0,0,0,0,0,0,0};
    float mang[] = {0,0,0,0,0,0,0,0,0};
    float mang1[] = {0,0,0,0,0,0,0,0,0};
    LineGraphSeries<DataPoint> series;
    LineGraphSeries<DataPoint> series1;
    String notif = "1";
    Spinner spAge, spSex, spState;
    int i;
    int warn3=1;
    // warn3=1: nông độ oxy trong máu tốt
    // warn3=0: nồng độ oxy trong máu thấp, cần chú ý sức khỏe
    // warn3=-1: nồng độ OXY trong máu quá thấp, cần đến bác sĩ
    //XỬ LÍ
    int warn2=-1;//0 là bth, 1 là qua thap, 2 la qua cao, 3 la that thuong
    int warn1=-2;
    //-1 là quá thấp; 0 là hoạt động nhẹ, 1 là hoạt động cường độ tb
    // 2 là mạnh, 3 là quá mạnh nên nghỉ ngơi, 4 là nguy hiểm phải nghỉ ngơi gấp
    int age =0;
    boolean sex=true; //false is men, true is woman
    int state = 0; //1 is do warn1, 0 is relax
    //1-5;6-10;11-14;15-17;18-25;26-35;36-45;46-55;56-65;65+
    int[] agerange={1,6,11,15,18,26,36,46,56,66};
    float[] hbmmin= {80,70,60,60,49,49,50,50,51,50};//men's heart beat when relax
    float[] hbmmax= {130,120,105,100,82,82,83,84,82,80};
    float[] hbwmax= {80,70,60,56,54,54,54,54,54,54};//woman's heart beat when relax
    float[] hbwmin= {130,120,105,96,85,83,85,84,84,84};
    float[] hbam={105,95,82,80,72,73,73,74,74,72};
    float[] hbaw={105,95,82,77,76,75,76,76,76,75};
    float[] luu = new float[12];
    long timeDelay = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        anhxa();
        receiveFromFB();
        //set các giá trị ban đầu
        bpmOut = 0; bpm="0";
        spo2Out = 0; spo2 ="0";
        tvTB.setText("loading...");
        displayArc();
        set3Spinner();
        actionToolbar();
        ////////clickbutton
        btnClick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                run = !run;
                LoadData();
                Log.e("hung",String.valueOf(run));
            }
        });
    }

    private void HistoryTable() {
//        arrayList = new ArrayList<>();
//        arrayList.add(new HistoryUser("hưng3","test1"));
//        arrayList.add(new HistoryUser(bpm,"test1"));
//        menuAdapter = new MenuAdapter(this,R.layout.activity_navigation_menu,arrayList);
//        listView.setAdapter(menuAdapter);
    }

    private void anhxa() {
        drawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        navigationView = (NavigationView) findViewById(R.id.navigationView);
        listView = (ListView) findViewById(R.id.lv_navi);
        graphView = (GraphView) findViewById(R.id.graphview);
        arcGauge1 = (ArcGauge) findViewById(R.id.bmp);
        arcGauge2 = (ArcGauge) findViewById(R.id.spo2);
        btnClick = (Button) findViewById(R.id.btnClick);
        tvBpm = (TextView) findViewById(R.id.tvBpm);
        tvSpo2 = (TextView) findViewById(R.id.tvSpo2);
        tvTB = (TextView) findViewById(R.id.tvTB);
        tvTB2 = (TextView) findViewById(R.id.tvTB2);
        imHeat = (ImageView) findViewById(R.id.heart);
        spAge = (Spinner) findViewById(R.id.sp_age);
        spSex = (Spinner) findViewById(R.id.sp_sex);
        spState = (Spinner) findViewById(R.id.sp_state);
    }
    private void displayGraphView(){
        int u=0,k=0;
        //GraphView
        series = new LineGraphSeries<DataPoint>();
        series1 = new LineGraphSeries<DataPoint>();
        for(int t=0; t<9;t++){
            series1.appendData(new DataPoint(u++, mang1[t]), true, 9);
            series.appendData(new DataPoint(k++, mang[t]), true, 9);

        }
        graphView.addSeries(series1);
        graphView.addSeries(series);
        series.setColor(Color.parseColor("#F6070F"));
        series1.setColor(Color.parseColor("#009688"));
        // customize a little bit viewport
        Viewport viewport = graphView.getViewport();
        viewport.setYAxisBoundsManual(true);
        viewport.setMinY(0);
        viewport.setMaxY(250);
        viewport.setScrollable(true);
    }

    private void actionToolbar() {
        setSupportActionBar(toolbar);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        toolbar.setNavigationIcon(R.drawable.ic_action_menu);
//        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                drawerLayout.openDrawer(GravityCompat.START);
//            }
//        });
    }
    void LoadData(){
        if(run){
            checkInfo();
            for (int j=0;j<10;j++) luu[j]=(sex==true) ? hbaw[i]:hbam[i];
            btnClick.setBackground(getDrawable(R.drawable.cancel));//icon
            EffectHeart();
            final ArrayList<String> arrayList1 = new ArrayList<>();
            reference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    arrayList1.clear();
                    for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                        arrayList1.add(dataSnapshot.getValue().toString());
                    }
                    if(checkInfo()){
                        for (int j=0;j<12;j++) luu[j]=(sex==true) ? hbaw[i]:hbam[i];
                    }
                    bpm = arrayList1.get(0);
                    spo2 = arrayList1.get(1);
                    timeFirebase = (long) Long.parseLong(arrayList1.get(2));

                    bpmOut = (float) Float.parseFloat(bpm);
                    bpmOut = (float)Math.round(bpmOut * 100) / 100;
                    spo2Out = (float)Float.parseFloat(spo2);
                    spo2Out = (float)Math.round(spo2Out*100)/100;
                    if(timeFirebase!=timeTem){
                        graphView.removeAllSeries();//xóa biểu đồ cũ
                        //vẽ
                        for(int t=0;t<8;t++){
                            mang[t]=mang[t+1];
                            mang1[t]=mang1[t+1];
                        }
                        mang[8]=bpmOut;
                        Log.e("hung",String.valueOf(mang[8]));
                        Log.e("hung","-------");
                        mang1[8]=spo2Out;
                        //thắng
                        for (int j=0;j<11;j++) luu[j]=luu[j+1];
                        luu[11]=bpmOut;
                        Log.e("Bo",String.valueOf(warn2));
                        checkState();
                        Log.e("Bo2",String.valueOf(warn2));
                        displayGraphView();
                        displayArc();
                        checkNotification();
                        timeTem = timeFirebase;
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.e("Bo","kcn");
                }
            });
        }else{
            Log.e("hung","-------");
            btnClick.setBackground(getDrawable(R.drawable.save));
            finish();
        }
    }
    private void displayArc(){
        tvBpm.setText(String.valueOf(bpmOut));
        tvSpo2.setText(String.valueOf(spo2Out));

        Range range = new Range();
        Range range1 = new Range();
        Range range2 = new Range();

        Range range3 = new Range();
        Range range4 = new Range();
        Range range5 = new Range();

        range.setColor(Color.parseColor("#00b20b"));
        range.setFrom(0.0);
        range.setTo(50.0);
        range1.setColor(Color.parseColor("#E3E500"));
        range1.setFrom(50.0);
        range1.setTo(100.0);
        range2.setColor(Color.parseColor("#ce0000"));
        range2.setFrom(100.0);
        range2.setTo(400.0);

        range3.setColor(Color.parseColor("#00b20b"));
        range3.setFrom(0.0);
        range3.setTo(50.0);
        range4.setColor(Color.parseColor("#E3E500"));
        range4.setFrom(50.0);
        range4.setTo(100.0);
        range5.setColor(Color.parseColor("#ce0000"));
        range5.setFrom(100.0);
        range5.setTo(200.0);

        arcGauge1.addRange(range);
        arcGauge1.addRange(range1);
        arcGauge1.addRange(range2);
        arcGauge1.setUseRangeBGColor(true);
        arcGauge1.setValueColor(WHITE);

        arcGauge2.addRange(range3);
        arcGauge2.addRange(range4);
        arcGauge2.addRange(range5);
        arcGauge2.setUseRangeBGColor(true);
        arcGauge2.setValueColor(WHITE);

        arcGauge1.setMinValue(0.0);
        arcGauge1.setMaxValue(400.0);
        arcGauge1.setValue(bpmOut);

        arcGauge2.setMinValue(0.0);
        arcGauge2.setMaxValue(100.0);
        arcGauge2.setValue(spo2Out);

    }
    void EffectHeart(){
        Animation mAnimation = new AlphaAnimation(1, 0);
        mAnimation.setDuration(1000);
        mAnimation.setRepeatCount(Animation.INFINITE);
        mAnimation.setRepeatMode(Animation.REVERSE); imHeat.startAnimation(mAnimation);
    }
    void sentNotification1(){
        Uri sound = Uri.parse("android.resource://"+getPackageName()+"/"+R.raw.warrning);
        Notification builder = new NotificationCompat.Builder(this, myNotification.CHANNEL_ID)
                .setSmallIcon(R.drawable.notification)
                .setContentTitle("SPO2-CẢNH BÁO!!!")
                .setContentText("SPO2 quá thấp, sức khỏe không tốt")
                .setSound(sound)
                .setColor(getResources().getColor(R.color.colorAccent))
                .build();
        NotificationManagerCompat notificationCompat = NotificationManagerCompat.from(this);
        notificationCompat.notify(getIDNotif(),builder);
    }
    void sentNotification2(){
        Notification builder = new NotificationCompat.Builder(this, myNotification.CHANNEL_ID)
                .setSmallIcon(R.drawable.notification)
                .setContentTitle("BPM-CẢNH BÁO!!!")
                .setContentText("Nguy hiểm! Bạn nên nghỉ ngơi")
                .setColor(getResources().getColor(R.color.colorAccent))
                .build();

        NotificationManagerCompat notificationCompat = NotificationManagerCompat.from(this);
        notificationCompat.notify(getIDNotif(),builder);
    }
    void sentNotification3(){
        Notification builder = new NotificationCompat.Builder(this, myNotification.CHANNEL_ID)
                .setSmallIcon(R.drawable.notification)
                .setContentTitle("BPM-CẢNH BÁO!!!")
                .setContentText("Nhịp Tim không ổn định!")
                .setColor(getResources().getColor(R.color.colorAccent))
                .build();

        NotificationManagerCompat notificationCompat = NotificationManagerCompat.from(this);
        notificationCompat.notify(getIDNotif(),builder);
    }
    private int getIDNotif(){
        return 1;
    }
    void checkNotification(){
        if(state==0){
            if(warn2==0){
                String currentDateandTime = sdf.format(new Date());
                tvTB.setText("Nhịp tim bình thường");
            }
            else if(warn2==1){
                String currentDateandTime = sdf.format(new Date());
                tvTB.setText("Nhịp tim quá thấp");
                sendToFB(currentDateandTime,"BPM quá thấp");
                receiveFromFB();
            }else if(warn2==2){
                    String currentDateandTime = sdf.format(new Date());
                    tvTB.setText("Nhịp tim quá cao");
                    sendToFB(currentDateandTime,"BPM quá cao");
                    receiveFromFB();
            }else if(warn2==3) {
                        String currentDateandTime = sdf.format(new Date());
                        tvTB.setText("Nhịp tim không ổn định");
                        sendToFB(currentDateandTime,"BPM không ổn định");
                        receiveFromFB();
                        sentNotification3();
                        tvTB.setTextColor(Color.RED);
                    };
        }
        if(state==1){
            if(warn1==-1) {
                String currentDateandTime = sdf.format(new Date());
                tvTB.setText("Nhịp tim quá thấp");
                sendToFB(currentDateandTime,"BPM quá thấp");
                receiveFromFB();
            }
            else if(warn1==0) tvTB.setText("Hoạt động nhẹ");
                else if(warn1==1) tvTB.setText("Cường độ hoạt động trung bình");
                    else if(warn1==2) tvTB.setText("Cường độ hoạt động mạnh");
                        else if(warn1==3){
                            tvTB.setText("Bạn nên nghỉ ngơi");
                            sentNotification2();
                        }
        }
        if(warn3 == 1){
            tvTB2.setText("Nồng độ oxi trong máu tốt");
        }else if(warn3==0){
            tvTB2.setText("Chú ý - Spo2 thấp");
            String currentDateandTime = sdf.format(new Date());
            sendToFB(currentDateandTime,"SPO2 thấp");
            receiveFromFB();
        }else {
            tvTB2.setText("Nguy hiểm - Spo2 rất thấp");
            tvTB2.setTextColor(RED);
            String currentDateandTime = sdf.format(new Date());
            sendToFB(currentDateandTime,"SPO2 rất thấp");
            receiveFromFB();
            sentNotification1();
        }
    }
    void set3Spinner(){
        ArrayList<Integer> arrayListAge = new ArrayList<Integer>();
        for(int i = 0; i<=100;i++){
            arrayListAge.add(i);
        }
        ArrayList<String> arrayListSex = new ArrayList<String>();
        arrayListSex.add("Nam");
        arrayListSex.add("Nữ");

        ArrayList<String> arrayListState = new ArrayList<String>();
        arrayListState.add("Nghỉ ngơi");
        arrayListState.add("Hoạt động");

        ArrayAdapter arrayAdapterAge = new ArrayAdapter(this,android.R.layout.simple_spinner_item,arrayListAge);
        ArrayAdapter arrayAdapterSex = new ArrayAdapter(this,android.R.layout.simple_spinner_item,arrayListSex);
        ArrayAdapter arrayAdapterState = new ArrayAdapter(this,android.R.layout.simple_spinner_item,arrayListState);

        spAge.setAdapter(arrayAdapterAge);
        spSex.setAdapter(arrayAdapterSex);
        spState.setAdapter(arrayAdapterState);
    }
    boolean checkInfo(){
        boolean dem =false;
        int z;
        text1 = spAge.getSelectedItem().toString();
        age = Integer.valueOf(text1);
        Log.e("tuoi",String.valueOf(age));
        for( z=0;z<9;z++){
            if(age>=agerange[z] && age<agerange[z+1]) break;
        }
        if(z!=i){
            dem =true;
            i=z;
        }
        text2 = spSex.getSelectedItem().toString();
        Log.e("thang",String.valueOf(i));
        if(text2=="Nam"){
            if(sex==true) dem =true;
            sex = false;
        }else{
            if(sex==false) dem =true;
            sex = true;
        }
        text3 = spState.getSelectedItem().toString();

        if(text3=="Nghỉ ngơi"){
            if(state==1) dem =true;
            state =0;
        }else{
            if(state==0) dem =true;
            state =1;
        }
        return dem;
    }
    void checkState() {
        int dem1 = 0;
        int dem2 = 0;
        if (mang1[8]>=95) warn3=1;
        else if (mang1[8]<95 && mang1[8]>=90) warn3=0;
        else warn3 = -1;
        if (state == 0) {
            if (sex == true) {
                for (int j = 0; j < 12; j++) {
                    if (luu[j] > hbwmax[i]) dem2++;
                    if (luu[j] < hbwmin[i]) dem1++;
                }
            } else {
                for (int j = 0; j < 12; j++) {
                    if (luu[j] > hbmmax[i]) dem2++;
                    if (luu[j] < hbmmin[i]) dem1++;
                }
            }
            if (dem1 + dem2 > 3 && dem1>=1 &&dem2>=1) warn2 = 3;
            else if (dem1 > 3) warn2 = 1;
            else if (dem2 > 3) warn2 = 2;
            else warn2 = 0;

        } else {
            float MHR = 220 - age;
            float HRR;
            double avr = 0;
            double HRR50, HRR70, HRR85;
            if (sex == false) {
                HRR = MHR - hbam[i];
                HRR50 = 0.5 * HRR + hbam[i];
                HRR70 = 0.7 * HRR + hbam[i];
                HRR85 = 0.85 * HRR + hbam[i];
            } else {
                HRR = MHR - hbaw[i];
                HRR50 = 0.5 * HRR + hbaw[i];
                HRR70 = 0.7 * HRR + hbaw[i];
                HRR85 = 0.85 * HRR + hbaw[i];
            }
        //    for (int j = 5; j < 10; j++) avr += luu[j] / 5;
            if (sex == false) {
                if (luu[11] < hbmmin[i]) warn1 = -1;
                else if (luu[11] < HRR50) warn1 = 0;
                else if (luu[11] < HRR70) warn1 = 1;
                else if (luu[11] < HRR85) warn1 = 2;
                else warn1 = 3;
            } else {
                if (avr < hbwmin[i]) warn1 = -1;
                else if (luu[11] < HRR50) warn1 = 0;
                else if (luu[11] < HRR70) warn1 = 1;
                else if (luu[11] < HRR85) warn1 = 2;
                else warn1 = 3;
            }
        }
    }
    void sendToFB(String time,String tb){
        reference1.child(String.valueOf(time)).setValue(tb);
    }
    void receiveFromFB() {
        final ArrayList<String> arrayListValue = new ArrayList<>();
        final ArrayList<String> arrayListKey = new ArrayList<>();
        final String y[] =new String[30];
        reference1.limitToLast(30).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                arrayListValue.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    arrayListValue.add(dataSnapshot.getValue().toString());
                    arrayListKey.add(dataSnapshot.getKey().toString());
                }
                arrayList = new ArrayList<>();
                for(int v = 0; v<arrayListValue.size();v++){
                    arrayList.add(new HistoryUser(arrayListValue.get(v),arrayListKey.get(v)));
                }
                Collections.sort(arrayList);
                menuAdapter = new MenuAdapter(MainActivity.this,R.layout.activity_navigation_menu,arrayList);
                listView.setAdapter(menuAdapter);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("Bo", "kcn");
            }
        });

    }
}
