package com.theappwelt.vhelp.model;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.List;

public class UserViewModel extends AndroidViewModel
{
    private UserRepository repository;
    private List<User> allContacts;
    private LiveData<List<User>> allProducts;
    private MutableLiveData<List<User>> searchResults;

    public UserViewModel(Application application) {
        super(application);
        repository = new UserRepository(application);
        allProducts = repository.getAllProducts();
        searchResults = repository.getSearchResults();
        //allContacts = repository.getAllConatcts();
    }

    MutableLiveData<List<User>> getSearchResults() {
        return searchResults;
    }

    public List<User> getAllContacts() {
        return allContacts;
    }
    public LiveData<List<User>> getAllProducts() {
        return allProducts;
    }

    public void insertProduct(User product) {
        repository.insertProduct(product);
    }

    public void findProduct(String name) {
        repository.findProduct(name);
    }

    public void deleteProduct() {
        repository.deleteProduct();
    }

//    public void updateProduct(User name) {
//        repository.updateProduct(name);
//    }
}
