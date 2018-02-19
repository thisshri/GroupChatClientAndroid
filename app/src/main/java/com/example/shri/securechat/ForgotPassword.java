package com.example.shri.securechat;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft_17;
import org.java_websocket.handshake.ServerHandshake;

import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.NoSuchAlgorithmException;


public class ForgotPassword extends AppCompatActivity {

    URI uri = new URI(HashSha.getServerIP());

    Button btnGetQuestion;
    Button btnSubmit;

    TextView tvSecurityQuestion;
    EditText etUsername;
    EditText etSecurityAnswer;
    EditText etDOB;
    EditText etPass;
    EditText etPassConfirm;
    Intent intent;

    String strRequestNumber;

    Context context;

    public ForgotPassword() throws URISyntaxException {
    }


    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        context = getApplicationContext();
        intent = getIntent();
        strRequestNumber = intent.getStringExtra("STRREQUESTNUMBER");



        btnGetQuestion = (Button)findViewById(R.id.btnGetQuestion);
        btnSubmit= (Button)findViewById(R.id.btnSubmit);

        tvSecurityQuestion = (TextView)findViewById(R.id.tvSecurityQuestion);
        etUsername = (EditText)findViewById(R.id.etUsername);
        etSecurityAnswer= (EditText)findViewById(R.id.etSecurityQuestion);
        etDOB= (EditText)findViewById(R.id.etDOB);
        etPass= (EditText)findViewById(R.id.etPassword);
        etPassConfirm= (EditText)findViewById(R.id.etPasswordConfirm);

        SocketClient.connect();

        btnGetQuestion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //send name avalible request to server

                String username = etUsername.getText().toString().trim();
                SocketClient.send("fq"+username+","+strRequestNumber+",");      // send for question
                Toast.makeText(context,"fq"+username+","+strRequestNumber+",",Toast.LENGTH_SHORT).show();
            }
        });

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(context,"SUMBIT",Toast.LENGTH_SHORT).show();
                //send all the info to server for user registration.
                String securityAns = etSecurityAnswer.getText().toString();
                String dob = etDOB.getText().toString();
                String pass1 = etPass.getText().toString();
                String pass2 = etPassConfirm.getText().toString();
                String username = etUsername.getText().toString();

                boolean isInfoCorrect = true;
                if(pass1.equals(pass2) && pass1.length()>4){
                    try {
                        pass1 = HashSha.SHA1(pass1+username);
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    } catch (NoSuchAlgorithmException e) {
                        e.printStackTrace();
                    }
                }else {
                    isInfoCorrect = false;
                    Toast.makeText(context,"Error: InPassword",Toast.LENGTH_LONG).show();
                }
                if(securityAns.length()>0){
                    try {
                        securityAns = HashSha.SHA1(securityAns);
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    } catch (NoSuchAlgorithmException e) {
                        e.printStackTrace();
                    }
                }else {
                    isInfoCorrect = false;
                    Toast.makeText(context,"Error: In Security Answer",Toast.LENGTH_LONG).show();
                }
                if(dob.length() == 10){
                    try {
                        dob = HashSha.SHA1(dob);
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    } catch (NoSuchAlgorithmException e) {
                        e.printStackTrace();
                    }

                }else {
                    isInfoCorrect = false;
                    Toast.makeText(context,"Error: In DOB ",Toast.LENGTH_LONG).show();
                }

                if(isInfoCorrect){
                    SocketClient.send("fc"+username+","+pass1+","+securityAns+","+dob+","+strRequestNumber+",");
                    finish();
                }else {
                    Toast.makeText(context,"Error: Incorrect Information Entered",Toast.LENGTH_LONG).show();
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

                    String answer = s.substring(11);
                    if ("fa".equals(s.substring(0,2)) && strRequestNumber.equals(s.substring(2,10))){
                        Toast.makeText(context,s.substring(11),Toast.LENGTH_LONG).show();
                        String question = s.substring(11);
                        int questionLenght = question.length();

                        tvSecurityQuestion.setText("QUESTION: "+question.substring(0,questionLenght-1));
                        if(s.substring(11).length() == 1){// , is returned from the server
                            Toast.makeText(context,"ERROR: Invalid username",Toast.LENGTH_LONG).show();
                        }

                    }
                    //Toast.makeText(context,s.substring(2,10),Toast.LENGTH_LONG).show();
                    //Toast.makeText(context,"1: "+reqStr,Toast.LENGTH_LONG).show();
                    //Toast.makeText(context,"answer: "+s.substring(11),Toast.LENGTH_LONG).show();
                    //Toast.makeText(context,"Length of reply"+s.substring(11).length(),Toast.LENGTH_LONG).show();

                    if ("fd".equals(s.substring(0,2)) && strRequestNumber.equals(s.substring(2,10)) && s.length() >12){
                        Toast.makeText(context, "PASSWORD CHANGE SUCESSFUL", Toast.LENGTH_LONG).show();

                    }else if ("fd".equals(s.substring(0,2)) && strRequestNumber.equals(s.substring(2,10)) && s.length() == 12){
                        Toast.makeText(context, "ERROR: Enter Correct Information.", Toast.LENGTH_LONG).show();
                    }

                }
            });

        }
        /*
        @Override
        public void onMessage(String s) {

            if ("fq".equals(s.substring(0,1))){
                Toast.makeText(context,s,Toast.LENGTH_LONG).show();
            }
            Toast.makeText(context,s,Toast.LENGTH_LONG).show();


            Toast.makeText(context,"MESSAGE RECEIVED",Toast.LENGTH_LONG).show();

        }  */

        @Override
        public void onClose(int i, String s, boolean b) {

        }

        @Override
        public void onError(Exception e) {

        }
    };
}
