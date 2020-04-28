package com.vaagdevi.newsnevents;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatCheckBox;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity<gso, mGoogleSignInClient> extends AppCompatActivity {


    private FirebaseAuth mAuth;

    int RC_SIGN_IN = 0;
    GoogleSignInClient mGoogleSignInClient;


    EditText Emailid;
    EditText Passid;

    Button login;
    Button facebook;
    Button google;

    ImageButton register;

    TextView forgotpassid;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Emailid = (EditText) findViewById(R.id.ETemailId);
        Passid = (EditText) findViewById(R.id.ETpassId);

        login = (Button) findViewById(R.id.BTNlogin);
        google = (Button) findViewById(R.id.BTNgoogle);
        register = (ImageButton) findViewById(R.id.IMGBTNregister);


        forgotpassid = (TextView) findViewById(R.id.TVForgot);


        mAuth = FirebaseAuth.getInstance();



        @SuppressLint("WrongViewCast") AppCompatCheckBox checkBox = (AppCompatCheckBox)findViewById(R.id.show_hide_password);
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked) {
                    // show password
                    Passid.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                } else {
                    // hide password
                    Passid.setTransformationMethod(PasswordTransformationMethod.getInstance());
                }

            }
        });



        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        google.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signIn();
            }
        });


        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String emailid = Emailid.getText().toString();
                String passid = Passid.getText().toString();


                if (emailid.isEmpty() && passid.isEmpty()) {
                    Toast.makeText(MainActivity.this, "Fields Empty!", Toast.LENGTH_SHORT).show();
                } else if (emailid.isEmpty()) {
                    Emailid.setError("Provide Your Email");
                    Emailid.requestFocus();
                } else if (passid.isEmpty()) {
                    Passid.setError("Enter Your Password");
                    Passid.requestFocus();
                } else if (!(emailid.isEmpty() && passid.isEmpty())) {

                    mAuth.signInWithEmailAndPassword(emailid, passid)
                            .addOnCompleteListener(MainActivity.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if ((task.isSuccessful())) {
                                        // Sign in success, update UI with the signed-in user's information


                                        Toast.makeText(MainActivity.this, "Logined Successfully", Toast.LENGTH_SHORT).show();
                                        startActivity(new Intent(MainActivity.this, Dashboard2.class));
                                        finish();


                                    } else {
                                        // If sign in fails, display a message to the user.

                                        Toast.makeText(MainActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();

                                    }


                                }
                            });

                } else {
                    Toast.makeText(MainActivity.this, "Error", Toast.LENGTH_SHORT).show();
                }


            }
        });

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(MainActivity.this, Registration.class));

            }
        });

        forgotpassid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(MainActivity.this, ForgotPassword.class));

            }
        });


    }


    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);



        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            // Signed in successfully, show authenticated UI.
            startActivity(new Intent(MainActivity.this, Dashboard2.class));
        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w("Google Sign In Error", "signInResult:failed code=" + e.getStatusCode());
            Toast.makeText(MainActivity.this, "Failed", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onStart() {

        FirebaseUser currentUser = mAuth.getCurrentUser();
        // Check for existing Google Sign In account, if the user is already signed in
        // the GoogleSignInAccount will be non-null.
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        if (account != null) {
            startActivity(new Intent(MainActivity.this, Dashboard2.class));
        }
        super.onStart();
    }



}
