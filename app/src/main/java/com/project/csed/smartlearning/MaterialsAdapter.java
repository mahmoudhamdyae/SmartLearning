package com.project.csed.smartlearning;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

public class MaterialsAdapter extends RecyclerView.Adapter<MaterialsAdapter.MaterialHolder> {

    private List<String> materialList;
    private Context context;

    public MaterialsAdapter (Context context, List<String> materialList){
        this.materialList = materialList;
        this.context = context;
    }

    @NonNull
    @Override
    public MaterialsAdapter.MaterialHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View materialRow = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.material_row,viewGroup,false);
        MaterialsAdapter.MaterialHolder holder = new MaterialsAdapter.MaterialHolder(materialRow);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull MaterialsAdapter.MaterialHolder materialHolder, int i) {
    }

    @Override
    public int getItemCount() {
        return materialList.size();
    }

    public class  MaterialHolder extends RecyclerView.ViewHolder{
        public MaterialHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
