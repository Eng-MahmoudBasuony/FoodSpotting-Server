package food.spotting.eng_mahnoud83coffey.embeatitserver;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;

import food.spotting.eng_mahnoud83coffey.embeatitserver.Common.Common;

import food.spotting.eng_mahnoud83coffey.embeatitserver.R;

import food.spotting.eng_mahnoud83coffey.embeatitserver.ViewHolder.OrderDetailsAdabter;

public class OrderDetails extends AppCompatActivity
{

    private TextView textId,textPhone,textTotal,textAddress,textComment;
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    //--------------------------------
    private OrderDetailsAdabter orderDetailsAdabter;
    private String orderId="";


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_details);


        //----------------------Id-------------------------------//
        textId=(TextView)findViewById(R.id.id_order_details);
        textPhone=(TextView)findViewById(R.id.phone_order_details);
        textAddress=(TextView)findViewById(R.id.address_order_details);
        textTotal=(TextView)findViewById(R.id.total_order_details);
        textComment=(TextView)findViewById(R.id.comment_order_details);
        recyclerView=(RecyclerView)findViewById(R.id.recyclerView_order_details);


        recyclerView.setHasFixedSize(true);
        layoutManager=new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);


        if (getIntent()!=null)
        {
            orderId=getIntent().getStringExtra("orderId");
        }

          //-----------Event----------------//
        textId.setText(orderId);
        textPhone.setText(Common.currentRequest.getPhoneClient());
        textAddress.setText(Common.currentRequest.getAddress());
        textTotal.setText(Common.currentRequest.getTotal());
        textComment.setText(Common.currentRequest.getComment());

        //Adabter
        orderDetailsAdabter=new OrderDetailsAdabter(OrderDetails.this,Common.currentRequest.getFoods());
        orderDetailsAdabter.notifyDataSetChanged();
        recyclerView.setAdapter(orderDetailsAdabter);



    }
}
