package com.mobica.widgets;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;

public class StartActivity extends AppCompatActivity {

    private final int PERMISSIONS_REQUEST_CODE = 32;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        ArrayList<String> permissionsArray = new ArrayList<>();
        permissionsArray.add(Manifest.permission.ACCESS_FINE_LOCATION);
        permissionsArray.add(Manifest.permission.ACCESS_COARSE_LOCATION);

        String[] permissionsToRequest = getListOfPermissionsToRequest(permissionsArray);
        if (permissionsToRequest.length > 0) {
            ActivityCompat.requestPermissions(this, permissionsToRequest, PERMISSIONS_REQUEST_CODE);
        } else {
            finish();
        }
    }

    private String[] getListOfPermissionsToRequest(ArrayList<String> permissions) {
        ArrayList<String> result = new ArrayList<>();
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(this, permission) !=
                    PackageManager.PERMISSION_GRANTED) {
                result.add(permission);
            }
        }
        return result.toArray(new String[0]);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == PERMISSIONS_REQUEST_CODE) {
            String permissionsResult = "";
            for (int i = 0; i < permissions.length; ++i) {
                permissionsResult += permissions[i] + "\n";
                if (grantResults.length > i) {
                    permissionsResult += grantResults[i] == PackageManager.PERMISSION_GRANTED ?
                            "GRANTED\n" : "DENIED\n";
                } else {
                    permissionsResult += "NO_DATA\n";
                }
            }
            Toast.makeText(this, permissionsResult, Toast.LENGTH_LONG).show();
        }
    }
}
