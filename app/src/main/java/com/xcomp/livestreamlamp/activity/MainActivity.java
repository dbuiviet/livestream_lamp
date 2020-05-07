package com.xcomp.livestreamlamp.activity;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Rect;
import android.media.AudioFormat;
import android.media.AudioTrack;
import android.media.MediaPlayer;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.xcomp.livestreamlamp.Entity.ImageModel;
import com.xcomp.livestreamlamp.Entity.SessionModel;
import com.xcomp.livestreamlamp.R;
import com.xcomp.livestreamlamp.Utils.Utils;
import com.xcomp.livestreamlamp.WebserviceGeneralManage.VolleyRequest;
import com.xcomp.livestreamlamp.WebserviceGeneralManage.WebserviceInfors;
import com.xcomp.livestreamlamp.adapter.GridViewAdapter_Images;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.DatagramSocket;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

import helpers.MqttHelper;

import static android.media.AudioManager.STREAM_MUSIC;

public class MainActivity extends BaseActivity {

    static private String TAG = MainActivity.class.getSimpleName();

    final Handler handler = new Handler();

    MqttHelper mqttHelper;

    private SwipeRefreshLayout mSwipeRefreshLayout;

    private TextView textViewDataFromClient;
    private TextView mTextViewReplyFromServer;
    private EditText mEditTextSendMessage;
    private AudioPlayingThread audioPlayingThread;

    private boolean end = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionbar = getSupportActionBar();

        actionbar.setDisplayShowCustomEnabled(true);

        LayoutInflater inflator = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View navbar_custom_view = inflator.inflate(R.layout.navbar_custom_view, null);
        ActionBar.LayoutParams layout = new ActionBar.LayoutParams(ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.MATCH_PARENT);
        actionbar.setCustomView(navbar_custom_view);
        setupNavbarActions(navbar_custom_view);
        textViewDataFromClient = (TextView) findViewById(R.id.tv_data_from_client);

        mEditTextSendMessage = (EditText) findViewById(R.id.edt_send_message);
        mTextViewReplyFromServer = (TextView) findViewById(R.id.tv_reply_from_server);

//        gridRecycleView = (RecyclerView) findViewById(R.id.images_gridview);
//        gridViewAdapter = new GridViewAdapter_Images(this);
//        RecyclerView.LayoutManager gridLayoutManager = new GridLayoutManager(this, 2);
//        gridRecycleView.setLayoutManager(gridLayoutManager);
//        ((GridLayoutManager) gridLayoutManager).setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
//            @Override
//            public int getSpanSize(int position) {
//                return gridViewAdapter.getItemViewType(position) == GridViewAdapter_Images.ITEM_VIEW_TYPE_SESSION ? 2 : 1;
//            }
//        });
//
//        gridRecycleView.addItemDecoration(new GridSpacingItemDecoration(2, dpToPx(2), true));
////        gridRecycleView.addItemDecoration(new MarginDecoration(this));
//        gridRecycleView.setItemAnimator(new DefaultItemAnimator());
//        gridRecycleView.setAdapter(gridViewAdapter);

        this.rootViewToShowLoadingIndicator = (ViewGroup) findViewById(R.id.content_frame);

        this.mSwipeRefreshLayout = this.findViewById(R.id.swipeRefresh);
        setupRefreshWhenSwipe();

    }

    private void playAudio() {

        try {

            Log.e(TAG, "onCreate:  - start play video" );
            AudioTrack myAudioTrack = new AudioTrack(STREAM_MUSIC,
                    88200,//192000,
                    AudioFormat.CHANNEL_OUT_MONO,
                    AudioFormat.ENCODING_PCM_16BIT,
                    4096,
                    AudioTrack.MODE_STREAM);
            myAudioTrack.play();
            InputStream inStream = this.getResources().openRawResource(R.raw.ru);
            byte[] audioBuffer = new byte[120000];
            int i = Integer.MAX_VALUE;

            while ((i = inStream.read(audioBuffer, 0, audioBuffer.length)) > 0) {
                myAudioTrack.write(audioBuffer, 0, i);
                Log.e(TAG, "playAudio: read and play a chunk");
            }
        }
        catch (Exception e) {
            Log.e(TAG, "onCreate: exception when read mp3 file" + e);
        }

    }

    public void setupNavbarActions(View navbar_custom_view) {
        navbar_custom_view.findViewById(R.id.bt_setting).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.this.onMenuButtonClicked(v);
            }
        });
