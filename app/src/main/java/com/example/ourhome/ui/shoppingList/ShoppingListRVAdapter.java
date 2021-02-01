package com.example.ourhome.ui.shoppingList;

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

public class ShoppingListRVAdapter extends RecyclerView.Adapter<ShoppingListRVAdapter.MyViewHolder> {

    Context context;
    ArrayList<String> shoppingList;

    public ShoppingListRVAdapter(Context context, ArrayList<String> shoppingList) {
        this.context = context;
        this.shoppingList = shoppingList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.item_shopping_list, parent, false);
        return new ShoppingListRVAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        holder.textViewShoppingListProducts.setText(shoppingList.get(position));
    }

    @Override
    public int getItemCount() {
        return shoppingList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView textViewShoppingListProducts;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewShoppingListProducts = itemView.findViewById(R.id.textViewShoppingListProducts);
        }
    }

}
