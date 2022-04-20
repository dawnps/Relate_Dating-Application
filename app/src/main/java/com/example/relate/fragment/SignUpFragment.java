package com.example.relate.fragment;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import com.example.relate.HomeActivity;
import com.example.relate.PreferenceActivity;
import com.example.relate.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class SignUpFragment extends Fragment {
    private FirebaseAuth mAuth;
    private EditText mName;
    private EditText mEmail;
    private EditText mPhone;
    private EditText mPassword;
    private Button mSignUpButton;
    private EditText mDob;
    private FirebaseFirestore mStore;
    private int USER_AGE = 0;
    String gender ="";
    private RadioButton maleGender;
    private RadioButton femaleGender;

    public SignUpFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_sign_up, container, false);
        mAuth = FirebaseAuth.getInstance();
        mName = view.findViewById(R.id.signup_name);
        mEmail = view.findViewById(R.id.signup_email);
        mPhone = view.findViewById(R.id.signup_phone);
        mPassword = view.findViewById(R.id.signup_password);
        mSignUpButton = view.findViewById(R.id.signup_button);
        maleGender = (RadioButton) view.findViewById(R.id.male_gender);
        femaleGender = (RadioButton) view.findViewById(R.id.female_gender);
        mStore = FirebaseFirestore.getInstance();
        mDob = view.findViewById(R.id.dob);
        Calendar calendar = Calendar.getInstance();
        int todayYear = calendar.get(Calendar.YEAR);
        int todayMonth = calendar.get(Calendar.MONTH);
        int todayDay = calendar.get(Calendar.DAY_OF_MONTH);




        mDob.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int dayOfMonth) {
                        USER_AGE = todayYear - year;
                        mDob.setText(dayOfMonth+"/"+month+"/"+year);
                    }
                },todayYear,todayMonth,todayDay).show();

            }
        });

        mSignUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = mEmail.getText().toString();
                String password = mPassword.getText().toString();

                if (maleGender.isChecked()) {
                    gender="male";
                } else if (femaleGender.isChecked()) {
                    gender="female";
                }

                mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if(task.isSuccessful()){
                            Map<String,Object> map = new HashMap<>();
                            map.put("name",mName.getText().toString());
                            map.put("dob",mDob.getText().toString());
                            map.put("age",USER_AGE);
                            map.put("phone",mPhone.getText().toString());
                            map.put("gender",gender);

                            mStore.collection("Users").document(mAuth.getCurrentUser().getUid())
                                    .set(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {

                                    if(task.isSuccessful()){
                                        Toast.makeText(getContext(), "Account Created", Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(getContext(), PreferenceActivity.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        startActivity(intent);
                                        getActivity().finish();
                                    }

                                }
                            });


                        }else{
                            Toast.makeText(getContext(),""+task.getException().getMessage(), Toast.LENGTH_SHORT);
                        }

                    }
                });
            }
        });

        return view;
    }
}