package com.example.b07demosummer2024;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.GravityInt;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.graphics.Insets;
import androidx.core.view.GravityCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class HomeScreenActivity extends AppCompatActivity {

    private TextView firstNameTxt, betterTxt, annualTxt;
    private PieChart pieChart;
    private BottomNavigationView bottomNavigationView;
    private DrawerLayout drawerLayout;
    private NavigationView drawerNavigationView;
    private FirebaseUser user;
    private FirebaseDatabase mDataBase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home_screen_activity);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.drawerLayout), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize Firebase and U.I
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        mDataBase = FirebaseDatabase.getInstance();

        bottomNavigationView = findViewById(R.id.bottomNavigationViewHomeScreen);
        drawerLayout = findViewById(R.id.drawerLayout);
        drawerNavigationView = findViewById(R.id.drawerNavigationView);
        firstNameTxt = findViewById(R.id.userFirstNameTxtHomeScreen);
        betterTxt = findViewById(R.id.betterTxtMessageHomeScreen);
        annualTxt = findViewById(R.id.annualEmissionsTxtMessageHomeScreen);

        pieChart = findViewById(R.id.pieChartHomeScreen);

        setNavBar();
        setDrawerNav();
        unCheckNavBar();
        welcomeMsg();
        setPieChart();
        setUserDisplayMessages();
    }

    private void setNavBar() {

        // On click listeners for icons in bottom navigation bar
        bottomNavigationView.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.navOther) {
                // if drawer is open, close it
                if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                    drawerLayout.closeDrawer(GravityCompat.START);
                } else {
                    //if it isn't opened open the drawer
                    drawerLayout.openDrawer(GravityCompat.START);;
                }
                return true;
            }
            if (item.getItemId() == R.id.navEcoTracker) {
                // add code to transition to tracker feature

                return true;
            }
            if (item.getItemId() == R.id.navEcoGauge) {
                // add code to transition to gauge feature

                return true;
            }
            return false;
        });

    }

    private void setDrawerNav() {

        drawerNavigationView.setNavigationItemSelectedListener(menuItem -> {
            if (menuItem.getItemId() == R.id.navProfile) {
                // Transition to settings to change user first name and last name
                Intent intent = new Intent(getApplicationContext(), ProfileActivity.class);
                startActivity(intent);
                finish();

                return true;
            }
            if (menuItem.getItemId() == R.id.navSettings) {
                // transition to settings to access general settings
                Intent intent = new Intent(getApplicationContext(), SettingsActivity.class);
                startActivity(intent);
                finish();
            }
            if (menuItem.getItemId() == R.id.navLogOut) {
                // sign out user and bring us back to welcome page
                DialogState.clearState(user.getUid());
                FirebaseAuth.getInstance().signOut();

                Intent intent = new Intent(getApplicationContext(), WelcomeActivity.class);
                startActivity(intent);
                finish();
                return true;
            }
            return false;
        });

    }

    private void unCheckNavBar() {
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationViewHomeScreen);
        DrawerLayout drawerLayout = findViewById(R.id.drawerLayout);
        // Uncheck icons when clicking anywhere on home-screen
        ConstraintLayout mainHomeLayout = findViewById(R.id.mainHome);
        if (!drawerLayout.isDrawerOpen(GravityCompat.START)) {
            mainHomeLayout.setOnClickListener(v -> {
                bottomNavigationView.getMenu().setGroupCheckable(0, true, false);

                for (int i = 0; i < bottomNavigationView.getMenu().size(); i++) {
                    bottomNavigationView.getMenu().getItem(i).setChecked(false);
                }

                bottomNavigationView.getMenu().setGroupCheckable(0, true, true);
            });
        }
    }

    private void welcomeMsg() {

        // Grab user's first name and set it for the welcome message

        if (user!=null) {
            // Open instance of real-time db

            // Grab name from db and set in the textView
            mDataBase.getReference("Users").child(user.getUid()).child("name").child("firstName").get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {

                    // change it on first name text vie
                    firstNameTxt.setText(task.getResult().getValue(String.class));
                }  else {
                    // Handle the error
                    Log.e("Firebase", "Error getting data", task.getException());
                }
            });
        } else {
            firstNameTxt.setText(getString(R.string.default_user));
        }
    }

    private void setPieChart() {

        if (user != null) {

            // Set pie chart to current / yesterdays daily emissions if there is
            DatabaseReference ref = mDataBase.getReference("Users").child(user.getUid()).child("emissionData");

            ref.get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    if (task.getResult().exists()) {
                        // Add code here to actually fill up pie chart
                    } else {
                        // Case that user has not inputted any daily data and/or data in general
                        setEmptyDataPieChart();
                    }
                } else {
                    //Log error
                    Log.e("FirebaseError", "Error fetching data", task.getException());
                }
            });
        } else {
            setEmptyDataPieChart();
        }

    }

    private void setEmptyDataPieChart() {

        // Placeholder data
        ArrayList<PieEntry> entries = new ArrayList<>();

        // Add a visible placeholder slice
        entries.add(new PieEntry(1, ""));

        // Add an invisible slice
        entries.add(new PieEntry(0, ""));

        // Create a dataset for the pie chart
        PieDataSet dataSet = new PieDataSet(entries, "");

        // Set the fill color for the visible slice and a transparent color for the invisible slice
        dataSet.setColors(Color.parseColor("#009899"), Color.TRANSPARENT);

        // Set the space between slices
        dataSet.setSliceSpace(2f);

        // Hide values for the placeholder data
        dataSet.setDrawValues(false);

        // Create PieData object and set it to the chart
        PieData pieData = new PieData(dataSet);
        pieChart.setData(pieData);

        // Set the center text for "No data"
        pieChart.setCenterText("No data");

        // Set the size of the center text
        pieChart.setCenterTextSize(16f);

        // Set the color of the center text
        pieChart.setCenterTextColor(Color.BLACK);

        // Disable the description label
        pieChart.getDescription().setEnabled(false);

        // Disable the legend
        pieChart.getLegend().setEnabled(false);

        // Customize the chart's hole and inner band
        // Set the radius of the hole in the center
        pieChart.setHoleRadius(40f);

        // Keep the hole color white
        pieChart.setHoleColor(Color.WHITE);

        // Set the radius of the transparent circle around the hole
        pieChart.setTransparentCircleRadius(50f);

        // Set the transparent circle's color to match the pie chart's background
        pieChart.setTransparentCircleColor(Color.WHITE);

        // Make the transparent circle fully opaque
        pieChart.setTransparentCircleAlpha(255);

        // Animate the chart with a vertical animation
        pieChart.animateY(1000, Easing.EaseInOutQuad);

        // Refresh the chart to apply changes
        pieChart.notifyDataSetChanged();
        pieChart.invalidate();
    }

    private void setUserPieChart() {
        // add code after tracker is built and display today/previous days breakdown
    }

    private void setUserDisplayMessages() {
        if (user != null) {
            // Display user unique messages of annual carbon emissions and other information
            DatabaseReference ref = mDataBase.getReference("Users").child(user.getUid()).child("primaryData");

            ref.get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    if (task.getResult().exists()) {
                        String better;
                        Double annualEmissions, nationalAverage, annualEmissionsRd, userBetterPercentage;

                        // grab annual footprint and national avg
                        DataSnapshot snapshot = task.getResult();
                        annualEmissions = snapshot.child("annualFootprint").child("total").getValue(Double.class);
                        nationalAverage = snapshot.child("countryAverage").getValue(Double.class);

                        annualEmissionsRd = Math.round(annualEmissions * 100.0) / 100.0;

                        userBetterPercentage = Math.abs(((annualEmissions - nationalAverage) / nationalAverage) * 100);
                        userBetterPercentage = Math.round(userBetterPercentage * 100.0) / 100.0;

                        // update text views
                        String annualEmissionsTxt = annualEmissionsRd + " tonnes of CO₂";
                        String betterUserTxt = "You're ahead of " + userBetterPercentage +"% of people nationally!";

                        annualTxt.setText(annualEmissionsTxt);
                        betterTxt.setText(betterUserTxt);

                    } else {
                        // Prompt user to complete primary data collection once per log in
                        if (!DialogState.dialogShown(user.getUid())) {
                            showDialogBoxWithDelay();
                            DialogState.setDialogState(user.getUid(), true);
                        }
                    }
                } else {
                    //Log error
                    Log.e("FirebaseError", "Error fetching data", task.getException());
                }
            });
        }
    }

    private void showDialogBoxWithDelay() {
        new android.os.Handler().postDelayed(() -> {
            showPrimaryDataDialogBox();
        }, 3000);
    }

    private void showPrimaryDataDialogBox() {

        //Initialize U.I elements and show dialog box
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.primary_data_dialog_box);
        dialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.primary_data_dialog_background));
        dialog.setCancelable(false);

        Button btnDialogNo = dialog.findViewById(R.id.noBtnPrimaryDataDialog);
        Button btnDialogYes = dialog.findViewById(R.id.yesBtnPrimaryDataDialog);

        dialog.show();


        // On-click listeners to close dialog box and to start primary data collection
        btnDialogYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Switch over to primary data activity
                Intent intent = new Intent(HomeScreenActivity.this, CountrySelectionActivity.class);
                startActivity(intent);
                finish();
            }
        });

        btnDialogNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }

}

