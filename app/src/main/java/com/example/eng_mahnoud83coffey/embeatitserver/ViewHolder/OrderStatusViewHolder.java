package com.example.eng_mahnoud83coffey.embeatitserver.ViewHolder;

import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.View;
import android.widget.TextView;

import com.example.eng_mahnoud83coffey.embeatitserver.Common.Common;
import com.example.eng_mahnoud83coffey.embeatitserver.Interface.ItemClickListiner;
import com.example.eng_mahnoud83coffey.embeatitserver.R;

public class OrderStatusViewHolder extends RecyclerView.ViewHolder implements
                                                                   View.OnClickListener,
                                                                   View.OnCreateContextMenuListener
                                                                    {


    public TextView textOrderId,textOrderStatus,textOrderPhone,textOrderAddress;

    private ItemClickListiner itemClickListener;


    public OrderStatusViewHolder(View itemView)
    {
        super(itemView);

        textOrderId=(TextView)itemView.findViewById(R.id.order_status_Id);
        textOrderStatus=(TextView)itemView.findViewById(R.id.order_status);
        textOrderPhone=(TextView)itemView.findViewById(R.id.order_status_phone);
        textOrderAddress=(TextView)itemView.findViewById(R.id.order_status_address);


        itemView.setOnCreateContextMenuListener(this);
        itemView.setOnClickListener(this);

        }



    public void setItemClickListener(ItemClickListiner itemClickListener)
    {
        this.itemClickListener = itemClickListener;
    }

    @Override
    public void onClick(View view)
    {
        itemClickListener.onClick(view,getAdapterPosition(),false);

    }


    @Override
    public void onCreateContextMenu(ContextMenu contextMenu, View view, ContextMenu.ContextMenuInfo contextMenuInfo)
    {
          contextMenu.setHeaderTitle("Select this Action");
          contextMenu.add(0,0,getAdapterPosition(), Common.UPDATE);
          contextMenu.add(0,1,getAdapterPosition(), Common.DELETE);


    }


}
