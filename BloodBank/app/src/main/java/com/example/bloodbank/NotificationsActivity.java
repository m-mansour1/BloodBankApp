package com.example.bloodbank;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.example.bloodbank.Adapter.NotificationAdapter;
import com.example.bloodbank.Model.Notification;

import java.util.List;

public class NotificationsActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private RecyclerView recyclerView;

    List<Notification> notificationList;
    NotificationAdapter notificationAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications);
    }
}