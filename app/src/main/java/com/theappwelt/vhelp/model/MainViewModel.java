package com.theappwelt.vhelp.model;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.List;

public class MainViewModel extends AndroidViewModel
{
    private ContactRepository repository;
    private List<Contact> allContacts;
    private LiveData<List<Contact>> allProducts;
    private MutableLiveData<List<Contact>> searchResults;

    public MainViewModel (Application application) {
        super(application);
        repository = new ContactRepository(application);
        allProducts = repository.getAllProducts();
        searchResults = repository.getSearchResults();
        allContacts = repository.getAllConatcts();
    }

    MutableLiveData<List<Contact>> getSearchResults() {
        return searchResults;
    }

    public List<Contact> getAllContacts() {
        return allContacts;
    }
    public LiveData<List<Contact>> getAllProducts() {
        return allProducts;
    }

    public void insertProduct(Contact product) {
        repository.insertProduct(product);
    }

    public void findProduct(String name) {
        repository.findProduct(name);
    }

    public void deleteProduct() {
        repository.deleteProduct();
    }

    public void updateProduct(Contact name) {
        repository.updateProduct(name);
    }

    public void deleteContact(String name){repository.DeleteContact(name);}
}
