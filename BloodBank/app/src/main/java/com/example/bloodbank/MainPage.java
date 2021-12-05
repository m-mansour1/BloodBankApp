package com.example.bloodbank;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.bloodbank.Adapter.UserAdapter;
import com.example.bloodbank.Model.User;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainPage extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    private DrawerLayout drawerLayout;
    private Toolbar toolbar;
    private NavigationView navigationView;

    private CircleImageView nav_profile_img;
    private TextView nav_fullname, nav_email, nav_bloodtype, nav_type;

    private DatabaseReference userRef;

    private RecyclerView recyclerView;
    private ProgressBar progressBar;

    private List<User> userList;
    private UserAdapter userAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_page);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Blood Donation App");


        drawerLayout = findViewById(R.id.drawerlayout);
        navigationView = findViewById(R.id.navView);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(MainPage.this, drawerLayout, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);

        progressBar = findViewById(R.id.progressbar);
        recyclerView = findViewById(R.id.recyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(layoutManager);

        userList = new ArrayList<>();
        userAdapter = new UserAdapter(MainPage.this,userList);

        recyclerView.setAdapter(userAdapter);

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("users").
                child(FirebaseAuth.getInstance().getCurrentUser().getUid());

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String type = snapshot.child("type").getValue().toString();
                if(type.equals("donor")){
                    readRecipient();
                }else{
                    readDonor();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        nav_profile_img = navigationView.getHeaderView(0).findViewById(R.id.nav_user_img);
        nav_fullname= navigationView.getHeaderView(0).findViewById(R.id.nav_user_fullname);

        nav_email = navigationView.getHeaderView(0).findViewById(R.id.nav_user_email);

        nav_bloodtype = navigationView.getHeaderView(0).findViewById(R.id.nav_user_bloodtype);

        nav_type= navigationView.getHeaderView(0).findViewById(R.id.nav_user_type);

        userRef = FirebaseDatabase.getInstance().getReference().child("users").child(
          FirebaseAuth.getInstance().getCurrentUser().getUid()
        );

        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    String name = snapshot.child("name").getValue().toString();//extracting from database
                    nav_fullname.setText(name);
                    String email = snapshot.child("email").getValue().toString();//extracting from database
                    nav_email.setText(email);
                    String bloodtype = snapshot.child("bloodType").getValue().toString();//extracting from database
                    nav_bloodtype.setText(bloodtype);
                    String type = snapshot.child("type").getValue().toString();//extracting from database
                    nav_type.setText(type);
                    if(snapshot.hasChild("profilepictureurl")){
                        String imageURL = snapshot.child("profilepictureurl").getValue().toString();//extracting from database
                        Glide.with(getApplicationContext()).load(imageURL).into(nav_profile_img);
                    }else{
                        nav_profile_img.setImageResource(R.drawable.profile);
                    }

                    Menu nav_menu = navigationView.getMenu();
                    if(type.equals("donor")){
                        nav_menu.findItem(R.id.sentEmail).setTitle("Received Emails");

                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void readDonor() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("users");
        Query query = ref.orderByChild("type").equalTo("donor");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userList.clear();
                for(DataSnapshot dataSnapshot: snapshot.getChildren()){
                    User user = dataSnapshot.getValue(User.class);
                    userList.add(user);
                }
                userAdapter.notifyDataSetChanged();
                progressBar.setVisibility(View.GONE);

                if(userList.isEmpty()){
                    Toast.makeText(MainPage.this, "No Donors", Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void readRecipient() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("users");
        Query query = ref.orderByChild("type").equalTo("recipient");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userList.clear();
                for(DataSnapshot dataSnapshot: snapshot.getChildren()){
                    User user = dataSnapshot.getValue(User.class);
                    userList.add(user);
                }
                userAdapter.notifyDataSetChanged();
                progressBar.setVisibility(View.GONE);

                if(userList.isEmpty()){
                    Toast.makeText(MainPage.this, "No Recipient", Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId()){
            case R.id.aplus:
                Intent intent3 = new Intent(MainPage.this, SelectedCategoryActivity.class);
                intent3.putExtra("group", "A+");
                startActivity(intent3);
                break;

            case R.id.aminus:
                Intent intent4 = new Intent(MainPage.this, SelectedCategoryActivity.class);
                intent4.putExtra("group", "A-");
                startActivity(intent4);
                break;

            case R.id.bplus:
                Intent intent5 = new Intent(MainPage.this, SelectedCategoryActivity.class);
                intent5.putExtra("group", "B+");
                startActivity(intent5);
                break;

            case R.id.bminus:
                Intent intent6 = new Intent(MainPage.this, SelectedCategoryActivity.class);
                intent6.putExtra("group", "B-");
                startActivity(intent6);
                break;

            case R.id.abplus:
                Intent intent7 = new Intent(MainPage.this, SelectedCategoryActivity.class);
                intent7.putExtra("group", "AB+");
                startActivity(intent7);
                break;

            case R.id.abminus:
                Intent intent8 = new Intent(MainPage.this, SelectedCategoryActivity.class);
                intent8.putExtra("group", "AB-");
                startActivity(intent8);
                break;

            case R.id.oplus:
                Intent intent9 = new Intent(MainPage.this, SelectedCategoryActivity.class);
                intent9.putExtra("group", "O+");
                startActivity(intent9);
                break;

            case R.id.ominus:
                Intent intent10 = new Intent(MainPage.this, SelectedCategoryActivity.class);
                intent10.putExtra("group", "O-");
                startActivity(intent10);
                break;

            case R.id.compatible:
                Intent intent11 = new Intent(MainPage.this, SelectedCategoryActivity.class);
                intent11.putExtra("group", "Compatible with me");
                startActivity(intent11);
                break;

            case R.id.sentEmail:
                Intent intent12 = new Intent(MainPage.this, SendEmailActivity.class);
                startActivity(intent12);
                break;


            case R.id.profile:
                Intent intent = new Intent(MainPage.this, ProfileActivity.class);
                startActivity(intent);
                break;

            case R.id.logout:
                FirebaseAuth.getInstance().signOut();
                Intent intent2 = new Intent(MainPage.this, LoginActivity.class);
                startActivity(intent2);
                break;
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }
}