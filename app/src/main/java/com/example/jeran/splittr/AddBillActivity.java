package com.example.jeran.splittr;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class AddBillActivity extends AppCompatActivity implements View.OnClickListener {

    private Button enterBillButton;
    private Button takeBillPicButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_bill);

        enterBillButton = (Button) findViewById(R.id.enterBillButton);
        takeBillPicButton = (Button) findViewById(R.id.takeBillPictureButton);

        enterBillButton.setOnClickListener(this);
        takeBillPicButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.enterBillButton:
                startActivity(new Intent(AddBillActivity.this, EnterBillActivity.class));
                break;
            case R.id.takeBillPictureButton:
                startActivity(new Intent(getApplicationContext(), PhotoCaptureActivity.class));
                break;
        }
    }
}
