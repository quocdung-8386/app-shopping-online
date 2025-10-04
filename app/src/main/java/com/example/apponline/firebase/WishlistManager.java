package com.example.apponline.firebase;

import android.util.Log;
import com.example.apponline.models.Product;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
public class WishlistManager {

    private static final String TAG = "WishlistManager";
    private static final String COLLECTION_WISHLIST = "wishlists";

    private static WishlistManager instance;

    private List<Product> wishlistItems;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    public interface WishlistLoadCallback {
        void onWishlistLoaded();
    }

    private WishlistManager() {
        wishlistItems = new ArrayList<>();

        db = FirebaseHelper.getFirestoreInstance();
        mAuth = FirebaseHelper.getFirebaseAuth();


        loadWishlistFromFirestore(null);
    }

    public static synchronized WishlistManager getInstance() {
        if (instance == null) {
            instance = new WishlistManager();
        }
        return instance;
    }

    public void addProductToWishlist(Product product) {
        if (product == null || product.getId() == null) return;

        if (!isProductInWishlist(product)) {
            if (product.getId() == null) {
                Log.e(TAG, "Product ID is NULL when adding to wishlist.");
                return;
            }
            wishlistItems.add(product);

            String userId = FirebaseHelper.getCurrentUserId();
            if (userId != null) {
                saveWishlistToFirestore(userId);
            }
        }
    }

    public void removeProductFromWishlist(Product product) {
        if (product == null || product.getId() == null) return;
        wishlistItems.removeIf(item -> item.getId().equals(product.getId()));

        String userId = FirebaseHelper.getCurrentUserId();
        if (userId != null) {
            saveWishlistToFirestore(userId);
        }
    }

    public boolean isProductInWishlist(Product product) {
        if (product == null || product.getId() == null) return false;
        return wishlistItems.stream().anyMatch(item -> item.getId().equals(product.getId()));
    }


    public List<Product> getWishlistItems() {
        return new ArrayList<>(wishlistItems);
    }


    public void saveWishlistToFirestore(String userId) {
        if (userId == null || db == null) return;

        List<String> productIds = new ArrayList<>();
        for (Product product : wishlistItems) {
            if (product.getId() != null) {
                productIds.add(product.getId());
            }
        }

        DocumentReference docRef = db.collection(COLLECTION_WISHLIST).document(userId);


        docRef.set(new WishlistData(productIds))
                .addOnSuccessListener(aVoid -> Log.d(TAG, "Wishlist saved successfully for user: " + userId))
                .addOnFailureListener(err -> Log.e(TAG, "Error saving wishlist document: " + err.getMessage()));
    }

    public void loadWishlistFromFirestore(WishlistLoadCallback callback) {
        String userId = FirebaseHelper.getCurrentUserId();

        if (userId == null || db == null) {
            Log.d(TAG, "User not logged in, cannot load wishlist.");
            if (callback != null) {
                callback.onWishlistLoaded();
            }
            return;
        }

        db.collection(COLLECTION_WISHLIST).document(userId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        List<String> productIds = (List<String>) documentSnapshot.get("productIds");

                        if (productIds != null && !productIds.isEmpty()) {
                            wishlistItems.clear();
                            loadProductDetailsByIds(productIds, callback);
                        } else {
                            wishlistItems.clear();
                            if (callback != null) callback.onWishlistLoaded();
                        }
                    } else {
                        Log.d(TAG, "Wishlist document does not exist for user: " + userId);
                        wishlistItems.clear();
                        if (callback != null) callback.onWishlistLoaded();
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error loading wishlist IDs: " + e.getMessage());
                    if (callback != null) callback.onWishlistLoaded();
                });
    }

    private void loadProductDetailsByIds(List<String> productIds, WishlistLoadCallback callback) {
        if (productIds.isEmpty()) {
            if (callback != null) callback.onWishlistLoaded();
            return;
        }

        final AtomicInteger counter = new AtomicInteger(productIds.size());

        for (String id : productIds) {
            db.collection("products").document(id)
                    .get()
                    .addOnSuccessListener(doc -> {
                        if (doc.exists()) {
                            Product product = doc.toObject(Product.class);

                            if (product != null) {
                                product.setId(doc.getId());
                            }

                            if (product != null && !isProductInWishlist(product)) {
                                wishlistItems.add(product);
                                Log.d(TAG, "Product loaded successfully: " + product.getName() + " (ID: " + product.getId() + ")");
                            } else if (product == null) {
                                Log.e(TAG, "Mapping FAILED for Product ID: " + id + ". Check Product Model.");
                            }
                        } else {
                            Log.w(TAG, "Product ID not found in 'products' collection: " + id);
                        }
                    })
                    .addOnFailureListener(e -> Log.e(TAG, "Failed to load product details for ID " + id + ": " + e.getMessage()))
                    .addOnCompleteListener(task -> {
                        if (counter.decrementAndGet() == 0) {
                            if (callback != null) {
                                callback.onWishlistLoaded();
                            }
                        }
                    });
        }
    }

    private static class WishlistData {
        public List<String> productIds;

        public WishlistData(List<String> productIds) {
            this.productIds = productIds;
        }

        public WishlistData() {}
    }
}