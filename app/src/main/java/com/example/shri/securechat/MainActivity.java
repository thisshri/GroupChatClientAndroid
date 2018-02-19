package com.example.shri.securechat;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Layout;
import android.text.LoginFilter;
import android.util.Log;
import android.view.View;
import android.view.textservice.TextInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft_17;
import org.java_websocket.handshake.ServerHandshake;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

import static java.lang.Math.log10;
import static java.lang.Math.random;


public class MainActivity extends AppCompatActivity {



    private WebSocketClient mWebSocketClient;
    private String strRequestNumber;
    private String username;
    Intent intentChat;
    Intent intentAdminChat;

    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        connectWebSocket();
        strRequestNumber = getStrRequestNumber();
        intentChat = new Intent(this, UserChat.class);
        intentAdminChat = new Intent(this, AdminChat.class);

        context = getApplicationContext();

    }

    public static String getStrRequestNumber(){
        String strRequest = "";
        
        while (strRequest.length() != 8){
            Double num = random();
            strRequest = num.toString();
            strRequest = strRequest.substring(2,10);
        }
        return strRequest;
    }

    public void onClickBLogin(View view) throws UnsupportedEncodingException, NoSuchAlgorithmException {
        EditText etUsername = (EditText)findViewById(R.id.etUsername);
        EditText etPassword = (EditText)findViewById(R.id.etPassword);
        username = etUsername.getText().toString();
        String password = etPassword.getText().toString();

        if( username.equals("admin") ){
            String hashedPassword = HashSha.SHA1(password);

            mWebSocketClient.send("a"+hashedPassword+","+strRequestNumber);
            //open admin chat windows
            //Intent intent = new Intent(this, AdminChat.class);

            Log.d("act2","in admin "+username);
        }else {
            String hashedPassword = null;
            try {
                hashedPassword = HashSha.SHA1(password+username);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            }
            String strForLogin = "l"+username+","+hashedPassword+","+strRequestNumber+",";
            mWebSocketClient.send(strForLogin);

            Toast.makeText(this,strForLogin,Toast.LENGTH_SHORT).show();

            //mWebSocketClient.close();
        }
        etUsername.setText("");
        etPassword.setText("");

    }

    public void onClickTvForgotPass(View view){
        Intent intent = new Intent(this, ForgotPassword.class);
        intent.putExtra("STRREQUESTNUMBER",strRequestNumber);
        startActivity(intent);
    }

    public void onClickTvSignUp(View view){
        Intent intent = new Intent(this, UserRegistration.class);
        intent.putExtra("STRREQUESTNUMBER",strRequestNumber);
        startActivity(intent);
    }

    //CHAT RECEIVER CODE
    private void connectWebSocket() {
        URI uri;
        try {
            uri = new URI(HashSha.getServerIP());
        } catch (URISyntaxException e) {
            e.printStackTrace();
            return;
        }

        mWebSocketClient = new WebSocketClient(uri, new Draft_17()) {
            @Override
            public void onOpen(ServerHandshake serverHandshake) {

            }

            @Override
            public void onMessage(final String messageFromServer) {
                //Toast.makeText(context, messageFromServer, Toast.LENGTH_LONG).show();
                //android.widget.Toast.makeText(this, messageFromServer, Toast.LENGTH_LONG).show();
                //final String message = s;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //TextView textView = (TextView) findViewById(R.id.tvUsername);
                        //textView.setText(textView.getText() + "\n" + message);
                        if(messageFromServer.charAt(0) == 'l' && messageFromServer.charAt(1) == 'u'){
                            String tempMessage = messageFromServer.substring(2);
                            if (tempMessage.equals(strRequestNumber)){
                                //open user chat window
                                //Intent intentChat = new Intent(this, UserChat.class);
                                //send putextra username;
                                intentChat.putExtra("USERNAME",username);
                                startActivity(intentChat);
                                //Log.d("act2","in user "+username);

                            }
                        }else if(messageFromServer.charAt(0) == 'a' && messageFromServer.charAt(1) == 'a' && messageFromServer.substring(3).equals(strRequestNumber)){
                            Toast.makeText(context,messageFromServer.substring(3),Toast.LENGTH_SHORT).show();
                            startActivity(intentAdminChat);
                        }



                    }
                });
            }

            @Override
            public void onClose(int i, String s, boolean b) {
                Log.i("Websocket", "Closed " + s);
            }

            @Override
            public void onError(Exception e) {
                Log.i("Websocket", "Error " + e.getMessage());
            }
        };//websocketclient

        mWebSocketClient.connect();

    }


}