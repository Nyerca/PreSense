package com.example.appar.database;


import android.content.Context;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.appar.GlobalVariable;
import com.example.appar.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Optional;

public class AchievementChecker {

    private enum Achievement {
        BRONZE(1, "Scout", "bronze_"),
        SILVER(5, "Expert", "silver_"),
        GOLD(10, "Professor", "gold_");

        private int animals;
        private String title;
        private String imagePath;

        private Achievement(int animals, String title, String imagePath) {
            this.animals = animals;
            this.title = title;
            this.imagePath = imagePath;
        }

        public int getAnimals() {
            return animals;
        }

        public String getTitle() {
            return title;
        }

        public String getImagePath() {
            return imagePath;
        }

        public static Optional<Achievement> getAchievement(int amount) {
            if(amount == BRONZE.getAnimals()) return Optional.of(BRONZE);
            else if(amount == SILVER.getAnimals()) return Optional.of(SILVER);
            else if(amount == GOLD.getAnimals()) return Optional.of(GOLD);
            return Optional.empty();
        }

        public String toString() {
            return this.animals + "";
        }
    }

    public static void check(String animal, int parkid) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference();
        myRef.addListenerForSingleValueEvent(new ValueEventListener(){

            @Override
            public void onDataChange(DataSnapshot dataSnapshot){
                int total = 1;

                for (com.google.firebase.database.DataSnapshot element : dataSnapshot.child("user_sensors/"+ "prova3").getChildren()) {
                    int amount = element.child(animal).getValue(Integer.class); //This is a1
                    total += amount;
                    //Toast.makeText(context, "Total: " + total, Toast.LENGTH_LONG).show();
                }

                //Toast.makeText(context, "Total: " + total, Toast.LENGTH_LONG).show();

                int value = 0;

                if(dataSnapshot.child("user_sensors/" + "prova3" + "/" + parkid + "/" + animal).exists())  {
                    value = dataSnapshot.child("user_sensors/" + "prova3" + "/" + parkid + "/" + animal).getValue(Integer.class);

                } else {

                }

                DatabaseReference usersRef = myRef.child("user_sensors/" + "prova3" + "/" + parkid + "/" + animal);
                usersRef.setValue(value + 1);

                Optional<Achievement> achievement = Achievement.getAchievement(total);
                if(achievement.isPresent()) {
                    String currentDate = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(new Date());
                    UserAchievement user_achievement = new UserAchievement(achievement.get().getImagePath() + animal, currentDate);

                    String str = "java";
                    String final_title = animal.substring(0, 1).toUpperCase() + animal.substring(1);

                    DatabaseReference achievement_ref = myRef.child("achievements/" + "prova3" + "/" + final_title + " " + achievement.get().getTitle());
                    achievement_ref.setValue(user_achievement);
                }



            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {}

        });

    }
}