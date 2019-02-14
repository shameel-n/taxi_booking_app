package com.example.android.auto;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class signupActivity extends AppCompatActivity {

    EditText e1,e2,e3;
    FirebaseAuth auth;
    ProgressDialog dialog;
    DatabaseReference databaseReference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        e1 = (EditText)findViewById(R.id.editText7);
        e2 = (EditText)findViewById(R.id.editText8);
        e3 = (EditText)findViewById(R.id.editText9);
        auth = FirebaseAuth.getInstance();
        dialog = new ProgressDialog(this);
    }
    public void signUpUser(View v){
        dialog.setMessage("registering");
        dialog.show();
        String name = e1.getText().toString();
        String email = e2.getText().toString();
        String password = e3.getText().toString();
        if(name.equals("")||email.equals("")|| password.equals("")){
            dialog.hide();
            Toast.makeText(getApplicationContext(),"fields can not be blank",Toast.LENGTH_SHORT).show();
        }
        else{
            auth.createUserWithEmailAndPassword(email,password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>(){
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task){
                            if(task.isSuccessful()){
                                dialog.hide();
                                Toast.makeText(getApplicationContext(),"registered successfully",Toast.LENGTH_SHORT).show();
                                databaseReference= FirebaseDatabase.getInstance().getReference().child("Users");
                                Users user_object= new Users(e1.getText().toString(),e2.getText().toString(),e3.getText().toString());
                                FirebaseUser firebaseUser =auth.getCurrentUser();
                                databaseReference.child(firebaseUser.getUid()).setValue(user_object).addOnCompleteListener(new OnCompleteListener<Void>()
                                {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task){
                                        if(task.isSuccessful()){
                                            Toast.makeText(getApplicationContext(),"user data saved",Toast.LENGTH_SHORT).show();
                                            Intent myintent= new Intent(signupActivity.this,MainpageActivity.class);
                                            startActivity(myintent);
                                        }
                                        else{
                                            Toast.makeText(getApplicationContext(),"user data could not be saved",Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                            }
                            else{
                                dialog.hide();
                                Toast.makeText(getApplicationContext(),"user could not be registered",Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }
}
