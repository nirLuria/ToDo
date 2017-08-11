package com.example2.nirlu.todo.missionList;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
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
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

/**
 * Created by nluria on 4/4/2017.
 */

public class ManageGroup extends AppCompatActivity
{
    private DatabaseReference mRootRef2;
    DataBaseHelper myDb;
    FireBaseHelper fireDb;
    String nameOfGroup;
    String myPhoneNumber, nameOfGroupFullName;
    private DatabaseReference mRootRef;
    private static ListView listView;
    private ArrayList<String> membersArray = new ArrayList<String>();
    private ArrayList<String> membersPhonesArray = new ArrayList<String>();

    MyAdapterMembers myAdapter;
    private static Button delete_group_button;
    private static Button leave_group_button;
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
        setContentView(R.layout.manage_group);
        myDb = new DataBaseHelper(this);
        fireDb = new FireBaseHelper();

        //print title of group on screen.
        buttonFont= Typeface.createFromAsset(getAssets(), "tamir.ttf");
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
        deleteGroup();
        leaveGroup();
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


            case R.id.action_settings:
                Toast.makeText(getApplicationContext(), "setting icon is selected", Toast.LENGTH_SHORT).show();
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



    public void deleteGroup()
    {
        delete_group_button = (Button)findViewById(R.id.delete_groups);
        delete_group_button.setTypeface(buttonFont);
        delete_group_button.setOnClickListener(
                new View.OnClickListener()
                {
                    @Override
                    public void onClick(View view)
                    {
                        if (manager.equals(myPhoneNumber))
                        {
                            deleteGroup(nameOfGroup);
                            goBackToMain();
                            Toast.makeText(ManageGroup.this, "הרשימה נמחקה בהצלחה.", Toast.LENGTH_LONG).show();
                        }
                        else
                        {
                            Toast.makeText(ManageGroup.this, "אינך יכול למחוק את הקבוצה. אתה לא המנהל.", Toast.LENGTH_LONG).show();
                        }
                    }
                }
        );
    }


    public void deleteGroup(String group)
    {
        mRootRef = FirebaseDatabase.getInstance()
                .getReferenceFromUrl("https://todo-ba2f4.firebaseio.com");
        Query query = mRootRef.child("groups").orderByChild("nameOfGroup").equalTo(group);

        query.addListenerForSingleValueEvent(new ValueEventListener()
        {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                for (DataSnapshot appleSnapshot: dataSnapshot.getChildren())
                {
                    appleSnapshot.getRef().removeValue();
                    // finish();
                    //refreshActivity();

                }
                //refreshActivity();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                //  Log.e(TAG, "onCancelled", databaseError.toException());
            }
        });
        removeUserFromUsersList( group, myPhoneNumber);
    }



    public void leaveGroup()
    {
        leave_group_button = (Button)findViewById(R.id.leave_group);
        leave_group_button.setTypeface(buttonFont);
        leave_group_button.setOnClickListener(
                new View.OnClickListener()
                {
                    @Override
                    public void onClick(View view)
                    {
                        if (manager.equals(myPhoneNumber))
                        {
                            Toast.makeText(ManageGroup.this, "אינך יכול לצאת מהקבוצה. אתה המנהל. כדי לצאת - מחק את הקבוצה.", Toast.LENGTH_LONG).show();
                        }
                        else
                        {
                            mRootRef = FirebaseDatabase.getInstance()
                                    .getReferenceFromUrl("https://todo-ba2f4.firebaseio.com/groups/"+nameOfGroupFullName);
                            Query query = mRootRef.child("members").orderByKey().equalTo(myPhoneNumber);

                            query.addListenerForSingleValueEvent(new ValueEventListener()
                            {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot)
                                {
                                    for (DataSnapshot appleSnapshot: dataSnapshot.getChildren())
                                    {
                                        System.out.println("appleSnapshot is "+appleSnapshot);
                                        appleSnapshot.getRef().removeValue();
                                    }

                                    //remove also from users table.
                                    mRootRef2= FirebaseDatabase.getInstance()
                                            .getReferenceFromUrl("https://todo-ba2f4.firebaseio.com/users/"+myPhoneNumber+"/groups/"+nameOfGroupFullName);
                                    mRootRef2.setValue(null);
                                }



                                @Override
                                public void onCancelled(DatabaseError databaseError) {
                                    //  Log.e(TAG, "onCancelled", databaseError.toException());
                                }
                            });
                        }
                    }
                }
        );
    }







    public void removeUserFromUsersList(final String g, final String userNumber)
    {
        final String groupToDelete=userNumber+"_"+g;

        mRootRef = FirebaseDatabase.getInstance()
                .getReferenceFromUrl("https://todo-ba2f4.firebaseio.com/users/");

        mRootRef.addChildEventListener(new ChildEventListener()
        {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s)
            {
                {
                    for (DataSnapshot child: dataSnapshot.getChildren())
                    {
                        for (DataSnapshot grandsun: child.getChildren())
                        {
                            if (grandsun.getKey().equals(groupToDelete))
                            {
                                grandsun.getRef().removeValue();
                            }
                        }
                    }
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {}

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {}

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {}

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });
    }


    public void refreshActivity()
    {
        Intent intent = getIntent();
        finish();
        startActivity(intent);
    }


    public void goBackToMain()
    {
        Intent intent = new Intent(this,WelcomeScreen.class);

        intent.putExtra("myPhoneNumber", myPhoneNumber);
        intent.putExtra("firstLogin", "false");
        intent.putExtra("enter", "false");
        ManageGroup.this.finish();
        ManageGroup.this.startActivity(intent);
    }

}
