package work.zice.petseller.service;

import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import org.json.JSONObject;

import cn.bmob.v3.BmobRealTimeData;
import cn.bmob.v3.listener.ValueEventListener;
import work.zice.petseller.R;

public class OrderService extends Service {

    private int id = 1;
    private BmobRealTimeData realTimeData;
    private MediaPlayer mediaPlayer;

    public OrderService() {
        realTimeData = new BmobRealTimeData();
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        mediaPlayer =MediaPlayer.create(this,R.raw.dingdong);
        //初始化音频管理器
        AudioManager mAudioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
        //获取系统最大音量
        int maxVolume=mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        // 获取设备当前音量
        int currentVolume =mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);

        mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC,(maxVolume/3),AudioManager.FLAG_PLAY_SOUND);
        Log.d("service", "onCreate");
        realTimeData.start(new ValueEventListener() {
            @Override
            public void onDataChange(JSONObject data) {
                Log.d("bmob", "(" + data.optString("action") + ")" + "数据：" + data);
                sendNotification();
            }

            @Override
            public void onConnectCompleted(Exception ex) {
                Log.d("bmob", "连接成功:" + realTimeData.isConnected());
                if (realTimeData.isConnected()) {
                    // 监听表更新
                    realTimeData.subTableUpdate("Order");
                }
            }

        });
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("service", "onStartCommand");
        return super.onStartCommand(intent, flags, startId);
    }

    private void sendNotification() {
        //获取NotificationManager实例
        NotificationManager notifyManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        //实例化NotificationCompat.Builde并设置相关属性
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                //设置小图标
                .setSmallIcon(R.mipmap.ic_launcher)
                //设置通知标题
                .setContentTitle("订单提醒")
                //设置通知内容
                .setContentText("订单发生变化");
        //设置通知时间，默认为系统发出通知的时间，通常不用设置
        //.setWhen(System.currentTimeMillis());
        //通过builder.build()方法生成Notification对象,并发送通知,id=1
        id++;
        notifyManager.notify(id, builder.build());
        // 播放声音
        mediaPlayer.start();
    }
}
