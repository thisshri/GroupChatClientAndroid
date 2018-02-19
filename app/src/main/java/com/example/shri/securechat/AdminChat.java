package com.example.shri.securechat;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.util.SortedList;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft_17;
import org.java_websocket.handshake.ServerHandshake;

import java.net.Socket;
import java.net.URI;
import java.net.URISyntaxException;

public class AdminChat extends AppCompatActivity {



    URI uri = new URI(HashSha.getServerIP());

    Spinner sBlockUser;
    Spinner sUnblockUser;
    Context context;
    TextView tvAdminChat;
    private  WebSocketClient SocketClientA;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_chat);

        sBlockUser = (Spinner)findViewById(R.id.spinnerBlockUser);
        sUnblockUser= (Spinner)findViewById(R.id.spinnerUnblockUser);
        tvAdminChat = (TextView)findViewById(R.id.tvAdminChat);
        tvAdminChat.setMovementMethod(new ScrollingMovementMethod());


        context = getApplicationContext();

        //SocketClient.connect();

        connectSocket();

        sBlockUser.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(position != 0){
                    String userToBlock = parent.getItemAtPosition(position).toString();
                    //Toast.makeText(context,)
                    SocketClientA.send("b"+userToBlock);
                    Toast.makeText(context,"USER BLOCKED: "+userToBlock,Toast.LENGTH_LONG).show();
                    SocketClientA.send("v");
                    SocketClientA.send("c");

                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        sUnblockUser.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(position != 0){
                    String userToUnblock = parent.getItemAtPosition(position).toString();
                    //Toast.makeText(context,)
                    SocketClientA.send("u"+userToUnblock);
                    Toast.makeText(context,"USER UNBLOCKED: "+userToUnblock,Toast.LENGTH_LONG).show();
                    SocketClientA.send("v");
                    SocketClientA.send("c");
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });// OnItemSelected.



    }


    void connectSocket() {
        //uri, new Draft_17() throws URISyntaxException
        SocketClientA = new WebSocketClient(uri, new Draft_17()) {
            @Override
            public void onOpen(ServerHandshake serverHandshake) {
                SocketClientA.send("v");
                SocketClientA.send("c");
            }

            @Override
            public void onMessage(String s) {
                final String message = s;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //Toast.makeText(context, message,Toast.LENGTH_LONG).show();

                        if (message.startsWith("m")) {
                            TextView textViewChat = (TextView) findViewById(R.id.tvAdminChat);
                            textViewChat.append(message.substring(1, message.length()));
                            textViewChat.append("\n");
                        } else if ("vv".equals(message.substring(0, 2))) {
                            String vvString = "SELECT USER TO BLOCK,";

                            String SpinnerString = vvString.concat(message.substring(2));


                            //Toast.makeText(context, vvString,Toast.LENGTH_LONG).show();

                            String[] arrayStr = SpinnerString.split("\\s*,\\s*");

                            ArrayAdapter<String> arrAdapterBlock = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, arrayStr);
                            arrAdapterBlock.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                            // attaching data adapter to spinner
                            //sUnblockUser.setPrompt("Select User to block");
                            sBlockUser.setAdapter(arrAdapterBlock);

                        } else if ("cc".equals(message.substring(0, 2))) {
                            String vvString = "SELECT USER TO UNBLOCK,";
                            String SpinnerString = vvString.concat(message.substring(2));


                            //Toast.makeText(context, vvString,Toast.LENGTH_LONG).show();

                            String[] arrayStr = SpinnerString.split("\\s*,\\s*");

                            ArrayAdapter<String> arrAdapterBlock = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, arrayStr);
                            arrAdapterBlock.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                            //attaching data adapter to spinner
                            sUnblockUser.setAdapter(arrAdapterBlock);

                        } else {
                            //Toast.makeText(context, "ELSE:",Toast.LENGTH_LONG).show();
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

        SocketClientA.connect();
    }


    public AdminChat() throws URISyntaxException {
    }


}

