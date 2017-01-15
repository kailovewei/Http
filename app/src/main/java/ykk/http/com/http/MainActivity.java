package ykk.http.com.http;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private Button button;
    private EditText editText;
    private Handler handler;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        button= (Button) findViewById(R.id.button_Id);
        editText= (EditText) findViewById(R.id.editText_Id);
        button.setOnClickListener(this);
        handler=new NetworkHandler();
    }
    @Override
    public void onClick(View v) {
        //原则:在主线程当中不能访问网络。
        NetworkThread thread =new NetworkThread();
        thread.start();
    }
    class NetworkHandler extends Handler
    {
        @Override
        public void handleMessage(Message msg) {
            String data=(String)msg.obj;
            editText.setText(data);
        }
    }
    class NetworkThread extends Thread
    {
        @Override
        public void run() {
            //创建HttpClient
            HttpClient httpClient=new DefaultHttpClient();
            //创建代表请求的对象，参数是访问的服务器地址。
            //   http://www.baidu.com
            HttpGet httpGet=new HttpGet("http://www.marschen.com/data1.html");
            //获取网址的请求头文件信息
            Header[] reqHeader=httpGet.getAllHeaders();
            for(int i=0;i<reqHeader.length;i++) {
                String name = reqHeader[i].getName();
                String value=reqHeader[i].getValue();
                Log.d("MianActivity","request Header name"+name+" value "+value);
            }
            try {
                //执行请求，获取服务器发送的相应对象。
                HttpResponse response=httpClient.execute(httpGet);
                //检查相应的状态是否正常。检查状态码的值是否等于200
                if(response.getStatusLine().getStatusCode() == HttpStatus.SC_OK);
                {
                    //从相应对象当中取出数据
                    HttpEntity entity=response.getEntity();
                    //可用下面一行代码代替注释的代码，简化代码，直接从网络上请求一段字符串，转化为中文的形式。
                    String data= EntityUtils.toString(entity,"utf-8");
                    /*
                    InputStream in=entity.getContent();
                    BufferedReader reader=new BufferedReader(new InputStreamReader(in));
                    String data=reader.readLine();
                    */
                    Message msg=handler.obtainMessage();
                    msg.obj=data;
                    handler.sendMessage(msg);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
