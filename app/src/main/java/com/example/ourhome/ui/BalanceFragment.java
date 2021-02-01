package com.example.ourhome.ui;

import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.example.ourhome.R;
import com.example.ourhome.SignupActivity;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Arrays;
import java.util.Map;

public class BalanceFragment extends Fragment {

    FirebaseAuth mFirebaseAuth;
    FirebaseFirestore fStore;
    String userID;

    String[] homegroupsNames;
    long currentHomeGroup;

    double amountTotal;
    double amountSpent;
    double balancePerUser;
    double balance;

    int sizeOfHomeGroup;

    TextView textViewTotalGroupBalance;
    TextView textViewAmountSpent;
    TextView textViewBalancePerUser;
    TextView textViewBalance;

    Button buttonBalance;
    Button buttonClearShoppingList;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_balance, container, false);

        mFirebaseAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        userID = mFirebaseAuth.getCurrentUser().getUid();

        textViewTotalGroupBalance = root.findViewById(R.id.textViewTotalGroupBalance);
        textViewAmountSpent = root.findViewById(R.id.textViewAmountSpent);
        textViewBalancePerUser = root.findViewById(R.id.textViewBalancePerUser);
        textViewBalance = root.findViewById(R.id.textViewBalance);
        buttonBalance = root.findViewById(R.id.buttonBalance);
        buttonClearShoppingList = root.findViewById(R.id.buttonClearShoppingList);

        DialogInterface.OnClickListener dialogBalanceClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
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
                                                map.remove("products");
                                                documentReference2.set(map);
                                                textViewTotalGroupBalance.setText("0.0");
                                                textViewBalancePerUser.setText("0.0");
                                                textViewAmountSpent.setText("0.0");
                                                textViewBalance.setText("Balanced! :)");
                                            }
                                        }
                                    });
                                }
                            }
                        }
                    }
                });
            }
        };

        buttonBalance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(root.getContext());
                builder.setMessage("This will delete all products and set the balance to 0. Proceed?").setPositiveButton("Yes", dialogBalanceClickListener)
                        .setNegativeButton("No", null).show();
            }
        });

        DialogInterface.OnClickListener dialogClearShoppingListClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
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
                                                map.remove("shopping_list");
                                                documentReference2.set(map);

                                                Toast.makeText(root.getContext(), "Shopping list cleared!", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                                }
                            }
                        }
                    }
                });
            }
        };

        buttonClearShoppingList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(root.getContext());
                builder.setMessage("This will clear the shopping list. Proceed?").setPositiveButton("Yes", dialogClearShoppingListClickListener)
                        .setNegativeButton("No", null).show();
            }
        });

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
                                    textViewBalancePerUser.setText("0.0");
                                    if (map != null) {
                                        Object entry = map.get("products");
                                        if (entry != null) {
                                            String entryWithBrackets = entry.toString();
                                            String entryWithoutBrackets = entryWithBrackets.substring(1, (entryWithBrackets.length() - 1)); //removing brackets []
                                            String[] products = entryWithoutBrackets.split(", ");

                                            amountTotal = 0;
                                            amountSpent = 0;

                                            for (String product : products) {
                                                String[] productParsed = product.split("/");
                                                Log.v("AICI", Arrays.toString(productParsed));
                                                //0 = userID
                                                //1 = userName
                                                //2 = products
                                                //3 = price
                                                if (productParsed.length == 4) {
                                                    double priceForProduct = Double.parseDouble(productParsed[3]);
                                                    amountTotal += priceForProduct;
                                                    if(userID.equals(productParsed[0])) {
                                                        amountSpent += priceForProduct;
                                                    }
                                                }
                                            }

                                            entry = map.get("users");
                                            if(entry != null) {
                                                entryWithBrackets = entry.toString();
                                                entryWithoutBrackets = entryWithBrackets.substring(1, (entryWithBrackets.length() - 1)); //removing brackets []
                                                String[] users = entryWithoutBrackets.split(", ");
                                                sizeOfHomeGroup = users.length;

                                                balancePerUser = amountTotal / sizeOfHomeGroup;
                                                textViewBalancePerUser.setText(String.valueOf(balancePerUser));

                                                balance = balancePerUser - amountSpent;
                                                if(balance > 0) {
                                                    textViewBalance.setText("You have to pay " + balance + " lei.");
                                                } else if(balance < 0) {
                                                    textViewBalance.setText("You have to receive " + (-balance) + " lei.");
                                                } else {
                                                    textViewBalance.setText("Balanced! :)");
                                                }
                                            }
                                        } else {
                                            textViewBalance.setText("Balanced! :)");
                                        }
                                    }
                                    textViewTotalGroupBalance.setText(String.valueOf(amountTotal));
                                    textViewAmountSpent.setText(String.valueOf(amountSpent));
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