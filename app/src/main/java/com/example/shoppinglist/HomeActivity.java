package com.example.shoppinglist;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.common.util.NumberUtils;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.util.Date;

public class HomeActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private FloatingActionButton flab;
    private DatabaseReference database;
    private FirebaseAuth mAuth;
    private RecyclerView recyclerView;
    private TextView totlSum;
    private FirebaseRecyclerOptions<Data> options;
    private  FirebaseRecyclerAdapter<Data,MyViewHolder> adapter;

    //Global variable:
    private String type;
    private double amount;
    private String note;
    private String post_key;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();

        //extract the name from the email
        String name = user.getEmail().split("@")[0] ;

        toolbar = findViewById(R.id.home_toolbar);

        //add the toolbar layout as the application toolbar
        setSupportActionBar(toolbar);

        //set the toolbar title
        getSupportActionBar().setTitle(name+"'s Shopping List");
        flab = findViewById(R.id.fab);
        String uid = user.getUid();

        //create refrence to the user database
        database = FirebaseDatabase.getInstance().getReference().child("Shopping List").child(uid);
        database.keepSynced(true);
        totlSum = findViewById(R.id.total);
        recyclerView = findViewById(R.id.recycler);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setStackFromEnd(true);
        layoutManager.setReverseLayout(true);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);

        //Total amount calculator
        database.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                double total = 0;
                for (DataSnapshot snap : snapshot.getChildren()){
                    Data data = snap.getValue(Data.class);
                    total += data.getAmount();
                }
                String  strTotal = String.valueOf(total);
                totlSum.setText(strTotal);
            }

            @Override
            public void onCancelled( DatabaseError error) { }
        });

        //the floating "plus" button
        flab.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) { customeDialog(); }
        });
    }// end of onCreate


    //adds a new item to the shopping list
    private void customeDialog(){
        AlertDialog.Builder myDialog = new AlertDialog.Builder(HomeActivity.this);
        LayoutInflater inflater = LayoutInflater.from(HomeActivity.this);
        View myview = inflater.inflate(R.layout.input_data,null);
        AlertDialog dialog = myDialog.create();
        dialog.setView(myview);
        EditText type = myview.findViewById(R.id.edt_type);
        EditText amount = myview.findViewById(R.id.edt_amount);
        EditText note = myview.findViewById(R.id.edt_note);
        Button btnSave = myview.findViewById(R.id.btn_save);

        //add item function
        //after pressing save button an input validation will be performed
        btnSave.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                String mType = type.getText().toString().trim();
                String mAmount = amount.getText().toString().trim();
                String mNote = note.getText().toString().trim();

                // type must have a value
                if(TextUtils.isEmpty(mType)){
                    type.setError("Required Field...");
                    return;
                }

                // amount must have a value
                if(mAmount.length() <= 0){
                    amount.setError("Required Field...");
                    return;
                }

                // amount value can't be 0
                if(mAmount.equals("0") || mAmount.equals("0.0")
                        || mAmount.equals(".0") || mAmount.equals("0.")){
                    amount.setError("Amount can't be 0...");
                    return;
                }

                //convert the amount from text to double
                double amnt = Double.parseDouble(mAmount);
                String id = database.push().getKey();

                //save the date
                String date = DateFormat.getDateInstance().format(new Date());

                //create a new Data object using all the information
                Data data = new Data(mType, mNote, date, id, amnt);

                //add the object to the database
                database.child(id).setValue(data);

                //close the pop up window
                dialog.dismiss();
            }
        });

        dialog.show();
    }// end of customeDialog

    protected void onStart(){
        super.onStart();

        options = new FirebaseRecyclerOptions.Builder<Data>().setLifecycleOwner(this).setQuery(database,Data.class).build();
        adapter = new FirebaseRecyclerAdapter <Data,MyViewHolder>( options ){
            @Override
            public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_data, parent, false);
                return new MyViewHolder(v);
            }

            @Override
            protected void onBindViewHolder(MyViewHolder viewHolder, int position,  Data model) {
                viewHolder.setDate(model.getDate());
                viewHolder.setType(model.getType());
                viewHolder.setNote(model.getNote());
                viewHolder.setAmount(model.getAmount());
                viewHolder.v.setOnClickListener(new View.OnClickListener() {
                    @Override

                    //when you click on one of the items on the list then a pop up window
                    //will occur with the data of that item.
                    public void onClick(View v) {
                        post_key = getRef(position).getKey();
                        type = model.getType();
                        note = model.getNote();
                        amount = model.getAmount();

                        //call the update function
                        updateData();
                    }
                });
            }
        };
        recyclerView.setAdapter(adapter);
        adapter.startListening();

    }// end of onStart

    public void updateData(){
        AlertDialog.Builder myDialog = new AlertDialog.Builder(HomeActivity.this);
        LayoutInflater inflater = LayoutInflater.from(HomeActivity.this);
        View mView = inflater.inflate(R.layout.update, null);
        AlertDialog dialog = myDialog.create();
        dialog.setView(mView);
        EditText edt_Type = mView.findViewById(R.id.edt_type_upd);
        EditText edt_Amount = mView.findViewById(R.id.edt_amount_upd);
        EditText edt_Note = mView.findViewById(R.id.edt_note_upd);

        //import the text of the selected line from the database
        edt_Type.setText(type);

        //place the marker on the end of the writing
        edt_Type.setSelection(type.length());

        //import the text of the selected line from the database
        edt_Amount.setText(String.valueOf(amount));

        //place the marker on the end of the writing
        edt_Amount.setSelection(String.valueOf(amount).length());

        //import the text of the selected line from the database
        edt_Note.setText(note);

        //place the marker on the end of the writing
        edt_Note.setSelection(note.length());

        //update button refrence
        Button btnUpd = mView.findViewById(R.id.btn_upd);

        //delete button refrence
        Button btnDlt = mView.findViewById(R.id.btn_delete);

        //update function
        //after pressing update button an input validation will be performed
        btnUpd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String mType = edt_Type.getText().toString().trim();
                String mAmount = edt_Amount.getText().toString().trim();
                String mNote = edt_Note.getText().toString().trim();

                // type must have a value
                if(TextUtils.isEmpty(mType)){
                    edt_Type.setError("Required Field...");
                    return;
                }

                // amount must have a value
                if(mAmount.length() <= 0){
                    edt_Amount.setError("Required Field...");
                    return;
                }

                // amount value can't be 0
                if(mAmount.equals("0") || mAmount.equals("0.0")
                        || mAmount.equals(".0") || mAmount.equals("0.")){
                    edt_Amount.setError("Amount can't be 0...");
                    return;
                }

                type = mType;
                note = mNote;
                double amnt = Double.parseDouble(mAmount);
                String date = DateFormat.getDateInstance().format(new Date());
                Data d = new Data(type, note, date, post_key, amnt);
                database.child(post_key).setValue(d);

                //close the pop up window
                dialog.dismiss();
            }
        });

        //delete function
        //after pressing this button the item will be deleted
        btnDlt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                database.child(post_key).removeValue();

                //close the pop up window
                dialog.dismiss();
            }
        });

        dialog.show();
    }// end of updateData

    //option bar on the top right side of the screen
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }// end of onCreateOptionsMenu

    @Override
    //log out option
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId()){
            //log out the user.
            case R.id.log_out:
                mAuth.signOut();

                //return to the main page which is the Log In page
                startActivity(new Intent(getApplicationContext(), MainActivity.class));

                //prevent the user from returning to this page(HomeActivity) by pressing the
                //back button(triangular button).
                finish();
                break;
            //delete all data of this user.
            case R.id.dlt_all:
                DatabaseReference myMessageRef = database;
                myMessageRef.removeValue();
                break;
        }

        return super.onOptionsItemSelected(item);
    }// end of onOptionsItemSelected

    //application life cycle - onStop
    @Override
    protected void onStop() {
        super.onStop();

        adapter.stopListening();

        //sign out the account
        mAuth.signOut();

        //return to the main page which is the Log In page
        Intent intent = new Intent(getBaseContext(),MainActivity.class);
        startActivity(intent);

        //prevent the user from returning to this page(HomeActivity) by pressing the
        //back button(triangular button).
        finish();
    }// end of onStop
}