package picklebit.project.gkhn.serverping;

import android.content.Context;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {

    private Button statusButton;
    private TextView infoping;
    private int success=0;
    private int fail=0;
    private Timer timer;
    private TimerTask timerTask;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);

        statusButton = (Button) findViewById(R.id.button);
        infoping = (TextView) findViewById(R.id.textView);

/*      final Handler handler = new Handler();  //after 5000 milisecond, handler will run one times
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                new Thread(new Runnable() {
                    public void run(){
                        isURLReachable(getApplicationContext());
                    }
                }).start();
            }
       }, 5000);                    */
        timer = new Timer();
        initializeTimerTask();
        timer.schedule(timerTask, 5000, 1000); //schedule the timer and after 5000 milisecond timertask will run every 1000 milisecond

    }

    private void initializeTimerTask() {
        timerTask = new TimerTask() {
            public void run() {
                new Thread(new Runnable() {
                    public void run(){
                        isURLReachable(getApplicationContext());
                    }}).start();
            }
        };
    }

    public boolean isURLReachable(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnected()) {
            try {
                URL url = new URL("http://185.29.120.42");
                HttpURLConnection urlc = (HttpURLConnection) url.openConnection();
                urlc.setConnectTimeout(1 * 1000);
                urlc.connect();

                if (urlc.getResponseCode() == 200) {        // 200 = "OK" code (http connection is fine).
                    Log.i("Connection", "Success !");
                    urlc.disconnect();
                    //View elements changable only in main thread. If we want to change view in another thread, we must use runOnUiThread
                    MainActivity.this.runOnUiThread(new Runnable() {
                        public void run(){
                            success++;
                            statusButton.setBackgroundColor(Color.GREEN);
                            infoping.setText(getResources().getString(R.string.total) + (success+fail)+"   "+
                                    getResources().getString(R.string.success) +success+"   "+getResources().getString(R.string.failed) +  fail);
                        }
                    });
                    return true;
                } else {
                    Log.i("Connection", "Failed !");
                    //View elements changable only in main thread. If we want to change view in another thread, we must use runOnUiThread
                    MainActivity.this.runOnUiThread(new Runnable() {
                        public void run(){
                            fail++;
                            statusButton.setBackgroundColor(Color.RED);
                            infoping.setText(getResources().getString(R.string.total) + (success+fail)+"   "+
                                    getResources().getString(R.string.success) +success+"   "+getResources().getString(R.string.failed) +  fail);
                        }
                    });
                    return false;
                }
            } catch (MalformedURLException e1) {return false;
            } catch (IOException e) {return false;}
        }//end of if
        //If there is no internet conection
        else{
            MainActivity.this.runOnUiThread(new Runnable() {
                public void run(){
                    statusButton.setBackgroundColor(Color.DKGRAY);
                }
            });
        }// end of else
        return false;
    }//end of isURLReachable
}
