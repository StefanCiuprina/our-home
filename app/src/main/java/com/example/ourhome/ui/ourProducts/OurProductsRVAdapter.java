package com.example.ourhome.ui.ourProducts;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ourhome.R;
import com.example.ourhome.ui.myProducts.Product;

import java.util.ArrayList;

public class OurProductsRVAdapter extends RecyclerView.Adapter<OurProductsRVAdapter.MyViewHolder> {

    Context context;
    ArrayList<Product> ourProducts;

    public OurProductsRVAdapter(Context context, ArrayList<Product> ourProducts) {
        this.context = context;
        this.ourProducts = ourProducts;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.item_our_products, parent, false);
        return new OurProductsRVAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        holder.textViewOurProductsUser.setText(ourProducts.get(position).getUserName());
        holder.textViewOurProductsProduct.setText(ourProducts.get(position).getProducts());
        holder.textViewOurProductsPrice.setText(String.valueOf(ourProducts.get(position).getPrice()));
    }

    @Override
    public int getItemCount() {
        return ourProducts.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView textViewOurProductsUser, textViewOurProductsProduct, textViewOurProductsPrice;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewOurProductsUser = itemView.findViewById(R.id.textViewOurProductsUser);
            textViewOurProductsProduct = itemView.findViewById(R.id.textViewOurProductsProduct);
            textViewOurProductsPrice = itemView.findViewById(R.id.textViewOurProductsPrice);
        }
    }

}
