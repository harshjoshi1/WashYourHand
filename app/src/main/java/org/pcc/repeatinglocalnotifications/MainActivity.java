package org.pcc.repeatinglocalnotifications;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import org.pcc.repeatinglocalnotifications.notification.AlarmReceiver;
import org.pcc.repeatinglocalnotifications.notification.NotificationHelper;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContext = getApplicationContext();

        final TextView tv = (TextView) findViewById(R.id.tv);
        NumberPicker np = (NumberPicker) findViewById(R.id.np);
        clickcounter = (TextView) findViewById(R.id.clicksTextView);

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

        clickcounter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                clicks = getSharedPreferences("sp", MODE_PRIVATE).getInt("clicks", 0);
                if(clicks == 0){
                    showDialog(MainActivity.this);
                }else {
                    clicks++;
                    getSharedPreferences("sp", MODE_PRIVATE)
                            .edit()
                            .putInt("clicks", clicks)
                            .commit();
                    clickcounter.setText(String.valueOf(clicks));
                    appWidgetManager = AppWidgetManager.getInstance(MainActivity.this);
                    widgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(MainActivity.this, MyAppWidget.class));
                    for (int appWidgetId : widgetIds) {
                        MyAppWidget.updateAppWidget(getApplicationContext(), appWidgetManager, appWidgetId);
                    }
                }
            }
        });
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
                clickcounter.setText(String.valueOf(clicks));
                appWidgetManager = AppWidgetManager.getInstance(MainActivity.this);
                widgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(MainActivity.this, MyAppWidget.class));
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
        ((TextView) findViewById(R.id.clicksTextView)).setText(String.valueOf(clicks));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // action with ID action_refresh was selected
            case R.id.widget:
                Intent i = new Intent(MainActivity.this, WidgetInstruction.class);
                startActivity(i);
                finish();
                break;

            case R.id.info:
                alarmManagerElapsed.cancel(alarmIntentElapsed);
                NotificationHelper.disableBootReceiver(mContext);
                System.exit(0);

                break;

            // action with ID action_settings was selected

            default:
                break;
        }

        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.

        getMenuInflater().inflate(R.menu.menu_main, menu);
        MenuItem item = (MenuItem) menu.findItem(R.id.switchId);

        item.setActionView(R.layout.show_protected_switch);
        Switch switchAB = (Switch) item.getActionView().findViewById(R.id.switchAB);
        switchAB.setChecked(false);

        switchAB.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView,boolean isChecked) {
                if (isChecked) {
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTimeInMillis(System.currentTimeMillis());

                    Intent intent = new Intent(MainActivity.this, AlarmReceiver.class);
                    alarmIntentElapsed = PendingIntent.getBroadcast(MainActivity.this, 1, intent, PendingIntent.FLAG_UPDATE_CURRENT);

                    alarmManagerElapsed = (AlarmManager) MainActivity.this.getSystemService(ALARM_SERVICE);
                    alarmManagerElapsed.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                            newValue * 60 * 1000, alarmIntentElapsed);

                    Toast.makeText(MainActivity.this, "Reminder is on", Toast.LENGTH_LONG).show();

                } else {
                    alarmManagerElapsed.cancel(alarmIntentElapsed);
                    NotificationHelper.disableBootReceiver(mContext);
                    Toast.makeText(mContext, "Your reminder is off", Toast.LENGTH_SHORT).show();
                }
            }
        });
        return true;
    }


}
