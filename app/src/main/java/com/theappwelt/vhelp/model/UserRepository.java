package com.theappwelt.vhelp.model;

import android.app.Application;
import android.os.AsyncTask;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.List;

public class UserRepository {

    private UserDao productDao;
    private LiveData<List<User>> allProducts;
    private List<Contact> allContacts;

    public UserRepository(Application application) {
        UserRoomDatabase db;
        db = UserRoomDatabase.getDatabase(application);
        productDao = db.productDao();
        allProducts = productDao.getAllProducts();
        //allContacts = productDao.getAllContacts();
    }

    public LiveData<List<User>> getAllProducts() {
        return allProducts;
    }

    public MutableLiveData<List<User>> getSearchResults() {
        return searchResults;
    }

    public void insertProduct(User newproduct) {
        InsertAsyncTask task = new InsertAsyncTask(productDao);
        task.execute(newproduct);
    }

    public void deleteProduct() {
        DeleteAsyncTask task = new DeleteAsyncTask(productDao);
        task.execute();
    }

    public void findProduct(String name) {
        QueryAsyncTask task = new QueryAsyncTask(productDao);
        task.delegate = this;
        task.execute(name);
    }

//    public void updateProduct(User name) {
//        UpdateCourseAsyncTask task = new UpdateCourseAsyncTask(productDao);
//        task.execute(name);
//    }


    private MutableLiveData<List<User>> searchResults =
            new MutableLiveData<>();

    private void asyncFinished(List<User> results) {
        searchResults.setValue(results);
    }

    private static class QueryAsyncTask extends
            AsyncTask<String, Void, List<User>> {

        private UserDao asyncTaskDao;
        private UserRepository delegate = null;

        QueryAsyncTask(UserDao dao) {
            asyncTaskDao = dao;
        }

        @Override
        protected List<User> doInBackground(final String... params) {
            return asyncTaskDao.findProduct(params[0]);
        }

        @Override
        protected void onPostExecute(List<User> result) {
            delegate.asyncFinished(result);
        }
    }

    private static class InsertAsyncTask extends AsyncTask<User, Void, Void> {

        private UserDao asyncTaskDao;

        InsertAsyncTask(UserDao dao) {
            asyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(final User... params) {
            asyncTaskDao.insertProduct(params[0]);
            return null;
        }
    }

    private static class DeleteAsyncTask extends AsyncTask<String, Void, Void> {

        private UserDao asyncTaskDao;

        DeleteAsyncTask(UserDao dao) {
            asyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(final String... params) {
            asyncTaskDao.deleteProduct();
            return null;
        }

    }

    private static class UpdateCourseAsyncTask extends AsyncTask<Contact, Void, Void> {
        private UserDao dao;

        private UpdateCourseAsyncTask(UserDao dao) {
            this.dao = dao;
        }

        @Override
        protected Void doInBackground(Contact... models) {
            // below line is use to update
            // our modal in dao.
            //dao.updateProduct(models[0].getRelationship(),models[0].isCall(),models[0].isSms(),models[0].getMobile());
            return null;
        }
    }


}
