package com.example.b07demosummer2024.Habits.LogHabits;

import static androidx.appcompat.content.res.AppCompatResources.getDrawable;

import android.app.Dialog;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.b07demosummer2024.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class HabitAdapter extends RecyclerView.Adapter<HabitAdapter.HabitViewHolder> {

    private ArrayList<String> habitNames;
    private Context context;
    private FirebaseDatabase mDataBase;
    private FirebaseUser user;
    private FirebaseAuth mAuth;

    public HabitAdapter(ArrayList<String> habitNames, Context context) {
        this.habitNames = habitNames;
        this.context = context;

        mDataBase = FirebaseDatabase.getInstance();
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
    }

    // View Holders
    @NonNull
    @Override
    public HabitViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        // Inflate the item layout with custom row layout (layout of each row in the recycler view)
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_layout_habits_tracker, parent, false);

        return new HabitViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HabitViewHolder holder, int position) {

        // Set the data in the text views of the recycler view items

        String habitName = habitNames.get(position);

        holder.habit.setText(habitName);

        // Adding onClickListeners event on items in the recycler list
        holder.itemView.setOnClickListener (new View.OnClickListener() {
            @Override
            public void onClick(View v) {

               showHabitDialogBox(habitName);
            }
        });
    }

    @Override
    public int getItemCount() {
        return habitNames.size();
    }

    public class HabitViewHolder extends RecyclerView.ViewHolder {

        // Text Views of a row in the recycler view
        TextView habit;

        public HabitViewHolder(@NonNull View itemView) {

            super(itemView);
            
            // REPLACE THIS WITH THE ID OF THE TEXT VIEW IN THE ROW LAYOUT
            habit = itemView.findViewById(R.id.habitNameTrackerHabit);

        }

    }

    private void showHabitDialogBox(String habitName) {

        // Initialize U.I elements and show dialog box
        Dialog dialog = new Dialog(context);

        // REPLACE THIS WITH THE ID OF THE LAYOUT OF THE DIALOG BOX
        dialog.setContentView(R.layout.track_habit_dialog_box);

        // YOU CAN KEEP THIS LINE AS IT IS
        dialog.getWindow().setBackgroundDrawable(getDrawable(context, R.drawable.primary_data_dialog_background));

        Button noBtnHabitDialogBox, yesBtnHabitDialogBox;

        // REPLACE THESE WITH THE ID OF THE BUTTONS IN THE DIALOG BOX (just the R.id. part)
        noBtnHabitDialogBox = dialog.findViewById(R.id.noBtnDialogTrackHabit);
        yesBtnHabitDialogBox = dialog.findViewById(R.id.yesBtnDialogTrackHabit);

        // REPLACE THIS WITH THE ID OF THE TEXT VIEW IN THE DIALOG BOX
        TextView message = dialog.findViewById(R.id.messageDialogTrackHabit);
        message.setText(habitName);

        // Make it so that if user taps outside of dialog box it closes
        dialog.setCancelable(true);

        dialog.show();

        //if yes gets clicked, add habit to uses account in firebase db
        yesBtnHabitDialogBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logHabit(habitName);
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

    private void logHabit(String habit) {

        // Increment habbit logged days by 1 to the user in the firebase database
        DatabaseReference habits = mDataBase.getReference("Users").child(user.getUid()).child("habits").child(habit);

        habits.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()){
                DataSnapshot snapshot = task.getResult();
                
                String loggedDays = snapshot.child("loggedDays").getValue(String.class);
                String lastLogged = snapshot.child("lastLogged").getValue(String.class);

                long currentTime = System.currentTimeMillis();

                if (lastLogged == null) {

                    // First time logging the habit, add a day and create lastLogged node with current date
                    habits.child("loggedDays").setValue(String.valueOf(1));
                    habits.child("lastLogged").setValue(String.valueOf(currentTime));

                    Toast.makeText(context, "Habit logged successfully for the first time!", Toast.LENGTH_SHORT).show();
                } else {
                    
                    // Grab last logged time and compare if 24 hrs have passed, we only log once a day
                    long lastLoggedTime = Long.parseLong(lastLogged.trim());

                    if (currentTime - lastLoggedTime >= 86400000) {

                        // 24 hours have passed, log the habit
                        int logged = Integer.parseInt(loggedDays) + 1;
                        habits.child("loggedDays").setValue(String.valueOf(logged));
                        habits.child("lastLogged").setValue(String.valueOf(currentTime));

                        Toast.makeText(context, "Habit logged successfully!", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(context, "You have already logged this habit today!", Toast.LENGTH_SHORT).show();
                    }
                }
            } else {
                //Log error
                Log.e("FirebaseError", "Error fetching data", task.getException());
            }
        });
    }
}