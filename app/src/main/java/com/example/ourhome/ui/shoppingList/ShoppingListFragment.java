package com.example.ourhome.ui.shoppingList;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ourhome.R;
import com.example.ourhome.ui.myProducts.Product;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Map;

public class ShoppingListFragment extends Fragment {

    FirebaseAuth mFirebaseAuth;
    FirebaseFirestore fStore;
    String userID;
    ArrayList<String> shoppingList;

    String[] homegroupsNames;
    long currentHomeGroup;

    RecyclerView recyclerViewShoppingList;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_shopping_list, container, false);

        recyclerViewShoppingList = root.findViewById(R.id.recyclerView_shoppingList);

        mFirebaseAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();

        userID = mFirebaseAuth.getCurrentUser().getUid();

        DocumentReference documentReference = fStore.collection("users").document(userID);
        documentReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                Map<String, Object> map = documentSnapshot.getData();
                if (map != null) {
                    Object entry = map.get("homegroups");
                    if (entry != null) {
                        String entryWithBrackets = entry.toString();
                        String entryWithoutBrackets = entryWithBrackets.substring(1, (entryWithBrackets.length() - 1)); //removing brackets []
                        homegroupsNames = entryWithoutBrackets.split(", ");

                        currentHomeGroup = documentSnapshot.getLong("currentHomeGroup");

                        if (homegroupsNames != null) {
                            DocumentReference documentReference2 = fStore.collection("homegroups").document(homegroupsNames[(int) currentHomeGroup]);
                            documentReference2.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                @Override
                                public void onSuccess(DocumentSnapshot documentSnapshot) {
                                    Map<String, Object> map = documentSnapshot.getData();
                                    if (map != null) {
                                        Object entry = map.get("shopping_list");
                                        if (entry != null) {
                                            String entryWithBrackets = entry.toString();
                                            String entryWithoutBrackets = entryWithBrackets.substring(1, (entryWithBrackets.length() - 1)); //removing brackets []
                                            String[] products = entryWithoutBrackets.split(", ");

                                            shoppingList = new ArrayList<>();
                                            Collections.addAll(shoppingList, products);

                                            ShoppingListRVAdapter adapter = new ShoppingListRVAdapter(root.getContext(), shoppingList);
                                            recyclerViewShoppingList.setAdapter(adapter);
                                            recyclerViewShoppingList.setLayoutManager(new LinearLayoutManager(root.getContext()));
                                        }
                                    }
                                }
                            });
                        }
                    }
                }
            }
        });

        return root;
    }
}