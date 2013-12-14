package org.gdgfinistere.bootcamp.chat.activities;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


import org.gdgfinistere.bootcamp.chat.R;
import org.gdgfinistere.bootcamp.chat.adapters.TweetListAdapter;
import org.gdgfinistere.bootcamp.chat.model.SessionObject;
import org.gdgfinistere.bootcamp.chat.model.Tweet;
import org.gdgfinistere.bootcamp.chat.tools.AsyncConnection;
import org.gdgfinistere.bootcamp.chat.tools.JsonParser;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends Activity {

    /**
     * JSON Services for Bootcamp Chat
     *
     * Signup
     * PUT /AndroidBootcampServer/rest/user?username=horacio&password=toto
     *
     * SignIn
     * POST /AndroidBootcampServer/rest/user?username=horacio&password=toto
     *
     * Send message
     * POST /AndroidBootcampServer/rest/message?username=horacio&token=ba7db59b5c2a7173c38bc1cc601f1f8c&content=Hello%20world!
     *
     * get msg without token -> error
     * GET /AndroidBootcampServer/rest/message
     *
     * get msg  GET /AndroidBootcampServer/rest/message/timestamp?timestamp=1340573987746
     */
    public static final String MESSAGE_URL = "http://lostinbrittany.org/java/AndroidBootcampServer/rest/message";
    public static final String MESSAGE_TS_URL = "http://lostinbrittany.org/java/AndroidBootcampServer/rest/message/timestamp";
    public static final String MESSAGE_SEND_METHOD = "POST";
    public static final String MESSAGE_READ_METHOD = "GET";


    //Defining Tweet adapter which will handle data of ListView
    private TweetListAdapter adapter;

    //list of displayed tweet
    private List<Tweet> tweets = null;

    //TimerTask for scanning
    private TimerTask timerTask;
    private Timer timer= new Timer();
    //Handler : to execute task in UI thread -> enable refreshing list
    private final Handler handler = new Handler();

    /**
     * UI components
     */
    private ListView tweetsView = null;
    private Button sendBtn = null;
    private EditText tweetBox = null;

    private boolean isFirstRead = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }
    }

    @Override
    protected void onStart() {

        super.onStart();
        adapter = new TweetListAdapter(getApplicationContext(), tweets);
        tweetsView = (ListView)findViewById(R.id.main_list);

        if (null == tweetsView ) {
            Log.d("MainActivity", "list stay null");
        }
        tweetsView.setAdapter(adapter);
        tweetBox = (EditText)findViewById(R.id.main_message);

        sendBtn = (Button) findViewById(R.id.main_send_button);
        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new SendMessageTask().execute();

            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            return rootView;
        }
    }


    /**
     * startScan: scanning for new tweets
     */
    private void startScan() {
        timerTask = new TimerTask() {
            public void run() {
                handler.post(new Runnable() {
                    public void run() {
                        //execute AsyncTask
                        new ReadTweets().execute();
                    }
                });
            }};
        //start timerTask in 100 ms each 3000ms
        timer.schedule(timerTask, 100, 3000);
    }

    /**
     * stopScan: cancel timer
     */
    public void stopScan(){
        if(timerTask!=null){
            timerTask.cancel();
        }
    }
    /**
     * ReadTweets: AsyncTask to readTweet in Background and update un UI
     * @author Stephane
     *
     */
    private class ReadTweets extends AsyncConnection {

        @Override
        protected String doInBackground(String... params) {

            //first get last 20 msg
            if(isFirstRead ||
                    SessionObject.getTweetList() == null ||
                    SessionObject.getTweetList().size() == 0){
                    //Get last 20 MSG

                //Quick and dirty
                String url = MESSAGE_URL+"?username="+SessionObject.getUsername()+"&token="+SessionObject.getToken();

                Log.d("MainActivity - ReadTweets AsyncTask", "Begin SignUp HTTP call "+ url);
                String result = connect(url,MESSAGE_READ_METHOD, null);
                Log.d("MainActivity - ReadTweets AsyncTask", "End SignUp HTTP call, result: "+result);
                isFirstRead = false;
                SessionObject.setTweetList(JsonParser.parseJsonFromGetMsgs(result));
                return result;
            }
            //Find new messages from last message
            //Quick and dirty
            String url = MESSAGE_TS_URL+"?timestamp="+SessionObject.getTweetList().get(0).getTimestamp();
            Log.d("MainActivity - ReadTweets AsyncTask", "Begin SignUp HTTP call "+url);
            String result = connect(url,MESSAGE_READ_METHOD, null);
            Log.d("MainActivity - ReadTweets AsyncTask", "End SignUp HTTP call, result: "+result);
            SessionObject.addTwettToList(JsonParser.parseJsonFromGetMsgs(result));
            return result;
        }
        @Override
        protected void onPostExecute( String result )  {
            if (null == JsonParser.isError(result)) {
                adapter.updateList(SessionObject.getTweetList());
                //notify adapters that list has been changed
                adapter.notifyDataSetChanged();
            }
        }
    }

    /**
     * stop scan if pause app
     */
    @Override
    protected void onPause() {
        super.onPause();
        stopScan();
    }


    /**
     * restart scan if app resume
     */
    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        startScan();
    }

    private class SendMessageTask extends AsyncConnection {

        @Override
        protected String doInBackground(String... params) {


            //Quick and dirty
            String url = MESSAGE_URL+"?username="+SessionObject.getUsername()+"&token="+SessionObject.getToken()+"&content="+tweetBox.getText().toString();

            Log.d("MainActivity - SendMessage Async Task", "Begin SignUp HTTP call "+url);
            String result = connect(url,MESSAGE_SEND_METHOD, null);
            Log.d("MainActivity - SendMessage Async Task", "End SignUp HTTP call, result: "+result);
            return result;

        }

        @Override
        protected void onPostExecute(String result) {
            if (null != JsonParser.isError(result)) {
                Toast.makeText(MainActivity.this.getBaseContext(),
                        R.string.send_error, Toast.LENGTH_LONG).show();

            } else {
                SessionObject.setLastTweetTS(JsonParser.parseJsonFromSendMsg(result));
                tweetBox.setText("");
            }
        }
    }
}
