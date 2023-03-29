package com.hackydesk.kavach_womensafetyapp.kit;



import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.Nullable;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.hackydesk.kavach_womensafetyapp.R;

public class bottomsheetDialog extends BottomSheetDialogFragment {

    Tools t1 ;
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable
    ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View v = inflater.inflate(R.layout.mottom,
                container, false);
//
        t1= new Tools(getActivity(),getContext());
        Button call_button = v.findViewById(R.id.call_button);
        Button course_button = v.findViewById(R.id.course_button);

        call_button.setOnClickListener(v1 -> {
            Toast.makeText(getActivity(),
                            "Calling....", Toast.LENGTH_SHORT)
                    .show();
            Intent callIntent = new Intent(Intent.ACTION_CALL);
            callIntent.setData(Uri.parse("tel:" +t1.getData("phoneNumberForPolice")));
            getContext().startActivity(callIntent);
            dismiss();
        });



        course_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                Toast.makeText(getActivity(),
                                "Opening Maps", Toast.LENGTH_SHORT)
                        .show();
                getDirections();
                dismiss();
            }
        });
        return v;
    }
    public void getDirections() {

        Double lat = Double.parseDouble(t1.getData("UserLat"));
        Double lng = Double.parseDouble(t1.getData("UserLng"));
        t1.getdirections(lat,lng);
    }

}
