package com.hackydesk.kavach_womensafetyapp;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hackydesk.kavach_womensafetyapp.kit.contactModel;
import com.hackydesk.kavach_womensafetyapp.kit.contactRecyclerAdaptor;

import java.util.ArrayList;


public class Dialer extends Fragment {

    ArrayList<contactModel> contacts ;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_dialer, container,false);
        getActivity().setTitle("Emergency Dialer");

        contacts = new ArrayList<>();
        RecyclerView recyclerView =rootView.findViewById(R.id.contactsRecycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        // Inflate the layout for this fragment
        contacts.add( new contactModel("NATIONAL EMERGENCY NUMBER","112"));
        contacts.add(new contactModel("POLICE","100"));
        contacts.add(new contactModel("FIRE","101"));
        contacts.add(new contactModel("AMBULANCE","102"));
        contacts.add(new contactModel("Disaster Management Services","108"));
        contacts.add(new contactModel("Women Helpline","1091"));
        contacts.add(new contactModel("CYBER CRIME HELPLINE","155620"));
        contacts.add(new contactModel("Children In Difficult Situation","1098"));
        contacts.add(new contactModel("Road Accident Emergency Service","1073"));
        contacts.add(new contactModel("Missing Child And Women","1094"));

        contactRecyclerAdaptor c1 = new contactRecyclerAdaptor(getContext(),contacts,getActivity());
        recyclerView.setAdapter(c1);
        return rootView;
    }
}