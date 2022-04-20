package com.example.relate.fragment;

import static android.app.Activity.RESULT_OK;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.nfc.Tag;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.relate.HomeActivity;
import com.example.relate.MainActivity;
import com.example.relate.PreferenceActivity;
import com.example.relate.R;
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

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProfileFragment extends Fragment {
    private static final int RESULT_LOAD_IMAGE = 1212;
    ImageView mImg;
    EditText mDesc;
    TextView userName;
    TextView userAge;
    TextView education;
    TextView interestedin;
    ChipGroup mHashtagsGroup;
    ChipGroup mHashtagsChipGroup;
    List<String> mInterestList;
    List<String> mHashtagsList;
    Button mSaveProfile;
    Uri url = null;
    Button mEditProfile;
    private FirebaseAuth mAuth;
    private FirebaseFirestore mStore;
    private StorageReference mStorage;

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        mImg = view.findViewById(R.id.pro_image);
        mDesc = view.findViewById(R.id.pro_desc);
        education = view.findViewById(R.id.education);
        userName = view.findViewById(R.id.userName);
        userAge = view.findViewById(R.id.userAge);
        interestedin = view.findViewById(R.id.interesedin);
        mHashtagsGroup = view.findViewById(R.id.chip_c);
        mHashtagsChipGroup = view.findViewById(R.id.chip_hashtags);
        mSaveProfile = view.findViewById(R.id.save_pro);
        mEditProfile = view.findViewById(R.id.edit_pro);
        mAuth=FirebaseAuth.getInstance();
        mStore=FirebaseFirestore.getInstance();
        mStorage=FirebaseStorage.getInstance().getReference();
        mInterestList = new ArrayList<>();
        mHashtagsList = new ArrayList<>();
        displayChipData(mInterestList);

        //Get Profile Data
        getProfileData();

        mImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                photoPickerIntent.setType("image/");
                startActivityForResult(photoPickerIntent, RESULT_LOAD_IMAGE);

            }
        });

        //logout button
        Button btn = view.findViewById(R.id.logout);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(getContext(), MainActivity.class));
                getActivity().finish();

            }
        });

        //save data
        mSaveProfile.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View view) {
                Long tsLong = System.currentTimeMillis()/1000;
                String ts = tsLong.toString();

                if(url!=null){
                    //nag uupdate ng database
                    mStorage.child(ts+"/").putFile(url).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            //String downloadUrl = taskSnapshot.getStorage().getDownloadUrl().toString();
                            //Log.i("TAG","onSuccess:"+downloadUrl);
                            Task<Uri> res = taskSnapshot.getStorage().getDownloadUrl();
                            res.addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @RequiresApi(api = Build.VERSION_CODES.O)
                                @Override
                                public void onSuccess(Uri uri) {
                                    String downloadUrl = uri.toString();
                                    Map<String,Object> map = new HashMap<>();
                                    map.put("Desc", mDesc.getText().toString());
                                    map.put("Interested In", interestedin.getText().toString());
                                    map.put("Education", education.getText().toString());
                                    map.put("Img_url", downloadUrl);
                                    map.put("name", userName.getText().toString());
                                    //map.put("age", userAge.getText().toString());
                                    mStore.collection("Users").document(mAuth.getCurrentUser().getUid())
                                            .update(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if(task.isSuccessful()){
                                                Toast.makeText(getContext(), "Profile Updated", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });

                                }
                            });
                        }
                    });

                }else{

                    //nag uupdate ng database
                    Map<String,Object> map = new HashMap<>();
                    map.put("Desc", mDesc.getText().toString());
                    map.put("Interested In", interestedin.getText().toString());
                    map.put("Education", education.getText().toString());
                    map.put("name", userName.getText().toString());
                    //map.put("age", userAge.getText().toString());
                    mStore.collection("Users").document(mAuth.getCurrentUser().getUid())
                            .update(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                Toast.makeText(getContext(), "Profile Updated", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                }


            }
        });

        mEditProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), PreferenceActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                getActivity().finish();
            }
        });

        return view;
    }
    //taga kuha ng database
    private void getProfileData() {
        mStore.collection("Users").document(mAuth.getCurrentUser().getUid())
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    String Desc = task.getResult().getString("Desc");
                    String InterestedIn = task.getResult().getString("Interested In");
                    String Education = task.getResult().getString("Education");
                    String Interest = task.getResult().getString("Interest");
                    String Hashtags = task.getResult().getString("Hashtags");
                    String Img_url = task.getResult().getString("Img_url");
                    String Name = task.getResult().getString("name");
                    double Age = task.getResult().getDouble("age");
                    int aged = (int) Age;
                    mDesc.setText(Desc);
                    interestedin.setText(InterestedIn);
                    education.setText(Education);
                    userAge.setText(String.valueOf(aged));
                    userName.setText(Name);


                    if(Interest!=null){
                        List<String> mList = Arrays.asList(Interest.split("\\s*,\\s*"));
                        mInterestList.addAll(mList);
                        displayChipData(mInterestList);
                    }

                    if(Hashtags!=null) {
                        List<String> cList = Arrays.asList(Hashtags.split("\\s*,\\s*"));
                        mHashtagsList.addAll(cList);
                        displayLangData(mHashtagsList);
                    }

                    if(Img_url!=null){
                        Glide.with(getContext()).load(Img_url).into(mImg);
                    }
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
            Glide.with(getContext()).load(imageUri).into(mImg);

        }else{
            Toast.makeText(getContext(), "You Haven't picked Image", Toast.LENGTH_LONG).show();
        }
    }

    private void displayLangData(List<String> mHashtagsList) {
        mHashtagsChipGroup.removeAllViews();
        for(String s: mHashtagsList){
            Chip chip = (Chip) this.getLayoutInflater().inflate(R.layout.single_chip_item, null, false);
            chip.setText(s);

            mHashtagsChipGroup.addView(chip);
        }
    }

    private void displayChipData(List<String> mInterestList) {
        mHashtagsGroup.removeAllViews();
        for(String s: mInterestList){
            Chip chip = (Chip) getActivity().getLayoutInflater().inflate(R.layout.single_chip_item, null, false);
            chip.setText(s);

            mHashtagsGroup.addView(chip);
        }

    }
}