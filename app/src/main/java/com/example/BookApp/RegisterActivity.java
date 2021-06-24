package com.example.BookApp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.UserHandle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Toast;

import com.example.BookApp.databinding.ActivityRegisterBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {



    //view binding

    private ActivityRegisterBinding binding;



    //firabase auth

    private FirebaseAuth  firebaseAuth;

    //progress dialog


    private ProgressDialog  progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());



        //init Firebase Auth

        firebaseAuth = FirebaseAuth.getInstance();

        //setup progress dialoag


        progressDialog = new ProgressDialog(this);


        progressDialog.setTitle("Please wait");
        progressDialog.setCanceledOnTouchOutside(false);


        //handel click  go back register

        binding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                onBackPressed();

            }
        });




        //handel click  begin register

        binding.registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validateData();
            }
        });
    }


    private String name="",email="",password="";
    private void validateData() {

      name =  binding.nameEt.getText().toString().trim();
      email = binding.emailEt.getText().toString().trim();
      password = binding.passwordEt.getText().toString().trim();
      String   cpassword = binding.cpasswordEt.getText().toString().trim();


      //validate data

        if(TextUtils.isEmpty(name)){


            Toast.makeText(this, "Enter your name", Toast.LENGTH_SHORT).show();
        }else if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){


            Toast.makeText(this, "Invalid Email Pattern ...!", Toast.LENGTH_SHORT).show();


        }else if(TextUtils.isEmpty(password)){
            Toast.makeText(this, "Enter Password ...", Toast.LENGTH_SHORT).show();
        }else if(TextUtils.isEmpty(cpassword)){
            Toast.makeText(this, "Confirm password ...", Toast.LENGTH_SHORT).show();
        }else if(!password.equals(cpassword)){
            Toast.makeText(this, "Password doesn't match", Toast.LENGTH_SHORT).show();
        }else{
            createUserAccount();
        }


    }

    private void createUserAccount() {


        //show progress

        progressDialog.setMessage("Creating account ...");

        progressDialog.show();
        //creat user auth in firabase


        firebaseAuth.createUserWithEmailAndPassword(email, password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {

                //account creation success , now add in  firbase realtime database

                updateUserInfo();



            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(Exception e) {

                //account creating failed
                progressDialog.dismiss();

                Toast.makeText(RegisterActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });




    }

    private void updateUserInfo() {
        progressDialog.setMessage("Saving user info ...");

        //timestamp


        long timestamp = System.currentTimeMillis();


        String uid = firebaseAuth.getUid();


        //setup data
        HashMap<String ,Object> hashMap = new HashMap<>();

        hashMap.put("uid", uid);
        hashMap.put("email", email);
        hashMap.put("name",name);
        hashMap.put("profileImage","");//add empty
        hashMap.put("userType","user");
        hashMap.put("timestamp",timestamp);




        //set data to database

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.child(uid)
                .setValue(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                //data added to db

                progressDialog.dismiss();

                Toast.makeText(RegisterActivity.this, "Account Created ...", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(RegisterActivity.this, DashboardUserActivity.class));
                finish();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(Exception e) {

                //data failed adding to db

                progressDialog.dismiss();
                Toast.makeText(RegisterActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });
    }

}