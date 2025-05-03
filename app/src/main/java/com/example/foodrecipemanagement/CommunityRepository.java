package com.example.foodrecipemanagement;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CommunityRepository extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "recipe_management.db";
    private static final int DATABASE_VERSION = 1;
    private static final String TABLE_COMMUNITIES = "communities";
    private static final String TABLE_RECIPES = "recipes";
    private static final String TABLE_QUESTIONS = "questions";
    private static final String TABLE_REPLIES = "replies";
    private static CommunityRepository instance;
    private final boolean useFirebase = true;

    public static CommunityRepository getInstance(Context context) {
        if (instance == null) {
            instance = new CommunityRepository(context.getApplicationContext());
        }
        return instance;
    }

    private CommunityRepository(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_COMMUNITIES + " (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, description TEXT, is_public INTEGER)");
        db.execSQL("CREATE TABLE " + TABLE_RECIPES + " (id INTEGER PRIMARY KEY AUTOINCREMENT, title TEXT, ingredients TEXT, steps TEXT, community_id INTEGER)");
        db.execSQL("CREATE TABLE " + TABLE_QUESTIONS + " (id INTEGER PRIMARY KEY AUTOINCREMENT, content TEXT, user_name TEXT, community_id INTEGER)");
        db.execSQL("CREATE TABLE " + TABLE_REPLIES + " (id INTEGER PRIMARY KEY AUTOINCREMENT, content TEXT, user_name TEXT, question_id INTEGER)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_COMMUNITIES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_RECIPES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_QUESTIONS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_REPLIES);
        onCreate(db);
    }

    public void addQuestion(Question question) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("content", question.getContent());
        values.put("user_name", question.getUserName());
        values.put("community_id", question.getCommunityId());

        long id = db.insert(TABLE_QUESTIONS, null, values);
        question.setId((int) id);
        db.close();

        if (useFirebase) {
            try {
                FirebaseFirestore dbFirebase = FirebaseFirestore.getInstance();
                dbFirebase.collection("questions")
                        .document(String.valueOf(id))
                        .set(question)
                        .addOnFailureListener(e -> android.util.Log.e("CommunityRepository", "Failed to add question to Firebase: " + e.getMessage()));
            } catch (Exception e) {
                android.util.Log.e("CommunityRepository", "Firebase error on addQuestion: " + e.getMessage());
            }
        }
    }

    public void deleteQuestion(int questionId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_QUESTIONS, "id = ?", new String[]{String.valueOf(questionId)});
        db.delete(TABLE_REPLIES, "question_id = ?", new String[]{String.valueOf(questionId)});
        db.close();

        if (useFirebase) {
            try {
                FirebaseFirestore dbFirebase = FirebaseFirestore.getInstance();
                dbFirebase.collection("questions")
                        .document(String.valueOf(questionId))
                        .delete()
                        .addOnFailureListener(e -> android.util.Log.e("CommunityRepository", "Failed to delete question from Firebase: " + e.getMessage()));
                dbFirebase.collection("replies")
                        .whereEqualTo("questionId", questionId)
                        .get()
                        .addOnSuccessListener(queryDocumentSnapshots -> {
                            for (DocumentSnapshot doc : queryDocumentSnapshots) {
                                doc.getReference().delete();
                            }
                        })
                        .addOnFailureListener(e -> android.util.Log.e("CommunityRepository", "Failed to delete replies from Firebase: " + e.getMessage()));
            } catch (Exception e) {
                android.util.Log.e("CommunityRepository", "Firebase error on deleteQuestion: " + e.getMessage());
            }
        }
    }

    public List<Question> getQuestions(int communityId) {
        List<Question> questions = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_QUESTIONS, null, "community_id = ?",
                new String[]{String.valueOf(communityId)}, null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
                String content = cursor.getString(cursor.getColumnIndexOrThrow("content"));
                String userName = cursor.getString(cursor.getColumnIndexOrThrow("user_name"));
                int commId = cursor.getInt(cursor.getColumnIndexOrThrow("community_id"));
                questions.add(new Question(id, content, userName, commId));
            } while (cursor.moveToNext());
            cursor.close();
        }
        db.close();
        return questions;
    }

    public void addReply(Reply reply) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("content", reply.getContent());
        values.put("user_name", reply.getUserName());
        values.put("question_id", reply.getQuestionId());

        long id = db.insert(TABLE_REPLIES, null, values);
        reply.setId((int) id);
        db.close();

        if (useFirebase) {
            try {
                FirebaseFirestore dbFirebase = FirebaseFirestore.getInstance();
                dbFirebase.collection("replies")
                        .document(String.valueOf(id))
                        .set(reply)
                        .addOnFailureListener(e -> android.util.Log.e("CommunityRepository", "Failed to add reply to Firebase: " + e.getMessage()));
            } catch (Exception e) {
                android.util.Log.e("CommunityRepository", "Firebase error on addReply: " + e.getMessage());
            }
        }
    }

    public void updateReply(Reply reply) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("content", reply.getContent());
        values.put("user_name", reply.getUserName());
        values.put("question_id", reply.getQuestionId());

        db.update(TABLE_REPLIES, values, "id = ?", new String[]{String.valueOf(reply.getId())});
        db.close();

        if (useFirebase) {
            try {
                FirebaseFirestore dbFirebase = FirebaseFirestore.getInstance();
                dbFirebase.collection("replies")
                        .document(String.valueOf(reply.getId()))
                        .set(reply)
                        .addOnFailureListener(e -> android.util.Log.e("CommunityRepository", "Failed to update reply in Firebase: " + e.getMessage()));
            } catch (Exception e) {
                android.util.Log.e("CommunityRepository", "Firebase error on updateReply: " + e.getMessage());
            }
        }
    }

    public void deleteReply(int replyId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_REPLIES, "id = ?", new String[]{String.valueOf(replyId)});
        db.close();

        if (useFirebase) {
            try {
                FirebaseFirestore dbFirebase = FirebaseFirestore.getInstance();
                dbFirebase.collection("replies")
                        .document(String.valueOf(replyId))
                        .delete()
                        .addOnFailureListener(e -> android.util.Log.e("CommunityRepository", "Failed to delete reply from Firebase: " + e.getMessage()));
            } catch (Exception e) {
                android.util.Log.e("CommunityRepository", "Firebase error on deleteReply: " + e.getMessage());
            }
        }
    }

    public List<Reply> getReplies(int questionId) {
        List<Reply> replies = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_REPLIES, null, "question_id = ?",
                new String[]{String.valueOf(questionId)}, null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
                String content = cursor.getString(cursor.getColumnIndexOrThrow("content"));
                String userName = cursor.getString(cursor.getColumnIndexOrThrow("user_name"));
                int qId = cursor.getInt(cursor.getColumnIndexOrThrow("question_id"));
                replies.add(new Reply(id, content, userName, qId));
            } while (cursor.moveToNext());
            cursor.close();
        }
        db.close();
        return replies;
    }
    private FirebaseFirestore getFirestoreInstance() {
        try {
            return FirebaseFirestore.getInstance();
        } catch (Exception e) {
            android.util.Log.e("CommunityRepository", "Failed to initialize Firebase Firestore: " + e.getMessage());
            return null;
        }
    }

    public void addCommunity(Community community) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("name", community.getName());
        values.put("description", community.getDescription());
        values.put("is_public", community.isPublic() ? 1 : 0);

        long id = db.insert(TABLE_COMMUNITIES, null, values);
        community.setId((int) id);
        db.close();

        if (useFirebase) {
            try {
                FirebaseFirestore dbFirebase = getFirestoreInstance();
                if (dbFirebase != null) {
                    dbFirebase.collection("communities")
                            .document(String.valueOf(id))
                            .set(community)
                            .addOnFailureListener(e -> android.util.Log.e("CommunityRepository", "Failed to add community to Firebase: " + e.getMessage()));
                }
            } catch (Exception e) {
                android.util.Log.e("CommunityRepository", "Firebase error on addCommunity: " + e.getMessage());
            }
        }
    }

    public List<Community> getCommunities() {
        List<Community> communities = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_COMMUNITIES, null, null, null, null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            Set<Integer> seenIds = new HashSet<>();
            do {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
                if (seenIds.add(id)) {
                    String name = cursor.getString(cursor.getColumnIndexOrThrow("name"));
                    String description = cursor.getString(cursor.getColumnIndexOrThrow("description"));
                    boolean isPublic = cursor.getInt(cursor.getColumnIndexOrThrow("is_public")) == 1;
                    Community community = new Community(id, name, description, isPublic);

                    Cursor recipeCursor = db.rawQuery("SELECT COUNT(*) FROM " + TABLE_RECIPES + " WHERE community_id = ?",
                            new String[]{String.valueOf(id)});
                    if (recipeCursor != null && recipeCursor.moveToFirst()) {
                        community.setRecipeCount(recipeCursor.getInt(0));
                        recipeCursor.close();
                    }

                    Cursor questionCursor = db.rawQuery("SELECT COUNT(*) FROM " + TABLE_QUESTIONS + " WHERE community_id = ?",
                            new String[]{String.valueOf(id)});
                    if (questionCursor != null && questionCursor.moveToFirst()) {
                        community.setQuestionCount(questionCursor.getInt(0));
                        questionCursor.close();
                    }

                    communities.add(community);
                } else {
                    android.util.Log.w("CommunityRepository", "Duplicate community ID found in SQLite: " + id);
                }
            } while (cursor.moveToNext());
            cursor.close();
        }
        db.close();
        android.util.Log.d("CommunityRepository", "Retrieved communities from SQLite: " + communities.size());
        return communities;
    }

    public void deleteCommunity(int communityId) {
        SQLiteDatabase db = this.getWritableDatabase();

        db.delete(TABLE_RECIPES, "community_id = ?", new String[]{String.valueOf(communityId)});
        db.delete(TABLE_QUESTIONS, "community_id = ?", new String[]{String.valueOf(communityId)});
        db.delete(TABLE_COMMUNITIES, "id = ?", new String[]{String.valueOf(communityId)});

        db.close();

        if (useFirebase) {
            try {
                FirebaseFirestore dbFirebase = getFirestoreInstance();
                if (dbFirebase != null) {
                    dbFirebase.collection("communities")
                            .document(String.valueOf(communityId))
                            .delete()
                            .addOnFailureListener(e -> android.util.Log.e("CommunityRepository", "Failed to delete community from Firebase: " + e.getMessage()));
                    dbFirebase.collection("recipes")
                            .whereEqualTo("communityId", communityId)
                            .get()
                            .addOnSuccessListener(queryDocumentSnapshots -> {
                                for (DocumentSnapshot doc : queryDocumentSnapshots) {
                                    doc.getReference().delete();
                                }
                            })
                            .addOnFailureListener(e -> android.util.Log.e("CommunityRepository", "Failed to delete recipes from Firebase: " + e.getMessage()));
                    dbFirebase.collection("questions")
                            .whereEqualTo("communityId", communityId)
                            .get()
                            .addOnSuccessListener(queryDocumentSnapshots -> {
                                for (DocumentSnapshot doc : queryDocumentSnapshots) {
                                    doc.getReference().delete();
                                }
                            })
                            .addOnFailureListener(e -> android.util.Log.e("CommunityRepository", "Failed to delete questions from Firebase: " + e.getMessage()));
                }
            } catch (Exception e) {
                android.util.Log.e("CommunityRepository", "Firebase error on deleteCommunity: " + e.getMessage());
            }
        }
    }

    public void updateCommunity(Community community) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("name", community.getName());
        values.put("description", community.getDescription());
        values.put("is_public", community.isPublic() ? 1 : 0);

        db.update(TABLE_COMMUNITIES, values, "id = ?", new String[]{String.valueOf(community.getId())});
        db.close();

        if (useFirebase) {
            try {
                FirebaseFirestore dbFirebase = getFirestoreInstance();
                if (dbFirebase != null) {
                    dbFirebase.collection("communities")
                            .document(String.valueOf(community.getId()))
                            .set(community)
                            .addOnFailureListener(e -> android.util.Log.e("CommunityRepository", "Failed to update community in Firebase: " + e.getMessage()));
                }
            } catch (Exception e) {
                android.util.Log.e("CommunityRepository", "Firebase error on updateCommunity: " + e.getMessage());
            }
        }
    }

    public void updateQuestion(Question question) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("content", question.getContent());
        values.put("user_name", question.getUserName());
        values.put("community_id", question.getCommunityId());

        db.update(TABLE_QUESTIONS, values, "id = ?", new String[]{String.valueOf(question.getId())});
        db.close();

        if (useFirebase) {
            try {
                FirebaseFirestore dbFirebase = FirebaseFirestore.getInstance();
                dbFirebase.collection("questions")
                        .document(String.valueOf(question.getId()))
                        .set(question);
            } catch (Exception e) {
                android.util.Log.e("CommunityRepository", "Firebase error: " + e.getMessage());
            }
        }
    }
}