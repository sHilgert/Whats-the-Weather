package com.sergiohilgert.whatstheweather;

import android.content.Context;
import android.hardware.input.InputManager;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

public class MainActivity extends AppCompatActivity {
  
  EditText cityName;
  TextView result;
  
  public void findWeather(View view){
    InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
    inputMethodManager.hideSoftInputFromWindow(cityName.getWindowToken(), 0);
    DownloadTask task = new DownloadTask();
    try {
      String encodedCity = URLEncoder.encode(cityName.getText().toString(), "UTF-8");
      task.execute("http://api.openweathermap.org/data/2.5/weather?q=" + encodedCity + "&appid=0bfa615712631ff021e4bd1fd3663581");
    } catch (Exception e) {
      Toast.makeText(getApplicationContext(), "Could not find weather", Toast.LENGTH_LONG).show();
    }
    
    
  }
  
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    cityName = (EditText) findViewById(R.id.city);
    result = (TextView) findViewById(R.id.result);
  }
  
  public class DownloadTask extends AsyncTask<String, Void, String>{
  
    @Override
    protected String doInBackground(String... strings) {
      String result = "";
      URL url;
      HttpURLConnection urlConnection;
  
      try {
        url = new URL(strings[0]);
        urlConnection = (HttpURLConnection) url.openConnection();
        InputStream inputStream = urlConnection.getInputStream();
        InputStreamReader reader = new InputStreamReader(inputStream);
        int data = reader.read();
        char current;
        while(data != -1){
          current = (char) data;
          result += current;
          data = reader.read();
        }
        Log.i("RESULT", result);
        return result;
      } catch (Exception e) {
        Toast.makeText(getApplicationContext(), "Could not find weather", Toast.LENGTH_LONG).show();
      }
      return null;
    }
  
    @Override
    protected void onPostExecute(String s) {
      super.onPostExecute(s);
      try {
        JSONObject jsonObject = new JSONObject(s);
        String weatherInfo = jsonObject.getString("weather");
        JSONArray jsonArray = new JSONArray(weatherInfo);
        String message = "", main, description;
        
        for(int i = 0; i < jsonArray.length(); ++i){
          jsonObject = jsonArray.getJSONObject(i);
          main = jsonObject.getString("main");
          description = jsonObject.getString("description");
          if(main != null && description != null && !main.isEmpty() && !description.isEmpty()){
            message += main + ": " + description + "\r\n";
          }
        }
        if(message != null && !message.isEmpty()){
          result.setText(message);
        }else{
          Toast.makeText(getApplicationContext(), "Could not find weather", Toast.LENGTH_LONG).show();
        }
        
      } catch (Exception e) {
        Toast.makeText(getApplicationContext(), "Could not find weather", Toast.LENGTH_LONG).show();
      }
    }
  }
  
}
