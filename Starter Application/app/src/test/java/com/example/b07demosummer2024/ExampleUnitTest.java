package com.example.b07demosummer2024;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.verify;

import com.example.b07demosummer2024.Login.LoginActivityModel;
import com.example.b07demosummer2024.Login.LoginActivityPresenter;
import com.example.b07demosummer2024.Login.LoginActivityView;

@RunWith(MockitoJUnitRunner.class)
public class ExampleUnitTest {

    @Mock
    LoginActivityView view;

    @Mock
    LoginActivityModel model;

    //Test Cases

    @Test
    public void testLoginEmptyEmail() {
        LoginActivityPresenter presenter = new LoginActivityPresenter(view, model);
        presenter.loginBtnClicked("", "password");

        verify(view).toastMsg("Email is empty, please enter email");
    }

    @Test
    public void testLoginEmptyPassword() {
        LoginActivityPresenter presenter = new LoginActivityPresenter(view, model);
        presenter.loginBtnClicked("email", "");

        verify(view).toastMsg("Password is empty, please enter password");
    }

    @Test
    public void testLoginSuccess() {
        // Stimulate firebase API call
        doAnswer(invocation -> {
            LoginActivityModel.LoginCallback callback = invocation.getArgument(2);
            callback.LoginSuccess();
            return null;
        }).when(model).login(eq("test@test.com"), eq("testPassword"), any());

        LoginActivityPresenter presenter = new LoginActivityPresenter(view, model);
        presenter.loginBtnClicked("test@test.com", "testPassword");

        verify(view).toastMsg("Login Successful!");
        verify(view).goHome();
    }

    @Test
    public void testLoginFailureInvalidCredentials() {
        // Stimulate firebase API call
        doAnswer(invocation -> {
            LoginActivityModel.LoginCallback callback = invocation.getArgument(2);
            callback.LoginFailure("Invalid credentials. Check your email and/or password");
            return null;
        }).when(model).login(eq("test@test.com"), eq("invalidPassword"), any());

        LoginActivityPresenter presenter = new LoginActivityPresenter(view, model);
        presenter.loginBtnClicked("test@test.com", "invalidPassword");

        verify(view).toastMsg("Invalid credentials. Check your email and/or password");
    }

    @Test
    public void testForgotPasswordEmptyEmail() {
        LoginActivityPresenter presenter = new LoginActivityPresenter(view, model);
        presenter.forgotPasswordBtnClicked("");

        verify(view).toastMsg("Email is empty, please enter email");
    }

    @Test
    public void testForgotPasswordInvalidEmail() {
        LoginActivityPresenter presenter = new LoginActivityPresenter(view, model);
        presenter.forgotPasswordBtnClicked("Invalid email");

        verify(view).toastMsg("Invalid email, please enter valid email");
    }

    @Test
    public void testForgotPasswordSuccess() {
        // Stimulate Firebase API call
        doAnswer(invocation -> {
           LoginActivityModel.ResetCallBack callBack = invocation.getArgument(1);
           callBack.SentSuccess(true);
           return null;
        }).when(model).forgotPassword(eq("test@test.com"), any());

        LoginActivityPresenter presenter = new LoginActivityPresenter(view, model);
        presenter.forgotPasswordBtnClicked("test@test.com");

        verify(view).toastMsg("Password reset email has been sent");
    }

    @Test
    public void testForgotPasswordFailure() {
        // Stimulate Firebase API call
        doAnswer(invocation -> {
            LoginActivityModel.ResetCallBack callBack = invocation.getArgument(1);
            callBack.SentSuccess(false);
            return null;
        }).when(model).forgotPassword(eq("Invalid email"), any());

        LoginActivityPresenter presenter = new LoginActivityPresenter(view, model);
        presenter.forgotPasswordBtnClicked("Invalid email");

        verify(view).toastMsg("Password reset email was unsuccessful, please try again later");
    }
}