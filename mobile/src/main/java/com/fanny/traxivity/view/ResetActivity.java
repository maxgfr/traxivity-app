package com.fanny.traxivity.view;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.fanny.traxivity.R;

/**
 * Created by huextrat <www.hugoextrat.com>.
 */

public class ResetActivity extends AppCompatActivity {
    private static final int REQUEST_RESET = 0;
    private FirebaseAuth auth;
    EditText _emailText;
    TextView _signupLink;
    TextView _loginLink;
    Button _resetBtn;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset);
        auth = FirebaseAuth.getInstance();

        _emailText = (EditText) findViewById(R.id.input_email);
        _signupLink = (TextView) findViewById(R.id.link_signup);
        _loginLink = (TextView) findViewById(R.id.link_login);
        _resetBtn = (Button) findViewById(R.id.btn_reset);

        _resetBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                resetPassword();
            }
        });

        _loginLink.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivityForResult(intent, REQUEST_RESET);
                finish();
                overridePendingTransition(R.animator.push_left_in, R.animator.push_left_out);
            }
        });

        _signupLink.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), SignupActivity.class);
                startActivityForResult(intent, REQUEST_RESET);
                finish();
                overridePendingTransition(R.animator.push_left_in, R.animator.push_left_out);
            }
        });
    }

    public void resetPassword() {
        final ProgressDialog progressDialog = new ProgressDialog(ResetActivity.this, R.style.AppTheme);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Sending email password reset...");
        progressDialog.show();

        String email = _emailText.getText().toString();

        auth.sendPasswordResetEmail(email)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Intent intent = new Intent(ResetActivity.this, LoginActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                        }
                        else {
                            _emailText.setError("invalid email");
                        }
                    }
                });
        progressDialog.dismiss();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_RESET) {
            if (resultCode == RESULT_OK) {
                this.finish();
            }
        }
    }
}
