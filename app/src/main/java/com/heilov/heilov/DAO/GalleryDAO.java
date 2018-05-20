package com.heilov.heilov.DAO;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.heilov.heilov.Activities.GalleryActivity;
import com.heilov.heilov.Model.Photo;
import com.heilov.heilov.Utils.ImageAdapter;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

public class GalleryDAO {
    private DatabaseReference mDatabase;
    private FirebaseAuth auth;

    public void getPhotos(String email, GalleryCallback galleryCallback) {
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();

        DatabaseReference ref = mDatabase.child("server/saving-data/pics/");
        ValueEventListener valueEventListener = ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ArrayList<Bitmap> photos = new ArrayList<>();
                for (DataSnapshot singleSnapshot : dataSnapshot.getChildren()) {
                    Photo photo = singleSnapshot.getValue(Photo.class);
                    if (photo.getUser().equals(email)) {
                        try {
                            Bitmap imageBitmap = decodeFromFirebaseBase64(photo.getBitmap());
                            photos.add(imageBitmap);

                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
                galleryCallback.onCallback(photos);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private static Bitmap decodeFromFirebaseBase64(String image) throws IOException {
        byte[] decodedByteArray = android.util.Base64.decode(image, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(decodedByteArray, 0, decodedByteArray.length);
    }

    public void savePhoto(Bitmap bitmap) {
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference("server/saving-data/");
        DatabaseReference usersRef = ref.child("pics");

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        String imageEncoded = Base64.encodeToString(baos.toByteArray(), Base64.DEFAULT);
        Random rand = new Random();

        int n = rand.nextInt(5000) + 1;
        usersRef.child(auth.getCurrentUser().getUid() + n).setValue(new Photo(imageEncoded, auth.getCurrentUser().getEmail()));
    }

}
