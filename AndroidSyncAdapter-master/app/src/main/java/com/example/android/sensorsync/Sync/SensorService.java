package com.example.android.sensorsync.Sync;

import android.app.Service;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationManager;
import android.location.LocationListener;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.TextView;

import com.example.android.sensorsync.MainActivity;
import com.example.android.sensorsync.R;
import com.example.android.sensorsync.data.SensorContract;

import java.util.ArrayList;
import java.util.TimerTask;

/**
 * Created by sandeepchawan on 2017-10-26.
 */

public class SensorService extends Service implements SensorEventListener {

//    private TextView ms1,ms2,ms3,ms4,ms5,ms6;
    //private TextView mTextView;

    private SensorManager mSensorManager;
    private LocationManager locationManager;
    private Sensor mAccelerometer;
    private Sensor mGyroscope;
    private Sensor mMagnetometer;
    private int count = 0;
    public ArrayList<ContentValues> values = new ArrayList<>();
    ContentValues value = new ContentValues();





//    locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE); // 位置
//		if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
//        // TODO: Open GPS
//    } else
//
//    {
//        String bestProvider = locationManager.getBestProvider(
//                getLocationCriteria(), true);
//        // 获取位置信息
//        // 如果不设置查询要求，getLastKnownLocation方法传人的参数为LocationManager.GPS_PROVIDER
//        Location location = locationManager
//                .getLastKnownLocation(bestProvider);
//        // 监听状态
//        locationManager.addGpsStatusListener(gpsStatusListener);
//        // 绑定监听，有4个参数
//        // 参数1，设备：有GPS_PROVIDER和NETWORK_PROVIDER两种
//        // 参数2，位置信息更新周期，单位毫秒
//        // 参数3，位置变化最小距离：当位置距离变化超过此值时，将更新位置信息
//        // 参数4，监听
//        // 备注：参数2和3，如果参数3不为0，则以参数3为准；参数3为0，则通过时间来定时更新；两者为0，则随时刷新
//
//        // 1秒更新一次，或最小位移变化超过1米更新一次；
//        // 注意：此处更新准确度非常低，推荐在service里面启动一个Thread，在run中sleep(10000);然后执行handler.sendMessage(),更新位置
//        locationManager.requestLocationUpdates(
//                LocationManager.GPS_PROVIDER, 1000, 1, locationListener);
//    }



    /**
     * a tag for logging
     */
    private static final String TAG = SensorService.class.getSimpleName();

