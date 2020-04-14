package org.pcc.repeatinglocalnotifications;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

import org.pcc.repeatinglocalnotifications.notification.AlarmReceiver;
import org.pcc.repeatinglocalnotifications.notification.NotificationHelper;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;


public class HomeActivity extends BaseActivity {

    ImageView clickcounter;
    public AlertDialog myDialog;
    AppWidgetManager appWidgetManager;
    ImageView minus;
    int[] widgetIds;
    int clicks;
    TextView change;
    private static ViewPager mPager;
    private static int currentPage = 0;
    private static int NUM_PAGES = 0;
    private ArrayList<ImageModel> imageModelArrayList;
    AdView adView;

    private int[] myImageList = new int[]{R.drawable.first, R.drawable.second,
            R.drawable.handdtwo,R.drawable.handone
            ,R.drawable.unnamed,R.drawable.second};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setIcon(R.drawable.logotop);
        getSupportActionBar().setTitle(R.string.app_name);

        MobileAds.initialize(this,"ca-app-pub-8592398596825727~7805295370");
        adView= (AdView)  findViewById(R.id.adview1);
       // adView = findViewById(R.id.adview2);
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);


        imageModelArrayList = new ArrayList<>();
        imageModelArrayList = populateList();

        init();

       clickcounter = (ImageView) findViewById(R.id.clicksTextView);
        change = (TextView) findViewById(R.id.change);
        minus = (ImageView) findViewById(R.id.minus);

        clickcounter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                clicks = getSharedPreferences("sp", MODE_PRIVATE).getInt("clicks", 0);
                if(clicks == 0){
                    showDialog(HomeActivity.this);
                }else {
                    clicks++;
                    getSharedPreferences("sp", MODE_PRIVATE)
                            .edit()
                            .putInt("clicks", clicks)
                            .commit();
                    change.setText(String.valueOf(clicks));
                    appWidgetManager = AppWidgetManager.getInstance(HomeActivity.this);
                    widgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(HomeActivity.this, MyAppWidget.class));
                    for (int appWidgetId : widgetIds) {
                        MyAppWidget.updateAppWidget(getApplicationContext(), appWidgetManager, appWidgetId);
                    }
                }
            }
        });



        minus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clicks = getSharedPreferences("sp", MODE_PRIVATE).getInt("clicks", 0);
                if(clicks == 0){
                    showDialog(HomeActivity.this);
                }else {
                    clicks--;
                    getSharedPreferences("sp", MODE_PRIVATE)
                            .edit()
                            .putInt("clicks", clicks)
                            .commit();
                    change.setText(String.valueOf(clicks));
                    appWidgetManager = AppWidgetManager.getInstance(HomeActivity.this);
                    widgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(HomeActivity.this, MyAppWidget.class));
                    for (int appWidgetId : widgetIds) {
                        MyAppWidget.updateAppWidget(getApplicationContext(), appWidgetManager, appWidgetId);
                    }
                }
            }
        });
    }


    private ArrayList<ImageModel> populateList(){

        ArrayList<ImageModel> list = new ArrayList<>();

        for(int i = 0; i < 6; i++){
            ImageModel imageModel = new ImageModel();
            imageModel.setImage_drawable(myImageList[i]);
            list.add(imageModel);
        }

        return list;
    }



    private void init() {

        mPager = (ViewPager) findViewById(R.id.pager);
        mPager.setAdapter(new SlidingImage_Adapter(HomeActivity.this,imageModelArrayList));

//        CirclePageIndicator indicator = (CirclePageIndicator)
//                findViewById(R.id.indicator);
//
//        indicator.setViewPager(mPager);
//
//        final float density = getResources().getDisplayMetrics().density;
//
////Set circle indicator radius
//        indicator.setRadius(10 * density);

        NUM_PAGES =imageModelArrayList.size();

        // Auto start of viewpager
        final Handler handler = new Handler();
        final Runnable Update = new Runnable() {
            public void run() {
                if (currentPage == NUM_PAGES) {
                    currentPage = 0;
                }
                mPager.setCurrentItem(currentPage++, true);
            }
        };
        Timer swipeTimer = new Timer();
        swipeTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                handler.post(Update);
            }
        }, 5000, 5000);

//        // Pager listener over indicator
//        indicator.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
//
//            @Override
//            public void onPageSelected(int position) {
//                currentPage = position;
//
//            }
//
//            @Override
//            public void onPageScrolled(int pos, float arg1, int arg2) {
//
//            }
//
//            @Override
//            public void onPageScrollStateChanged(int pos) {
//
//            }
//        });

    }


    @Override
    int getContentViewId() {
        return R.layout.activity_home;

    }

    public void showDialog(Context context) {
        if( myDialog != null && myDialog.isShowing() ) return;

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Hand Wah");
        builder.setMessage("This is your counter that how many times you wash your hand!!");

        builder.setPositiveButton("ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int arg1) {
                dialog.dismiss();
                clicks++;
                getSharedPreferences("sp", MODE_PRIVATE)
                        .edit()
                        .putInt("clicks", clicks)
                        .commit();
                change.setText(String.valueOf(clicks));
                appWidgetManager = AppWidgetManager.getInstance(HomeActivity.this);
                widgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(HomeActivity.this, MyAppWidget.class));
                for (int appWidgetId : widgetIds) {
                    MyAppWidget.updateAppWidget(getApplicationContext(), appWidgetManager, appWidgetId);
                }
            }});
        builder.setCancelable(false);
        myDialog = builder.create();
        myDialog.show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        int clicks = getSharedPreferences("sp", MODE_PRIVATE).getInt("clicks", 0);
        ((TextView) findViewById(R.id.change)).setText(String.valueOf(clicks));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // action with ID action_refresh was selected
            case R.id.widget:
                Intent i = new Intent(HomeActivity.this, WidgetInstruction.class);
                startActivity(i);
                finish();
                break;

            default:
                break;
        }

        return true;
    }


    @Override
    int getNavigationMenuItemId() {
        return R.id.navigation_home;
    }

}
