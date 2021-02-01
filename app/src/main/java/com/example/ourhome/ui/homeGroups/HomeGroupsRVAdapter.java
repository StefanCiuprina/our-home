package com.example.ourhome.ui.homeGroups;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ourhome.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Map;

public class HomeGroupsRVAdapter extends RecyclerView.Adapter<HomeGroupsRVAdapter.MyViewHolder> {

    Context context;
    ArrayList<HomeGroup> homeGroups;
    String previouslySelectedName;

    public HomeGroupsRVAdapter(Context context, ArrayList<HomeGroup> homeGroups) {
        this.context = context;
        this.homeGroups = homeGroups;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.item_home_groups, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.textViewHomeGroupName.setText(homeGroups.get(position).getName());
        holder.textViewHomeGroupAddress.setText(homeGroups.get(position).getAddress());
        StringBuilder users = new StringBuilder();
        for(String user : homeGroups.get(position).getUsers()) {
            users.append(user);
            users.append(", ");
        }
        users.delete(users.length()-2, users.length()-1); //delete the last ", "
        holder.textViewHomeGroupUsers.setText(users.toString());
        if(homeGroups.get(position).isChecked()) {
                holder.radioButtonCurrentHome.setChecked(true);
                previouslySelectedName = homeGroups.get(position).getName();
                Log.v("AICIRADIO25", homeGroups.get(position).getName());
        } else {
            holder.radioButtonCurrentHome.setChecked(false);
        }

        //listeners
        holder.radioButtonCurrentHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
                DocumentReference documentReference = FirebaseFirestore.getInstance().collection("users").document(userID);
                documentReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        Map<String, Object> map = documentSnapshot.getData();
                        String selectedHomeGroup = holder.textViewHomeGroupName.getText().toString();
                        Object entry = map.get("homegroups");
                        if(entry != null) {
                            String entryWithBrackets = entry.toString();
                            String entryWithoutBrackets = entryWithBrackets.substring(1, (entryWithBrackets.length() - 1)); //removing brackets []
                            String[] homeGroupsList = entryWithoutBrackets.split(", ");

                            int i = 0;
                            for(String homeGroup : homeGroupsList) {
                                String newHomeGroupName = homeGroup.split("_")[1]; //get the name of the homeGroup (they are of the type creatorUsername_homeGroupName)
                                if(newHomeGroupName.equals(selectedHomeGroup)) {
                                    map.put("currentHomeGroup", i);
                                    for(HomeGroup homeGroup1 : homeGroups) {
                                        if(homeGroup1.getName().equals(newHomeGroupName)) {
                                            homeGroup1.setChecked(true);
                                        } else if(homeGroup1.getName().equals(previouslySelectedName)) {
                                            homeGroup1.setChecked(false);
                                            Log.v("AICIRADIO23", "e fals acum");
                                        }
                                    }
                                    Log.v("AICIRADIO23", previouslySelectedName);
                                    previouslySelectedName = newHomeGroupName;
                                    notifyItemRangeChanged(0, homeGroups.size());
                                    break;
                                }
                                i++;
                            }
                            documentReference.set(map);
                        }
                    }
                });
            }
        });
    }

    @Override
    public int getItemCount() {
        return homeGroups.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        TextView textViewHomeGroupName, textViewHomeGroupAddress, textViewHomeGroupUsers;
        CheckBox radioButtonCurrentHome;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewHomeGroupName = itemView.findViewById(R.id.textViewHomeGroupName);
            textViewHomeGroupAddress = itemView.findViewById(R.id.textViewHomeGroupAddress);
            textViewHomeGroupUsers = itemView.findViewById(R.id.textViewHomeGroupUsers);
            radioButtonCurrentHome = itemView.findViewById(R.id.radioButtonCurrentHome);
        }
    }

}
