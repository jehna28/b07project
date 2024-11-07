package com.example.b07demosummer2024;

import com.google.firebase.Firebase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

public class LoginActivityPresenter {

    private LoginActivityView view;
    private LoginActivityModel model;

    public LoginActivityPresenter(LoginActivityView view, LoginActivityModel model) {
        this.view = view;
        this.model = model;
    }
    public void loginBtnClicked(String email, String password) {

        //Check to see if email and password are non-empty

        if (email.isEmpty()) {
            view.toastMsg("Email is empty, please enter email");
            return;
        }

        if (password.isEmpty()) {
            view.toastMsg("Password is empty, please enter password");
            return;
        }

        // Use model to authenticate login
        model.login(email, password, new LoginActivityModel.LoginCallback() {

            /*
            We implement the callback method for login in the model in here, we then pass on this implementation to
            then model, it uses this model to relay information back to the view and in turn the user.
             */

            @Override
            public void LoginSuccess(String user) {
                view.toastMsg("Login Successful!");

                // Go to home screen after successful login and pass along User
                view.goHome(user);

            }

            @Override
            public void LoginFailure(String errorMessage) {
                view.toastMsg(errorMessage);
                return;
            }
        });

    }

    public void forgotPasswordBtnClicked(String email) {

        // Check if email is empty
        if (email.isEmpty()) {
            view.toastMsg("Email is empty, please enter email");
            return;
        }

        // Check if email is valid, jsut a simple check for now that checks if email has the symbol '@'
        if (!(email.contains("@"))) {
            view.toastMsg("Invalid email, please enter valid email");
        }

        // use model to send email to change password
        model.forgotPassword(email, new LoginActivityModel.ResetCallBack() {

            /*
            We again implement and send a callback method for the same reasonings as the
            login callback method for login
             */

            @Override
            public void SentSuccess(Boolean s) {
                if (s) {
                    view.toastMsg("Password reset email has been sent");
                } else {
                    view.toastMsg("Password reset email was unsuccessful, please try again later");
                }
                return;
            }
        });
    }
}
