package com.example2.nirlu.todo.missionList;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;



import com.example22.nluria.mDataObject.TaskObject;
import com.example22.nluria.mRecycler.MyAdapterMembers;
import com.example22.nluria.mSwiper.SwipeHelperMembers;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

/**
 * Created by nluria on 4/4/2017.
 */

public class members extends AppCompatActivity
{

    DataBaseHelper myDb;
    FireBaseHelper fireDb;
    String nameOfGroup;
    String myPhoneNumber, nameOfGroupFullName;
    private DatabaseReference mRootRef;
    private static ListView listView;
    private ArrayList<String> membersArray = new ArrayList<String>();
    private ArrayList<String> membersPhonesArray = new ArrayList<String>();

    MyAdapterMembers myAdapter;
    private static Button add_member_button;
    Typeface buttonFont;
    private final static int PICK_CONTACT = 1;
    RecyclerView rv;
    private ArrayList<String> tasksArray = new ArrayList<String>();
    ArrayList<TaskObject> names=new ArrayList<>();
    ArrayList<TaskObject> phones=new ArrayList<>();

    String manager;


    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.members);
        myDb = new DataBaseHelper(this);
        fireDb = new FireBaseHelper();

        //print title of group on screen.
        Intent intent = getIntent();
        myPhoneNumber= intent.getStringExtra("myPhoneNumber");
        nameOfGroupFullName= intent.getStringExtra("nameOfGroupFullName");

        fireDb.initialize(myPhoneNumber);
        nameOfGroup= intent.getStringExtra("nameOfGroup");
        TextView title= (TextView) findViewById(R.id.title);
        title.setText(nameOfGroup);
        buttonFont= Typeface.createFromAsset(getAssets(), "tamir.ttf");
        title.setTypeface(buttonFont);


        //execute methods.
        optionBar();
        getManager();
        getMembers();
        addMembers();
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

    //the menu bar itself.
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch(item.getItemId())
        {
            case R.id.refresh:
                refreshActivity();
                return true;


            default:
                return super.onOptionsItemSelected(item);
        }
    }


    public void getManager()
    {
        String group=nameOfGroupFullName;

        mRootRef = FirebaseDatabase.getInstance()
                .getReferenceFromUrl("https://todo-ba2f4.firebaseio.com/groups/"+group);

        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,R.layout.members_style, membersArray );

        mRootRef.addChildEventListener(new ChildEventListener()
        {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s)
            {
                System.out.println("dataSnapshot: "+dataSnapshot);

                if(dataSnapshot.getKey().equals("manager"))
                //   if (dataSnapshot!=null)
                {
                    manager=(String) dataSnapshot.getValue();
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    public void addMembers()
    {
        add_member_button = (Button)findViewById(R.id.add_member_button);
        add_member_button.setTypeface(buttonFont);
        add_member_button.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(manager.equals(myPhoneNumber))
                        {
                            callContacts(null, nameOfGroup);
                        }
                        else
                        {
                            Toast.makeText(members.this, "רק מנהל יכול לצרף חברים לקבוצה!", Toast.LENGTH_LONG).show();
                        }
                    }
                }
        );
    }

    public void getMembers()
    {
        // String group=myPhoneNumber+"_"+nameOfGroup;
        System.out.println("nameOfGroupFullName: "+nameOfGroupFullName);
        String group=nameOfGroupFullName;

        mRootRef = FirebaseDatabase.getInstance()
                .getReferenceFromUrl("https://todo-ba2f4.firebaseio.com/groups/"+group);

        listView = (ListView)findViewById(R.id.mem);



        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,R.layout.list_view_style, tasksArray );


        // listView.setAdapter(adapter);


        mRootRef.addChildEventListener(new ChildEventListener()
        {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s)
            {
                if(dataSnapshot.getKey().equals("members"))
                {
                    for (DataSnapshot child: dataSnapshot.getChildren())
                    {
                        //name of member.
                        if (child.getKey().equals(myPhoneNumber))
                        {
                            membersArray.add("אני");
                            membersPhonesArray.add(myPhoneNumber);

                        }
                        else
                        {
                            String value = child.getValue().toString();
                            membersArray.add(value);
                            membersPhonesArray.add(child.getKey());
                        }
                        adapter.notifyDataSetChanged();
                    }
                }

                //swipe code.//
                myAdapter=new MyAdapterMembers(members.this,names,phones, nameOfGroup, myPhoneNumber, nameOfGroupFullName);

                rv= (RecyclerView) findViewById(R.id.mem2);
                rv.setLayoutManager(new LinearLayoutManager(members.this));

                //swipe area.
                names.clear();
                int size=membersPhonesArray.size();
                int i=0;
                while (i<size)
                {
                    //names array.
                    String data=membersArray.get(i);
                    TaskObject p=new TaskObject();
                    p.setData(data);
                    p.setId(i);
                    names.add(p);

                    //phones array.
                    String thePhone=membersPhonesArray.get(i);
                    p=new TaskObject();
                    p.setData(thePhone);
                    p.setId(i);
                    phones.add(p);

                    ++i;
                }
                if(names.size()>0)
                {
                    rv.setAdapter(myAdapter);
                }

                ItemTouchHelper.Callback callback=new SwipeHelperMembers(myAdapter, nameOfGroup, manager,myPhoneNumber, members.this, nameOfGroupFullName);
                ItemTouchHelper helper=new ItemTouchHelper(callback);
                helper.attachToRecyclerView(rv);


            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }


    //functions for getting contact information.
    //1.
    public  void callContacts(View v, String group)
    {
        nameOfGroup=group;
        Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
        startActivityForResult(intent, PICK_CONTACT);
    }



    //2.
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {

        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode==PICK_CONTACT)
        {
            if (resultCode== ActionBarActivity.RESULT_OK)
            {
                Uri contactData = data.getData();
                Cursor c = getContentResolver().query(contactData, null, null,null,null);

                if(c.moveToFirst())
                {
                    String friendName = c.getString(c.getColumnIndexOrThrow(ContactsContract.Contacts.DISPLAY_NAME));
                    if (Integer.parseInt(c.getString(c.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0)
                    {
                        String friendNumber = getContactNumber(friendName);
                        fireDb.addUserToGroup(friendNumber, friendName,nameOfGroup, members.this);
                        //   goToViewGroups();
                    }
                }
            }
        }
    }

    //3.Find contact based on name.
    private String getContactNumber(String name)
    {
        String ret=null;
        ContentResolver cr = getContentResolver();
        Cursor cursor = cr.query(ContactsContract.Contacts.CONTENT_URI, null,
                "DISPLAY_NAME = '" + name + "'", null, null);
        if (cursor.moveToFirst())
        {
            String contactId =
                    cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));

            Cursor phones = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                    ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + contactId, null, null);
            while (phones.moveToNext())
            {
                String number = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                ret=number;
            }
            phones.close();
        }
        cursor.close();

        return ret;
    }


    public void refreshActivity()
    {
        Intent intent = getIntent();
        finish();
        startActivity(intent);
    }


}
