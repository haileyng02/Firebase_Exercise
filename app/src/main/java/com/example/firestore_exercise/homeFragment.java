package com.example.firestore_exercise;

import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link homeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class homeFragment extends Fragment {

    FirebaseFirestore db;

    TextView nameText;
    EditText changenameEdit;

    String name;
    String email;


    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";


    private String mParam1;
    private String mParam2;

    public homeFragment() {
        // Required empty public constructor
    }

    public static homeFragment newInstance(String param1, String param2) {
        homeFragment fragment = new homeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.option_menu, menu);
        super.onCreateOptionsMenu(menu,inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.signout_option:
                logOut();
                break;
        }
        return super.onOptionsItemSelected(item);
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
        setHasOptionsMenu(true);
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        db = FirebaseFirestore.getInstance();

        nameText = view.findViewById(R.id.nameText);
        changenameEdit = view.findViewById(R.id.changename_edit);

        name = getArguments().getString("name");
        nameText.setText(name);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        email = user.getEmail();

        Button changenameBtn = view.findViewById(R.id.changename_btn);
        changenameBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String newname = changenameEdit.getText().toString();
                if (newname.isEmpty()){
                    changenameEdit.setError("Name cannot be empty!");
                    changenameEdit.requestFocus();
                }
                else {
                    changeName(newname);
                }
            }
        });
    }
    public void changeName(String newname) {
        DocumentReference documentReference = db.collection("users").document(email);
        documentReference.update("name",newname);

        name= newname;
        nameText.setText(name);
    }
    public void logOut() {
        FirebaseAuth.getInstance().signOut();
        Toast.makeText(getContext(),"Signed out successfully!",Toast.LENGTH_LONG);
        Navigation.findNavController(getView()).navigate(R.id.action_homeFragment_to_loginFragment);

    }
    public void deleteUser() {
        db.collection("users").document(email).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Toast.makeText(getContext(),"Deleted account successfully!",Toast.LENGTH_LONG);
                Navigation.findNavController(getView()).navigate(R.id.action_homeFragment_to_loginFragment);
            }
        })
                .addOnFailureListener(new OnFailureListener() {
                                          @Override
                                          public void onFailure(@NonNull Exception e) {
                                              Toast.makeText(getContext(),"Error deleting account!",Toast.LENGTH_LONG);
                                          }
                                      }
                );
    }

}