package com.hackydesk.kavach_womensafetyapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.hackydesk.kavach_womensafetyapp.kit.Tools;


public class Profile extends Fragment {

Button logout;
    Tools tools;
TextView username , email , aadhaar , phonenumber ;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
         getActivity().setTitle("Profile");
        View rootView = inflater.inflate(R.layout.fragment_profile, container,false);
        // Inflate the layout for this fragment
        username = rootView.findViewById(R.id.profile_username);
        email = rootView.findViewById(R.id.profile_email);
        aadhaar = rootView.findViewById(R.id.profile_aadhar);
        phonenumber = rootView.findViewById(R.id.profile_phonenumber);
        logout = rootView.findViewById(R.id.logout);

         tools = new Tools(getActivity(),getContext());
        //loads data from server
        dataRetriver();
        //offlineuiinflator
        oflfineUiInflator();

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getActivity(), "LOGOUT SUCCESSFUL", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(getContext(),Login_Page.class));
                FirebaseAuth.getInstance().signOut();
                SharedPreferences preferences = getActivity().getSharedPreferences("KAVACH_DATABASE", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                editor.clear();
            }
        });

        return rootView;
    }

    void dataRetriver()
    {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference docRef = db.collection("Users").document(tools.currentUserid());
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        uiInflator(document);
                        Log.d("TAG", "DocumentSnapshot data: " + document.getData());
                    } else {
                        Log.d("TAG", "No such document");
                    }
                } else {
                    Log.d("TAG", "get failed with ", task.getException());
                }
            }
        });

    }

    void uiInflator(DocumentSnapshot snapshot)
    {

        tools.savetoDb("username",snapshot.get("fullname").toString());
        tools.savetoDb("email",snapshot.get("email").toString());
        tools.savetoDb("phonenumber",snapshot.get("phonenumber").toString());
        tools.savetoDb("aadhaar",snapshot.get("aadhar").toString());
        username.setText(tools.getData("username"));
        email.setText(tools.getData("email"));
        phonenumber.setText(tools.getData("phonenumber"));
        aadhaar.setText(tools.getData("aadhaar"));
    }

    void oflfineUiInflator()
    {
        username.setText(tools.getData("username"));
        email.setText(tools.getData("email"));
        phonenumber.setText(tools.getData("phonenumber"));
        aadhaar.setText(tools.getData("aadhaar"));
    }
}