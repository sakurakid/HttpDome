package com.example.hasee.httpdome;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private Button bt;
    private ImageView show;
    private EditText et;
    private HttpURLConnection conn;
    private String path;
    private Bitmap bitmap;

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if (msg.what==1){
                bitmap = (Bitmap)msg.obj;
                show.setImageBitmap(bitmap);
            }else if (msg.what==0){
                Toast.makeText(MainActivity.this,"失败",Toast.LENGTH_SHORT).show();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        et = (EditText) findViewById(R.id.et_path);
        show = (ImageView) findViewById(R.id.iv);
        bt = (Button) findViewById(R.id.bt);
        bt.setOnClickListener(this);
        et.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt:
                path = et.getText().toString().trim();
                //子线程网络请求
                Log.d("233","1");
                if (TextUtils.isEmpty(path)) {
                    Toast.makeText(MainActivity.this, "路径不为空", Toast.LENGTH_SHORT).show();
                } else {
                    sendConnection();
                    Log.d("233","1");
                }
        }
    }
    private void sendConnection(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                URL url;
                try {
                    url = new URL(path);
                    //打开连接
                    conn = (HttpURLConnection)url.openConnection();
                    //设置请求方式
                    conn.setRequestMethod("GET");
                    //超时时间
                    conn.setReadTimeout(5000);
                    //得到服务器的返回码
                    int code =conn.getResponseCode();
                    if (code==200){
                        //获取服务器的返回输入流
                        InputStream is = conn.getInputStream();
                        //转换成BIth对象
                        bitmap = BitmapFactory.decodeStream(is);
                        Message msg = new Message();
                        msg.what =1;
                        msg.obj=bitmap;
                        handler.sendMessage(msg);
                    }else {
                        Message message = new Message();
                        message.what = 0;
                        handler.sendMessage(message);
                    }
                    conn.disconnect();
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
