package com.project.brainbot;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
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

public class RegistrationActivity extends AppCompatActivity {

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        if (SharedPrefManager.getInstance(this).isRegistered()) {
            startActivity(new Intent(RegistrationActivity.this, MainActivity.class));
            finish();
        }

        final EditText name = (EditText) findViewById(R.id.reg_fullName);
        final EditText mail = (EditText) findViewById(R.id.reg_email);
        final EditText password = (EditText) findViewById(R.id.reg_password);
        Button signIn = (Button) findViewById(R.id.btnRegister);
        progressDialog = new ProgressDialog(this);

        String[] details = SharedPrefManager.getInstance(getApplicationContext()).getLoginDetails();

        if (details != null) {
            name.setText(details[0]);
            mail.setText(details[1]);
            password.setText(details[2]);
        }


        signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (isValid(mail.getText().toString(), password.getText().toString())) {

                    progressDialog.setMessage("Registering Device...");

                    progressDialog.show();

                    login(name.getText().toString(), mail.getText().toString(), password.getText().toString());
                }
            }
        });
    }

    public String login(final String name, final String mail, final String password) {


        final String token = SharedPrefManager.getInstance(this).getDeviceToken();
        if (token == null) {
            return null;
        }

        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constants.MY_HOST + Constants.URL_REGISTRATION,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject obj = new JSONObject(response);
                            Toast.makeText(RegistrationActivity.this, obj.getString("message"), Toast.LENGTH_LONG).show();
                            if (!obj.getBoolean("error")) {
                                SharedPrefManager.getInstance(RegistrationActivity.this).setRegistered(true);
                                SharedPrefManager.getInstance(RegistrationActivity.this)
                                        .saveLoginDetails(name, mail, password);
                                Intent i = new Intent(RegistrationActivity.this, MainActivity.class);
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
                        Toast.makeText(RegistrationActivity.this, error.getMessage(), Toast.LENGTH_LONG).show();
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
        return "request sent";
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
}
