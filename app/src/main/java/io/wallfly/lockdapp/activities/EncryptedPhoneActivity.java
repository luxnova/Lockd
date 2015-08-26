package io.wallfly.lockdapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.hmkcode.android.recyclerview.R;

import io.wallfly.lockdapp.lockutils.Utils;

public class EncryptedPhoneActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_encrypted_phone);
        initiate();
    }

    private void initiate() {
        Utils.getImageLoaderWithConfig().displayImage("drawable://" + R.drawable.lockd_logo_hires,
                (ImageView) findViewById(R.id.icon), Utils.getDisplayImageOptions());

        Button resetPassword = (Button) findViewById(R.id.reset_password_button);
        resetPassword.setOnClickListener(this);

        Button cancelButton = (Button) findViewById(R.id.cancel_button);
        cancelButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.reset_password_button:
                Intent intent = new Intent("android.app.action.SET_NEW_PASSWORD");
                startActivityForResult(intent, 100);
                break;

            case R.id.cancel_button:
                onBackPressed();
                break;
        }
    }
}
