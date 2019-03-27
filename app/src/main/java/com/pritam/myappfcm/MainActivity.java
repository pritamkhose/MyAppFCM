package com.pritam.myappfcm;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
//import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v7.widget.Toolbar;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    TextView textview;
    ProgressDialog progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
                sendSelfNotification();
            }
        });

        textview = ((TextView) findViewById(R.id.textview));
        textview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setClipboard(textview.getText().toString());
            }
        });

        progress = new ProgressDialog(this);
        progress.setTitle("Loading");
        progress.setMessage("Wait while loading...");
        progress.setCancelable(false);

    }

    @Override
    public void onResume(){
        super.onResume();
        SharedPrefUtils sh = new SharedPrefUtils(this);
        String s = sh.getSharedPrefString("deviceToken");
        if(s !=null && s.length() > 10) {
            textview.setText("deviceToken="+ s);
        }
    }


    private void setClipboard(String text) {
        if(android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.HONEYCOMB) {
            android.text.ClipboardManager clipboard = (android.text.ClipboardManager) getApplicationContext().getSystemService(Context.CLIPBOARD_SERVICE);
            clipboard.setText(text);
        } else {
            android.content.ClipboardManager clipboard = (android.content.ClipboardManager) getApplicationContext().getSystemService(Context.CLIPBOARD_SERVICE);
            android.content.ClipData clip = android.content.ClipData.newPlainText("Copied Text", text);
            clipboard.setPrimaryClip(clip);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            finishAffinity();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    String myResponse;
    private void sendSelfNotification() {
        String postBody = getBody();
        if(postBody != null){
            try {
                progress.show();
                String url = "https://fcm.googleapis.com/fcm/send";

                MediaType JSON = MediaType.parse("application/json; charset=utf-8");


                RequestBody body = RequestBody.create(JSON, postBody);

                OkHttpClient client = new OkHttpClient.Builder()
                        .connectTimeout(10, TimeUnit.SECONDS)
                        .writeTimeout(10, TimeUnit.SECONDS)
                        .readTimeout(30, TimeUnit.SECONDS)
                        .build();

                Request request = new Request.Builder()
                        .url(url)
                        .addHeader("Content-Type","application/json")
                        .addHeader("Authorization","key=AAAA-CBpE6c:APA91bEt7era5ur9lpyeKllap3qYnxnR7EULiB81q84xneudea4fLH3OF3hhfq1GiKWdR7HhVFZqTqXGvNLOYBco3IejDcJW0cVDMTDyYOFV73OWt_YuxFOnGtEbWAlGjrWgiNwQuoobGnCpM4sT43lC80n-KL5Keg")
                        .post(body)
                        .build();
                Log.d("-->>", request.toString());


                client.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        call.cancel();
                        progress.dismiss();
                        Log.d("-->>", getStackTrace(e));
                        alertDialog("Request Failure", getStackTrace(e));
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        progress.dismiss();
                        myResponse = response.body().string();

                        try {
                            JsonObject newObj = new JsonParser().parse(myResponse).getAsJsonObject();
                            myResponse = String.valueOf(newObj);
                        } catch (Exception e) {
                            Log.d("-->>", getStackTrace(e));
                        }

                        Log.d("-->>", myResponse);
                        MainActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                //text_response.setText(myResponse);
                                Toast.makeText(MainActivity.this, ""+ myResponse, Toast.LENGTH_LONG).show();
                            }
                        });

                    }
                });
            } catch (Exception e) {
                progress.dismiss();
                Log.d("-->>", getStackTrace(e));
                alertDialog("Exception", getStackTrace(e));
            }
        }
    }

    public String getStackTrace(Throwable aThrowable) {
        final Writer result = new StringWriter();
        final PrintWriter printWriter = new PrintWriter(result);
        aThrowable.printStackTrace(printWriter);
        return result.toString();
    }

    public void alertDialog(String title, String message) {
        final AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();

        // Setting Dialog Title
        alertDialog.setTitle(title);

        // Setting Dialog Message
        alertDialog.setMessage(message);

        // Setting Icon to Dialog
        //alertDialog.setIcon(R.drawable.ic_launcher_foreground);
        alertDialog.setCancelable(false);

        // Setting Cancel Button
        alertDialog.setButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                // Write your code here to execute after dialog closed
                alertDialog.cancel();
            }
        });

        // Showing Alert Message
        alertDialog.show();
    }

    public String getBody() {
        /*
         {
           "to":"deviceToken=cj629U7tt1g:APA91bFnxNpTewI6gP1lbMz0nIIoKRA8IAG7nFrE0RxnjYy9COV8rqv-wHYZ-5uh2j8TBXmcPY96Hu16faqBbTQP8g29Kai8ypxEbfqEfPBDa1UP2Pwy5qVZeU0KvkRxyWmULYXIiMGm",
           "priority":"high",
           "notification":{
              "title":"Pritam Notification hi how r u",
              "body":"Pritam is Happy with FCM!"
           },
           "data":{
              "Key-1":"JSA Data 1",
              "Key-2":"JSA Data 2",
              "Key-3":"JSA Data 3"
           }
        }
        */

        SharedPrefUtils sh = new SharedPrefUtils(this);
        String s = sh.getSharedPrefString("deviceToken");
        String title = ((EditText) findViewById(R.id.title)).getText().toString();
        String message = ((EditText) findViewById(R.id.message)).getText().toString();
        if(title == null) {
            title  ="No notification title";
        }
        if(message == null) {
            message  ="No message";
        }

        if(s !=null && s.length() > 10) {
            Gson gson = new Gson();
            HashMap<String, Object> body = new HashMap<>();
            body.put("to", "deviceToken="+s);
            body.put("priority", "high");
            HashMap<String, Object> hm = new HashMap<>();
            hm.put("title", title);
            hm.put("body", message);
            body.put("notification", hm);
            hm = new HashMap<>();
            hm.put("Key-1", "JSA Data 1");
            hm.put("Key-2", "JSA Data 2");
            body.put("data", hm);
            return gson.toJson(body);
        } else {
            Toast.makeText(MainActivity.this, "No valid Firebase Token Found", Toast.LENGTH_LONG).show();
            return null;
        }

    }

}
