package org.gdgfinistere.bootcamp.chat.activities;

import android.app.Activity;
import android.app.ActionBar;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.os.Build;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


import org.gdgfinistere.bootcamp.chat.R;
import org.gdgfinistere.bootcamp.chat.model.SessionObject;
import org.gdgfinistere.bootcamp.chat.tools.AsyncConnection;
import org.gdgfinistere.bootcamp.chat.tools.JsonParser;

public class SignInActivity extends Activity {

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
     */
    public static final String SIGNIN_URL = "http://lostinbrittany.org/java/AndroidBootcampServer/rest/user";
    public static final String SIGNIN_METHOD = "POST";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        Button signInBtn = (Button)findViewById(R.id.signin_button);
        signInBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new SignInTask().execute();

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.sign_in, menu);
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
            View rootView = inflater.inflate(R.layout.fragment_sign_in, container, false);
            return rootView;
        }
    }


    private class SignInTask extends AsyncConnection {

        @Override
        protected String doInBackground(String... params) {

            String username = ((TextView)findViewById(R.id.signin_name)).getText().toString();
            String password = ((TextView)findViewById(R.id.signin_pwd)).getText().toString();


            SessionObject.setUsername(username);
            //Quick and dirty
            String url = SIGNIN_URL+"?username="+username+"&password="+password;

            Log.d("org.gdgfinistere.bootcamp.chat", "Begin SignUp HTTP call "+url);
            String result = connect(url,SIGNIN_METHOD, null);
            Log.d("org.gdgfinistere.bootcamp.chat", "End SignUp HTTP call, result: "+result);
            return result;

        }

        @Override
        protected void onPostExecute(String result) {
            if (null != JsonParser.isError(result)) {
                Toast.makeText(SignInActivity.this.getBaseContext(),
                        R.string.signin_error, Toast.LENGTH_LONG).show();

            } else {
                SessionObject.setToken(JsonParser.parseJsonFromSignin(result));
                Intent signInIntent = new Intent(SignInActivity.this, MainActivity.class);
                SignInActivity.this.startActivity(signInIntent);
            }
        }
    }

}
