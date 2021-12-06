package com.example.bloodbank.Adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.bloodbank.Email.JavaMailApi;
import com.example.bloodbank.Model.User;
import com.example.bloodbank.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;
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
        final String RecieverName = user.getName();
        final String Recieverid = user.getId();
        holder.SendEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(context)
                        .setTitle("SEND EMAIL")
                        .setMessage("Send email to "+ user.getName()+"?")
                        .setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                DatabaseReference ref = FirebaseDatabase.getInstance().getReference()
                                        .child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
                                ref.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        String SenderName = snapshot.child("name").getValue().toString();
                                        String SenderEmail = snapshot.child("email").getValue().toString();
                                        String phone = snapshot.child("phonenumber").getValue().toString();
                                        String bloodtype = snapshot.child("bloodType").getValue().toString();

                                        String mEmail = user.getEmail();
                                        String mSubject = "Blood Donation";
                                        String mMessage = "Hello, " + RecieverName + ", \n"+
                                                SenderName +" needs blood. \n"+
                                                "Here is there details:\n"+
                                                "Name: "+ SenderName +"\n"+
                                                "Phone Number: "+ phone +"\n"+
                                                "Email: "+ SenderEmail +"\n"+
                                                "Blood Type: "+ bloodtype +"\n"+
                                                "Kindly reach out to them. \n"+
                                                "Blood Donation Application\n";

                                        JavaMailApi javaMailApi = new JavaMailApi(context, mEmail, mSubject, mMessage);
                                        javaMailApi.execute();
                                        DatabaseReference Sendreference = FirebaseDatabase.getInstance().getReference("emails")
                                                .child(FirebaseAuth.getInstance().getCurrentUser().getUid());
                                        Sendreference.child(Recieverid).setValue(true).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if(task.isSuccessful()){
                                                    DatabaseReference Receiveref = FirebaseDatabase.getInstance().getReference("emails")
                                                            .child(Recieverid);
                                                    Receiveref.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(true);

                                                    addNotification(Recieverid, FirebaseAuth.getInstance().getCurrentUser().getUid());
                                                }
                                            }
                                        });

                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });
                            }
                        })
                        .setNegativeButton("NO", null)
                        .show();

            }
        });
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

    private void addNotification(String receiverId, String senderId){
        DatabaseReference reference = FirebaseDatabase.
                getInstance().getReference().child("notifications")
                .child(receiverId);
        String date = DateFormat.getDateInstance().format(new Date());
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("receiverid", receiverId);
        hashMap.put("senderid", senderId);
        hashMap.put("text", "Sent you an Email. Check it out!");
        hashMap.put("date", date);

        reference.push().setValue(hashMap);

    }
}
