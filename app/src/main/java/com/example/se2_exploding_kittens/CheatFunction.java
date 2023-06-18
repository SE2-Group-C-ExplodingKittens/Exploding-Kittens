package com.example.se2_exploding_kittens;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

public class CheatFunction {

    String lightValueString;
    double lightValue;

    private SensorEventListener lightSensorListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {
            lightValueString = String.valueOf(event.values[0]);
            lightValue = Double.parseDouble(lightValueString);

//                if (lightValue.equals("400.0")) {
            System.out.println("Cheat detected");
            System.out.println(lightValue);
        }
//            }


        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
        }
    };

    public CheatFunction(SensorManager sensorManager) {
        Sensor lightSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
        sensorManager.registerListener(lightSensorListener, lightSensor, SensorManager.SENSOR_DELAY_NORMAL);
        lightValueString = "";
    }


//    @Override
//    public void onSensorChanged(SensorEvent sensorEvent) {
//        if (sensorEvent.sensor.getType() == Sensor.TYPE_LIGHT) {
//            if (sensorEvent.values[0] < 0.002) {
//                System.out.println("Cheat detected");
//            }
//        }
//    }
//
//    @Override
//    public void onAccuracyChanged(Sensor sensor, int i) {
//
//    }


}
