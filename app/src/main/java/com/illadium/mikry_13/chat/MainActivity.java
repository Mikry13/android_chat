package com.illadium.mikry_13.chat;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;

public class MainActivity extends AppCompatActivity {

    Socket socket;
    Scanner in;
    PrintStream out;
    EditText editText_send;
    Button button_send;

    ArrayList<String> messages;
    ArrayAdapter<String> mAdapter;

    ListView chat_rv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editText_send = findViewById(R.id.editText_send);
        button_send = findViewById(R.id.button_send);
        chat_rv = findViewById(R.id.chat_rv);

        messages = new ArrayList<>();

        mAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, messages);

        chat_rv.setAdapter(mAdapter);
        //chat_rv.setOnItemClickListener(mAdapter);

        new Thread()
        {
            @Override
            public void run()
            {
                try {
                    socket = new Socket("10.0.2.2", 2019);
                    in = new Scanner(socket.getInputStream());
                    out = new PrintStream(socket.getOutputStream());
                }catch (IOException e)
                {
                    e.printStackTrace();
                    return;
                }

                for(;;)
                {
                    final String mes = in.nextLine();
                    MainActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            messages.add(mes);
                            mAdapter.notifyDataSetChanged();
                        }
                    });
                }
            }
        }.start();
    }

    public void onSendClick(View view) {
        final String sent_message = editText_send.getText().toString();

        messages.add(sent_message);
        mAdapter.notifyDataSetChanged();

        new Thread()
        {
            @Override
            public void run()
            {
                out.println(sent_message);
            }
        };
    }
}
