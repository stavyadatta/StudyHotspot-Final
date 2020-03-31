package com.example.studyhotspot;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.View;
import android.widget.Button;

public class InvitationPage extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invitation_page);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        setActivityBackgroundColor(0xfcec03, actionBar);

    }

    public void setActivityBackgroundColor(int color, ActionBar actionBar) {
        ColorDrawable colorDrawable = new ColorDrawable(Color.parseColor("#E7E61D"));

        actionBar.setBackgroundDrawable(colorDrawable);

    }
}
