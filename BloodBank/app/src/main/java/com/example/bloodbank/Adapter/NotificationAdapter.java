package com.example.bloodbank.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.bloodbank.Model.Notification;
import com.example.bloodbank.Model.User;
import com.example.bloodbank.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.ViewHolder>{


    private Context context;
    private List<Notification> notificationList;

    public NotificationAdapter(Context context, List<Notification> notificationList) {
        this.context = context;
        this.notificationList = notificationList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.notification_item, parent, false);
        return new NotificationAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final Notification notification = notificationList.get(position);

        holder.notificationtext.setText(notification.getText());
        holder.notificationdate.setText(notification.getDate());

        getUserInfo(holder.notificationimg, holder.notificationname, notification.getSenderid());

    }


    @Override
    public int getItemCount() {
        return notificationList.size();
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

    private void getUserInfo(final CircleImageView circleImageView, final TextView nameTextView, final String senderId) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("users")
                .child(senderId);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                nameTextView.setText(user.getName());
                Glide.with(context).load(user.getProfilepictureurl()).into(circleImageView);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
}
