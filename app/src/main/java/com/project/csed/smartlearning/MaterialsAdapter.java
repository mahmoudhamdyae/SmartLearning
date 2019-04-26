package com.project.csed.smartlearning;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class MaterialsAdapter extends RecyclerView.Adapter<MaterialsAdapter.MaterialHolder> {

    private List<String> materialList;
    private Context context;
    private String courseName, userType;

    public MaterialsAdapter (Context context, List<String> materialList, String courseName, String userType){
        this.materialList = materialList;
        this.context = context;
        this.courseName = courseName;
        this.userType = userType;
    }

    @NonNull
    @Override
    public MaterialsAdapter.MaterialHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View materialRow = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.material_row,viewGroup,false);
        MaterialsAdapter.MaterialHolder holder = new MaterialsAdapter.MaterialHolder(materialRow);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull MaterialsAdapter.MaterialHolder materialHolder, int position) {
        String name = materialList.get(position);
        materialHolder.materialText.setText(name);
    }

    @Override
    public int getItemCount() {
        return materialList.size();
    }

    public class  MaterialHolder extends RecyclerView.ViewHolder{
        TextView materialText;
        ImageView materialDownload, materialPlay, materialDelete;

        public MaterialHolder(@NonNull View itemView) {
            super(itemView);

            materialText = itemView.findViewById(R.id.material_text);
            materialDownload = itemView.findViewById(R.id.material_download);
            materialPlay = itemView.findViewById(R.id.material_play);
            materialDelete = itemView.findViewById(R.id.material_delete);

            // Student is not allowed to delete materials
            if (userType.equals("Student"))
                materialDelete.setVisibility(View.GONE);

            materialDownload.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    downloadMaterial(getAdapterPosition());
                }
            });

            materialPlay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    playMaterial(getAdapterPosition());
                }
            });

            materialDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Create an AlertDialog.Builder and set the message, and click listeners
                    // for the positive and negative buttons on the dialog.
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setMessage(R.string.material_delete_dialog_msg);
                    builder.setPositiveButton(R.string.material_delete_dialog_delete, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // User clicked the "Delete" button, so delete the Material.
                            deleteMaterial(getAdapterPosition());
                        }
                    });
                    builder.setNegativeButton(R.string.material_delete_dialog_cancel, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // User clicked the "Cancel" button, so dismiss the dialog
                            if (dialog != null) {
                                dialog.dismiss();
                            }
                        }
                    });
                    // Create and show the AlertDialog
                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();
                }
            });
        }
    }

    // Download the material
    private void downloadMaterial(int position){
        // Get material name from the list
        String materialName = materialList.get(position) + ".pdf";
        StorageReference mStorageRef = FirebaseStorage.getInstance().getReference().child("materials").child(courseName);

        File rootPath = new File(Environment.getExternalStorageDirectory(), "Download");
        final File localFile = new File(rootPath,materialName);

        if(localFile.exists())
            Toast.makeText(context, R.string.the_file_is_already_exist, Toast.LENGTH_SHORT).show();
        else {
            final ProgressDialog progressDialog = new ProgressDialog(context);
            progressDialog.show();

            mStorageRef.child(materialName).getFile(localFile)
                    .addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                            // Successfully downloaded data to local file
                            Toast.makeText(context, R.string.the_file_downloaded_successfully_toast, Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    // Handle failed download
                    Toast.makeText(context, exception.getMessage(), Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                }
            }).addOnProgressListener(new OnProgressListener<FileDownloadTask.TaskSnapshot>() {
                @Override
                public void onProgress(FileDownloadTask.TaskSnapshot taskSnapshot) {
                    // progress percentage
                    double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();

                    // percentage in progress dialog
                    progressDialog.setMessage("Downloading " + ((int) progress) + "%...");
                }
            });
        }
    }

    // Play the material
    private void playMaterial(int position){
        // Get material name from the list
        String materialName = materialList.get(position) + ".pdf";

        File file = new File(context.getExternalCacheDir(), materialName);

        StorageReference mStorageRef = FirebaseStorage.getInstance().getReference().child("materials").child(courseName);
        mStorageRef.child(materialName).getFile(file);

        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.fromFile(file),"application/pdf");
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        try {
            context.startActivity(intent);
        } catch (ActivityNotFoundException e) {
            // No program to open pdf is installed
            Toast.makeText(context, R.string.there_is_no_app_to_read_pdf, Toast.LENGTH_SHORT).show();
        }
    }

    // Delete the material
    private void deleteMaterial(int position){
        // Get material name from the list
        final String materialName = materialList.get(position) + ".pdf";

        materialList.remove(position);
        notifyItemRemoved(position);

        // Delete the material from storage
        StorageReference mStorageRef = FirebaseStorage.getInstance().getReference().child("materials").child(courseName);
        mStorageRef.child(materialName).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                // Delete the material from database
                final DatabaseReference dataReference = FirebaseDatabase.getInstance().getReference().child("Materials").child(courseName);
                dataReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()){
                            if (materialName.equals(dataSnapshot1.getValue(String.class))){
                                dataReference.child(dataSnapshot1.getKey()).setValue(null).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        Toast.makeText(context, R.string.material_deleted_successfully_toast, Toast.LENGTH_SHORT).show();
                                    }
                                });
                                break;
                            }
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                    }
                });
            }
        });
    }
}
