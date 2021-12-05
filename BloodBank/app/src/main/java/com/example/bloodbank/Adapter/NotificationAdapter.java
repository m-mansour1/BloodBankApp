package com.example.bloodbank.Adapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bloodbank.R;

import de.hdodenhof.circleimageview.CircleImageView;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.ViewHolder>{


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        public CircleImageView notificationimg;
        public TextView notificationname, notificationtext, notificationdate;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            notificationimg = itemView.findViewById(R.id.notification_img);
            notificationname = itemView.findViewById(R.id.notification_name);
            notificationtext = itemView.findViewById(R.id.notification_text);
            notificationdate = itemView.findViewById(R.id.notification_date);
        }
    }
}
