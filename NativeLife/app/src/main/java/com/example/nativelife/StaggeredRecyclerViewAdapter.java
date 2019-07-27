package com.example.nativelife;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;

public class StaggeredRecyclerViewAdapter extends RecyclerView.Adapter<StaggeredRecyclerViewAdapter.ViewHolder>{

    // for debugging
    private static final String TAG = "StaggerdRecyclerViewAd";

    private ArrayList<String> mNames = new ArrayList<>();
    private ArrayList<String> mImageUrls = new ArrayList<>();
    private ArrayList<String> mCats = new ArrayList<>();
    private String Country;
    private String City;
    private String userName;
    private Context mContext;
    private String cat;
    private String Subject;

    public StaggeredRecyclerViewAdapter(Context context, ArrayList<String> names, ArrayList<String> imageUrls, ArrayList<String> cats, String country, String city, String username) {
        mNames = names;
        mImageUrls = imageUrls;
        mCats = cats;
        userName = username;
        Country = country;
        City = city;
        mContext = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_grid_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Log.d(TAG, "onBindViewHolder: called.");

        RequestOptions requestOptions = new RequestOptions().placeholder(R.drawable.ic_launcher_background);

        Glide.with(mContext)
                .load(mImageUrls.get(position))
                .apply(requestOptions)
                .into(holder.image);

        holder.name.setText(mNames.get(position));
        holder.catogory.setText(mCats.get(position));

    }

    @Override
    public int getItemCount() {
        return mImageUrls.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        ImageView image;
        TextView name;
        TextView catogory;
        CardView cardView;

        public ViewHolder(View itemView) {
            super(itemView);
            this.catogory = itemView.findViewById(R.id.cat_widget);
            this.image = itemView.findViewById(R.id.image_widget);
            this.name = itemView.findViewById(R.id.name_widget);
            this.cardView = itemView.findViewById(R.id.cardView_item);


            cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
//                    Toast.makeText(view.getContext(), "Jump to the show case layout", Toast.LENGTH_SHORT).show();

                    Context clickContext = view.getContext();
                    Intent i = new Intent(clickContext, Stories.class);
                    System.out.println(name.getText()+ "========== text");

                    System.out.println(Country + ", " + City + " ," + userName + " , "+ Subject+ ", " + cat + "==== lll");
                    i.putExtra("country", Country);
                    i.putExtra("city", City);
                    i.putExtra("userName", userName);
                    i.putExtra("subject", name.getText());
                    i.putExtra("category", catogory.getText());
                    clickContext.startActivity(i);
                }
            });

        }
    }
}
