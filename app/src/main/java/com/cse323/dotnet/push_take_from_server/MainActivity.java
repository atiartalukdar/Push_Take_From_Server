package com.cse323.dotnet.push_take_from_server;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class MainActivity extends AppCompatActivity {
    TextView name,email,phoneNumber;
    String data="",Name="",Email="",Number="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        name = (TextView) findViewById(R.id.nameED);
        email = (TextView) findViewById(R.id.emailED);
        phoneNumber = (TextView) findViewById(R.id.phoneED);



    }

    public void SendBtn(View view){
        new Send().execute();
        /*name.setText("");
        email.setText("");
        phoneNumber.setText("");*/
        Toast.makeText(getApplicationContext(), "Uploaded to server",Toast.LENGTH_LONG).show();
    }

    public void CheckBtn(View view){
        /*Intent intent=new Intent(MainActivity.this,Main2Activity.class);
        startActivity(intent);*/
        Toast.makeText(getApplicationContext(),name.getText().toString() +"||" +Name +phoneNumber.getText().toString()+"||" +Number,Toast.LENGTH_LONG).show();

    }

    class Send extends AsyncTask<String, Void,Long > {

       // http://stackoverflow.com/questions/30740359/namevaluepair-httpparams-httpconnection-params-deprecated-on-server-request-cl

        protected Long doInBackground(String... urls) {
            Name=name.getText().toString();
            Email=email.getText().toString();
            Number= phoneNumber.getText().toString();

           /* List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
            nameValuePairs.add(new BasicNameValuePair("Name",Name));
            nameValuePairs.add(new BasicNameValuePair("Email",Email));
            nameValuePairs.add(new BasicNameValuePair("Number",Number));*/

            //Use HashMap, it works similar to NameValuePair
            Map<String,String> dataToSend = new HashMap<>();
            dataToSend.put("Name", Name);
            dataToSend.put("Email", Email);
            dataToSend.put("Number", Number);



            //Server Communication part - it's relatively long but uses standard methods

            //Encoded String - we will have to encode string by our custom method (Very easy)
            String encodedStr = getEncodedData(dataToSend);

            //data = dataToSend.toString() + "=="+ encodedStr+ "=="; //for testing
            //Will be used if we want to read some data from server
            BufferedReader reader = null;

            //Connection Handling
            try {
                //Converting address String to URL
                URL url = new URL("http://bopbd.org/phpcode.php");
                //Opening the connection (Not setting or using CONNECTION_TIMEOUT)
                HttpURLConnection con = (HttpURLConnection) url.openConnection();

                //Post Method
                con.setRequestMethod("POST");
                //To enable inputting values using POST method
                //(Basically, after this we can write the dataToSend to the body of POST method)
                con.setDoOutput(true);
                OutputStreamWriter writer = new OutputStreamWriter(con.getOutputStream());
                //Writing dataToSend to outputstreamwriter
                writer.write(encodedStr);
                //Sending the data to the server - This much is enough to send data to server
                //But to read the response of the server, you will have to implement the procedure below
                writer.flush();

                //Data Read Procedure - Basically reading the data comming line by line
                StringBuilder sb = new StringBuilder();
                reader = new BufferedReader(new InputStreamReader(con.getInputStream()));

                String line;
                while((line = reader.readLine()) != null) { //Read till there is something available
                    sb.append(line + "\n");     //Reading and saving line by line - not all at once
                }
                line = sb.toString();           //Saving complete data received in string, you can do it differently

                //Just check to the values received in Logcat
                Log.i("custom_check","The values received in the store part are as follows:");
                Log.i("custom_check",line);

            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if(reader != null) {
                    try {
                        reader.close();     //Closing the
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            //Same return null, but if you want to return the read string (stored in line)
            //then change the parameters of AsyncTask and return that type, by converting
            //the string - to say JSON or user in your case
            return null;

        }
        protected void onProgressUpdate(Integer... progress) {

        }

        protected void onPostExecute(Long result) {

        }
    }

    private String getEncodedData(Map<String,String> data) {
        StringBuilder sb = new StringBuilder();
        for(String key : data.keySet()) {
            String value = null;
            try {
                value = URLEncoder.encode(data.get(key),"UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

            if(sb.length()>0)
                sb.append("&");

            sb.append(key + "=" + value);
        }
        return sb.toString();
    }
}
