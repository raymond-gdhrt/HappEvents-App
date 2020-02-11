package com.example.happevents;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.happevents.Model.Users;
import com.example.happevents.Prevalent.Prevalent;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import io.paperdb.Paper;

public class LoginActivity extends AppCompatActivity {

    private Button LoginButton;
    private EditText InputNumber,InputPassword;
    private ProgressDialog loadingBar;
    private TextView AdminLink, NotAdminLink;

    private String parentDbName = "Users";
    private CheckBox chkBoxRememberMe;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        LoginButton=(Button) findViewById(R.id.login_button);
        InputNumber=(EditText) findViewById(R.id.login_phone_input);
        InputPassword=(EditText) findViewById(R.id.login_password_input);
        AdminLink= (TextView) findViewById(R.id.login_admin_link);
        NotAdminLink=(TextView) findViewById(R.id.login_not_admin_link);
        loadingBar=new ProgressDialog(this);
        chkBoxRememberMe = (CheckBox) findViewById(R.id.remember_me_checkBox);
        Paper.init(this);

        LoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LoginUser();
            }
        });

        AdminLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LoginButton.setText("Login Admin");
                AdminLink.setVisibility(View.INVISIBLE);
                NotAdminLink.setVisibility(View.VISIBLE);
                parentDbName = "Admins";
            }
        });

        NotAdminLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LoginButton.setText("Login");
                AdminLink.setVisibility(View.VISIBLE);
                NotAdminLink.setVisibility(View.INVISIBLE);
                parentDbName = "Users";

            }
        });


    }

    private void LoginUser(){

        String phone = InputNumber.getText().toString();
        String password = InputPassword.getText().toString();
        if(TextUtils.isEmpty(phone)){
            Toast.makeText(this,"Please enter your phone number...",Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(password)){
            Toast.makeText(this,"Please your password...",Toast.LENGTH_SHORT).show();
        }
        else{
            loadingBar.setTitle("Login Account");
            loadingBar.setMessage("Please wait we are checking your details");
            loadingBar.setCanceledOnTouchOutside(false);
            loadingBar.show();

            AllowAccessToAccount(phone,password);

        }
    }

    private void AllowAccessToAccount(final String phone, final String password) {

        if (chkBoxRememberMe.isChecked()){
            Paper.book().write(Prevalent.UserPhoneKey,phone);
            Paper.book().write(Prevalent.UserPasswordKey,password);

        }
        final DatabaseReference RootRef;
        RootRef = FirebaseDatabase.getInstance().getReference();
         RootRef.addListenerForSingleValueEvent(new ValueEventListener() {
             @Override
             public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                 if(dataSnapshot.child(parentDbName).child(phone).exists()){
                     Users userData = dataSnapshot.child(parentDbName).child(phone).getValue(Users.class);
                     if (userData.getPhone().equals(phone)){
                         if(userData.getPassword().equals(password)){
                             if(parentDbName.equals("Admins")){
                                 Toast.makeText(LoginActivity.this, "Logged in successfully", Toast.LENGTH_SHORT).show();
                                 loadingBar.dismiss();
                                 Intent intent = new Intent(LoginActivity.this,AdminCategoryActivity.class);
                                 startActivity(intent);
                             }else if(parentDbName.equals("Users")){
                                 Toast.makeText(LoginActivity.this, "Logged in successfully", Toast.LENGTH_SHORT).show();
                                 loadingBar.dismiss();
                                 Intent intent = new Intent(LoginActivity.this,HomeActivity.class);
                                 Prevalent.currentOnlineUser=userData;

                                  startActivity(intent);
                             }

                         }
                     }




                 }else{
                     Toast.makeText(LoginActivity.this, "Account with "+phone+" doesn't exist", Toast.LENGTH_SHORT).show();
                     loadingBar.dismiss();
                     Toast.makeText(LoginActivity.this, "You need to create a new account", Toast.LENGTH_SHORT).show();
                 }
             }

             @Override
             public void onCancelled(@NonNull DatabaseError databaseError) {

             }
         });


    }
}
