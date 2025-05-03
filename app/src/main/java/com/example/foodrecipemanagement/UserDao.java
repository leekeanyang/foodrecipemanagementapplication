package com.example.foodrecipemanagement;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

@Dao
public interface UserDao {
    @Insert
    void insert(User user);

    @Query("SELECT * FROM users WHERE email = :email")
    User getUserByEmail(String email);

    @Query("SELECT * FROM users WHERE email = :email AND password = :password")
    User login(String email, String password);

    @Query("SELECT EXISTS(SELECT * FROM users WHERE email = :email)")
    Boolean emailExists(String email);
}