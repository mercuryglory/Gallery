package com.mercury.app;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.mercury.gallery.SelectPhotoActivity;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    public static final int EXTERNAL_STORAGE = 1;
    public static final String TAG = "MainActivity";

    public static final int REQUEST_SELECT = 101;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.i(TAG, "maxMemory=" + Runtime.getRuntime().maxMemory() / 1024 / 1024);
    }

    //选取相册
    public void photo(View view) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) !=
                PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission
                    .READ_EXTERNAL_STORAGE}, EXTERNAL_STORAGE);
        } else {
            startActivityForResult(new Intent(this, SelectPhotoActivity.class), REQUEST_SELECT);
        }
    }

    //拍照
    public void camera(View view) {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_SELECT) {
            if (data != null) {
                ArrayList<String> pathList = data.getStringArrayListExtra("pathList");
                Log.i(TAG, "onActivityResult: " + pathList);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == EXTERNAL_STORAGE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startActivityForResult(new Intent(this, SelectPhotoActivity.class), REQUEST_SELECT);
            } else {
                new AlertDialog.Builder(this)
                        .setMessage("您还没有授予读取外部存储的权限")
                        .setPositiveButton("去设置页面", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                intent.setData(Uri.fromParts("package", getPackageName(), null));
                                startActivity(intent);

                            }
                        })
                        .setNegativeButton("取消", null)
                        .show();
            }
        }
    }
}
