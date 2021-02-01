package com.example.ourhome;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.ourhome.ui.myProducts.Product;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class AddNewItemActivity extends AppCompatActivity {

    FirebaseAuth mFirebaseAuth;
    FirebaseFirestore fStore;
    String userID;
    String userName;

    String[] homegroupsNames;
    long currentHomeGroup;

    EditText editTextShoppingListProducts;
    Button buttonAddItemToShoppingList;

    EditText editTextProducts;
    EditText editTextPrice;
    Button buttonAddProducts;

    EditText editTextHomeGroupName;
    EditText editTextHomeGroupAddress;
    EditText editTextHomeGroupUsernames;
    Button buttonSendInvites;

    ArrayList<String> shoppingList;
    ArrayList<Product> myProducts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_item);
        setTitle("Add new items");

        mFirebaseAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        userID = mFirebaseAuth.getCurrentUser().getUid();

        editTextShoppingListProducts = findViewById(R.id.editTextShoppingListProducts);
        buttonAddItemToShoppingList = findViewById(R.id.buttonAddItemToShoppingList);

        editTextProducts = findViewById(R.id.editTextProducts);
        editTextPrice = findViewById(R.id.editTextPrice);
        buttonAddProducts = findViewById(R.id.buttonAddProducts);

        editTextHomeGroupName = findViewById(R.id.editTextHomeGroupName);
        editTextHomeGroupAddress = findViewById(R.id.editTextHomeGroupAddress);
        editTextHomeGroupUsernames = findViewById(R.id.editTextHomeGroupUsernames);
        buttonSendInvites = findViewById(R.id.buttonSendInvites);

        DocumentReference documentReference = fStore.collection("users").document(userID);
        documentReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                Map<String, Object> map = documentSnapshot.getData();
                userName = map.get("username").toString();
                Object entry = map.get("homegroups");
                if(entry != null) {
                    String entryWithBrackets = entry.toString();
                    String entryWithoutBrackets = entryWithBrackets.substring(1, (entryWithBrackets.length() - 1)); //removing brackets []
                    homegroupsNames = entryWithoutBrackets.split(", ");
                    currentHomeGroup = documentSnapshot.getLong("currentHomeGroup");

                    buttonAddItemToShoppingList.setEnabled(true);
                    buttonAddProducts.setEnabled(true);
                } else {
                    buttonAddItemToShoppingList.setEnabled(false);
                    buttonAddProducts.setEnabled(false);
                }
            }
        });

        buttonAddItemToShoppingList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(homegroupsNames != null) {
                    DocumentReference documentReference2 = fStore.collection("homegroups").document(homegroupsNames[(int) currentHomeGroup]);
                    documentReference2.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            Map<String, Object> map = documentSnapshot.getData();
                            shoppingList = new ArrayList<>();
                            if(map != null) {
                                Object entry = map.get("shopping_list");
                                if(entry != null) {
                                    String entryWithBrackets = entry.toString();
                                    String entryWithoutBrackets = entryWithBrackets.substring(1, (entryWithBrackets.length() - 1)); //removing brackets []
                                    String[] products = entryWithoutBrackets.split(", ");
                                    Collections.addAll(shoppingList, products);
                                }
                            }
                            String[] newProducts = editTextShoppingListProducts.getText().toString().split(",");
                            short productCount = 0;
                            for(String newProduct : newProducts) {
                                shoppingList.add(newProduct.trim());
                                productCount++;
                            }

                            map.put("shopping_list", shoppingList);
                            documentReference2.set(map);

                            String toastMessage = productCount == 1 ? "Successfully added 1 product!" : "Successfully added " + productCount + " products!";
                            Toast.makeText(getApplicationContext(), toastMessage, Toast.LENGTH_SHORT).show();
                            editTextShoppingListProducts.setText("");

                        }
                    });
                }
            }
        });

        buttonSendInvites.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String homeGroupName = editTextHomeGroupName.getText().toString();
                String homeGroupAddress = editTextHomeGroupAddress.getText().toString();
                String[] users = editTextHomeGroupUsernames.getText().toString().split(",");
                ArrayList<String> userIDs = new ArrayList<>();

                //verifying input
                if(homeGroupName.isEmpty()) {
                    editTextHomeGroupName.setError("Please enter a name.");
                    editTextHomeGroupName.requestFocus();
                    return;
                }
                if(homeGroupAddress.isEmpty()) {
                    editTextHomeGroupAddress.setError("Please enter an address");
                    editTextHomeGroupAddress.requestFocus();
                    return;
                }
                if(editTextHomeGroupUsernames.getText().toString().isEmpty()) {

                }

                String homeGroupDocumentName = userName + "_" + homeGroupName;
                DocumentReference documentReference = fStore.collection("homegroups").document(homeGroupDocumentName);
                documentReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if(documentSnapshot.exists()) {
                            editTextHomeGroupName.setError("You already have a homegroup with this name.");
                            editTextHomeGroupName.requestFocus();
                        } else {
                            DocumentReference documentReference2 = fStore.collection("users").document("usernames");
                            documentReference2.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                @Override
                                public void onSuccess(DocumentSnapshot documentSnapshot) {
                                    boolean allUsernamesExist = true;
                                    for(String user : users) {
                                        Object entry = documentSnapshot.get(user);
                                        if(entry != null) {
                                            userIDs.add(entry.toString());
                                        } else {
                                            allUsernamesExist = false;
                                            editTextHomeGroupUsernames.setError("You have entered an username which doesn't exist.");
                                            editTextHomeGroupUsernames.requestFocus();
                                            break;
                                        }
                                    }
                                    if(allUsernamesExist) {
                                        //create invite
                                        DocumentReference documentReference2 = fStore.collection("invites").document(homeGroupDocumentName);
                                        Map<String, Object> inviteContents = new HashMap<>();
                                        inviteContents.put("address", homeGroupAddress);
                                        ArrayList<String> peopleInvited = new ArrayList<>();
                                        int i = 0;
                                        for(String user : users) {
                                            peopleInvited.add(user + "/pending/accept_not_notified");

                                            DocumentReference documentReference3 = fStore.collection("users").document(userIDs.get(i));
                                            documentReference3.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                                @Override
                                                public void onSuccess(DocumentSnapshot documentSnapshot) {
                                                    Map<String, Object> map = documentSnapshot.getData();
                                                    ArrayList<String> invites = new ArrayList<>();
                                                    if(map != null) {
                                                        Object entry = map.get("invites");
                                                        if(entry != null) {
                                                            String entryWithBrackets = entry.toString();
                                                            String entryWithoutBrackets = entryWithBrackets.substring(1, (entryWithBrackets.length() - 1)); //removing brackets []
                                                            String[] invitesString = entryWithoutBrackets.split(", ");
                                                            for(String invite : invitesString) {
                                                                if(!invite.isEmpty()) {
                                                                    invites.add(invite);
                                                                }
                                                            }
                                                        }
                                                    }
                                                    invites.add(homeGroupDocumentName);
                                                    map.put("invites", invites);
                                                    documentReference3.set(map);
                                                }
                                            });
                                            i++;
                                        }
                                        inviteContents.put("people_invited", peopleInvited);
                                        documentReference2.set(inviteContents);

                                        String toastMessage = "Invites sent!";
                                        Toast.makeText(getApplicationContext(), toastMessage, Toast.LENGTH_SHORT).show();
                                        editTextHomeGroupName.setText("");
                                        editTextHomeGroupAddress.setText("");
                                        editTextHomeGroupUsernames.setText("");
                                    }
                                }
                            });
                        }
                    }
                });
            }
        });

        buttonAddProducts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(homegroupsNames != null) {
                    Log.v("AICIA", "nu i nul");
                    myProducts = new ArrayList<>();
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

                                    for (String product : products) {
                                        String[] productParsed = product.split("/");
                                        Log.v("AICI", Arrays.toString(productParsed));
                                        //0 = userID
                                        //1 = userName
                                        //2 = products
                                        //3 = price
                                        myProducts.add(new Product(productParsed[0], productParsed[1], productParsed[2], Double.parseDouble(productParsed[3])));
                                    }

                                }

                                String newProducts = editTextProducts.getText().toString().replace(',', (char) 220);
                                String price = editTextPrice.getText().toString();
                                myProducts.add(new Product(userID, userName, newProducts, Double.parseDouble(price)));

                                map.put("products", myProductsToString(myProducts));
                                Log.v("AICIA", myProductsToString(myProducts).toString());
                                documentReference2.set(map);

                                String toastMessage = "Succesfully added new products";
                                Toast.makeText(getApplicationContext(), toastMessage, Toast.LENGTH_SHORT).show();
                                editTextProducts.setText("");
                                editTextPrice.setText("");
                            }
                        }
                    });
                }
            }
        });
    }

    private static ArrayList<String> myProductsToString (ArrayList<Product> myProducts){
        ArrayList<String> products = new ArrayList<>();
        for(Product product : myProducts) {
            products.add(product.getUserID() + "/" + product.getUserName() + "/" + product.getProducts() + "/" + product.getPrice());
        }
        return products;
    }
}