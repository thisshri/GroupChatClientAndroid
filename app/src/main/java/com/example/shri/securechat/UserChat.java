package com.example.shri.securechat;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft_17;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.net.URISyntaxException;
import java.text.DateFormat;
import java.util.Date;

public class UserChat extends AppCompatActivity {

    private WebSocketClient mWebSocketClient;
    TextView tvUserChat ;
    Intent intent;
    String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_chat);

        connectWebSocket();

        //textview chat
        //String temp = "temp";
        //textViewChat.append(temp);
        tvUserChat = (TextView)findViewById(R.id.tvAdminChat);
        tvUserChat.setMovementMethod(new ScrollingMovementMethod());
        intent = getIntent();
        username = intent.getStringExtra("USERNAME");


    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        mWebSocketClient.send("m\n< "+username+" left the group >\n");
        mWebSocketClient.close();
    }



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
                mWebSocketClient.send("m\n< "+username+" joined the group >\n");
            }

            @Override
            public void onMessage(String s) {
                final String message = s;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (message.startsWith("m") ){
                            TextView textViewChat = (TextView)findViewById(R.id.tvAdminChat);
                            textViewChat.append(message.substring(1,message.length()));
                            textViewChat.append("\n");
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
    }// connect websocket funciton.



    public void onClickSendbtn(View view){
        Button sendBtn = (Button)findViewById(R.id.btnUserChat);
        EditText etMessage = (EditText)findViewById(R.id.etUserChat);
        String temp = "m";
        //String currentDateTimeString = DateFormat.getDateTimeInstance().format(new Date());
        String currentTimeString = DateFormat.getTimeInstance(DateFormat.SHORT).format(new Date());


        mWebSocketClient.send("m"+username+"\n"+etMessage.getText().toString()+"\n"+currentTimeString+"\n");

        etMessage.setText("");


        //tvUserChat.setMovementMethod(new ScrollingMovementMethod());


        /*
        final int scrollAmount = tvUserChat.getLayout().getLineTop(tvUserChat.getLineCount()) - tvUserChat.getHeight();

        if (scrollAmount > 0)
            tvUserChat.scrollTo(0, scrollAmount);
        else
            tvUserChat.scrollTo(0, 0);
*/
    }//onClickSendbtn




}
