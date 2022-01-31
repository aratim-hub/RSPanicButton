package com.theappwelt.vhelp.model;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface ContactDao {
    @Insert
    void insertProduct(Contact product);

    @Query("UPDATE contacts SET relationship = :relationship, isCall = :isCall, isSms = :isSms WHERE contactMobile = :mobile")
    void updateProduct(String relationship, boolean isCall, boolean isSms, String mobile);

    @Query("SELECT * FROM contacts WHERE contactName = :name")
    List<Contact> findProduct(String name);

    @Query("DELETE FROM contacts")
    void deleteProduct();

    @Query("SELECT * FROM contacts")
    LiveData<List<Contact>> getAllProducts();

    @Query("SELECT * FROM contacts")
    List<Contact> getAllContacts();

    @Query("DELETE FROM contacts WHERE contactMobile = :name")
    void deleteContact(String name);
}
