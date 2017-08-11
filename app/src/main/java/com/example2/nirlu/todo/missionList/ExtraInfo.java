package com.example2.nirlu.todo.missionList;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;


import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/**
 * Created by nluria on 5/30/2017.
 */

public class ExtraInfo extends AppCompatActivity
{
    DataBaseHelper myDb;
    FireBaseHelper fireDb;
    String nameOfGroup,task, nameOfGroupFullName, myPhoneNumber;
    private DatabaseReference mRootRef,mRootRef2;


    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.extra_info);
        myDb = new DataBaseHelper(this);
        fireDb = new FireBaseHelper();

        //print the title of group on screen.
        Intent intent = getIntent();
        task= intent.getStringExtra("completedTask");
        nameOfGroupFullName= intent.getStringExtra("nameOfGroupFullName");
        myPhoneNumber= intent.getStringExtra("myPhoneNumber");
        TextView title= (TextView) findViewById(R.id.title);
        System.out.println("task: "+task);
        title.setText(task);
        title.setTextColor(Color.parseColor("#bfbfbf"));    //gray.


        //execute methods.
        optionBar();
        getWhoFinishedTheTask("completed by","מבצע המשימה:  ", mRootRef);
        getDate("date","תאריך:  ", mRootRef2);
    }


    public void getWhoFinishedTheTask(final String key, final String desc, DatabaseReference m)
    {
        String group=nameOfGroupFullName;

        m = FirebaseDatabase.getInstance()
                .getReferenceFromUrl("https://todo-ba2f4.firebaseio.com/groups/"+group+"/completedTasks/"+task+"/"+key+"/");

        m.addValueEventListener(new ValueEventListener()
        {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                String user= (String) dataSnapshot.getValue();
                TextView extra_info= (TextView) findViewById(R.id.completed_by);
                extra_info.setText(desc+getContactDisplayNameByNumber(user,ExtraInfo.this));
                System.out.println(getContactDisplayNameByNumber(user,ExtraInfo.this));
                extra_info.setTextColor(Color.parseColor("#000000"));
            }

            @Override
            public void onCancelled(DatabaseError databaseError)
            {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });
    }


    public void getDate(final String key, final String desc, DatabaseReference m)
    {
        String group=nameOfGroupFullName;

        m = FirebaseDatabase.getInstance()
                .getReferenceFromUrl("https://todo-ba2f4.firebaseio.com/groups/"+group+"/completedTasks/"+task+"/"+key+"/");

        m.addValueEventListener(new ValueEventListener()
        {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                String date= (String) dataSnapshot.getValue();
                System.out.println("nir: "+date);
                TextView extra_info= (TextView) findViewById(R.id.date);
                extra_info.setText(desc+date);
                extra_info.setTextColor(Color.parseColor("#000000"));

            }

            @Override
            public void onCancelled(DatabaseError databaseError)
            {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });
    }


    public String getContactDisplayNameByNumber(String number,Context context)
    {
        String name=null;
        if (number.equals(myPhoneNumber))
        {
            name="אני";
        }
        else
        {
            Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(number));
            name = "Incoming call from";

            ContentResolver contentResolver = context.getContentResolver();
            Cursor contactLookup = contentResolver.query(uri, null, null, null, null);

            try {
                if (contactLookup != null && contactLookup.getCount() > 0)
                {
                    contactLookup.moveToNext();
                    name = contactLookup.getString(contactLookup.getColumnIndex(ContactsContract.Data.DISPLAY_NAME));
                }
                else
                {
                    name = "Unknown number";
                }
            } finally
            {
                if (contactLookup != null) {
                    contactLookup.close();
                }
            }
        }
        return name;
    }


    public void onBackPressed()
    {
        finish();
    }


    private void optionBar()
    {
        ActionBar ab = getSupportActionBar();
        ab.setLogo(R.drawable.logo_brown);
        ab.setDisplayUseLogoEnabled(true);
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setDisplayShowHomeEnabled(true);
        ab.setTitle("2Do");
        ab.setBackgroundDrawable(new ColorDrawable(Color.argb(252,352,524,525)));

    }
}
