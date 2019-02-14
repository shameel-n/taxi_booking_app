package com.example.android.auto;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FirebaseUser user=FirebaseAuth.getInstance().getCurrentUser();
        if(user!=null){
            Intent i= new Intent(MainActivity.this,MainpageActivity.class);
            startActivity(i);
            finish();
        }
        else{
            setContentView(R.layout.activity_main);
        }
    }
    public void open_signUp(View v) {
        Intent i=new Intent(MainActivity.this,signupActivity.class);
        startActivity(i);
    }
    public void open_signin(View v) {
        Intent i=new Intent(MainActivity.this,SigninActivity.class);
        startActivity(i);
    }
}
