package com.example.b07demosummer2024;

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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class CustomSearchAdapter extends RecyclerView.Adapter<CustomSearchAdapter.SearchViewHolder>{

    private ArrayList<String> habitNames;
    private ArrayList<String> habitImpact;
    private Context context;
    private FirebaseDatabase mDataBase;
    private FirebaseUser user;
    private FirebaseAuth mAuth;

    public CustomSearchAdapter(ArrayList<String> habitNames, ArrayList<String> habitImpact, Context context) {
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
    public SearchViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate the item layout with custom row layout (layout of each row in the recycler view)
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_layout_search_bar, parent, false);

        return new SearchViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SearchViewHolder holder, int position) {

        // Set the data in the text views of the recycler view items

        String habitName = habitNames.get(position);
        String impactType = habitImpact.get(position);
        String concat = "Impact: " + impactType;

        holder.habit.setText(habitName);
        holder.impact.setText(concat);

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

    @Override
    public int getItemCount() {
        return habitNames.size();
    }
    public class SearchViewHolder extends RecyclerView.ViewHolder {

        // Text Views of a row in the recycler view

        TextView habit, impact;

        public SearchViewHolder(@NonNull View itemView) {

            super(itemView);

            habit = itemView.findViewById(R.id.habitNameCardViewSearch);
            impact = itemView.findViewById(R.id.habitImpactCardViewSearch);

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


