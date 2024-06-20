package my.edu.utar.fyp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.nfc.Tag;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class Signup extends AppCompatActivity {

    private static final String TAG ="TAG";
    EditText memail,mfullname,mpassword,cpassword,mphone,mstuID;
    Button msignupbtn;
    FirebaseAuth fAuth;
    FirebaseFirestore fstore;
    String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);


        msignupbtn = findViewById(R.id.signupbtn);
        memail = findViewById(R.id.email);
        mfullname = findViewById(R.id.username);
        mpassword = findViewById(R.id.password);
        cpassword = findViewById(R.id.password2);
        mphone = findViewById(R.id.phone);
        mstuID = findViewById(R.id.studentID);


        fAuth = FirebaseAuth.getInstance();
        fstore = FirebaseFirestore.getInstance();

        /*(fAuth.getCurrentUser() != null){
            startActivity(new Intent(getApplicationContext(),MainActivity.class));
            finish();
        }*/

        TextView textView = findViewById(R.id.tvsignup);
        String text = "Already a user? SIGN IN";
        SpannableString ss = new SpannableString(text);

        ClickableSpan clickablespan = new ClickableSpan() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Signup.this,Login.class);
                startActivity(intent);
            }
        };
        ss.setSpan(clickablespan,16,23, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        textView.setText(ss);
        textView.setMovementMethod(LinkMovementMethod.getInstance());

        msignupbtn.setOnClickListener(view -> {
            String email = memail.getText().toString().trim();
            String password = mpassword.getText().toString().trim();
            String password2 = cpassword.getText().toString().trim();
            String fullName = mfullname.getText().toString();
            String phone = mphone.getText().toString();
            String stuID = mstuID.getText().toString();

            if(TextUtils.isEmpty(email)){
                memail.setError("Email is required.");
            }

            if(TextUtils.isEmpty(stuID)){
                mstuID.setError("Student ID is required.");
            }

            if(TextUtils.isEmpty(password)) {
                mpassword.setError("Password is required");
            }

            if(password.length()<6){
                mpassword.setError("Password Must be >= 6 Characters");
                return;
            }

            else if(!password.equals(password2)){
                Toast.makeText(Signup.this,"Passwords do not match",
                        Toast.LENGTH_SHORT).show();
                return;
            }

                //register user in firebase
                fAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(task -> {
                    if(task.isSuccessful()){

                        //send verification link
                        FirebaseUser fuser = fAuth.getCurrentUser();
                        fuser.sendEmailVerification().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Toast.makeText(Signup.this,"Verification Email Has Been Sent", Toast.LENGTH_SHORT).show();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {

                                Log.d(TAG,"onFailure Email not sent" + e.getMessage());
                            }
                        });

                        Toast.makeText(Signup.this,"User created.",Toast.LENGTH_SHORT).show();
                        userID = fAuth.getCurrentUser().getUid();
                        DocumentReference documentReference = fstore.collection("users").document(userID);
                        Map<String, Object> user = new HashMap<>();
                        user.put("fName",fullName);
                        user.put("email",email);
                        user.put("phone",phone);
                        user.put("studentID",stuID);
                        documentReference.set(user).addOnSuccessListener(unused -> Log.d(TAG,"onSuccess: User profile is created for "+userID))
                                .addOnFailureListener(e -> Log.d(TAG,"onFailure: " + e.toString()));


                        startActivity(new Intent(getApplicationContext(),MainActivity1.class));
                    }else{
                        Toast.makeText(Signup.this,"Error !!!" + task.getException().getMessage(),
                                Toast.LENGTH_SHORT).show();

                    }

                });
        });
    }
}
