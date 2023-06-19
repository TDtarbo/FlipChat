package com.codelink.flipchat.bottom_tab_navigation.ui.groups;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.codelink.flipchat.R;

public class GroupsFragment extends Fragment {

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_groups, container, false);
        TextView textView = view.findViewById(R.id.text_dashboard);
        textView.setText("Dashboard");

        return view;
}}