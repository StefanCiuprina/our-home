package com.example.ourhome;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class InvitationActivity extends AppCompatActivity {

    FirebaseAuth mFirebaseAuth;
    FirebaseFirestore fStore;

    TextView textViewName;
    TextView textViewAddress;
    Button buttonAccept;
    Button buttonDecline;

    String username;
    String userID;
    String homeGroup;
    String homeGroupCreator;
    String homeGroupName;
    String homeGroupAddress;
    ArrayList<String> homeGroupUsers;

    public static final String ACCEPTED = "accepted";
    public static final String DECLINED = "declined";

    public static final char COMMA_SAFE = 220;

    Map<String, Object> map;

    DocumentReference documentReferenceInvite;

    boolean everyoneAccepted;
    boolean everyoneDeclined;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invitation);

        mFirebaseAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();

        textViewName = findViewById(R.id.textViewName);
        textViewAddress = findViewById(R.id.textViewAddress);
        buttonAccept = findViewById(R.id.buttonAccept);
        buttonDecline = findViewById(R.id.buttonDecline);

        Intent intent = getIntent();
        username = intent.getExtras().getString("username");
        userID = intent.getExtras().getString("userID");
        homeGroup = intent.getExtras().getString("homeGroup");
        Log.v("AICIA2", homeGroup);
        String[] homeGroupSplit = homeGroup.split("_");
        homeGroupCreator = homeGroupSplit[0];
        homeGroupName = homeGroupSplit[1];


        documentReferenceInvite = fStore.collection("invites").document(homeGroup);
        documentReferenceInvite.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                textViewName.setText(homeGroupName);
                map = documentSnapshot.getData();
                if (map != null) {
                    Object entry = map.get("address");
                    if (entry != null) {
                        homeGroupAddress = entry.toString();
                        textViewAddress.setText(homeGroupAddress);
                    }
                    entry = map.get("people_invited");
                    if (entry != null) {
                        String entryWithBrackets = entry.toString();
                        String entryWithoutBrackets = entryWithBrackets.substring(1, (entryWithBrackets.length() - 1)); //removing brackets []
                        String[] peopleInvited = entryWithoutBrackets.split(", ");
                        boolean someoneDeclined = false;
                        for (String user : peopleInvited) {
                            String[] userParsed = user.split("/");
                            if (!username.equals(userParsed[0]) && userParsed[1].equals(DECLINED)) {
                                someoneDeclined = true;
                                break;
                            }
                        }
                        if (someoneDeclined) { //delete invitation & set declined as well & finish
                            setStatus(DECLINED);
                        }
                    }

                }
            }
        });

        buttonAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setStatus(ACCEPTED);
            }
        });

        buttonDecline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setStatus(DECLINED);
            }
        });
    }


    private void setStatus(String status) {
        Object entry = map.get("people_invited");
        if (entry != null) {
            String entryWithBrackets = entry.toString();
            String entryWithoutBrackets = entryWithBrackets.substring(1, (entryWithBrackets.length() - 1)); //removing brackets []
            String[] peopleInvited = entryWithoutBrackets.split(", ");
            int i = 0;
            int numberOfPeopleAccepted = status.equals(ACCEPTED) ? 1 : 0;
            int numberOfPeopleDeclined = status.equals(DECLINED) ? 1 : 0;
            homeGroupUsers = new ArrayList<>();
            for (String user : peopleInvited) {
                String[] userParsed = user.split("/");
                homeGroupUsers.add(userParsed[0]);
                if (username.equals(userParsed[0])) {
                    peopleInvited[i] = userParsed[0] + "/" + status;
                } else if (userParsed[1].equals(ACCEPTED)) {
                    numberOfPeopleAccepted++;
                } else if(userParsed[1].equals(DECLINED)) {
                    numberOfPeopleDeclined++;
                }
                i++;
            }
            everyoneAccepted = numberOfPeopleAccepted == i;
            everyoneDeclined = numberOfPeopleDeclined == i;
            map.put("people_invited", Arrays.asList(peopleInvited));
            documentReferenceInvite.set(map);

            DocumentReference documentReference2 = fStore.collection("users").document(userID);
            documentReference2.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    Map<String, Object> map = documentSnapshot.getData();
                    Log.v("AICIA", userID);
                    String entryWithBrackets = map.get("invites").toString();
                    String entryWithoutBrackets = entryWithBrackets.substring(1, (entryWithBrackets.length() - 1)); //removing brackets []
                    String[] invites = entryWithoutBrackets.split(", ");
                    ArrayList<String> invitesList = new ArrayList<>(Arrays.asList(invites));
                    invitesList.remove(0);
                    map.put("invites", invitesList);
                    documentReference2.set(map);

                    if (everyoneAccepted) {
                        //create homeGroup
                        documentReferenceInvite.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                //add the creator to the list of users
                                homeGroupUsers.add(homeGroupCreator);

                                DocumentReference documentReferenceNewHomegroup = fStore.collection("homegroups").document(homeGroup);
                                Map<String, Object> map = new HashMap<>();
                                map.put("name", homeGroupName);
                                map.put("address", homeGroupAddress);
                                map.put("users", homeGroupUsers);
                                documentReferenceNewHomegroup.set(map);

                                int i = 0;
                                //assign the new homegroup to each user
                                for (String user : homeGroupUsers) {
                                    //get user ID
                                    DocumentReference documentReferenceUserNames = fStore.collection("users").document("usernames");
                                    int finalI = i;
                                    documentReferenceUserNames.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                        @Override
                                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                                            String userID = documentSnapshot.getString(user);

                                            DocumentReference documentReferenceUser = fStore.collection("users").document(userID);
                                            documentReferenceUser.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                                @Override
                                                public void onSuccess(DocumentSnapshot documentSnapshot) {
                                                    Map<String, Object> map = documentSnapshot.getData();
                                                    ArrayList<String> homegroupsList = new ArrayList<>();
                                                    Object entry = map.get("homegroups");
                                                    if (entry != null) {
                                                        String entryWithBrackets = entry.toString();
                                                        String entryWithoutBrackets = entryWithBrackets.substring(1, (entryWithBrackets.length() - 1)); //removing brackets []
                                                        String[] homegroups = entryWithoutBrackets.split(", ");
                                                        homegroupsList.addAll(Arrays.asList(homegroups));
                                                    }
                                                    homegroupsList.add(homeGroup);
                                                    map.put("homegroups", homegroupsList);

                                                    entry = map.get("currentHomeGroup");
                                                    if (entry == null) {
                                                        map.put("currentHomeGroup", 0);
                                                    }
                                                    documentReferenceUser.set(map);

                                                    if(finalI == (homeGroupUsers.size()-1)) {
                                                        String message = "Your request for creating the homegroup " + homeGroupName + " has been accepted! :)";
                                                        sendMessageToUser(fStore, homeGroupCreator, message);
                                                        documentReferenceInvite.delete(); //delete invite
                                                    }
                                                }
                                            });
                                        }
                                    });
                                    i++;
                                }
                            }
                        });
                    } else if(everyoneDeclined) {
                        String message = "Unfortunately" + COMMA_SAFE + " your request for creating the homegroup " + homeGroupName + " was declined :(";
                        sendMessageToUser(fStore, homeGroupCreator, message);
                        documentReferenceInvite.delete();
                    }

                    finish();
                }
            });
        }
    }

    public static void sendMessageToUser(FirebaseFirestore fStore, String username, String message) {
        DocumentReference documentReferenceUserNames = fStore.collection("users").document("usernames");
        documentReferenceUserNames.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                String creatorID = documentSnapshot.getString(username);

                DocumentReference documentReferenceUser = fStore.collection("users").document(creatorID);
                documentReferenceUser.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        Map<String, Object> map = documentSnapshot.getData();
                        if (map != null) {
                            Object entry = map.get("info_messages");
                            ArrayList<String> infoMessages = new ArrayList<>();
                            if(entry != null) {
                                String entryWithBrackets = entry.toString();
                                String entryWithoutBrackets = entryWithBrackets.substring(1, (entryWithBrackets.length() - 1)); //removing brackets []
                                String[] infoMessagesString = entryWithoutBrackets.split(", ");

                                for(String infoMessage : infoMessagesString) {
                                    if(!infoMessage.isEmpty()) {
                                        infoMessages.add(infoMessage);
                                    }
                                }
                            }
                            infoMessages.add(message);
                            map.put("info_messages", infoMessages);
                            documentReferenceUser.set(map);
                        }
                    }
                });
            }
        });
    }
}