package com.hackydesk.kavach_womensafetyapp;

import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.provider.Settings;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Switch;
import android.widget.Toast;

import com.hackydesk.kavach_womensafetyapp.kit.DangerModeBackgroundListener;


public class Setting extends Fragment {

    Switch speechRecognition;
    Switch rapidsos;
    Switch shakeToActivate;
    boolean accessibilityServiceEnabled;
    SharedPreferences sharedPreferences ;
    SharedPreferences.Editor prefeditor;

    View rootView;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        sharedPreferences = getContext().getSharedPreferences("KAVACH_DATABASE", Context.MODE_PRIVATE);
        prefeditor = sharedPreferences.edit();
         accessibilityServiceEnabled = isAccessibilityServiceEnabled(getContext(), DangerModeBackgroundListener.class);
        rootView = inflater.inflate(R.layout.fragment_settings, container,false);
        rapidsos = (Switch) rootView.findViewById(R.id.rapidsos);

        rapidsos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (rapidsos.isChecked())
                {
                    accessibiltyconsentpopup();
                }
                else{
//                    Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
//                    startActivity(intent);
                    Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
                    startActivity(intent);

                    Toast.makeText(getContext(), "Find The Saviour App And Turn Off Accessibility Permission", Toast.LENGTH_SHORT).show();
                    prefeditor.putBoolean("RAPID_SOS",false);
                }
                prefeditor.apply();
            }
        });
        return rootView;
    }

    void accessibiltyconsentpopup()
    {

        AlertDialog.Builder builder = new AlertDialog.Builder((getContext()));
        builder.setTitle("Accessibility Permission Needed");
        builder.setMessage("Don't Worry We Are not  Collecting Any Data with This Permission It Will  Only Access Volume Up Button Events By Which You Can Activate Sos Mode Without Opening App To Turn Off Permission Use Rapid Sos Switch Again ");
        builder.setIcon(R.drawable.female_avatar_girl_face_woman_user_2_svgrepo_com);
        builder.setPositiveButton("Allow", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
                startActivity(intent);

                Toast.makeText(getContext(), "Find The Kavach App And Activate Accessibility Permission", Toast.LENGTH_SHORT).show();

                prefeditor.putBoolean("RAPID_SOS",true);
                rapidsos.setChecked(true);
            }
        }).setNegativeButton("Close ", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(getContext(), "Permission Denied", Toast.LENGTH_SHORT).show();
                rapidsos.setChecked(false);
                prefeditor.putBoolean("RAPID_SOS",false);
            }
        });
        prefeditor.apply();
        builder.show();
    }

    void LoadLastState()
    {
     //   skipsplash.setChecked(sharedPreferences.getBoolean("SKIP_SPLASH",false));
        // rapidsos.setChecked(sharedPreferences.getBoolean("RAPID_SOS",false));

        if (accessibilityServiceEnabled)
        {
            rapidsos.setChecked(true);
        }
        else {
            rapidsos.setChecked(false);
        }
    }
    public static boolean isAccessibilityServiceEnabled(Context context, Class<?> accessibilityService) {
        ComponentName expectedComponentName = new ComponentName(context, accessibilityService);
        String enabledServicesSetting = Settings.Secure.getString(context.getContentResolver(),  Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES);
        if (enabledServicesSetting == null)
            return false;
        TextUtils.SimpleStringSplitter colonSplitter = new TextUtils.SimpleStringSplitter(':');
        colonSplitter.setString(enabledServicesSetting);
        while (colonSplitter.hasNext()) {
            String componentNameString = colonSplitter.next();
            ComponentName enabledService = ComponentName.unflattenFromString(componentNameString);
            if (enabledService != null && enabledService.equals(expectedComponentName))
                return true;
        }
        return false;
    }
}