//        navbar_custom_view.findViewById(R.id.bt_turnon).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                MainActivity.this.onTurnOnButtonClicked(v);
//            }
//        });
//
//        navbar_custom_view.findViewById(R.id.bt_turnoff).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                MainActivity.this.onTurnOffButtonClicked(v);
//            }
//        });
    }

    /**
     * RecyclerView item decoration - give equal margin around grid item
     */
    public class GridSpacingItemDecoration extends RecyclerView.ItemDecoration {

        private int spanCount;
        private int spacing;
        private boolean includeEdge;

        public GridSpacingItemDecoration(int spanCount, int spacing, boolean includeEdge) {
            this.spanCount = spanCount;
            this.spacing = spacing;
            this.includeEdge = includeEdge;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            int position = parent.getChildAdapterPosition(view); // item position
            int column = position % spanCount; // item column

            if (includeEdge) {
                outRect.left = spacing - column * spacing / spanCount; // spacing - column * ((1f / spanCount) * spacing)
                outRect.right = (column + 1) * spacing / spanCount; // (column + 1) * ((1f / spanCount) * spacing)

                if (position < spanCount) { // top edge
                    outRect.top = spacing;
                }
                outRect.bottom = spacing; // item bottom
            } else {
                outRect.left = column * spacing / spanCount; // column * ((1f / spanCount) * spacing)
                outRect.right = spacing - (column + 1) * spacing / spanCount; // spacing - (column + 1) * ((1f /    spanCount) * spacing)
                if (position >= spanCount) {
                    outRect.top = spacing; // item top
                }
            }
        }
    }

    /**
     * Converting dp to pixel
     */
    private int dpToPx(int dp) {
        Resources r = getResources();
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics()));
    }

    @Override
    protected void onResume() {
        super.onResume();
//        AudioPlayingThread p = new AudioPlayingThread(this);
//        p.start();
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.setting_menu, menu);
//        return true;
//    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.setting_item) {

            Intent intent = new Intent(this,SettingActivity.class);
            startActivity(intent);

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void setupRefreshWhenSwipe() {

        this.mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                MainActivity.this.mSwipeRefreshLayout.setRefreshing(false);
                if (!Utils.hasInternetConnection(MainActivity.this)) {
                    Toast.makeText(MainActivity.this, MainActivity.this.getResources().getString(R.string.connect_internet_require_message), Toast.LENGTH_SHORT).show();
                    return;
                }
//                MainActivity.this.showLoadingIndicator("");
//                MainActivity.this.getAllSession();
            }
        });
    }

    public void onSendMessageButtonClicked(View v) {
        sendMessage(mEditTextSendMessage.getText().toString());
    }

    public void onSendAudioButtonClicked(View v) {
        sendAudio();
    }

    public void onTurnOnServerSocketButtonClicked(View v) {
        startServerSocket();
    }

    public void onMenuButtonClicked(View v) {
        Intent intent = new Intent(this,SettingActivity.class);
        startActivity(intent);
    }

    private void sendMessage(final String msg) {

        final Handler handler = new Handler();
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {

                try {
                    //Replace below IP with the IP of that device in which server socket open.
                    //If you change port then change the port number in the server side code also.
                    Socket s = new Socket("192.168.1.254", 9000);

                    OutputStream out = s.getOutputStream();

                    PrintWriter output = new PrintWriter(out);

                    output.println(msg);
                    output.flush();
                    BufferedReader input = new BufferedReader(new InputStreamReader(s.getInputStream()));
                    final String st = input.readLine();

                    handler.post(new Runnable() {
                        @Override
                        public void run() {

                            String s = mTextViewReplyFromServer.getText().toString();
                            if (st.trim().length() != 0)
                                mTextViewReplyFromServer.setText(s + "\nFrom Server : " + st);
                        }
                    });

                    output.close();
                    out.close();
                    s.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        thread.start();
    }

    private void sendAudio() {

        final Handler handler = new Handler();
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
//                Toast.makeText(MainActivity.this, "Start send audio via socket", Toast.LENGTH_LONG).show();
                Log.e(TAG, "sendAudio: Start send audio via socket");
                try {
                    //Replace below IP with the IP of that device in which server socket open.
                    //If you change port then change the port number in the server side code also.
                    Socket s = new Socket("192.168.1.254", 9000);

                    OutputStream out = s.getOutputStream();

                    InputStream inStream = MainActivity.this.getResources().openRawResource(R.raw.ru);
                    byte[] audioBuffer = new byte[1024];
                    int i = Integer.MAX_VALUE;

                    while ((i = inStream.read(audioBuffer, 0, audioBuffer.length)) > 0) {
                        out.write(audioBuffer);
                        Log.e(TAG, "playAudio: read and send a chunk");
                    }

                    out.close();
                    s.close();
//                    Toast.makeText(MainActivity.this, "Finish send audio via socket", Toast.LENGTH_LONG).show();
                    Log.e(TAG, "sendAudio: Finish send audio via socket");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        thread.start();
    }

    private void startServerSocket() {

        Toast.makeText(this,"Start socket server", Toast.LENGTH_LONG).show();

        Thread thread = new Thread(new Runnable() {

//            Toast.makeText(this,"Start socket server", Toast.LENGTH_LONG).show();

            private String stringData = null;

            @Override
            public void run() {

                Log.e(TAG, "Start socket server");
                try {
                    AudioTrack myAudioTrack = new AudioTrack(STREAM_MUSIC,
                            88200, //192000,
                            AudioFormat.CHANNEL_OUT_MONO,
                            AudioFormat.ENCODING_PCM_16BIT,
                            4096,
                            AudioTrack.MODE_STREAM);
                    myAudioTrack.play();

//                    DatagramSocket serverSocket = new DatagramSocket(9000);
                    ServerSocket serverSocket = new ServerSocket(9000);

                    while (true) {
                        Socket socket = serverSocket.accept();
                        if(socket != null) {
                            updateUI("\nHave a client connect");
                            InputStream inputStream = socket.getInputStream();
                            byte[] data = new byte[4096];
                            int count = inputStream.read(data);
                            if(count > 0) {
                                myAudioTrack.write(data, 0, count);
                                updateUI("received something from client");
                            }
//                            else {
//                                Log.e(TAG, "dont have any things to receive");
//                            }
                        }

//                        try {
//                            Thread.sleep(1000);
//                        } catch (InterruptedException e) {
//                            e.printStackTrace();
//                        }
                    }


//                    while (!end) {
//                        //Server is waiting for client here, if needed
//                        Socket s = ss.accept();
//                        BufferedReader input = new BufferedReader(new InputStreamReader(s.getInputStream()));
//                        PrintWriter output = new PrintWriter(s.getOutputStream());
//
//                        stringData = input.readLine();
//                        output.println("FROM SERVER - " + stringData.toUpperCase());
//                        output.flush();
//                        try {
//                            Thread.sleep(1000);
//                        } catch (InterruptedException e) {
//                            e.printStackTrace();
//                        }
//                        updateUI(stringData);
//                        if (stringData.equalsIgnoreCase("STOP")) {
//                            end = true;
//                            output.close();
//                            s.close();
//                            break;
//                        }
//                        output.close();
//                        s.close();
//                    }
//                    ss.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        thread.start();
    }

    private void updateUI(final String stringData) {

        handler.post(new Runnable() {
            @Override
            public void run() {

                String s = textViewDataFromClient.getText().toString();
                if (stringData.trim().length() != 0)
                    textViewDataFromClient.setText(s + "\n" + "From Client : " + stringData);
            }
        });
    }

    class AudioPlayingThread extends Thread {

        Context context;

        AudioPlayingThread(Context context) {
            this.context = context;
        }

        public void run() {
            try {

                Log.e(TAG, "onCreate:  - start play video" );
                AudioTrack myAudioTrack = new AudioTrack(STREAM_MUSIC,
                        88200,//192000,
                        AudioFormat.CHANNEL_OUT_MONO,
                        AudioFormat.ENCODING_PCM_16BIT,
                        4096,
                        AudioTrack.MODE_STREAM);
                myAudioTrack.play();
                InputStream inStream = this.context.getResources().openRawResource(R.raw.ru);
                byte[] audioBuffer = new byte[120000];
                int i = Integer.MAX_VALUE;

                while ((i = inStream.read(audioBuffer, 0, audioBuffer.length)) > 0) {
                    myAudioTrack.write(audioBuffer, 0, i);
                    Log.e(TAG, "playAudio: read and play a chunk");
                }
            }
            catch (Exception e) {
                Log.e(TAG, "onCreate: exception when read mp3 file" + e);
            }
        }
    }

}
