package com.project.brainbot;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LoginActivity extends AppCompatActivity {

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        if (SharedPrefManager.getInstance(this).isRegistered()) {
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
            finish();
        }

        final EditText mail = (EditText) findViewById(R.id.signin_mail);
        final EditText password = (EditText) findViewById(R.id.signin_password);
        Button signIn = (Button) findViewById(R.id.button_signin);
        Button register = (Button) findViewById(R.id.button_register);
        progressDialog = new ProgressDialog(this);

        signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (isValid(mail.getText().toString(), password.getText().toString())) {

                    progressDialog.setMessage("Verifying E-mail and password.");

                    progressDialog.show();

                    verify( mail.getText().toString(), password.getText().toString());
                }
            }
        });

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(LoginActivity.this, RegistrationActivity.class);
                startActivity(i);
            }
        });

    }

    private void verify(final String mail, final String password) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constants.MY_HOST + Constants.URL_VERIFICATION,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject obj = new JSONObject(response);
                            Toast.makeText(LoginActivity.this, obj.getString("message"), Toast.LENGTH_LONG).show();
                            if (!obj.getBoolean("error")) {
                                String name = obj.getString("name");
                                login(name, mail, password);
                            }else {
                                progressDialog.dismiss();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            progressDialog.dismiss();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(LoginActivity.this, error.getMessage(), Toast.LENGTH_LONG).show();
                        progressDialog.dismiss();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("email", mail);
                params.put("password", password);
                return params;
            }
        };
        MyVolley.getInstance(getApplicationContext()).addToRequestQueue(stringRequest);
    }

    private boolean isValid(String email, String password) {
        Pattern VALID_EMAIL_ADDRESS_REGEX =
                Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);

        Matcher matcher = VALID_EMAIL_ADDRESS_REGEX.matcher(email);
        if (!matcher.find()) {
            Toast.makeText(this, "Invalid mail address.", Toast.LENGTH_LONG).show();
            return false;
        }

        int length;
        length = password.length();
        if (length < 6 || length > 11) {
            Toast.makeText(this, "Password must be 6 - 10 characters long!", Toast.LENGTH_LONG).show();
            return false;
        }

        return true;
    }

    public void login(final String name, final String mail, final String password){

        final String token = SharedPrefManager.getInstance(this).getDeviceToken();
        if (token == null) {
            Toast.makeText(LoginActivity.this, "Token not generated!", Toast.LENGTH_LONG).show();
            progressDialog.dismiss();
            return;
        }

        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constants.MY_HOST + Constants.URL_REGISTRATION,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject obj = new JSONObject(response);
                            if (!obj.getBoolean("error")) {
                                SharedPrefManager.getInstance(LoginActivity.this).setRegistered(true);
                                SharedPrefManager.getInstance(LoginActivity.this)
                                        .saveLoginDetails(name, mail, password);
                                Intent i = new Intent(LoginActivity.this, MainActivity.class);
                                startActivity(i);
                                finish();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        progressDialog.dismiss();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(LoginActivity.this, error.getMessage(), Toast.LENGTH_LONG).show();
                        progressDialog.dismiss();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("name", name);
                params.put("email", mail);
                params.put("password", password);
                params.put("token", token);
                return params;
            }
        };
        MyVolley.getInstance(getApplicationContext()).addToRequestQueue(stringRequest);
    }
}
