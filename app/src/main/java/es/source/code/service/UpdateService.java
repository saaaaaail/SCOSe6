package es.source.code.service;

import android.annotation.TargetApi;
import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Handler;
import android.util.Log;

import com.google.gson.Gson;

import java.util.List;

import es.source.code.AppGlobal;
import es.source.code.R;
import es.source.code.activity.FoodDetailed;
import es.source.code.activity.MainScreen;
import es.source.code.callback.OnDataFinishedListener;
import es.source.code.gson.FoodItem;
import es.source.code.gson.UpdateFood;
import es.source.code.model.Food;
import es.source.code.model.FoodContent;
import es.source.code.util.HttpConnectionUtil;
import es.source.code.util.SharedPreferenceUtil;
import es.source.code.util.XmlUtil;


/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * TODO: Customize class - update intent actions and extra parameters.
 */
public class UpdateService extends IntentService {
    private Context mContext;
    private static final int NOTIFICATION_ID = 6666;
    private static final int FLAG_CLEAN =101;

    private Handler handler= new Handler();
    @TargetApi(16)
    private Notification.Builder builder;

    public UpdateService() {
        super("UpdateService");

    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.d(": ", "onHandleIntent");
        mContext = this;
        //receiveFoods();

        handler.post(new Runnable() {
            @Override
            public void run() {
                forUpdateFoodsJSON();
                //forUpdateFoodsXML();
                //receiveFoods();
                Log.v("onHandleIntent: "," ");
                handler.postDelayed(this,10000);
            }
        });
    }

    private void forUpdateFoodsJSON(){
        String url = getString(R.string.update_service_url)+"?format=json";
        HttpConnectionUtil httpConnectionUtil = HttpConnectionUtil.getInstance(mContext);
        httpConnectionUtil.sendMessageByGet(url);
        httpConnectionUtil.setOnDataFinishedListener(new OnDataFinishedListener() {
            @Override
            public void onDataSuccess(Object t) {
                String url = (String)t;
                //System.out.println(url);

                long startTime = System.currentTimeMillis();
                UpdateFood updateFood = new Gson().fromJson(url,UpdateFood.class);
                long endTime = System.currentTimeMillis();
                System.out.println("解析JSON程序运行时间：" + (endTime - startTime) + "ms");

                int foodCount = updateFood.updateCounts;
                sendNotificationJSON(updateFood.foodItemList);
                playNotification();
            }
            @Override
            public void onDataFail() {

            }
        });
    }

    private void forUpdateFoodsXML(){
        String url = getString(R.string.update_service_url)+"?format=xml";
        HttpConnectionUtil httpConnectionUtil = HttpConnectionUtil.getInstance(mContext);
        httpConnectionUtil.sendMessageByGet(url);
        httpConnectionUtil.setOnDataFinishedListener(new OnDataFinishedListener() {
            @Override
            public void onDataSuccess(Object t)  {
                try{
                    String url = (String)t;
                    //System.out.println(url);
                    XmlUtil xmlUtil = XmlUtil.getInstance();

                    long startTime = System.currentTimeMillis();
                    es.source.code.pullxml.UpdateFood updateFood = xmlUtil.parseXMLWithPull(url);
                    long endTime = System.currentTimeMillis();
                    System.out.println("解析XML程序运行时间：" + (endTime - startTime) + "ms");

                    sendNotificationXML(updateFood.getFoodItemList());
                    playNotification();
                }catch(Exception e){
                    e.printStackTrace();
                }

            }
            @Override
            public void onDataFail() {

            }
        });
    }

