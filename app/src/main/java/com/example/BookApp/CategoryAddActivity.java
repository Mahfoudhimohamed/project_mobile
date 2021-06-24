package com.example.BookApp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import com.example.BookApp.databinding.ActivityCategoryAddBinding;
import com.example.BookApp.databinding.ActivityDashboardAdminBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class CategoryAddActivity extends AppCompatActivity {

    private ActivityCategoryAddBinding binding;

    //firbase Auth

    private FirebaseAuth firebaseAuth ;

    //progress dialog

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCategoryAddBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        //init firabase auth


        firebaseAuth = FirebaseAuth.getInstance();

        //configure progress

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please wait");

        progressDialog.setCanceledOnTouchOutside(false);

        //handle , click go back
        binding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });


        //handel click , begin upload category

        binding.submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validateData();
            }
        });
    }

    private String category ="";

    private void validateData() {




        category = binding.categoryEt.getText().toString().trim();

        //validate if not empty


        if(TextUtils.isEmpty(category)){
            Toast.makeText(this, "Please enter category", Toast.LENGTH_SHORT).show();
        }else{

            AddCatgoryFiribase();
        }
    }

    private void AddCatgoryFiribase() {

        progressDialog.setMessage("Adding Category ...");

        progressDialog.show();


        //get timestamp


        long timestamp =System.currentTimeMillis();

        //setup info to add in firebase

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("id",""+timestamp);

        hashMap.put("category", ""+category);

        hashMap.put("timestamp", timestamp);



        hashMap.put("uid", ""+firebaseAuth.getUid());


        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Categories");

        ref.child(""+timestamp).setValue(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {

                //category add on success
                progressDialog.dismiss();
                Toast.makeText(CategoryAddActivity.this, "Category added successfully ...", Toast.LENGTH_SHORT).show();

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                //category add failed

                progressDialog.dismiss();
                Toast.makeText(CategoryAddActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });

    }
}