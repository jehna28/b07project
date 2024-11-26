package com.example.b07demosummer2024;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
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
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterActivity extends AppCompatActivity {

    // Setup variables

    // Text Fields
    private TextInputEditText editTxtFirstName, editTxtLastName, editTxtEmail, editTxtPassword, editTxtConfirmPassword;

    // Buttons
    private Button continueBtn;

    //Firebase Auth
    private FirebaseAuth mAuth;

    private FirebaseDatabase mDataBase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize text fields
        editTxtFirstName = findViewById(R.id.firstNametxtField);
        editTxtLastName =  findViewById(R.id.lastNameTxtField);
        editTxtEmail = findViewById(R.id.emailTxtField);
        editTxtPassword = findViewById(R.id.passwordTxtField);
        editTxtConfirmPassword = findViewById(R.id.passwordConfirmTxtField);


        // Continue Button to save information and proceed with account building
        continueBtn = findViewById(R.id.continueBtn);

        // Use OnBackPressedDispatcher for handling back button presses
        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                Intent intent = new Intent(getApplicationContext(), WelcomeActivity.class);
                startActivity(intent);
                finish();
            }
        };
        getOnBackPressedDispatcher().addCallback(this, callback);

        // Method to attempt to register new User
        continueBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Gets strings to capture text in text fields
                String firstName, lastName, email, password, confirmPassword;

                // Get a working instance of FireBase Authentication
                mAuth = FirebaseAuth.getInstance();

                // Initialize Strings
                firstName = String.valueOf(editTxtFirstName.getText());
                lastName = String.valueOf((editTxtLastName.getText()));
                email = String.valueOf((editTxtEmail.getText()));
                password = String.valueOf((editTxtPassword.getText()));
                confirmPassword = String.valueOf((editTxtConfirmPassword.getText()));

                // Check if text fields are non-empty
                if (TextUtils.isEmpty(firstName)) {
                    Toast.makeText(RegisterActivity.this, "Enter First Name", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(lastName)) {
                    Toast.makeText(RegisterActivity.this, "Enter Last Name", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(RegisterActivity.this, "Enter Email", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(password)) {
                    Toast.makeText(RegisterActivity.this, "Enter Password", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(confirmPassword)) {
                    Toast.makeText(RegisterActivity.this, "Enter Confirm Password", Toast.LENGTH_SHORT).show();
                    return;
                }
                // Check if password is equal to confirmed passowrd
                if (!(password.equals(confirmPassword))) {
                    Toast.makeText(RegisterActivity.this, "Password and Confirmed Password do not match. Try again", Toast.LENGTH_SHORT).show();
                    editTxtConfirmPassword.setText("");
                    return;
                }

                //Update firstName and lastName so all characters are lowercase except the initial character, have to make them final so we can add it to the db later
                final String formattedFirstName = firstName.substring(0, 1).toUpperCase() + firstName.substring(1).toLowerCase();
                final String formattedLastName = lastName.substring(0, 1).toUpperCase() + lastName.substring(1).toLowerCase();


                // Use firebase code to attempt to create new user
                mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                // If user gets registered with valid email and password, we will add first name and last name to db
                                if (task.isSuccessful()) {

                                    // Gets newly registered user as current user
                                    FirebaseUser user = mAuth.getCurrentUser();

                                    // Make sure user is not null and account was successfully registered
                                    if (user != null) {

                                        // Open instance of real-time db
                                        mDataBase = FirebaseDatabase.getInstance();

                                        // Open branch users (Creates branch if it doesn't exist/for the first user)
                                        // Note getUid() returns a unique address that's special to every user, we're using it as the new node for this
                                        // newly created user
                                        DatabaseReference users = mDataBase.getReference("Users").child(user.getUid());

                                        // Add last name and first name to current user node in list of users in db
                                        users.child("name").child("firstName").setValue(formattedFirstName);
                                        users.child("name").child("lastName").setValue(formattedLastName);


                                        // Send a verification link to verify email for new user
                                        user.sendEmailVerification();

                                        Toast.makeText(RegisterActivity.this, "Account Successfully Created", Toast.LENGTH_SHORT).show();

                                        Intent intent = new Intent(getApplicationContext(), WelcomeActivity.class);
                                        startActivity(intent);
                                        finish();

                                        //later we have to switch this to login page, and then login
                                    }

                                } else {

                                    // Display password security breach message to user
                                    try {
                                        throw task.getException();
                                    } catch (FirebaseAuthWeakPasswordException e) {
                                        CustomToast.makeText(RegisterActivity.this, "Your password is too weak. Please use at least one upper-case & lower-case character, along with at least one numeric character. Make sure password is at least 6 characters long",
                                                Toast.LENGTH_LONG).show();
                                    } catch (FirebaseAuthInvalidCredentialsException e) {
                                        Toast.makeText(RegisterActivity.this, "Your email is invalid or already in use. Please enter a different email",
                                                Toast.LENGTH_LONG).show();
                                    } catch (FirebaseAuthUserCollisionException e) {
                                        Toast.makeText(RegisterActivity.this, "A user is already registered with this email. Please use an another email",
                                                Toast.LENGTH_LONG).show();
                                    } catch (FirebaseException e) {
                                        CustomToast.makeText(RegisterActivity.this, "Your password is too weak. Please use at least one upper-case & lower-case character, along with at least one numeric character. Make sure password is at least 6 characters long",
                                                Toast.LENGTH_LONG).show();
                                    } catch (Exception e) {
                                        Log.e("Unknown Register Error", "Error occurred: ", e);
                                        Toast.makeText(RegisterActivity.this, e.toString(),
                                                Toast.LENGTH_LONG).show();
                                    }
                                }
                            }
                        });

            }
        });
    }
}