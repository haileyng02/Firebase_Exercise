package com.example.firestore_exercise;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link signupFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class signupFragment extends Fragment {

    FirebaseFirestore db;
    FirebaseAuth mAuth;

    EditText editName;
    EditText editEmail;
    EditText editPassword;
    EditText editConfirmPw;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public signupFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static signupFragment newInstance(String param1, String param2) {
        signupFragment fragment = new signupFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_signup, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        db = FirebaseFirestore.getInstance();

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        if (user!=null){
            Navigation.findNavController(view).navigate(R.id.action_signupFragment_to_loginFragment);
        }

        editName = view.findViewById(R.id.editName);
        editEmail = view.findViewById(R.id.editEmail);
        editPassword = view.findViewById(R.id.editPassword);
        editConfirmPw = view.findViewById(R.id.editConfirmPassword);

        Button registerBtn = view.findViewById(R.id.registerBtn);
        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createUser();
            }
        });

        TextView loginText = view.findViewById(R.id.login_text);
        loginText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Navigation.findNavController(view).navigate(R.id.action_signupFragment_to_loginFragment);
            }
        });
    }
    public void createUser(){
        String name = editName.getText().toString();
        String email = editEmail.getText().toString();
        String password = editPassword.getText().toString();
        String confirmPw = editConfirmPw.getText().toString();

        /*if (name + email + password + confirmPw == ""){
            Toast.makeText(getContext(),"Please fill all the information.",Toast.LENGTH_LONG);
        }*/
        if (name.isEmpty()){
            editName.setError("Please enter your name.");
            editName.requestFocus();
        }
        if (email.isEmpty()){
            editEmail.setError("Email cannot be empty");
            editEmail.requestFocus();
        }
        else if (password.isEmpty()){
            editPassword.setError("Password cannot be empty");
            editPassword.requestFocus();
        }
        else if (confirmPw.isEmpty()){
            editConfirmPw.setError("Please confirm your password!");
            editConfirmPw.requestFocus();
        }
        else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            editEmail.setError("Please provide valid email!");
            editEmail.requestFocus();
            return;
        }
        else if (!confirmPw.equals(password)){
            editConfirmPw.setError("Please make sure your passwords match.");
            editConfirmPw.requestFocus();
        }
        else {
            mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()){
                        Toast.makeText(getContext(),"User registered successfully! Please login.",Toast.LENGTH_LONG).show();
                        Map<String,String> user = new HashMap<>();
                        user.put("name",name);
                        db.collection("users").document(email).set(user);
                        Navigation.findNavController(getView()).navigate(R.id.action_signupFragment_to_loginFragment);
                    }
                    else {
                        Toast.makeText(getContext(),"Registration error: "+ task.getException().getMessage(),Toast.LENGTH_LONG).show();
                        System.out.println("Registration error: " + task.getException().getMessage());
                    }
                }
            });
        }
    }
}