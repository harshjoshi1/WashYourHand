package org.pcc.repeatinglocalnotifications;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.RemoteViews;

import static android.content.Context.MODE_PRIVATE;

/**
 * Implementation of App Widget functionality.
 */
public class MyAppWidget extends AppWidgetProvider {

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {

        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.my_app_widget);

        Intent intent = new Intent(context, ClickIntentService.class);
        intent.setAction(ClickIntentService.ACTION_CLICK);
        PendingIntent pendingIntent = PendingIntent.getService(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        int clicks = context.getSharedPreferences("sp", MODE_PRIVATE).getInt("clicks", 0);

        Bundle options = appWidgetManager.getAppWidgetOptions(appWidgetId);

        int width = options.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_WIDTH);

        views.setOnClickPendingIntent(R.id.minus, pendingIntent);
        views.setOnClickPendingIntent(R.id.plus, pendingIntent);

            views.setOnClickPendingIntent(R.id.plus, pendingIntent);
                String clicksStr;
                if (width < 500) {
                    clicksStr = clicks + " times today";
                } else {
                    clicksStr = clicks + " times today";
                }

                views.setTextViewText(R.id.textView, clicksStr);

                // Instruct the widget manager to update the widget
                appWidgetManager.updateAppWidget(appWidgetId, views);


    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }

    @Override
    public void onAppWidgetOptionsChanged(Context context, AppWidgetManager appWidgetManager, int appWidgetId, Bundle newOptions) {
        super.onAppWidgetOptionsChanged(context, appWidgetManager, appWidgetId, newOptions);
        updateAppWidget(context, appWidgetManager, appWidgetId);
    }
}

