package com.example.shri.securechat;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft_17;
import org.java_websocket.handshake.ServerHandshake;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.NoSuchAlgorithmException;

public class UserRegistration extends AppCompatActivity {



    URI uri = new URI(HashSha.getServerIP());

    EditText etUsername;
    EditText etFirstName;
    EditText etLastName;
    EditText etPass1;
    EditText etPass2;
    EditText etEmail;
    EditText etDob;
    EditText etSecurityQuestion;
    EditText etSecurityAns;

    Button btnCheckUsername;
    Button btnSignup;

    Context context;
    Intent intent;

    String strRequestNumber;
    String username;
    String firstName;
    String lastName;
    String pass1;
    String pass2;
    String email;
    String dob;
    String securityQ;
    String securityA;

    public UserRegistration() throws URISyntaxException {
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_registration);

        context = getApplicationContext();
        intent = getIntent();

        strRequestNumber = intent.getStringExtra("STRREQUESTNUMBER");

        etUsername = (EditText)findViewById(R.id.etUsername);
        etFirstName = (EditText)findViewById(R.id.etFirstName);
        etLastName = (EditText)findViewById(R.id.etLastName);
        etPass1 = (EditText)findViewById(R.id.etPassword);
        etPass2 = (EditText)findViewById(R.id.etPasswordConfirm);
        etEmail = (EditText)findViewById(R.id.etEmail);
        etDob = (EditText)findViewById(R.id.etDOB);
        etSecurityQuestion = (EditText)findViewById(R.id.etSecurityQuestion);
        etSecurityAns = (EditText)findViewById(R.id.etSecurityAnswer);






        btnCheckUsername = (Button)findViewById(R.id.btnCheckUsername);
        btnSignup = (Button)findViewById(R.id.btnSignup);
        btnSignup.setEnabled(false);

        SocketClient.connect();

        btnCheckUsername.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                username = etUsername.getText().toString().trim();


                if(username.length() >3 && username.matches("([A-Za-z])\\w+")){
                    SocketClient.send("ra"+strRequestNumber+username);
                }else if(username.length() <4){
                    Toast.makeText(context,"username length can't be less than 4.",Toast.LENGTH_LONG).show();
                }

                //Toast.makeText(context,"ra"+strRequestNumber+username,Toast.LENGTH_LONG).show();

            }
        });

        btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                firstName = etFirstName.getText().toString().trim();
                lastName = etLastName.getText().toString().trim();
                pass1 = etPass1.getText().toString().trim();
                pass2 = etPass2.getText().toString().trim();
                email = etEmail.getText().toString().trim();
                dob = etDob.getText().toString();
                securityQ = etSecurityQuestion.getText().toString().trim();
                securityA = etSecurityAns.getText().toString().trim();

                boolean isInfoCorrent = true;

                if (username.length() >3 && username.matches("([A-Za-z])\\w+")){    //username

                }else{
                    isInfoCorrent = false;
                    Toast.makeText(context,"ERROR IN: "+username,Toast.LENGTH_LONG).show();
                }

                if (firstName.length() >3 && firstName.matches("[a-zA-Z]+")){       //firstname

                }else{
                    isInfoCorrent = false;
                    Toast.makeText(context,"ERROR IN: "+firstName,Toast.LENGTH_LONG).show();
                }

                if (lastName.length() >3 && lastName.matches("[a-zA-Z]+")){       //lastname

                }else{
                    isInfoCorrent = false;
                    Toast.makeText(context,"ERROR IN: "+lastName,Toast.LENGTH_LONG).show();
                }

                if (pass1.equals(pass2) && pass1.length()>4){       //password
                    try {
                        pass1 = HashSha.SHA1(pass1+username);
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    } catch (NoSuchAlgorithmException e) {
                        e.printStackTrace();
                    }
                }else{
                    isInfoCorrent = false;
                    Toast.makeText(context,"ERROR IN: PASSWORD",Toast.LENGTH_LONG).show();
                }

                if (email.matches("[a-z]+[0-9]*[_]*[a-z]*[0-9]*@[a-z]+.[a-z]+")){       //EMAIL

                }else{
                    isInfoCorrent = false;
                    Toast.makeText(context,"ERROR IN: "+email,Toast.LENGTH_LONG).show();
                }

                if (dob.length() == 10 && dob.matches("(0[1-9]|[12][0-9]|3[01])[/](0[1-9]|1[0-2])[/](19[0-9][0-9]|20[0-9][0-9])")){       //DOB
                    try {
                        dob = HashSha.SHA1(dob);
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                        isInfoCorrent = false;
                    } catch (NoSuchAlgorithmException e) {
                        e.printStackTrace();
                        isInfoCorrent = false;
                    }
                }else{
                    isInfoCorrent = false;
                    Toast.makeText(context,"ERROR IN: "+dob,Toast.LENGTH_LONG).show();
                }

                if (securityQ.length() > 0){       //SECURITY Q

                }else{
                    isInfoCorrent = false;
                    Toast.makeText(context,"ERROR IN SECURITY QUESTION: "+securityQ,Toast.LENGTH_LONG).show();
                }

                if (securityA.length() > 0){       //SECURITY Q

                }else{
                    isInfoCorrent = false;
                    Toast.makeText(context,"ERROR IN SECURITY ANSWER: "+securityA,Toast.LENGTH_LONG).show();
                }

                if (isInfoCorrent){
                    SocketClient.send("rr'"+username+"','"+firstName+"','"+lastName+"','"+pass1+"','"+dob+"','"+email+"','"+securityQ+"','"+securityA+"','u'");
                    Toast.makeText(context,"SIGN UP SUCCESSFUL",Toast.LENGTH_LONG).show();
                    finish();
                }


            }
        });

    }

    WebSocketClient SocketClient = new WebSocketClient(uri, new Draft_17()) {
        @Override
        public void onOpen(ServerHandshake serverHandshake) {

        }

        @Override
        public void onMessage(final String s) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    //String answer = s.substring(11);
                    //String str = "ra"+strRequestNumber+username;
                    if ("ra".equals(s.substring(0,2)) && strRequestNumber.equals(s.substring(2,10))){
                        Toast.makeText(context,"USERNAME AVAILABLE",Toast.LENGTH_LONG).show();
                        btnSignup.setEnabled(true);

                        }

                    if ("rn".equals(s.substring(0,2)) && strRequestNumber.equals(s.substring(2,10))){
                        Toast.makeText(context,"USERNAME NOT AVAILABLE",Toast.LENGTH_LONG).show();

                    }



                }
            });

        }


        @Override
        public void onClose(int i, String s, boolean b) {

        }

        @Override
        public void onError(Exception e) {

        }
    };



}
