package org.gdhote.gdhotecodegroup.pixcha.utils;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.gdhote.gdhotecodegroup.pixcha.model.CurrentUser;
import org.gdhote.gdhotecodegroup.pixcha.model.User;

import androidx.annotation.NonNull;

public class UserHelper {

    public static synchronized void updateCurrentUser() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {

            String documentId = user.getUid();
            FirebaseFirestore firestoreDb = FirebaseFirestore.getInstance();
            CollectionReference usersCollectionRef = firestoreDb.collection("users");
            final DocumentReference userDocumentRef = usersCollectionRef.document(documentId);

            userDocumentRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        User user = task.getResult().toObject(User.class);
                        CurrentUser currentUser = CurrentUser.getInstance();
                        currentUser.setId(user.getId());
                        currentUser.setDisplayName(user.getDisplayName());
                        currentUser.setEmailAddress(user.getEmailAddress());
                        currentUser.setBio(user.getBio());
                        currentUser.setProfileImageUrl(user.getProfileImageUrl());
                    }
                }
            });
        }
    }
}
