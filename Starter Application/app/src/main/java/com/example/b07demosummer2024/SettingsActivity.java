package com.example.b07demosummer2024;

import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

public class SettingsActivity extends AppCompatActivity {

    private Button resendVerificationBtn, changePasswordBtn, forgotPasswordBtn, resetPrimaryDataBtn, resetAllDataBtn;
    private TextInputEditText oldPasswordTxtField, newPasswordTxtField;

    private FirebaseAuth mAuth;

    private FirebaseDatabase mDataBase;

    private TextView emailVerifyTxtView;

    private ImageView emailVerifyImgView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_settings);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

       // Initialize variables
        resendVerificationBtn = findViewById(R.id.resendVerificationEmailBtnSettings);
        changePasswordBtn = findViewById(R.id.changePasswordBtnSettings);
        forgotPasswordBtn = findViewById(R.id.resetPasswordBtnSettings);
        resetPrimaryDataBtn = findViewById(R.id.resetInitialDataBtnSettings);
        resetAllDataBtn = findViewById(R.id.resetAllDataBtnSettings);

        oldPasswordTxtField = findViewById(R.id.passwordTxtFieldSettings);
        newPasswordTxtField = findViewById(R.id.passwordNewTxtFieldSettings);

        emailVerifyTxtView = findViewById((R.id.emailVerifyTxtSettings));
        emailVerifyImgView = findViewById(R.id.verifyImgSettings);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();

        // Check if email is verified to change text and image in text view
        if (user != null) {
            if (user.isEmailVerified()) {
                emailVerifyTxtView.setText(getString(R.string.your_email_is_verified));
                emailVerifyImgView.setImageResource(R.drawable.verify_icon);
                resendVerificationBtn.setEnabled(false);

            } else{
                emailVerifyTxtView.setText(getString(R.string.your_email_is_not_verifiied));
                emailVerifyImgView.setImageResource(R.drawable.no_icon);
                resendVerificationBtn.setEnabled(true);
            }
        }

        // On Click listener for resend verification email
        resendVerificationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (user!=null) {
                    user.sendEmailVerification();

                    Toast.makeText(SettingsActivity.this, "Verification Email Sent", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Use OnBackPressedDispatcher for handling back button presses
        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                Intent intent = new Intent(getApplicationContext(), HomeScreenActivity.class);
                startActivity(intent);
                finish();
            }
        };
        getOnBackPressedDispatcher().addCallback(this, callback);

        // On click listener for forgot password
        forgotPasswordBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Firebase code to send password reset email (again tweaked slightly)
                if (user!=null) {
                    mAuth.sendPasswordResetEmail(user.getEmail()).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(SettingsActivity.this, "Password reset email has been sent", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(SettingsActivity.this, "Password reset email was unsuccessful, please try again later", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });

        // On click lister to change password
        changePasswordBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String current_password = String.valueOf(oldPasswordTxtField.getText());
                String new_password = String.valueOf((newPasswordTxtField.getText()));

                if (TextUtils.isEmpty(current_password)) {
                    Toast.makeText(SettingsActivity.this, "Please enter current password", Toast.LENGTH_SHORT).show();
                    return;
                } if (TextUtils.isEmpty(new_password)) {
                    Toast.makeText(SettingsActivity.this, "Please enter new password", Toast.LENGTH_SHORT).show();
                    return;
                } if (current_password.matches(new_password)) {
                    Toast.makeText(SettingsActivity.this, "Passwords match, please enter a different password", Toast.LENGTH_SHORT).show();
                    return;
                }else {
                    if (user != null) {
                        AuthCredential credential = EmailAuthProvider.getCredential(user.getEmail(), current_password);

                        user.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    user.updatePassword(new_password).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                Toast.makeText(SettingsActivity.this, "Password succesfully changed", Toast.LENGTH_SHORT).show();
                                                return;
                                            } else {
                                                // Display password security breach message to user
                                                try {
                                                    throw task.getException();
                                                } catch (FirebaseException e) {
                                                    CustomToast.makeText(SettingsActivity.this, "Your password is too weak. Please use at least one upper-case & lower-case character, along with at least one numeric character. Make sure password is at least 6 characters long",
                                                            Toast.LENGTH_LONG).show();
                                                } catch (Exception e) {
                                                    Log.e("Unknown Register Error", "Error occurred: ", e);
                                                    Toast.makeText(SettingsActivity.this, e.toString(),
                                                            Toast.LENGTH_LONG).show();
                                                }
                                            }
                                        }
                                    });

                                } else {
                                    Toast.makeText(SettingsActivity.this, "Incorrect password, please try again", Toast.LENGTH_SHORT).show();
                                    return;
                                }
                            }
                        });
                    }
                }
            }
        });

    }
}