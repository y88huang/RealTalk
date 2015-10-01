package com.example.realtalk.realtalk;


import android.app.Activity;
import android.app.FragmentTransaction;
import android.graphics.Paint;
import android.os.Bundle;
import android.app.Fragment;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class YourAccount extends Fragment {

    TextView txtBlurb,txtLogin;
    Button btnConnectWithFacebok,btnSignUp;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_your_account, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        ((Authentication)getActivity()).SetToolBarTitle("YOUR ACCOUNT");


        txtBlurb = (TextView)getActivity().findViewById(R.id.yourAccountBlurb);
        txtBlurb.setTypeface(FontManager.setFont(getActivity(), FontManager.Font.MontSerratRegular));

        btnConnectWithFacebok = (Button)getActivity().findViewById(R.id.btnConnectWithFacebok);
        btnConnectWithFacebok.setTypeface(FontManager.setFont(getActivity(), FontManager.Font.OpenSansSemiBold));

        btnSignUp= (Button)getActivity().findViewById(R.id.btnSignUp);
        btnSignUp.setTypeface(FontManager.setFont(getActivity(), FontManager.Font.OpenSansSemiBold));

        txtLogin = (TextView)getActivity().findViewById(R.id.txtLogin);
        txtLogin.setTypeface(FontManager.setFont(getActivity(), FontManager.Font.OpenSansSemiBold));
        txtLogin.setPaintFlags(txtLogin.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

        btnConnectWithFacebok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(), "FaceBook clicked", Toast.LENGTH_SHORT).show();
            }
        });

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment signup = new SignUp();
                FragmentTransaction signUpTransaction = getFragmentManager().beginTransaction();
                signUpTransaction.addToBackStack(null);
                signUpTransaction.replace(R.id.fragmentReplacer,signup);
                signUpTransaction.setCustomAnimations(R.anim.fab_in,R.anim.abc_fade_out);
                signUpTransaction.commit();
            }
        });
    }

}