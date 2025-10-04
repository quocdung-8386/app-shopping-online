package com.example.apponline.firebase;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;

public class FirebaseHelper {
    private static FirebaseAuth mAuth;
    private static FirebaseFirestore db;

    public static FirebaseAuth getFirebaseAuth() {
        if (mAuth == null) mAuth = FirebaseAuth.getInstance();
        return mAuth;
    }

    public static FirebaseFirestore getFirestoreInstance() {
        if (db == null) {
            db = FirebaseFirestore.getInstance();
            FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                    .setPersistenceEnabled(true)
                    .build();
            db.setFirestoreSettings(settings);
        }
        return db;
    }

    public static boolean isUserLoggedIn() {
        return getFirebaseAuth().getCurrentUser() != null;
    }

    public static String getCurrentUserId() {
        FirebaseUser user = getFirebaseAuth().getCurrentUser();
        return user != null ? user.getUid() : null;
    }


    public static String getCurrentUserEmail() {
        FirebaseUser user = getFirebaseAuth().getCurrentUser();
        return user != null ? user.getEmail() : "N/A";
    }
    public static String getCurrentUserName() {
        FirebaseUser user = getFirebaseAuth().getCurrentUser();
        return (user != null && user.getDisplayName() != null) ? user.getDisplayName() : "Khách hàng";
    }
}