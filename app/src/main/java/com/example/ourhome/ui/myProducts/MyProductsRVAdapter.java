package com.example.ourhome.ui.myProducts;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ourhome.R;

import java.util.ArrayList;

public class MyProductsRVAdapter extends RecyclerView.Adapter<MyProductsRVAdapter.MyViewHolder> {

    Context context;
    ArrayList<Product> myProducts;

    public MyProductsRVAdapter(Context context, ArrayList<Product> myProducts) {
        this.context = context;
        this.myProducts = myProducts;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.item_my_products, parent, false);
        return new MyProductsRVAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        holder.textViewMyProductsProducts.setText(myProducts.get(position).getProducts());
        holder.textViewMyProductsPrice.setText(String.valueOf(myProducts.get(position).getPrice()));
    }

    @Override
    public int getItemCount() {
        return myProducts.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView textViewMyProductsProducts, textViewMyProductsPrice;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewMyProductsProducts = itemView.findViewById(R.id.textViewMyProductsProducts);
            textViewMyProductsPrice = itemView.findViewById(R.id.textViewMyProductsPrice);
        }
    }

}
