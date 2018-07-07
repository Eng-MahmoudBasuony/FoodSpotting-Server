package com.example.eng_mahnoud83coffey.embeatitserver.ViewHolder;

import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.eng_mahnoud83coffey.embeatitserver.Common.Common;
import com.example.eng_mahnoud83coffey.embeatitserver.Interface.ItemClickListiner;
import com.example.eng_mahnoud83coffey.embeatitserver.R;

public class MenuViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener ,View.OnCreateContextMenuListener {


    public TextView textMenuName;
    public ImageView imageView;
    //-------------------------
    private ItemClickListiner itemClickListener;// Interface






    public MenuViewHolder(View itemView) {
        super(itemView);

        textMenuName=(TextView)itemView.findViewById(R.id.menu_name);
        imageView=(ImageView)itemView.findViewById(R.id.menu_image);





        itemView.setOnCreateContextMenuListener(this); //Context Menu
        itemView.setOnClickListener(this); //Item Click Listiner

    }

    @Override
    public void onClick(View view)
    {

        itemClickListener.onClick(view , getAdapterPosition() , false);
    }

    //Setter ItemClickListiner
    public void setItemClickListener(ItemClickListiner itemClickListener)
    {
        this.itemClickListener = itemClickListener;


    }


    @Override
    public void onCreateContextMenu(ContextMenu contextMenu, View view, ContextMenu.ContextMenuInfo contextMenuInfo) {

        contextMenu.setHeaderTitle("Select this action ");

        contextMenu.add(0,0,getAdapterPosition(), Common.UPDATE);
        contextMenu.add(0,1,getAdapterPosition(),Common.DELETE);



    }
}
