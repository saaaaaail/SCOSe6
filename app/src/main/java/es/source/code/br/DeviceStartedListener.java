package es.source.code.br;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import es.source.code.AppGlobal;
import es.source.code.service.UpdateService;

/**
 * Created by sail on 2018/10/31.
 */

public class DeviceStartedListener extends BroadcastReceiver {

    private static final String TAG = "BootCompleteReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        switch (intent.getAction()){
            case AppGlobal.IntentAction.BOOT :
                Intent service = new Intent(context, UpdateService.class);
                context.startService(service);
                Log.i(TAG, "Boot Complete. Starting UpdateService...");
                break;
            case AppGlobal.IntentAction.CLOSE_NOTIFICATION :
                NotificationManager notifyManager = (NotificationManager)
                        context.getSystemService(Context.NOTIFICATION_SERVICE);
                notifyManager.cancel(intent.getIntExtra(AppGlobal.IntentKey.NOTIFICATION_ID, 0));
                break;
        }

    }


}
