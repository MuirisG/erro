package com.example.erro;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.example.erro.API.ApiClient;
import com.example.erro.API.GetMessageForm;
import com.example.erro.API.GetMessageResponse;
import com.example.erro.API.HttpHandler;
import com.example.erro.Utils.GlideToast;
import com.example.erro.Utils.WeiboDialogUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TestApiActivity extends AppCompatActivity implements View.OnClickListener {

    private GetMessageForm getMessageForm;
    protected Dialog loadingDialog;
    String login_success = "0";
    String login_success1 = "0";
    String username_myserver = "emailjereme@gmail.com";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_api);
        Button sendApiBtn1, sendApiBtn2, sendApiBtn3, sendApiBtn4, sendApiBtn5;
        sendApiBtn1 = findViewById(R.id.sendBtn1);
        sendApiBtn2 = findViewById(R.id.sendBtn2);
        sendApiBtn3 = findViewById(R.id.sendBtn3);
        sendApiBtn4 = findViewById(R.id.sendBtn4);
        sendApiBtn5 = findViewById(R.id.sendBtn5);

        sendApiBtn1.setOnClickListener(this);
        sendApiBtn2.setOnClickListener(this);
        sendApiBtn3.setOnClickListener(this);
        sendApiBtn4.setOnClickListener(this);
        sendApiBtn5.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        if(view.getId()==R.id.sendBtn1) {
            loadingDialog = WeiboDialogUtils.createLoadingDialog(this, "Login To Zone App");
            loadingDialog.show();
            RequestLogin("Muiris", "12345");
//            new GetLogin2().execute();
        } else if(view.getId() == R.id.sendBtn2) {
            loadingDialog = WeiboDialogUtils.createLoadingDialog(this, "Login To Zone App");
            loadingDialog.show();
            RequestLogin("aaaaaaaa", "aaaaaaa");
        } else if(view.getId() == R.id.sendBtn3) {
            loadingDialog = WeiboDialogUtils.createLoadingDialog(this, "Login To My Server");
            loadingDialog.show();
            login_success = "0";
            username_myserver = "emailjereme@gmail.com";
            new GetLogin().execute();
        } else if(view.getId() == R.id.sendBtn4) {
            login_success1 = "0";
            loadingDialog = WeiboDialogUtils.createLoadingDialog(this, "Login To Default Server");
            loadingDialog.show();
            new GetContacts().execute();
        } else if(view.getId() == R.id.sendBtn5) {
            loadingDialog = WeiboDialogUtils.createLoadingDialog(this, "Login To My Server");
            loadingDialog.show();
            login_success = "0";
            username_myserver = "aagasdasdr";
            new GetLogin().execute();
        }
    }

    public void RequestLogin(final String username, String password) {
        String code,  gmail;
        code = "login";
        gmail = "test@gmail";
        getMessageForm = new GetMessageForm(code, username, password, gmail);
        Call<GetMessageResponse> mService =
                ApiClient.getInstance()
                        .getApi()
                        .getMessage(
                                getMessageForm.getCode(),
                                getMessageForm.getUsername(),
                                getMessageForm.getPassword(),
                                getMessageForm.getgMail());
        mService.enqueue(
                new Callback<GetMessageResponse>() {
                    @Override
                    public void onResponse(
                            Call<GetMessageResponse> call,
                            Response<GetMessageResponse> response) {
                        try {
                            if (response.isSuccessful()) {
                                WeiboDialogUtils.closeDialog(loadingDialog);
                                new GlideToast.makeToast(TestApiActivity.this,"Login Success");
                                final Handler handler = new Handler();
                                handler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        Intent intent = new Intent(TestApiActivity.this, selectSurface.class);
                                        startActivity(intent);
                                    }
                                }, 1500);


                            } else {
                                WeiboDialogUtils.closeDialog(loadingDialog);
                                String s = response.errorBody().string();
                                try {
                                    JSONObject jsonObject = new JSONObject(s);
                                    new GlideToast.makeToast(TestApiActivity.this,"Login Fault Error 401");
                                } catch (JSONException e) {
                                    new GlideToast.makeToast(TestApiActivity.this,"Login Fault Error 404");
                                }
                            }
                        } catch (IOException e) {
                            WeiboDialogUtils.closeDialog(loadingDialog);
                            new GlideToast.makeToast(TestApiActivity.this,"Connect Server Error");
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(retrofit2.Call<GetMessageResponse> call, Throwable t) {
                        call.cancel();
                        WeiboDialogUtils.closeDialog(loadingDialog);
                        new GlideToast.makeToast(TestApiActivity.this,"Network Error");
                    }
                });
    }

    private class GetLogin extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            HttpHandler sh = new HttpHandler();
            String sub_url = "users/loginUser.php?";

            String password = "6Godmode!!";
            String parameters = "email=" +  username_myserver;
            String base_url = "https://urban.network/Api/";
            String url = base_url + sub_url + parameters;
            String jsonStr = sh.makeServiceCall(url);
            if (jsonStr != null) {
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);
                    String name = jsonObj.getString("name");
                    String id = jsonObj.getString("id");
                    String pw = jsonObj.getString("password");
                    if (password.equals(pw)){
                        login_success = "1";
                    }
                    else {
                        login_success = "0";
                    }
                } catch (final JSONException e) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            new GlideToast.makeToast(TestApiActivity.this,"Json parsing error 222: " + e.getMessage());
                        }
                    });
                }
            } else {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        new GlideToast.makeToast(TestApiActivity.this,"Json response error");
                    }
                });
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            WeiboDialogUtils.closeDialog(loadingDialog);
            if(login_success.equals("1")){
                new GlideToast.makeToast(TestApiActivity.this,"Login Success");
                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Intent intent = new Intent(TestApiActivity.this, selectSurface.class);
                        startActivity(intent);
                    }
                }, 1500);

            } else {
                Toast.makeText(getApplicationContext(),"Username or password is incorrect", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private class GetContacts extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            HttpHandler sh = new HttpHandler();
            String url = "https://api.androidhive.info/contacts/";
            String jsonStr = sh.makeServiceCall(url);
            if (jsonStr != null) {
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);
                    JSONArray contacts = jsonObj.getJSONArray("contacts");
                    login_success1 = "1";
                    for (int i = 0; i < contacts.length(); i++) {
                        JSONObject c = contacts.getJSONObject(i);
                        String id = c.getString("id");
                        String name = c.getString("name");
                        String email = c.getString("email");
                        String address = c.getString("address");
                        String gender = c.getString("gender");

                        JSONObject phone = c.getJSONObject("phone");
                        String mobile = phone.getString("mobile");
                        String home = phone.getString("home");
                        String office = phone.getString("office");
                        HashMap<String, String> contact = new HashMap<>();
                        contact.put("id", id);
                        contact.put("name", name);
                        contact.put("email", email);
                        contact.put("mobile", mobile);
                    }
                } catch (final JSONException e) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(),"Json parsing error222: " + e.getMessage(),
                                    Toast.LENGTH_LONG).show();
                        }
                    });

                }

            } else {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(),
                                "Couldn't get json from server. Check LogCat for possible errors!",
                                Toast.LENGTH_LONG).show();
                    }
                });
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            WeiboDialogUtils.closeDialog(loadingDialog);
            if(login_success1.equals("1")) {
                new GlideToast.makeToast(TestApiActivity.this,"login success");
                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Intent intent = new Intent(TestApiActivity.this, selectSurface.class);
                        startActivity(intent);
                    }
                }, 1500);
            } else {
                new GlideToast.makeToast(TestApiActivity.this,"Login Fault");
            }
        }
    }

    private class GetLogin2 extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            HttpHandler sh = new HttpHandler();
            String sub_url = "users/loginUser.php?";

            String password = "6Godmode!!";
            String parameters = "email=" +  username_myserver;
            String base_url = "https://mucouncil.net/api/apitest";
            String url = base_url + sub_url + parameters;
            String jsonStr = sh.makeServiceCall(url);
            if (jsonStr != null) {
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);
                    String name = jsonObj.getString("name");
                    String id = jsonObj.getString("id");
                    String pw = jsonObj.getString("password");
                    if (password.equals(pw)){
                        login_success = "1";
                    }
                    else {
                        login_success = "0";
                    }
                } catch (final JSONException e) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            new GlideToast.makeToast(TestApiActivity.this,"Json parsing error 222: " + e.getMessage());
                        }
                    });
                }
            } else {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        new GlideToast.makeToast(TestApiActivity.this,"Json response error");
                    }
                });
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            WeiboDialogUtils.closeDialog(loadingDialog);
            if(login_success.equals("1")){
                new GlideToast.makeToast(TestApiActivity.this,"Login Success");
                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Intent intent = new Intent(TestApiActivity.this, selectSurface.class);
                        startActivity(intent);
                    }
                }, 1500);

            } else {
                Toast.makeText(getApplicationContext(),"Username or password is incorrect", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
