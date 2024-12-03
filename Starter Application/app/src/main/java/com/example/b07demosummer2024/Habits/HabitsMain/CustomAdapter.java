package com.example.b07demosummer2024.Habits.HabitsMain;

import static androidx.appcompat.content.res.AppCompatResources.getDrawable;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.b07demosummer2024.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.MyViewHolder> {

    private ArrayList<String> habitNames;
    private ArrayList<String> habitImpact;
    private Context context;
    private FirebaseDatabase mDataBase;
    private FirebaseUser user;
    private FirebaseAuth mAuth;

    public CustomAdapter(ArrayList<String> habitNames, ArrayList<String> habitImpact, Context context) {
        this.habitNames = habitNames;
        this.habitImpact = habitImpact;
        this.context = context;

        mDataBase = FirebaseDatabase.getInstance();
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
    }

    // View Holders
    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate the item layout with custom row layout (layout of each row in the recycler view)
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_layout, parent, false);

        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        // Set the data in the text views of the recycler view items

        String habitName = habitNames.get(position);
        String impactType = habitImpact.get(position);

        if ("category".equalsIgnoreCase(impactType)) {
            // Then it's just a category header
            holder.habit.setText(habitName);
            holder.impact.setText("");

            // Make Category Title pop out
            holder.habit.setTextSize(20);
            holder.habit.setTypeface(null, Typeface.BOLD);
            holder.habit.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_START);

            // Remove clickability for category headers
            holder.itemView.setOnClickListener(null);

            // Make Transparent
            holder.itemView.setBackgroundColor(Color.TRANSPARENT);
        } else {
            holder.habit.setText(habitName);
            String concat = "Impact: " + impactType;
            holder.impact.setText(concat);

            //Reset text style for habit names
            holder.habit.setTextSize(16);
            holder.habit.setTypeface(null, Typeface.NORMAL);
            holder.habit.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_START);

            // Force the background color to be the one in the row layout when it's not a category
            // header
            holder.itemView.setBackgroundColor(ContextCompat.getColor(context, R.color.anotherblue));

            // Adding onClickListeners event on items in the recycler list
            holder.itemView.setOnClickListener (new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    // Check Firebase DataBase to see if user already added habit
                    DatabaseReference ref = mDataBase.getReference("Users").child(user.getUid()).child("habits").child(habitName);

                    ref.get().addOnCompleteListener(task -> {
                       if (task.isSuccessful()){
                           if (task.getResult().exists()) {
                               Toast.makeText(context, "You have already added this habit!", Toast.LENGTH_SHORT).show();
                           } else {
                               showHabitDialogBox(habitName);
                           }
                       } else {
                           //Log error
                           Log.e("FirebaseError", "Error fetching data", task.getException());
                       }
                    });
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return habitNames.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        // Text Views of a row in the recycler view
        TextView habit, impact;

        public MyViewHolder(@NonNull View itemView) {

            super(itemView);

            habit = itemView.findViewById(R.id.habitNameCardView);
            impact = itemView.findViewById(R.id.habitImpactCardView);

        }

    }

    private void showHabitDialogBox(String habitName) {

        // Initialize U.I elements and show dialog box
        Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.add_habit_dialog_box);
        dialog.getWindow().setBackgroundDrawable(getDrawable(context, R.drawable.primary_data_dialog_background));

        Button noBtnHabitDialogBox, yesBtnHabitDialogBox;
        noBtnHabitDialogBox = dialog.findViewById(R.id.noBtnDialogHabit);
        yesBtnHabitDialogBox = dialog.findViewById(R.id.yesBtnDialogHabit);

        TextView message = dialog.findViewById(R.id.messageDialogHabit);
        message.setText(habitName);

        // Make it so that if user taps outside of dialog box it closes
        dialog.setCancelable(true);

        dialog.show();

        //if yes gets clicked, add habit to uses account in firebase db
        yesBtnHabitDialogBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addHabit(habitName);
                dialog.dismiss();
            }
        });


        // If no gets clicked, just dismiss the box
        noBtnHabitDialogBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

    }

    private void addHabit(String habit) {

        // Add habit to the user in the firebase database
        DatabaseReference habits = mDataBase.getReference("Users").child(user.getUid()).child("habits");
        habits.child(habit).child("loggedDays").setValue("0");

        Toast.makeText(context, "Added habit successfully!", Toast.LENGTH_SHORT).show();
    }

}
