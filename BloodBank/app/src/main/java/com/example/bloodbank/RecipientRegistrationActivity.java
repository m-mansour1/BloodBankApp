package com.example.bloodbank;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class RecipientRegistrationActivity extends AppCompatActivity {

    private TextView backButton;
    private CircleImageView profile_image;
    private TextInputEditText registerFullName, registerIdNumber, registerPhoneNumber,
            registerEmail, registerPassword;
    private Spinner bloodGroupSpinner;
    private Button registerButton;

    private Uri resultURI;
    private ProgressDialog loader;

    private FirebaseAuth mAuth;
    private DatabaseReference userDatabaseRef;
    public static final int PICK_IMAGE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipient_registration);

        backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RecipientRegistrationActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });
        profile_image = findViewById(R.id.profile_image);
        registerFullName = findViewById(R.id.registerFullName);
        registerIdNumber = findViewById(R.id.registerIdNumber);
        registerPhoneNumber = findViewById(R.id.registerPhoneNumber);
        registerEmail = findViewById(R.id.registerEmail);
        registerPassword = findViewById(R.id.registerPassword);
        bloodGroupSpinner = findViewById(R.id.bloodGroupSpinner);
        registerButton = findViewById(R.id.registerButton);

        loader = new ProgressDialog(this);

        mAuth = FirebaseAuth.getInstance();

        profile_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE);
            }
        });





        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String email = registerEmail.getText().toString().trim();
                final String password = registerPassword.getText().toString().trim();
                final String fullName = registerFullName.getText().toString().trim();
                final String idNumber = registerIdNumber.getText().toString().trim();
                final String phoneNumber = registerPhoneNumber.getText().toString().trim();
                final String bloodType = bloodGroupSpinner.getSelectedItem().toString();

                if(TextUtils.isEmpty(email)){
                    registerEmail.setError("Email is Required");
                    return;
                }
                if(TextUtils.isEmpty(password)){
                    registerPassword.setError("Password is Required");
                    return;
                }
                if(TextUtils.isEmpty(fullName)){
                    registerFullName.setError("Email is Required");
                    return;
                }
                if(TextUtils.isEmpty(idNumber)){
                    registerIdNumber.setError("Email is Required");
                    return;
                }
                if(TextUtils.isEmpty(phoneNumber)){
                    registerPhoneNumber.setError("Email is Required");
                    return;
                }
                if(bloodType.equals("Select Your Blood Type")){
                    Toast.makeText(RecipientRegistrationActivity.this, "Select a Blood Type", Toast.LENGTH_SHORT).show();
                    return;
                } else{

                    loader.setMessage("Registering...");
                    loader.setCanceledOnTouchOutside(false);
                    loader.show();

                    mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(!task.isSuccessful()){
                                String error = task.getException().toString();
                                Toast.makeText(RecipientRegistrationActivity.this, "Error! " + error, Toast.LENGTH_SHORT).show();
                                Log.i("ERROR",error);
                            }else{
                                String currentUserId = mAuth.getCurrentUser().getUid();
                                userDatabaseRef = FirebaseDatabase.getInstance().getReference().child("users")
                                        .child(currentUserId);
                                //created a node named users in which there is also nodes
                                HashMap userInfo = new HashMap();
                                //inserting extracted data
                                userInfo.put("id", currentUserId);
                                userInfo.put("name", fullName);
                                userInfo.put("email", email);
                                userInfo.put("idNumber", idNumber);
                                userInfo.put("phonenumber", phoneNumber);
                                userInfo.put("bloodType", bloodType);
                                userInfo.put("type", "recipient");
                                userInfo.put("search", "recipient" + bloodType);//to filter donors from a certain Type

                                userDatabaseRef.updateChildren(userInfo).addOnCompleteListener(new OnCompleteListener() {
                                    @Override
                                    public void onComplete(@NonNull Task task) {
                                        if(task.isSuccessful()){
                                            Toast.makeText(RecipientRegistrationActivity.this, "Data is successfully set", Toast.LENGTH_SHORT).show();
                                        }else{
                                            Toast.makeText(RecipientRegistrationActivity.this, task.getException().toString(), Toast.LENGTH_SHORT).show();
                                            Log.i("ERROR", task.getException().toString());
                                        }

                                        finish();
                                        //loader.dismiss();
                                        //
                                        }
                                });

                                if(resultURI != null){
                                    final StorageReference filepath = FirebaseStorage.getInstance().getReference()
                                            .child("profile images").child(currentUserId);
                                    Bitmap bitmap = null;

                                    try{
                                        bitmap = MediaStore.Images.Media.getBitmap(getApplication().getContentResolver(), resultURI);

                                    }catch(IOException e){
                                        e.printStackTrace();
                                    }

                                    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                                    bitmap.compress(Bitmap.CompressFormat.JPEG, 20, byteArrayOutputStream);//gets the image and compresses it
                                    byte data[] = byteArrayOutputStream.toByteArray();
                                    UploadTask uploadTask = filepath.putBytes(data);// upload it too firebase

                                    uploadTask.addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(RecipientRegistrationActivity.this, "Image Upload Failed", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                    uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                        @Override
                                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                            if(taskSnapshot.getMetadata() != null && taskSnapshot.getMetadata().getReference()!=null){
                                                Task<Uri> result = taskSnapshot.getStorage().getDownloadUrl();
                                                result.addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                    @Override
                                                    public void onSuccess(Uri uri) {
                                                        String imageUri = uri.toString();
                                                        HashMap newImageMap = new HashMap();
                                                        newImageMap.put("profilepictureurl", imageUri);

                                                        userDatabaseRef.updateChildren(newImageMap).addOnCompleteListener(new OnCompleteListener() {
                                                            @Override
                                                            public void onComplete(@NonNull Task task) {
                                                                if(task.isSuccessful()){
                                                                    Toast.makeText(RecipientRegistrationActivity.this, "Image url added to the database", Toast.LENGTH_SHORT).show();
                                                                }else{
                                                                    Toast.makeText(RecipientRegistrationActivity.this, task.getException().toString(), Toast.LENGTH_SHORT).show();
                                                                    Log.i("ERROR", task.getException().toString());
                                                                }
                                                            }
                                                        });

                                                        finish();
                                                    }
                                                });
                                            }
                                        }
                                    });

                                    Intent intent = new Intent(RecipientRegistrationActivity.this, MainActivity.class);
                                    startActivity(intent);
                                    finish();
                                    loader.dismiss();
                                }
                            }
                        }
                    });
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1 && resultCode == RESULT_OK && data!=null) {
            resultURI = data.getData();
            profile_image.setImageURI(resultURI);
        }
    }
}