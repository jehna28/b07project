package com.example.b07demosummer2024;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

public class LoginActivityModel {
    private final FirebaseAuth mAuth;

    public LoginActivityModel() {
        mAuth = FirebaseAuth.getInstance();
    }

    /* Since firebase's way to authenticate and send password reset emails are async, they're not done immediately,
    thus making it not possible to return values to see if user was logged in the traditional way. Instead we have to use listeners
    and callbacks using interfaces. Using the below two interfaces we can communicate with the presenter and essentially tell it whether or not
    a user has been logged in or if a password reset email has been sent.
    */

    public interface LoginCallback {
        void LoginSuccess(String user);
        void LoginFailure(String errorMessage);
    }

    public interface ResetCallBack {
        void SentSuccess(Boolean s);
    }

    public void login(String email, String password, LoginCallback callback) {

        // Firebase code to sign in (slightly tweaked)
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            if (user != null) {
                                /* Use callback to communicate back to presenter and pass id of user to the presenter,
                                which will eventually get passed to the view and into the next view (homes screen)
                                */

                                callback.LoginSuccess(user.getUid());
                            }
                        } else {

                            /* User login failed, we will sort out the reason why by using FireBase Exceptions
                            We use the callback functionality to relay the error messages back to the presenter, which in
                            turn get passed to the view and the user gets notified
                            */

                            String eMsg;

                            Exception e = task.getException();

                            if (e instanceof FirebaseAuthInvalidUserException) {
                                eMsg = "User not found or has been disabled";
                            } else if (e instanceof FirebaseAuthInvalidCredentialsException) {
                                eMsg = "Invalid credentials. Check your email and/or password";
                            } else {
                                eMsg = "Login Failed, please try again later";
                            }
                            callback.LoginFailure(eMsg);
                        }
                    }
                });

    }

    public void forgotPassword(String email, ResetCallBack callback) {

        // Firebase code to send password reset email (again tweaked slightly)
        mAuth.sendPasswordResetEmail(email)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        /*
                        We again use callback functionality to relay back to the presenter whether or not
                        an email has been sent, which in turn will get passed on to the view, and the user gets notified
                         */

                        callback.SentSuccess(task.isSuccessful());
                    }
                });
    }
}
