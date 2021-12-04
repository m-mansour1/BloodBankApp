package com.example.bloodbank.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.bloodbank.Model.User;
import com.example.bloodbank.R;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {

    private Context context;
    private List<User> userList;

    public UserAdapter(Context context, List<User> userList) {
        this.context = context;
        this.userList = userList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.user_display_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final User user = userList.get(position);

        holder.type.setText(user.getType());
        if(user.getType().equals("donor")){
            holder.SendEmail.setVisibility(View.VISIBLE);
        }
        holder.email.setText(user.getEmail());
        holder.phoneNumber.setText(user.getPhonenumber());
        holder.name.setText(user.getName());
        holder.bloodType.setText(user.getBloodType());
        
        Glide.with(context).load(user.getProfilepictureurl()).into(holder.userProfile);
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        public CircleImageView userProfile;
        public TextView type,name, email,phoneNumber, bloodType;
        public Button SendEmail;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            userProfile = itemView.findViewById(R.id.userProfile);
            type = itemView.findViewById(R.id.type);
            name = itemView.findViewById(R.id.name);
            email = itemView.findViewById(R.id.email);
            phoneNumber = itemView.findViewById(R.id.phoneNumber);
            bloodType = itemView.findViewById(R.id.bloodType);
            SendEmail = itemView.findViewById(R.id.SendEmail);

        }
    }
}
