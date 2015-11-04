package com.serindlabs.realtalk;


import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

public class FirstOnBoarding extends Fragment {

    TextView txtCareerAdvice, txtCareerNextAdvice;
    Button btnGetStarted;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_first_on_boarding, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        txtCareerAdvice = (TextView) getActivity().findViewById(R.id.txtCareerAdvice);
        txtCareerAdvice.setTypeface(FontManager.setFont(getActivity(), FontManager.Font.JustAnotherHandRegular));

        txtCareerNextAdvice = (TextView) getActivity().findViewById(R.id.txtCareerNextSteps);
        txtCareerNextAdvice.setTypeface(FontManager.setFont(getActivity(), FontManager.Font.OpenSansRegular));

        btnGetStarted = (Button) getActivity().findViewById(R.id.btnGetStarted);
        btnGetStarted.setTypeface(FontManager.setFont(getActivity(), FontManager.Font.OpenSansSemiBold));

        btnGetStarted.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SecondOnBoarding secondOnBoarding = new SecondOnBoarding();
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.replace(android.R.id.content, secondOnBoarding);
                transaction.commit();
            }
        });

    }


}
