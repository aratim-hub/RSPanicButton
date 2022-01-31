package com.theappwelt.vhelp.model;

import android.app.Application;
import android.os.AsyncTask;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.List;

public class ContactRepository {

    private ContactDao productDao;
    private LiveData<List<Contact>> allProducts;
    private List<Contact> allContacts;

    public ContactRepository(Application application) {
        ContactRoomDatabase db;
        db = ContactRoomDatabase.getDatabase(application);
        productDao = db.productDao();
        allProducts = productDao.getAllProducts();
        //allContacts = productDao.getAllContacts();
    }

    public LiveData<List<Contact>> getAllProducts() {
        return allProducts;
    }
    public List<Contact> getAllConatcts() {
        return allContacts;
    }

    public MutableLiveData<List<Contact>> getSearchResults() {
        return searchResults;
    }

    public void insertProduct(Contact newproduct) {
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

    public void DeleteContact(String name) {
        DeleteContactAsyncTask task = new DeleteContactAsyncTask(productDao);
        task.execute(name);
    }

    public void updateProduct(Contact name) {
        UpdateCourseAsyncTask task = new UpdateCourseAsyncTask(productDao);
        task.execute(name);
    }


    private MutableLiveData<List<Contact>> searchResults =
            new MutableLiveData<>();

    private void asyncFinished(List<Contact> results) {
        searchResults.setValue(results);
    }

    private static class QueryAsyncTask extends
            AsyncTask<String, Void, List<Contact>> {

        private ContactDao asyncTaskDao;
        private ContactRepository delegate = null;

        QueryAsyncTask(ContactDao dao) {
            asyncTaskDao = dao;
        }

        @Override
        protected List<Contact> doInBackground(final String... params) {
            return asyncTaskDao.findProduct(params[0]);
        }

        @Override
        protected void onPostExecute(List<Contact> result) {
            delegate.asyncFinished(result);
        }
    }

    private static class InsertAsyncTask extends AsyncTask<Contact, Void, Void> {

        private ContactDao asyncTaskDao;

        InsertAsyncTask(ContactDao dao) {
            asyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(final Contact... params) {
            asyncTaskDao.insertProduct(params[0]);
            return null;
        }
    }

    private static class DeleteAsyncTask extends AsyncTask<String, Void, Void> {

        private ContactDao asyncTaskDao;

        DeleteAsyncTask(ContactDao dao) {
            asyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(final String... params) {
            asyncTaskDao.deleteProduct();
            return null;
        }

    }

    private static class DeleteContactAsyncTask extends AsyncTask<String, Void, Void> {

        private ContactDao asyncTaskDao;

        DeleteContactAsyncTask(ContactDao dao) {
            asyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(final String... params) {
            asyncTaskDao.deleteContact(params[0]);
            return null;
        }

    }

    private static class UpdateCourseAsyncTask extends AsyncTask<Contact, Void, Void> {
        private ContactDao dao;

        private UpdateCourseAsyncTask(ContactDao dao) {
            this.dao = dao;
        }

        @Override
        protected Void doInBackground(Contact... models) {
            // below line is use to update
            // our modal in dao.
            dao.updateProduct(models[0].getRelationship(),models[0].isCall(),models[0].isSms(),models[0].getMobile());
            return null;
        }
    }


}
