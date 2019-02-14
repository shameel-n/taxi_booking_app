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

public class SigninActivity extends AppCompatActivity {

    EditText e1_email,e2_password;
    FirebaseAuth auth;
    ProgressDialog dialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);
        e1_email = (EditText)findViewById(R.id.editText2);
        e2_password = findViewById(R.id.editText3);
        auth = FirebaseAuth.getInstance();
        dialog = new ProgressDialog(this);
    }
    public void signinUser(View v){
        dialog.setMessage("signing in..pls wait");
        dialog.show();
        if(e1_email.getText().toString().equals("")||e2_password.getText().toString().equals("")){
            dialog.hide();
            Toast.makeText(getApplicationContext(),"fields can not be blank",Toast.LENGTH_SHORT).show();
        }
        else{
            auth.signInWithEmailAndPassword(e1_email.getText().toString(),e2_password.getText().toString())
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>(){
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task){
                            if(task.isSuccessful()){
                                dialog.hide();
                                Toast.makeText(getApplicationContext(),"successfully signed in",Toast.LENGTH_SHORT).show();
                                Intent i= new Intent(SigninActivity.this,MainpageActivity.class);
                                startActivity(i);
                                finish();
                            }
                            else{
                                dialog.hide();
                                Toast.makeText(getApplicationContext(),"user not found",Toast.LENGTH_SHORT).show();
                            }
                        }

                    }
            );
        }
    }

}