    public SensorService() {

    }

//    static final float ALPHA = 0.9f;
//    protected float lowPassFilter( float input, float output ) {
//        //if ( output == null ) return input;
////        for ( int i=0; i<input.length; i++ ) {
////            output[i] = output[i] + ALPHA * (input[i] - output[i]);
////        }
//
//        output = output + ALPHA * (input - output);
//
//        return output;
//    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public int onStartCommand(Intent intent, int flags, int startId) {
        mSensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mGyroscope = mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        mMagnetometer = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);

        //SENSOR_DELAY_FASTEST (10ms), SENSOR_DELAY_GAME(20ms), SENSOR_DELAY_UI(65ms), SENSOR_DELAY_NORMAL(200ms)
        Log.d(TAG, "In OnStartCommand\n");
        if (mAccelerometer != null)
            mSensorManager.registerListener(this, mAccelerometer, 50000);//(SensorManager.SENSOR_DELAY_NORMAL);
        Log.d(TAG, "Registered Accelerometer!\n");
        if (mGyroscope != null)
            mSensorManager.registerListener(this, mGyroscope, 50000);//SensorManager.SENSOR_DELAY_NORMAL);
        Log.d(TAG, "Registered Gyrometer!\n");
        if (mMagnetometer != null)
            mSensorManager.registerListener(this, mMagnetometer, 50000);//SensorManager.SENSOR_DELAY_NORMAL);
        Log.d(TAG, "Registered Magnetometer!\n");
        return START_STICKY;
    }

    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        //do nothing
    }






    public void onSensorChanged(SensorEvent event) {

        Sensor sensor = event.sensor;

        final double alpha = 0.8;
        double gravity[] = new double[3];

        if (sensor.getType() == Sensor.TYPE_GRAVITY) {
            gravity[0] =  event.values[0];
            gravity[1] =  event.values[2];
            gravity[2] =  event.values[2];

        }
        if (sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            //Log.d(TAG, "Accelerometer changed at: " + System.currentTimeMillis() + " \n");
            //Log.d(TAG, "Count is: " + count +  "\n");
            //Log.d(TAG, "Accelerometer: " + String.valueOf(event.values[0]) + String.valueOf(event.values[1]) + String.valueOf(event.values[2]));

            //final float alpha = 0.8;
            gravity[0] = alpha * gravity[0] + (1 - alpha) * event.values[0];
            gravity[1] = alpha * gravity[1] + (1 - alpha) * event.values[1];
            gravity[2] = alpha * gravity[2] + (1 - alpha) * event.values[2];

            double a = event.values[0] - gravity[0];
            double b = event.values[1] - gravity[1];
            double c = event.values[2] - gravity[2];

            value.put(SensorContract.SensorEntry.COLUMN_ACC_X, Math.round(a));
//            value.put(SensorContract.SensorEntry.COLUMN_ACC_Y, Math.round(event.values[1]));
//            value.put(SensorContract.SensorEntry.COLUMN_ACC_Z, Math.round(event.values[2]));
            value.put(SensorContract.SensorEntry.COLUMN_ACC_Y, Math.round(b));
            value.put(SensorContract.SensorEntry.COLUMN_ACC_Z, Math.round(c));

            MainActivity.getMainActivity().setText(1,(Math.round(a * 100))/100);
            MainActivity.getMainActivity().setText(2,(Math.round(b * 100))/100);
            MainActivity.getMainActivity().setText(3,(Math.round(c * 100))/100);
//            ms1.setText("X：" + (float) (Math.round(event.values[0] * 100)) / 100);
//            ms2.setText("Y：" + (float) (Math.round(event.values[1] * 100)) / 100) ;
//            ms3.setText("Z：" + (float) (Math.round(event.values[2] * 100)) / 100) ;
            count++;


        } else if (sensor.getType() == Sensor.TYPE_GYROSCOPE) {
            //Log.d(TAG, "Gyro changed at: " + System.currentTimeMillis() + " \n");
            //Log.d(TAG, "Gyro: " + String.valueOf(event.values[0]) + String.valueOf(event.values[1]) + String.valueOf(event.values[2]));
            value.put(SensorContract.SensorEntry.COLUMN_GYRO_X, (float) Math.toDegrees(event.values[0]));
            value.put(SensorContract.SensorEntry.COLUMN_GYRO_Y, (float) Math.toDegrees(event.values[1]));
            value.put(SensorContract.SensorEntry.COLUMN_GYRO_Z, (float) Math.toDegrees(event.values[2]));

//            if (Math.toDegrees(event.values[0]) > 10) { // anticlockwise
//                MainActivity.getMainActivity().getWindow().getDecorView().setBackgroundColor(Color.GREEN);
//            } else if (Math.toDegrees(event.values[0]) < 10 ) { // clockwise
//                MainActivity.getMainActivity().getWindow().getDecorView().setBackgroundColor(Color.RED);
//            }

//            if (Math.toDegrees(event.values[1]) > 10) { // anticlockwise
//                MainActivity.getMainActivity().getWindow().getDecorView().setBackgroundColor(Color.GREEN);
//            } else if (Math.toDegrees(event.values[1]) < 10 ) { // clockwise
//                MainActivity.getMainActivity().getWindow().getDecorView().setBackgroundColor(Color.RED);
//            }

            if (Math.toDegrees(event.values[2]) > 30) { // anticlockwise
                MainActivity.getMainActivity().getWindow().getDecorView().setBackgroundColor(Color.BLUE);
            } else if (Math.toDegrees(event.values[2]) < 30 ) { // clockwise
                MainActivity.getMainActivity().getWindow().getDecorView().setBackgroundColor(Color.YELLOW);
            }

            MainActivity.mainActivity1.setText(4,(float) Math.toDegrees(event.values[0]));
            MainActivity.getMainActivity().setText(5,(float) Math.toDegrees(event.values[1]));
            MainActivity.getMainActivity().setText(6,(float) Math.toDegrees(event.values[2]));
//            ms4.setText(" X Angular velocity\n" + (float) Math.toDegrees(event.values[0]));
//            ms5.setText(" Y Angular velocity\n" + (float) Math.toDegrees(event.values[1]));
//            ms6.setText(" Z Angular velocity\n" + (float) Math.toDegrees(event.values[2]));
            //Log.d(TAG, "Count is: " + count +  "\n");
            count++;
        }

        else if (sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
            //Log.d(TAG, "Magnetometer changed at: " + System.currentTimeMillis() + " \n");
            //Log.d(TAG, "Magneto: " + String.valueOf(event.values[0]) + String.valueOf(event.values[1]) + String.valueOf(event.values[2]));
            value.put(SensorContract.SensorEntry.COLUMN_MAGNETO_X, event.values[0] + 0);
            value.put(SensorContract.SensorEntry.COLUMN_MAGNETO_Y, event.values[1] + 0);
            value.put(SensorContract.SensorEntry.COLUMN_MAGNETO_Z, event.values[2] + 0);
            //Log.d(TAG, "Count is: " + count +  "\n");
            count++;
        }

        if (count == 3) {
           // Log.d(TAG, "*** Count is: " + count +  " *** Pushing it to array list\n");
            count = 0;

            value.put(SensorContract.SensorEntry.COLUMN_SYNC_STATUS, 0);
            value.put(SensorContract.SensorEntry.COLUMN_DATE, System.currentTimeMillis());
            value.put(SensorContract.SensorEntry.COLUMN_UID, MainActivity.getMainActivity().getSpeed());
            value.put(SensorContract.SensorEntry.COLUMN_GPS_LAT, 0);
            value.put(SensorContract.SensorEntry.COLUMN_GPS_LONG, 0);
            value.put(SensorContract.SensorEntry.COLUMN_CLASSIFICATION, 0);

            ContentValues copy_cv = new ContentValues(value);
            values.add(copy_cv);

            if (copy_cv == null) Log.e(TAG, "CONTENT VALUE IS NULL- while pushing into arrayList ");

            value.clear();

            /* TO_DO: COMMENT THESE OUT */
            //mSensorManager.unregisterListener(this);
            //stopSelf();
        }

        if (values.size() > 100) {
            System.out.println(111111111);
            Log.d(TAG, "~~~~~~ Starting Bulk Sync ~~~~ \n");
            //create a copy of arraylist and insert
            ArrayList<ContentValues> copy_list = new ArrayList<>(values);
            bulk_insert(this, copy_list);
            values.clear();
        }

    }

    public void bulk_insert (Context context, ArrayList<ContentValues> values) {
        SensorTask.syncSensor(context, values);
    }

    public void onDestroy () {
        //This is called when stopService() is called. Not called when app is terminated by user.
        //Call stopService() in MainActivity to catch app terminations by user and take appropriate actions
        //TODO: Check if you want to sync pending items in arraylist to the DB
        Log.w(TAG, "App terminated by user!! ");
    }
}
