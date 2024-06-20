package my.edu.utar.fyp;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;


public class Login extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        EditText mEmail,mPassword;
        Button mLogin;
        FirebaseAuth fAuth;
        TextView forgot;

        mEmail = findViewById(R.id.email1);
        mPassword = findViewById(R.id.passwordl);
        fAuth = FirebaseAuth.getInstance();
        mLogin = findViewById(R.id.loginbtn);
        forgot = findViewById(R.id.forgotpass);



        TextView textView = findViewById(R.id.tvsignup);
        String text = "Need an account? SIGN UP";
        SpannableString ss = new SpannableString(text);

        ClickableSpan clickablespan = new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                Intent intent = new Intent(Login.this,Signup.class);
                startActivity(intent);
            }
        };

        ss.setSpan(clickablespan,17,24, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        textView.setText(ss);
        textView.setMovementMethod(LinkMovementMethod.getInstance());

        mLogin.setOnClickListener(view -> {
            String email = mEmail.getText().toString().trim();
            String password = mPassword.getText().toString().trim();

            if(TextUtils.isEmpty(email)){
                mEmail.setError("Email is required.");
                return;
            }

            if(TextUtils.isEmpty(password)) {
                mPassword.setError("Password is required");
                return;
            }

            if(TextUtils.isEmpty(password)&& TextUtils.isEmpty(email)){
                Toast.makeText(Login.this, "Email and Password is required.", Toast.LENGTH_SHORT).show();
                return;
            }

            //authenticate user
            fAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(task -> {
                if(task.isSuccessful()){
                    Toast.makeText(Login.this,"Log in Successfully.",Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(getApplicationContext(),MainActivity1.class));
                }else{
                    Toast.makeText(Login.this,"Error !!! Wrong password or email address." ,
                            Toast.LENGTH_SHORT).show();
                }
            });

        });

        forgot.setOnClickListener(view -> {
            EditText resetMail = new EditText(view.getContext());
            AlertDialog.Builder passwordResetDialog = new AlertDialog.Builder(view.getContext());
            passwordResetDialog.setTitle("Reset Password ?");
            passwordResetDialog.setMessage("Enter Your Email To Reset Password.");
            passwordResetDialog.setView(resetMail);

            passwordResetDialog.setPositiveButton("Yes", (dialogInterface, i) -> {
                //extract the email and send reset link
                String mail = resetMail.getText().toString();
                fAuth.sendPasswordResetEmail(mail).addOnSuccessListener(unused -> Toast.makeText(Login.this, "Reset Link Sent To Your Email", Toast.LENGTH_SHORT).show()).addOnFailureListener(e -> {
                    Toast.makeText(Login.this,"Error! Reset Link is Not Sent",Toast.LENGTH_SHORT).show();;
                });
            });

            passwordResetDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    //close the dialog
                }
            });

            passwordResetDialog.create().show();
        });




    }
}