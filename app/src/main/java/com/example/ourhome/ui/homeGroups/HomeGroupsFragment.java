package com.example.ourhome.ui.homeGroups;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ourhome.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class HomeGroupsFragment extends Fragment {

    FirebaseAuth mFirebaseAuth;
    FirebaseFirestore fStore;
    String userID;
    String[] homegroupsNames;
    long currentHomeGroup;
    ArrayList<HomeGroup> homeGroups;

    RecyclerView recyclerViewHomeGroups;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_home_groups, container, false);
        recyclerViewHomeGroups = root.findViewById(R.id.recyclerView_homeGroups);

        mFirebaseAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();

        userID = mFirebaseAuth.getCurrentUser().getUid();
        homeGroups = new ArrayList<>();

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
                            for (int i = 0; i < homegroupsNames.length; i++) {
                                DocumentReference documentReference2 = fStore.collection("homegroups").document(homegroupsNames[i]);
                                int copyOfI = i;
                                documentReference2.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                    @Override
                                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                                        String name = documentSnapshot.getString("name");
                                        String address = documentSnapshot.getString("address");

                                        Map<String, Object> map = documentSnapshot.getData();
                                        String entryWithBrackets = map.get("users").toString();
                                        String entryWithoutBrackets = entryWithBrackets.substring(1, (entryWithBrackets.length() - 1)); //removing brackets []
                                        String[] users = entryWithoutBrackets.split(", ");

                                        boolean isHomeGroupSelected = currentHomeGroup == copyOfI;
                                        homeGroups.add(new HomeGroup(name, address, users, isHomeGroupSelected));

                                        HomeGroupsRVAdapter adapter = new HomeGroupsRVAdapter(root.getContext(), homeGroups);
                                        recyclerViewHomeGroups.setAdapter(adapter);
                                        recyclerViewHomeGroups.setLayoutManager(new LinearLayoutManager(root.getContext()));
                                    }
                                });
                            }
                        }
                    } else {
                        String toastMessage = "You don't belong to any homegroup for the moment, click + to create one!";
                        Toast.makeText(root.getContext(), toastMessage, Toast.LENGTH_LONG).show();
                    }
                }
            }
        });

        return root;
    }
}