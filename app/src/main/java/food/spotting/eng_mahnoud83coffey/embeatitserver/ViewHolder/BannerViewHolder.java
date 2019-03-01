package food.spotting.eng_mahnoud83coffey.embeatitserver.ViewHolder;

import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import food.spotting.eng_mahnoud83coffey.embeatitserver.Common.Common;
import food.spotting.eng_mahnoud83coffey.embeatitserver.Interface.ItemClickListiner;
import food.spotting.eng_mahnoud83coffey.embeatitserver.R;

public class BannerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener ,View.OnCreateContextMenuListener
{

    public ImageView imageViewBanner;
    public TextView textViewBanner;
    public ItemClickListiner itemClickListiner;


    public BannerViewHolder(View itemView)
    {
        super(itemView);

        imageViewBanner=(ImageView)itemView.findViewById(R.id.imagefood_banner);
        textViewBanner=(TextView)itemView.findViewById(R.id.namefood_banner);


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

      //  contextMenu.add(0,0,getAdapterPosition(), Common.UPDATE);
        contextMenu.add(1,0,getAdapterPosition(),Common.DELETE);


    }

}
