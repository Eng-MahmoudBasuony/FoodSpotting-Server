package food.spotting.eng_mahnoud83coffey.embeatitserver.ViewHolder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import food.spotting.eng_mahnoud83coffey.embeatitserver.Interface.ItemClickListiner;
import food.spotting.eng_mahnoud83coffey.embeatitserver.R;

import food.spotting.eng_mahnoud83coffey.embeatitserver.R;

public class ShippersManagementViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    public TextView shipperName;
    public TextView shipperPhone;
    public Button btnEditShipper;
    public Button btnRemoveShipper;
    private ItemClickListiner itemClickListiner;



    public ShippersManagementViewHolder(View itemView)
    {
        super(itemView);


        shipperName=(TextView)itemView.findViewById(R.id.shippers_name);
        shipperPhone=(TextView)itemView.findViewById(R.id.shipper_phone);
        btnEditShipper=(Button)itemView.findViewById(R.id.btnEdit_shipper);
        btnRemoveShipper=(Button)itemView.findViewById(R.id.btnRemove_shipper);


    }



    @Override
    public void onClick(View view)
    {
          itemClickListiner.onClick(view,getAdapterPosition(),false);
    }

    public void setItemClickListiner(ItemClickListiner itemClickListiner)
    {
        this.itemClickListiner = itemClickListiner;
    }


}
