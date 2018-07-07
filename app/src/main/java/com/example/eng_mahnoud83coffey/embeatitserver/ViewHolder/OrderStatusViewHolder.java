package com.example.eng_mahnoud83coffey.embeatitserver.ViewHolder;

import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.eng_mahnoud83coffey.embeatitserver.Common.Common;
import com.example.eng_mahnoud83coffey.embeatitserver.Interface.ItemClickListiner;
import com.example.eng_mahnoud83coffey.embeatitserver.R;

public class OrderStatusViewHolder extends RecyclerView.ViewHolder


                                                                    {


    public TextView textOrderId,textOrderStatus,textOrderPhone,textOrderAddress;
    public Button btnEdit,btnRemove,btnDetails,btnDirection;




    public OrderStatusViewHolder(View itemView)
    {
        super(itemView);

        textOrderId=(TextView)itemView.findViewById(R.id.order_status_Id);
        textOrderStatus=(TextView)itemView.findViewById(R.id.order_status);
        textOrderPhone=(TextView)itemView.findViewById(R.id.order_status_phone);
        textOrderAddress=(TextView)itemView.findViewById(R.id.order_status_address);

        btnEdit=(Button)itemView.findViewById(R.id.btnEdit_itemStatus);
        btnDetails=(Button)itemView.findViewById(R.id.btnDetails_itemStatus);
        btnDirection=(Button)itemView.findViewById(R.id.btnDirection_itemStatus);
        btnRemove=(Button)itemView.findViewById(R.id.btnRemove_itemStatus);


        }









}
