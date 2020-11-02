package com.example.mobile;

import androidx.annotation.NonNull;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import io.flutter.embedding.android.FlutterActivity;
import io.flutter.embedding.engine.FlutterEngine;
import io.flutter.plugin.common.MethodChannel;
import java.util.*;

public class MainActivity extends FlutterActivity {
    private static final String rootChannel = "www.jshhkj.com";
    private static final String bluetoothChannel = rootChannel + "/bluetooth";

    BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

    @Override
    public void configureFlutterEngine(@NonNull FlutterEngine flutterEngine) {
        super.configureFlutterEngine(flutterEngine);

        new MethodChannel(flutterEngine.getDartExecutor().getBinaryMessenger(), bluetoothChannel)
                .setMethodCallHandler((call, result) -> {
                    if (call.method.equals("scanBluetoothDevices")) {
                        scanBluetoothDevices();
                    } else if (call.method.equals("bluetoothIsEnabled")) {
                        result.success(bluetoothIsEnabled());
                    }
                });
    }

    // scan
    private void scanBluetoothDevices() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothDevice.ACTION_FOUND);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        registerReceiver(receiver, filter);

        boolean isDiscovering = bluetoothAdapter.isDiscovering();
        if (isDiscovering) {
            System.out.println("Discovering in progress...");
            bluetoothAdapter.cancelDiscovery();
        }

        try {
            System.out.println("Start to do discovery...");
            boolean a = bluetoothAdapter.startDiscovery();
            System.out.println(a);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    // Create a BroadcastReceiver for ACTION_FOUND.
    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            try {
                String onReceiveAction = "current action: " + action;
                System.out.println(onReceiveAction);

                if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                    System.out.println("Action Found..........");
                    BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    String deviceName = device.getName();
                    String deviceHardwareAddress = device.getAddress(); // MAC
                                                                        // address

                    System.out.println("***********************************");
                    System.out.println(deviceName);
                    System.out.println(deviceHardwareAddress);
                } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                    System.out.println("Discovery Finished..........");
                    scanResult();
                } else if (BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(action)) {
                    System.out.println("Discovery Started..........");
                }
            } catch (Exception e) {
                // TODO: handle exception
                System.out.println("got error in onReceive");
                e.printStackTrace();
            }

        }
    };

    private void scanResult() {
        bluetoothAdapter.cancelDiscovery();
        unregisterReceiver(receiver);
    }

    private boolean bluetoothIsEnabled() {
        return bluetoothAdapter.isEnabled();
    }
}
