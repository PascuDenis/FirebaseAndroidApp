package com.example.db.activities.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;

import com.example.db.R;
import com.example.db.activities.ProfileNavigationActivity;
import com.example.db.config.Config;
import com.google.android.material.navigation.NavigationView;

import static com.example.db.config.Config.getThemeStatePref;

public class SettingsFragment extends Fragment {
    private Switch darkModeSwitch;
    private NestedScrollView rootLayout;
    private NavigationView navigationView;

    private Toolbar toolbar;
    private Spinner experianceSpinner;
    private EditText searchPeopleEditText;
    private GridLayout gridLayout;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_settings, container, false);

        darkModeSwitch = root.findViewById(R.id.dark_mode_switch);
        rootLayout = root.findViewById(R.id.fs_rootlaout);
        navigationView = getActivity().findViewById(R.id.nvView);

        gridLayout = getActivity().findViewById(R.id.app_bar_gridview);
        experianceSpinner = getActivity().findViewById(R.id.spinnerSort);
        searchPeopleEditText = getActivity().findViewById(R.id.search_people_textview);


        ViewGroup.LayoutParams layoutParams = gridLayout.getLayoutParams();
        layoutParams.height = 60;
        gridLayout.setLayoutParams(layoutParams);

        gridLayout.setAlpha(0);
        searchPeopleEditText.setEnabled(false);
        experianceSpinner.setEnabled(false);

        boolean isDark = getThemeStatePref(getContext());
        if (isDark){
            rootLayout.setBackgroundColor(getResources().getColor(R.color.hf_root_dark_mode));
        }
        else {
            rootLayout.setBackgroundColor(getResources().getColor(R.color.hf_root_light_mode));
        }

        darkModeSwitch.setChecked(isDark);

        darkModeSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                boolean isDark = buttonView.isChecked();
                if (isDark){
                    rootLayout.setBackgroundColor(getResources().getColor(R.color.hf_root_dark_mode));
                    navigationView.setBackgroundColor(getResources().getColor(R.color.navigation_dark_mode));
                }
                else {
                    rootLayout.setBackgroundColor(getResources().getColor(R.color.hf_root_light_mode));
                    navigationView.setBackgroundColor(getResources().getColor(R.color.navigation_light_mode));
                }
                saveThemeStatePref(isDark);
            }
        });
        return root;
    }

    private void saveThemeStatePref(boolean isDark) {
        SharedPreferences preferences = getContext().getSharedPreferences("myPref", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("isDark", isDark);
        editor.commit();
    }

}
