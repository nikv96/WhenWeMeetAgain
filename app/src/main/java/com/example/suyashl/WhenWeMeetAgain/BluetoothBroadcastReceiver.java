package com.example.suyashl.WhenWeMeetAgain;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import java.util.Calendar;

/**
 * Created by SuyashL on 25/1/15.
 */

public class BluetoothBroadcastReceiver extends Service {
    BluetoothAdapter mBtAdapter;
    RemindersDbAdapter reminddb;

    public BroadcastReceiver mReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            reminddb = new RemindersDbAdapter(context);

            String action = intent.getAction();

            System.out.println("Here.");

            if(BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                String address = device.getAddress();
                int[] ROWID = new int[10];

                ROWID = reminddb.SearchReminder(address);

                if (ROWID[0] != 0){
                    for(int e = 0; e < 10; e++){
                        if (ROWID[e] != 0){
                            if(reminddb.getReminded(ROWID[e]) == 0){
                                new ReminderManager(context).setReminder((long)ROWID[e], Calendar.getInstance());
                                reminddb.setasReminded(ROWID[e]);
                            }
                        }
                    }
                }

                mBtAdapter.cancelDiscovery();
                mBtAdapter.startDiscovery();
            }
            else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action))
            {
                Log.v("LocalService", "Entered the Finished.");

                mBtAdapter.cancelDiscovery();
                mBtAdapter.startDiscovery();
            }
            else {
                mBtAdapter.cancelDiscovery();
                mBtAdapter.startDiscovery();
            }
        }
    };

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        mBtAdapter = BluetoothAdapter.getDefaultAdapter();

        mBtAdapter.cancelDiscovery();
        mBtAdapter.startDiscovery();

        registerReceiver(mReceiver, new IntentFilter(BluetoothDevice.ACTION_FOUND));

        return START_REDELIVER_INTENT;
    }


    @Override
    public IBinder onBind(Intent intent){
        return null;
    }

    public void onCreate() {
        mBtAdapter = BluetoothAdapter.getDefaultAdapter();

        mBtAdapter.cancelDiscovery();
        mBtAdapter.startDiscovery();

        super.onCreate();
    }

    public void onDestroy() {
        super.onDestroy();
    }
}
