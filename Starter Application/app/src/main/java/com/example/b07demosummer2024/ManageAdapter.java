package com.example.b07demosummer2024;

import static androidx.appcompat.content.res.AppCompatResources.getDrawable;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Typeface;
import android.util.Log;
import android.util.Pair;
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

import java.util.List;

public class ManageAdapter extends RecyclerView.Adapter<ManageAdapter.ManagerViewHolder> {

    private List<Pair<String, Integer>> habitPlusLoggedDays;
    private Context context;
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private FirebaseDatabase mDataBase;

    public ManageAdapter(List<Pair<String, Integer>> habitPlusLoggedDays, Context context) {
        this.habitPlusLoggedDays = habitPlusLoggedDays;
        this.context = context;

        mDataBase = FirebaseDatabase.getInstance();
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
    }

    // View Holders
    @NonNull
    @Override
    public ManagerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate the item layout with custom row layout (layout of each row in the recycler view)
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_layout_manage_habits, parent, false);

        return new ManagerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ManagerViewHolder holder, int position) {

        // Set the data in the text views of the recycler view items

        String habitName = habitPlusLoggedDays.get(position).first;
        String loggedNum = String.valueOf(habitPlusLoggedDays.get(position).second);

        holder.habit.setText(habitName);

        String concat = "Days Logged: " + loggedNum;
        holder.loggedDays.setText(concat);

        // Make loggedDays bold
        holder.loggedDays.setTypeface(null, Typeface.BOLD);

        // Adding onClickListeners event on items in the recycler list
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showRemoveHabitDialogBox(habitName, holder.getAdapterPosition());
            }
        });
    }

    @Override
    public int getItemCount() {
        return habitPlusLoggedDays.size();
    }
    public class ManagerViewHolder extends RecyclerView.ViewHolder {

        // Text Views of a row in the recycler view

        TextView habit, loggedDays;

        public ManagerViewHolder(@NonNull View itemView) {
            super(itemView);

            habit = itemView.findViewById(R.id.habitNameManageHabit);
            loggedDays = itemView.findViewById(R.id.loggedDaysManageHabit);
        }
    }

    private void showRemoveHabitDialogBox(String habitName, int position) {

        // Initialize U.I elements and show dialog box
        Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.remove_habit_dialog_box);
        dialog.getWindow().setBackgroundDrawable(getDrawable(context, R.drawable.primary_data_dialog_background));

        Button noBtn, yesBtn;
        noBtn = dialog.findViewById(R.id.noBtnDialogManageHabit);
        yesBtn = dialog.findViewById(R.id.yesBtnDialogManageHabit);

        TextView message = dialog.findViewById(R.id.messageDialogManageHabit);
        message.setText(habitName);

        // Make it so that if user taps outside of dialog box it closes
        dialog.setCancelable(true);

        dialog.show();

        //if yes gets clicked, remove habit to uses account in firebase db
        yesBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeHabit(habitName, position);
                dialog.dismiss();
            }
        });

        // If no gets clicked, just dismiss the box
        noBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

    }

    private void removeHabit(String habit, int position) {

        // Remove habit from user in the firebase database

        DatabaseReference ref = mDataBase.getReference("Users").child(user.getUid()).child("habits").child(habit);

        ref.removeValue().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {

                // Remove the habit from the RecyclerView's data source as well
                habitPlusLoggedDays.remove(position);

                // Notify the adapter that the item has been removed
                notifyItemRemoved(position);

                Toast.makeText(context, "Habit removed successfully.", Toast.LENGTH_SHORT).show();
            } else {
                Log.e("Firebase Error", "Failed to remove habit: " + task.getException().getMessage());
                Toast.makeText(context, "Failed to remove habit.", Toast.LENGTH_SHORT).show();
            }
        });
    }

}
