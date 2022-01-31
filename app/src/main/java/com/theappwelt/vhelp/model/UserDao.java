package com.theappwelt.vhelp.model;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface UserDao {
    @Insert
    void insertProduct(User product);

    @Query("UPDATE users SET userMobile = :mobile")
    void updateProduct(String mobile);

    @Query("SELECT * FROM users WHERE userMobile = :mobile")
    List<User> findProduct(String mobile);

    @Query("DELETE FROM users")
    void deleteProduct();

    @Query("SELECT * FROM users")
    LiveData<List<User>> getAllProducts();

    @Query("SELECT * FROM users")
    List<User> getAllContacts();
}
