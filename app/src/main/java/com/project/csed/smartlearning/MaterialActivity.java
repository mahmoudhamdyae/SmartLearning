package com.project.csed.smartlearning;

import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.List;

public class MaterialActivity extends AppCompatActivity {
    private static final int PICK_FILE = 1;
    FloatingActionButton addButton;
    RecyclerView recyclerView;
    View emptyView;
    TextView subtitle;

    String courseName, userType;

    MaterialsAdapter materialsAdapter;
    List<String> materialList = new ArrayList<>();

    StorageReference mStorageRef;
    DatabaseReference mDataRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_material);

        subtitle = findViewById(R.id.empty_subtitle_text);
        addButton = findViewById(R.id.addbtn);
        recyclerView = findViewById(R.id.recyclerView);

        // Find and set empty view on the ListView, so that it only shows when the list has 0 items.
        emptyView = findViewById(R.id.empty_view);

        // Examine the intent that was used to launch this activity,
        // in order to get course name.
        Intent intent = getIntent();
        courseName = intent.getStringExtra("course_name");

        // Change the app bar to show course name
        setTitle(courseName);

        mStorageRef = FirebaseStorage.getInstance().getReference().child("materials").child(courseName);
        mDataRef = FirebaseDatabase.getInstance().getReference().child("Materials").child(courseName);

        userType = intent.getStringExtra("userType");

        // Student is not allowed to add materials
        if (userType.equals("Student")) {
            addButton.hide();
            subtitle.setVisibility(View.GONE);
        }

        materialsAdapter = new MaterialsAdapter(this, materialList, courseName, userType);

        // Read materials
        mDataRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                materialList.clear();
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()){
                    String materialFullName = dataSnapshot1.getValue(String.class);
                    materialFullName = materialFullName.substring(0, materialFullName.length() - 4);
                    materialList.add(materialFullName);
                    materialsAdapter.notifyDataSetChanged();

                    // If there is no material set empty view
                    if (materialsAdapter.getItemCount() == 0){
                        recyclerView.setVisibility(View.GONE);
                        emptyView.setVisibility(View.VISIBLE);
                    }
                    else {
                        recyclerView.setVisibility(View.VISIBLE);
                        emptyView.setVisibility(View.GONE);
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

        recyclerView.setLayoutManager(new LinearLayoutManager(MaterialActivity.this));
        recyclerView.setAdapter(materialsAdapter);

        // Add materials
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.setType("application/pdf");
                try {
                    startActivityForResult(Intent.createChooser(intent, "Select Material"), PICK_FILE);
                } catch (ActivityNotFoundException e) {
                    Toast.makeText(MaterialActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data == null)
            return;

        if (requestCode == PICK_FILE && resultCode == RESULT_OK){
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.show();

            Uri file = data.getData();
            // Get name of the file
            String nameOfFile = file.getPath();
            int cut = nameOfFile.lastIndexOf('/');
            if (cut != -1) {
                nameOfFile = nameOfFile.substring(cut + 1);
            }
            final String name = nameOfFile;

            mStorageRef.child(name).putFile(file).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    if (task.isSuccessful()) {
                        mDataRef.push().setValue(name).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                Toast.makeText(MaterialActivity.this, R.string.material_added_toast, Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                    else
                        Toast.makeText(MaterialActivity.this, "Error : " + task.getException(), Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();

                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                    // progress percentage
                    double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();

                    // percentage in progress dialog
                    progressDialog.setMessage("Uploading " + ((int) progress) + "%...");
                }
            });
        }
    }
}
