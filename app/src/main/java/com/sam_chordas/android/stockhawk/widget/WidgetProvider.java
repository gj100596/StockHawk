package com.sam_chordas.android.stockhawk.widget;

import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.widget.RemoteViews;

import com.sam_chordas.android.stockhawk.R;
import com.sam_chordas.android.stockhawk.ui.DetailActivity;
import com.sam_chordas.android.stockhawk.ui.MyStocksActivity;

/**
 * Created by Gaurav on 18-05-2016.
 */
public class WidgetProvider extends AppWidgetProvider {

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {

        for (int appWidgetId : appWidgetIds) {
            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_layout);

            // Create an Intent to launch MainActivity
            Intent intent = new Intent(context, MyStocksActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
            views.setOnClickPendingIntent(R.id.widgetTitle, pendingIntent);

            // Set up the collection
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
                views.setRemoteAdapter(R.id.list,
                        new Intent(context, WidgetRemoteService.class));
            } else {
                views.setRemoteAdapter(0, R.id.list,
                        new Intent(context, WidgetRemoteService.class));
            }

            Intent clickIntentTemplate = new Intent(context, DetailActivity.class);
            PendingIntent clickPendingIntentTemplate = null;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
                clickPendingIntentTemplate = TaskStackBuilder.create(context)
                        .addNextIntentWithParentStack(clickIntentTemplate)
                        .getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
            }
            views.setPendingIntentTemplate(R.id.list, clickPendingIntentTemplate);

            // Tell the AppWidgetManager to perform an update on the current app widget
            appWidgetManager.updateAppWidget(appWidgetId, views);
        }

    }
}
