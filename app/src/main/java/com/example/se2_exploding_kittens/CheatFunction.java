package com.example.se2_exploding_kittens;

import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.view.KeyEvent;

import com.example.se2_exploding_kittens.Activities.GameActivity;

public class CheatFunction extends Activity {

    String lightValueString;
    double lightValue;
    int counter = 0;
    private GameActivity gameActivity;

//    @Override
//    public boolean dispatchKeyEvent(KeyEvent event) {
//        int action = event.getAction();
//        int keyCode = event.getKeyCode();
//        switch (keyCode) {
//            case KeyEvent.KEYCODE_VOLUME_UP: {
//                if (KeyEvent.ACTION_UP == action) {
//                    System.out.println("UP");
//                    counter++;
//                }
//            }
//                case KeyEvent.KEYCODE_VOLUME_DOWN: {
//                    if (action == KeyEvent.ACTION_DOWN) {
//                        System.out.println("DOWN");
//                        counter--;
//                    }
//                }
//        }
//        return super.dispatchKeyEvent(event);
//    }







    private SensorEventListener lightSensorListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {
            lightValueString = String.valueOf(event.values[0]);
            lightValue = Double.parseDouble(lightValueString);

                if (lightValue>=3.4 && GameActivity.counter == 5) {
            System.out.println("Cheat detected");
            System.out.println(lightValue);
        }
            }


        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
        }
    };

    public CheatFunction(SensorManager sensorManager) {
        Sensor lightSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
        sensorManager.registerListener(lightSensorListener, lightSensor, SensorManager.SENSOR_DELAY_NORMAL);
        lightValueString = "";
    }



}
