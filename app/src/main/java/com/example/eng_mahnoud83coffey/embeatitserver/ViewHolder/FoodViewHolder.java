package com.example.eng_mahnoud83coffey.embeatitserver.ViewHolder;

import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.eng_mahnoud83coffey.embeatitserver.Common.Common;
import com.example.eng_mahnoud83coffey.embeatitserver.Interface.ItemClickListiner;
import com.example.eng_mahnoud83coffey.embeatitserver.R;

import java.security.PublicKey;

public class FoodViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener ,View.OnCreateContextMenuListener {


    public ImageView imageViewFoodList;
    public TextView textViewFoodList;
    public ItemClickListiner itemClickListiner;


    public FoodViewHolder(View itemView) {
        super(itemView);

        imageViewFoodList=(ImageView)itemView.findViewById(R.id.food_list_image);
        textViewFoodList=(TextView)itemView.findViewById(R.id.food_list_name);


        itemView.setOnCreateContextMenuListener(this); //Context Menu
        itemView.setOnClickListener(this);


    }

    public void setItemClickListiner(ItemClickListiner itemClickListiner) {
        this.itemClickListiner = itemClickListiner;
    }

    @Override
    public void onClick(View view)
    {

        itemClickListiner.onClick(view,getAdapterPosition(),false);
    }


    @Override
    public void onCreateContextMenu(ContextMenu contextMenu, View view, ContextMenu.ContextMenuInfo contextMenuInfo)
    {

        contextMenu.setHeaderTitle("Select this action ");

        contextMenu.add(0,0,getAdapterPosition(), Common.UPDATE);
        contextMenu.add(0,1,getAdapterPosition(),Common.DELETE);

    }
}
