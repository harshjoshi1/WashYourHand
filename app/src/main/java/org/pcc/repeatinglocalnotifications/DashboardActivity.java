package org.pcc.repeatinglocalnotifications;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.NumberPicker;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

import org.pcc.repeatinglocalnotifications.notification.AlarmReceiver;
import org.pcc.repeatinglocalnotifications.notification.NotificationHelper;

import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class DashboardActivity extends BaseActivity {
    private Context mContext;
    private static AlarmManager alarmManagerElapsed;
    private static PendingIntent alarmIntentElapsed;
    int newValue;
    int appWidgetId;
    TextView clickcounter;
    public AlertDialog myDialog;
    AppWidgetManager appWidgetManager;
    int[] widgetIds;
    int clicks;
    NumberPicker np;
    AdView adView;
    TimePicker timePicker, simpleTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setIcon(R.drawable.logotop);
        getSupportActionBar().setTitle(R.string.app_name);
        timePicker = (TimePicker) findViewById(R.id.simpleTimePicker); // wake up
       // simpleTime = (TimePicker) findViewById(R.id.simpleTime); // sleep

        MobileAds.initialize(this,"ca-app-pub-8592398596825727~7805295370");
        adView= (AdView)  findViewById(R.id.adview1);
        // adView = findViewById(R.id.adview2);
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);
        TextView tv = (TextView) findViewById(R.id.tv);
        np = (NumberPicker) findViewById(R.id.np);

        np.setMinValue(0);
        np.setMaxValue(59);
        np.setWrapSelectorWheel(true);
        np.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                tv.setText("You have set \n" + newVal + " minutes \n reminder to \n Wash your Hands");
                newValue = newVal;
            }
        });
    }

    private void setAlarm(long time) {
        //getting the alarm manager
        AlarmManager am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        //creating a new intent specifying the broadcast receiver
        Intent i = new Intent(this, AlarmReceiver.class);

        //creating a pending intent using the intent
        PendingIntent pi = PendingIntent.getBroadcast(this, 0, i, 0);

        //setting the repeating alarm that will be fired every day
        am.setRepeating(AlarmManager.RTC, time, AlarmManager.INTERVAL_DAY, pi);
        Toast.makeText(this, "Alarm is set", Toast.LENGTH_SHORT).show();
    }

    @Override
    int getContentViewId() {
        return R.layout.activity_dashboard;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.widget:
                Intent i = new Intent(DashboardActivity.this, WidgetInstruction.class);
                startActivity(i);
                finish();
                break;

            case R.id.info:
                alarmManagerElapsed.cancel(alarmIntentElapsed);
                NotificationHelper.disableBootReceiver(mContext);
                System.exit(0);

                break;

            default:
                break;
        }

        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_main, menu);
        MenuItem item = (MenuItem) menu.findItem(R.id.switchId);

        item.setActionView(R.layout.show_protected_switch);
        Switch switchAB = (Switch) item.getActionView().findViewById(R.id.switchAB);
        switchAB.setChecked(false);

        switchAB.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {

                   Intent intent = new Intent(DashboardActivity.this, AlarmReceiver.class);

                        Calendar calendars = Calendar.getInstance();
                        calendars.setTimeInMillis(System.currentTimeMillis());

                        //wake up
                        if (android.os.Build.VERSION.SDK_INT >= 23) {
                            calendars.set(calendars.get(Calendar.YEAR), calendars.get(Calendar.MONTH), calendars.get(Calendar.DAY_OF_MONTH),
                                    timePicker.getHour(), timePicker.getMinute(), 0);
                        } else {
                            calendars.set(calendars.get(Calendar.YEAR), calendars.get(Calendar.MONTH), calendars.get(Calendar.DAY_OF_MONTH),
                                    timePicker.getCurrentHour(), timePicker.getCurrentMinute(), 0);
                        }
                        //setAlarm(calendars.getTimeInMillis());

                        //this is notification wake up
                        alarmIntentElapsed = PendingIntent.getBroadcast(DashboardActivity.this, 1, intent, PendingIntent.FLAG_CANCEL_CURRENT);
                        alarmManagerElapsed = (AlarmManager) DashboardActivity.this.getSystemService(ALARM_SERVICE);
                        alarmManagerElapsed.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendars.getTimeInMillis(),
                                newValue * 60 * 1000, alarmIntentElapsed);

                        Toast.makeText(DashboardActivity.this, "Reminder is on", Toast.LENGTH_LONG).show();

                        Calendar calendar = Calendar.getInstance();
                        calendar.setTimeInMillis(System.currentTimeMillis());

                        //wake up
                        if (android.os.Build.VERSION.SDK_INT >= 23) {
                            calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH),
                                    timePicker.getHour(), timePicker.getMinute(), 0);
                        } else {
                            calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH),
                                    timePicker.getCurrentHour(), timePicker.getCurrentMinute(), 0);
                        }

                } else {
                    alarmManagerElapsed.cancel(alarmIntentElapsed);
                    NotificationHelper.disableBootReceiver(mContext);
                    Toast.makeText(mContext, "Your reminder is off", Toast.LENGTH_SHORT).show();
                }
            }
        });
        return true;
    }

    @Override
    int getNavigationMenuItemId() {
        return R.id.navigation_dashboard;
    }

}