    public void sendNotificationJSON(List<FoodItem> foods){
        Intent intent = new Intent(this, MainScreen.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(mContext,0,intent,PendingIntent.FLAG_CANCEL_CURRENT);

        int foodCount = foods.size();
        Notification.Builder builder = new Notification.Builder(this);
        String content = "新品上架 "+"菜品数量："+foodCount+"种";
        builder.setContentTitle(getString(R.string.app_name));
        builder.setContentText(content);
        builder.setSmallIcon(R.drawable.ic_launcher);
        Intent intentC = new Intent(AppGlobal.IntentAction.CLOSE_NOTIFICATION);
        intentC.putExtra(AppGlobal.IntentKey.NOTIFICATION_ID,NOTIFICATION_ID);
        PendingIntent pendingIntentC = PendingIntent.getBroadcast(mContext,FLAG_CLEAN,intentC,
                PendingIntent.FLAG_UPDATE_CURRENT);
        builder.addAction(R.drawable.btn_nor_down,"取消",pendingIntentC);
        builder.setContentIntent(pendingIntent);
        builder.setAutoCancel(true);

        NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(NOTIFICATION_ID,builder.build());
    }

    public void sendNotificationXML(List<es.source.code.pullxml.FoodItem> foods){
        Intent intent = new Intent(this, MainScreen.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(mContext,0,intent,PendingIntent.FLAG_CANCEL_CURRENT);

        int foodCount = foods.size();
        Notification.Builder builder = new Notification.Builder(this);
        String content = "新品上架 "+"菜品数量："+foodCount+"种";
        builder.setContentTitle(getString(R.string.app_name));
        builder.setContentText(content);
        builder.setSmallIcon(R.drawable.ic_launcher);
        Intent intentC = new Intent(AppGlobal.IntentAction.CLOSE_NOTIFICATION);
        intentC.putExtra(AppGlobal.IntentKey.NOTIFICATION_ID,NOTIFICATION_ID);
        PendingIntent pendingIntentC = PendingIntent.getBroadcast(mContext,FLAG_CLEAN,intentC,
                PendingIntent.FLAG_UPDATE_CURRENT);
        builder.addAction(R.drawable.btn_nor_down,"取消",pendingIntentC);
        builder.setContentIntent(pendingIntent);
        builder.setAutoCancel(true);

        NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(NOTIFICATION_ID,builder.build());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.v("onDestory: "," ");
    }

    private void playNotification() {
        Uri ringtone = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        MediaPlayer mediaPlayer = MediaPlayer.create(mContext, ringtone);
        mediaPlayer.start();
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                mp.release();
            }
        });
    }
    /**
     * 以上e6
     * 分割---------------------------------------------------------------------分割
     * 以下e5
     * @return
     */
    private Food addFoodCategory(){
        Food food = new Food("新的菜品",20, 1, AppGlobal.REMARK,0, false, R.drawable.food_cold_jysj);
        return food;
    }

    private void receiveFoods(){
        SharedPreferenceUtil spu = SharedPreferenceUtil.getInstance(mContext);
        List<Food> coldFoods = spu.getAllFood(0);
        int position = (int)(Math.random() * (coldFoods.size()-1));
        Food newFood = coldFoods.get(position);
        int cate =newFood.getCategory();
        sendNotification(newFood,position,cate);

    }

    private void sendNotification(Food food,int position,int cate ){
        Notification.Builder builder = new Notification.Builder(this);
        String content = "新品上架： "+food.getFoodName()+ ", "+food.getPrice()+"元 ";

        builder.setContentTitle(getString(R.string.app_name));
        builder.setContentText(content);
        builder.setSmallIcon(R.drawable.ic_launcher);

        Intent intent = new Intent(this, FoodDetailed.class);
        intent.putExtra(AppGlobal.IntentKey.CURRENT_CATEGORY,cate);
        intent.putExtra(AppGlobal.IntentKey.CURRENT_DETAILED_POSITION,position);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,0,
                intent,PendingIntent.FLAG_CANCEL_CURRENT);
        builder.setContentIntent(pendingIntent);
        builder.setAutoCancel(true);

        NotificationManager notificationManager =
                (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(NOTIFICATION_ID,builder.build());
    }
}
