package com.example.db.activities.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.example.db.R;
import com.example.db.activities.LoginActivity;
import com.google.android.material.appbar.AppBarLayout;
import com.google.firebase.auth.FirebaseAuth;

public class MessagingFragment extends Fragment {
    private TextView textView;

    private Toolbar toolbar;
    private Spinner experianceSpinner;
    private EditText searchPeopleEditText;
    private GridLayout gridLayout;

    private Bundle bundle;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_messaging, container, false);

        textView = root.findViewById(R.id.textview);

        gridLayout = getActivity().findViewById(R.id.app_bar_gridview);
        experianceSpinner = getActivity().findViewById(R.id.spinnerSort);
        searchPeopleEditText = getActivity().findViewById(R.id.search_people_textview);


        ViewGroup.LayoutParams layoutParams = gridLayout.getLayoutParams();
        gridLayout.setLayoutParams(layoutParams);

        searchPeopleEditText.setText("");
        experianceSpinner.setEnabled(false);
        experianceSpinner.setAlpha(0);

//        bundle = this.getArguments();
//        if (bundle != null){
//            textView.setText(bundle.getString("user_1") + "    "  + bundle.getString("user_2"));
//        }
//        else
//            System.out.println("PROOOOOST");
        return root;
    }
}