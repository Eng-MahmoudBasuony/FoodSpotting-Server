package food.spotting.eng_mahnoud83coffey.embeatitserver.ViewHolder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import food.spotting.eng_mahnoud83coffey.embeatitserver.R;

public class SuccessfulDeliveryViewHolder extends RecyclerView.ViewHolder{


    public TextView shipperName,shipperPhone,shipperDateRequestSend,feedBackClint;
    public RatingBar ratingBarShipper;
    public TextView idOrder,clintName,clintAddress,paymentMode,totalPrice
                     ,stateOrder,clintDateRequest,clintPhone;
    public ImageView btnRemoveitem;

    public SuccessfulDeliveryViewHolder(View itemView)
    {
        super(itemView);

        shipperName=(TextView)itemView.findViewById(R.id.done_requesr_shipper_name);
        shipperPhone=(TextView)itemView.findViewById(R.id.done_requesr_shipper_phone);
        shipperDateRequestSend=(TextView)itemView.findViewById(R.id.done_requesr_date_delevary);
        feedBackClint=(TextView)itemView.findViewById(R.id.done_requesr_comment_fedback);
        ratingBarShipper=(RatingBar)itemView.findViewById(R.id.done_requesr_ratingBar);

        idOrder=(TextView)itemView.findViewById(R.id.done_requesr_order_id);
        clintName=(TextView)itemView.findViewById(R.id.done_requesr_clint_name);
        clintAddress=(TextView)itemView.findViewById(R.id.done_requesr_address_order);
        paymentMode=(TextView)itemView.findViewById(R.id.done_requesr_payment_mode);
        totalPrice=(TextView)itemView.findViewById(R.id.done_requesr_total_price);
        stateOrder=(TextView)itemView.findViewById(R.id.done_requesr_state_order);
        clintDateRequest=(TextView)itemView.findViewById(R.id.done_requesr_date_order);
        clintPhone=(TextView)itemView.findViewById(R.id.done_requesr_clint_phone);

        btnRemoveitem=(ImageView) itemView.findViewById(R.id.done_remove_item);



    }



}
