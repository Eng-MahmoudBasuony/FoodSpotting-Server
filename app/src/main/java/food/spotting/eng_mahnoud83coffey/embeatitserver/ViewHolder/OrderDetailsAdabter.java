package food.spotting.eng_mahnoud83coffey.embeatitserver.ViewHolder;


import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import food.spotting.eng_mahnoud83coffey.embeatitserver.Model.Order;
import food.spotting.eng_mahnoud83coffey.embeatitserver.R;

import java.util.List;

 //View Holder
class ViewHolderOrderDetails extends RecyclerView.ViewHolder
{

    public TextView Name,Quentity,price,Discount;

    public ViewHolderOrderDetails(View itemView)
    {
        super(itemView);

        Name=(TextView)itemView.findViewById(R.id.proudact_name_item);
        Discount=(TextView)itemView.findViewById(R.id.proudact_discount_item);
        price=(TextView)itemView.findViewById(R.id.proudact_Price_item);
        Quentity=(TextView)itemView.findViewById(R.id.proudact_quentity_item);

    }

}


//Adabter
public class OrderDetailsAdabter extends RecyclerView.Adapter<ViewHolderOrderDetails>
{

    private List<Order>orderList;
    private Context context;

    public OrderDetailsAdabter(Context context,List<Order>orderList)
    {

         this.context=context;
         this.orderList=orderList;
    }


    @NonNull
    @Override
    public ViewHolderOrderDetails onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view=LayoutInflater.from(context).inflate(R.layout.order_details_item,parent,false);

        return new ViewHolderOrderDetails(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolderOrderDetails holder, int position)
    {

          Order order=orderList.get(position);

          holder.Name.setText(String.format("Name : %s",order.getProudactName()));
          holder.Discount.setText(String.format("Discount : %s",order.getDiscount()));
          holder.Quentity.setText(String.format("Quentity : %s",order.getQuentity()));
          holder.price.setText(String.format("Price : %s",order.getPrice()));

     }

    @Override
    public int getItemCount() {
        return orderList.size();
    }
}
