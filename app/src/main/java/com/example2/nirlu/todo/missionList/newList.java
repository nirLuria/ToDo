package com.example2.nirlu.todo.missionList;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;



import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;

public class newList extends AppCompatActivity {

    DataBaseHelper myDb;
    FireBaseHelper fireDb;
    EditText title;
    Button btnAddList;
    Typeface buttonFont;
    TextView giveTitle;
    String myPhoneNumber;
    private DatabaseReference mRootRef;
    private static ListView listView;
    private ArrayList<String> groupsArray = new ArrayList<String>();
    private ArrayList<String> groupsArrayFullName = new ArrayList<String>();


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_list);
        myDb = new DataBaseHelper(this);
        fireDb = new FireBaseHelper();

        // get my phone number
        Intent intent = getIntent();
        myPhoneNumber= intent.getStringExtra("myPhoneNumber");


        fireDb.initialize(myPhoneNumber);

        buttonFont= Typeface.createFromAsset(getAssets(), "tamir.ttf");


        title = (EditText)findViewById(R.id.add_a_title);
        title.setTypeface(buttonFont);
        giveTitle= (TextView)findViewById(R.id.giveTitle);
        giveTitle.setTypeface(buttonFont);
        btnAddList = (Button)findViewById(R.id.addNewListBtn);


        //methods.
        optionBar();
        addNewList();

    }



    public void addNewList()
    {
        btnAddList.setOnClickListener(
                new View.OnClickListener()
                {

                    public void onClick(View v)
                    {

                        String str=title.getText().toString();

                        //empty title.
                        if (str.equals(""))
                        {
                            errorTitleInsertedAlertDialog("The title name can't be blank!");
                        }
                        //check firebase valid input.
                        else if (str.contains(".") ||str.contains("#") ||str.contains("$")
                                ||str.contains("[")||str.contains("]")  )
                        {
                            errorTitleInsertedAlertDialog("The title name should not contain '.', '#', '$', '[', or ']'");
                        }
                        else
                        {
                            fireDb.addNewList(str, newList.this,title);
                            goToTasksPage(str);
                        }
                    }
                }
        );
    }

    public void goToTasksPage(String nameOfGroup)
    {
        Intent intent = new Intent("com.example2.nluria.missionList.tasks2");
        //pass the name of the group to the next activity.
        intent.putExtra("nameOfGroup", nameOfGroup);
        intent.putExtra("nameOfGroupFullName", myPhoneNumber+"_" + nameOfGroup);
        intent.putExtra("myPhoneNumber", myPhoneNumber);
        startActivity(intent);
    }


    public void errorTitleInsertedAlertDialog(String msg)
    {
        AlertDialog.Builder alert_builder = new AlertDialog.Builder(newList.this);
        alert_builder.setMessage("Please choose another name.")
                .setCancelable(false)
                .setNegativeButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                });
        AlertDialog alert = alert_builder.create();
        alert.setTitle(msg);
        alert.show();
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
