package ykk.http.com.http;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
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
            HttpClient httpClient=new DefaultHttpClient();
            HttpGet httpGet=new HttpGet("http://www.marschen.com/data1.html");
            try {
                HttpResponse response=httpClient.execute(httpGet);
                int code= response.getStatusLine().getStatusCode();
                if(code ==200)
                {
                    HttpEntity entity=response.getEntity();
                    InputStream in=entity.getContent();
                    BufferedReader reader=new BufferedReader(new InputStreamReader(in));
                    String data=reader.readLine();
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
