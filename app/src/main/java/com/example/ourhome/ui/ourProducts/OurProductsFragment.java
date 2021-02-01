package com.example.ourhome.ui.ourProducts;

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
import com.example.ourhome.ui.homeGroups.HomeGroup;
import com.example.ourhome.ui.homeGroups.HomeGroupsRVAdapter;
import com.example.ourhome.ui.myProducts.Product;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;

public class OurProductsFragment extends Fragment {

    FirebaseAuth mFirebaseAuth;
    FirebaseFirestore fStore;
    String userID;
    ArrayList<Product> ourProducts;

    String[] homegroupsNames;
    long currentHomeGroup;

    RecyclerView recyclerViewOurProducts;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_our_products, container, false);

        recyclerViewOurProducts = root.findViewById(R.id.recyclerView_ourProducts);

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
                                        Object entry = map.get("products");
                                        if (entry != null) {
                                            String entryWithBrackets = entry.toString();
                                            String entryWithoutBrackets = entryWithBrackets.substring(1, (entryWithBrackets.length() - 1)); //removing brackets []
                                            String[] products = entryWithoutBrackets.split(", ");

                                            ourProducts = new ArrayList<>();
                                            for (String product : products) {
                                                String[] productParsed = product.split("/");
                                                Log.v("AICI", Arrays.toString(productParsed));
                                                //0 = userID
                                                //1 = userName
                                                //2 = products
                                                //3 = price
                                                productParsed[2] = productParsed[2].replace((char) 220, ',');
                                                if (productParsed.length == 4)
                                                    ourProducts.add(new Product(productParsed[0], productParsed[1], productParsed[2], Double.parseDouble(productParsed[3])));
                                            }

                                            OurProductsRVAdapter adapter = new OurProductsRVAdapter(root.getContext(), ourProducts);
                                            recyclerViewOurProducts.setAdapter(adapter);
                                            recyclerViewOurProducts.setLayoutManager(new LinearLayoutManager(root.getContext()));
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