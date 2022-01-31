package com.theappwelt.vhelp;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;


import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.theappwelt.vhelp.model.Contact;
import com.theappwelt.vhelp.model.MainViewModel;

import java.util.ArrayList;
import java.util.List;

public class ContactActivity extends AppCompatActivity {

    private static final String TAG = "CONTACT";
    private final int ACTIVITY_REQUEST_CODE = 1;
    private Button addContactButton;
    ProductListAdapter productListAdapter;
    private RecyclerView coursesRV;
    private MainViewModel mViewModel;
    List<Contact> contactArrayList = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact);

        TextView contactTitle = findViewById(R.id.contactTitle);



        mViewModel = ViewModelProviders.of(this).get(MainViewModel.class);
        productListAdapter = new ProductListAdapter(R.layout.item_contact);
        coursesRV = findViewById(R.id.contactList);
        coursesRV.setLayoutManager(new LinearLayoutManager(this));
        coursesRV.setHasFixedSize(true);
        coursesRV.setAdapter(productListAdapter);

        observerSetup();
        productListAdapter.setOnItemClickListener(new ProductListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Contact model, int position) {
                String[] country = { "Mother", "Father", "Son", "Daughter", "Brother", "Sister", "Cousin"};
                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(ContactActivity.this);
                LayoutInflater inflater = ContactActivity.this.getLayoutInflater();
                View dialogView = inflater.inflate(R.layout.alert_layout, null);
                dialogBuilder.setView(dialogView);
                AlertDialog alertDialog = dialogBuilder.create();
                alertDialog.setCancelable(true);

                Spinner spnRelationship = (Spinner)dialogView.findViewById(R.id.spinnerKeluhan);
                ArrayAdapter aa = new ArrayAdapter(ContactActivity.this,android.R.layout.simple_spinner_item,country);
                aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spnRelationship.setAdapter(aa);

                CheckBox chkCall = (CheckBox)dialogView.findViewById(R.id.chkCall);
                chkCall.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        for (int i = 0; i < contactArrayList.size(); i++) {
                            if (contactArrayList.get(i).isCall()){
                                chkCall.setChecked(false);
                                Toast.makeText(ContactActivity.this, "Already selected for call", Toast.LENGTH_SHORT).show();
                                return;
                            }
                        }
                    }
                });
                CheckBox chkSms = (CheckBox)dialogView.findViewById(R.id.chkSms);
                chkSms.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                    }
                });
                Button btnSubmit = (Button)dialogView.findViewById(R.id.btnSubmit);
                btnSubmit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String selectedRelationship = spnRelationship.getSelectedItem().toString();
                        boolean isSMs = chkSms.isChecked();
                        boolean isCall = chkCall.isChecked();
                        final String name = model.getName();
                        final String contact = model.getMobile();
                        Log.d("name ",name);
                        Log.d("contact ",contact);
                        alertDialog.dismiss();

                        mViewModel.updateProduct(new Contact(name,contact,selectedRelationship,isCall,isSMs, "DEFAULT"));
                        observerSetup();
                    }
                });

                alertDialog.show();
            }
        });

        // initialize add contact button
        addContactButton = findViewById(R.id.addContactButton);
        addContactButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
                intent.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE);
                startActivityForResult(intent, ACTIVITY_REQUEST_CODE);
            }
        });
    }

    @Override
    public void onActivityResult(int reqCode, int resultCode, Intent data) {
        super.onActivityResult(reqCode, resultCode, data);

        switch (reqCode) {
            case (ACTIVITY_REQUEST_CODE):
                if (resultCode == Activity.RESULT_OK) {

                    // successfully selected contact to add
                    Uri contactData = data.getData();
                    Cursor c = managedQuery(contactData, null, null, null, null);
                    if (c.moveToFirst()) {

                        // add item to storage and update contact list
                        String name = c.getString(c.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                        String contact = c.getString(c.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                        Contact contact1 = new Contact(name, contact, "", false, false, "DEFAULT");
                        mViewModel.insertProduct(contact1);
                        Log.i(TAG, "added - name: " + name + ", contact: " + contact);
                        observerSetup();
             }
                }
                break;
        }
    }

    private void observerSetup() {
        mViewModel.getAllProducts().observe(this, new Observer<List<Contact>>() {
            @Override
            public void onChanged(@Nullable final List<Contact> products) {
                productListAdapter.setProductList(products);
                contactArrayList.addAll(products);
            }
        });
    }
}
