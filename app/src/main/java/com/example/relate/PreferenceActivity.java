package com.example.relate;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.security.AccessController;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PreferenceActivity extends AppCompatActivity {

    private static final int RESULT_LOAD_IMAGE = 1212;
    ImageView mImg, mImg2, mImg3;
    ChipGroup mChipHashtagsGroup;
    ChipGroup mChipInterestGroup;
    ChipGroup getChipHashtagsGroup;
    ChipGroup getChipInterestGroup;
    List<String> ListHashtags;
    List<String> ListInterest;
    Uri url = null;
    private FirebaseAuth mAuth;
    private FirebaseFirestore mStore;
    private StorageReference mStorage;
    Button mSavePreference;
    String []Hashtags = {"#Loveyourself", "#Happy", "#Motivated", "#Love", "#life", "#Educated", "#Beautiful", "#Fit", "#Healthy"};
    String []Interest = {"Exercise","Photography","Traveling","Drawing","Dancing","Politics","Cooking","Learning","Singing","Music","Calligraphy","Reading",
            "Cleaning","Watching Movies","Pets","Drinking"};
    String education="";
    String interestedIn="";
    RadioGroup personInterested;
    RadioGroup Educationterested;
    RadioButton personInterestedParticular;
    RadioButton educationInterestParticular;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preference);

        //For Setting up Firebase
        mAuth=FirebaseAuth.getInstance();
        mStore=FirebaseFirestore.getInstance();
        mStorage= FirebaseStorage.getInstance().getReference();

        //Image Viewing
        mImg = findViewById(R.id.pro_image1);
        mImg2 = findViewById(R.id.pro_image2);
        mImg3 = findViewById(R.id.pro_image3);

        //For Interest and Hashtags
        mChipHashtagsGroup = findViewById(R.id.chip_hashtags);
        mChipInterestGroup = findViewById(R.id.chip_interest);
        getChipInterestGroup = findViewById(R.id.chip_interest_data);
        getChipHashtagsGroup = findViewById(R.id.chip_hashtags_data);
        ListHashtags = new ArrayList<>();
        ListInterest = new ArrayList<>();

        //Saving Button
        mSavePreference = findViewById(R.id.savePreference);
        personInterested = findViewById(R.id.personInterest);
        Educationterested = findViewById(R.id.educationInterest);

        //Displaying Clicked Hashtags & Interest
        displayHashtagsData(ListHashtags);
        displayInterestData(ListInterest);

        //Getting data from Database
        getProfileData();

        //for clicking image to go to choose files
        mImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                photoPickerIntent.setType("image/");
                startActivityForResult(photoPickerIntent, RESULT_LOAD_IMAGE);

            }
        });

        //hashtags viewing array
        for(String Hashtags : Hashtags) {

            Chip chip = new Chip(mChipHashtagsGroup.getContext());
            chip = (Chip) this.getLayoutInflater().inflate(R.layout.single_chip_item, null, false);
            chip.setText(Hashtags);

                chip.setOnClickListener(new View.OnClickListener() {
                @SuppressLint("ResourceType")
                @RequiresApi(api = Build.VERSION_CODES.M)
                @Override
                // hashtags inserting clicked chip to array and changing color
                public void onClick(View view) {
                    String text = ((Chip) view).getText().toString();

                    if (((Chip)view).getChipBackgroundColor().equals(getResources().getColorStateList(R.color.colorRed,null))) {
                        ((Chip)view).setChipBackgroundColor(getResources().getColorStateList(R.color.colorLine, null));
                        Toast.makeText(PreferenceActivity.this, ""+ListHashtags, Toast.LENGTH_SHORT).show();
                    } else {
                        ((Chip) view).setChipBackgroundColor(getResources().getColorStateList(R.color.colorRed, null));
                        // Adding Hashtags to array
                        ListHashtags.add(text);
                        displayHashtagsData(ListHashtags);

                        Toast.makeText(PreferenceActivity.this, ""+ListHashtags, Toast.LENGTH_SHORT).show();
                    }

                }
            });

            mChipHashtagsGroup.addView(chip);
        }

        //interest viewing array
        for(String Interest : Interest) {

            Chip chip = new Chip(mChipInterestGroup.getContext());
            chip = (Chip) this.getLayoutInflater().inflate(R.layout.single_chip_item, null, false);
            chip.setText(Interest);

            chip.setOnClickListener(new View.OnClickListener() {
                @SuppressLint("ResourceType")
                @RequiresApi(api = Build.VERSION_CODES.M)
                @Override

                public void onClick(View view) {
                    String text = ((Chip) view).getText().toString();

                    if (((Chip)view).getChipBackgroundColor().equals(getResources().getColorStateList(R.color.colorRed,null))) {
                        ((Chip)view).setChipBackgroundColor(getResources().getColorStateList(R.color.colorLine, null));
                        Toast.makeText(PreferenceActivity.this, ""+ListInterest, Toast.LENGTH_SHORT).show();
                    } else {
                        ((Chip) view).setChipBackgroundColor(getResources().getColorStateList(R.color.colorRed, null));
                        // Adding Interest to array
                        ListInterest.add(text);
                        displayInterestData(ListInterest);

                        Toast.makeText(PreferenceActivity.this, ""+ListInterest, Toast.LENGTH_SHORT).show();
                    }

                }
            });

            mChipInterestGroup.addView(chip);
        }

        //Saving Profile Update
        mSavePreference.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View view) {
                Long tsLong = System.currentTimeMillis()/1000;
                String ts = tsLong.toString();

                int rd = Educationterested.getCheckedRadioButtonId();
                educationInterestParticular = findViewById(rd);
                education = educationInterestParticular.getText().toString();

                if(url!=null){

                    mStorage.child(ts+"/").putFile(url).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                            Task<Uri> res = taskSnapshot.getStorage().getDownloadUrl();
                            res.addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @RequiresApi(api = Build.VERSION_CODES.O)
                                @Override
                                public void onSuccess(Uri uri) {
                                    String downloadUrl = uri.toString();
                                    Map<String,Object> map = new HashMap<>();
                                    map.put("Hashtags", String.join(",",ListHashtags));
                                    map.put("Interest", String.join(",",ListInterest));
                                    map.put("Img_url", downloadUrl);
                                    map.put("Interested In",interestedIn);
                                    map.put("Education",education);
                                    mStore.collection("Users").document(mAuth.getCurrentUser().getUid())
                                            .update(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if(task.isSuccessful()){
                                                Toast.makeText(PreferenceActivity.this, "Profile Updated", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });

                                }
                            });
                        }
                    });
                    Intent intent = new Intent(PreferenceActivity.this, HomeActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();

                }else{

                    Map<String,Object> map = new HashMap<>();
                    map.put("Hashtags", String.join(",",ListHashtags));
                    map.put("Interest", String.join(",",ListInterest));
                    map.put("Interested In",interestedIn);
                    map.put("Education",education);
                    mStore.collection("Users").document(mAuth.getCurrentUser().getUid())
                            .update(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                Toast.makeText(PreferenceActivity.this, "Profile Updated", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                    Intent intent = new Intent(PreferenceActivity.this, HomeActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();

                }

            }

        });

    }
    private void getProfileData() {
        mStore.collection("Users").document(mAuth.getCurrentUser().getUid())
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    String Interest = task.getResult().getString("Interest");
                    String Hashtags = task.getResult().getString("Hashtags");

                    if(Hashtags!=null){
                        List<String> mList = Arrays.asList(Hashtags.split("\\s*,\\s*"));
                        ListHashtags.addAll(mList);
                    }

                    if(Interest!=null){
                        List<String> mList = Arrays.asList(Interest.split("\\s*,\\s*"));
                        ListInterest.addAll(mList);
                    }

                    //Radio Button 2x Interested In & College

                }

            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK){

            final Uri imageUri = data.getData();
            url = imageUri;
            Glide.with(PreferenceActivity.this).load(imageUri).into(mImg);

        }else{
            Toast.makeText(PreferenceActivity.this, "You Haven't picked Image", Toast.LENGTH_LONG).show();
        }
    }

    private void displayInterestData(List<String> listInterest) {
        getChipInterestGroup.removeAllViews();
        for(String s: ListInterest){
            Chip chip = (Chip) this.getLayoutInflater().inflate(R.layout.single_chip_item, null, false);
            chip.setText(s);

            chip.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    getChipInterestGroup.removeView(view);
                    Chip c = (Chip) view;
                    ListInterest.remove(c.getText().toString());
                }
            });

            getChipInterestGroup.addView(chip);
        }
    }

    private void displayHashtagsData(List<String> listHashtags) {
        getChipHashtagsGroup.removeAllViews();
        for(String s: ListHashtags){
            Chip chip = (Chip) this.getLayoutInflater().inflate(R.layout.single_chip_item, null, false);
            chip.setText(s);

            chip.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    getChipHashtagsGroup.removeView(view);
                    Chip c = (Chip) view;
                    ListHashtags.remove(c.getText().toString());
                }
            });

            getChipHashtagsGroup.addView(chip);
        }
    }

    public void checkButton(View v){

        int radioId = personInterested.getCheckedRadioButtonId();
        personInterestedParticular = findViewById(radioId);
        interestedIn = personInterestedParticular.getText().toString();
    }

}