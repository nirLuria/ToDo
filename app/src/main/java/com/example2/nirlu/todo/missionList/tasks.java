package com.example2.nirlu.todo.missionList;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;package com.example22.nluria.missionList;


import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example22.nluria.mDataObject.TaskObject;
import com.example22.nluria.mRecycler.MyAdapter;
import com.example22.nluria.mSwiper.SwipeHelper;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;


import java.util.ArrayList;

public class tasks extends AppCompatActivity
{
    private static ListView listView;
    TextView txtView;
    private static ListView completedListView;
    private ArrayList<String> tasksArray = new ArrayList<String>();
    private ArrayList<String> completedTasksArray = new ArrayList<String>();

    DataBaseHelper myDb;
    FireBaseHelper fireDb;
    Typeface buttonFont;
    private DatabaseReference mRootRef;
    String myPhoneNumber;
    String nameOfGroup, nameOfGroupFullName;
    private static Button btnAddTask;
    private static Button delete_tasks_button;
    EditText input;
    String inputTemp;


    ArrayList<TaskObject> tasksObj=new ArrayList<>();
    RecyclerView rv;
    MyAdapter myAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tasks2);
        myDb = new DataBaseHelper(this);
        fireDb= new FireBaseHelper();

        //get my phone number.
        Intent intent = getIntent();
        myPhoneNumber= intent.getStringExtra("myPhoneNumber");
        fireDb.initialize(myPhoneNumber);

        //print title of group on screen.
        nameOfGroup= intent.getStringExtra("nameOfGroup");
        TextView title= (TextView) findViewById(R.id.title);
        title.setText(nameOfGroup);

        nameOfGroupFullName= intent.getStringExtra("nameOfGroupFullName");
        System.out.println("my nameOfGroupFullName is "+nameOfGroupFullName);

        buttonFont= Typeface.createFromAsset(getAssets(), "tamir.ttf");
        title.setTypeface(buttonFont);

        btnAddTask= (Button)findViewById(R.id.Add_btn);


        String enter=intent.getStringExtra("enter");
        if ((enter!=null)&&(enter.equals("true")))
        {
            enterFunc();
        }


        //execute methods.
        optionBar();
        getTasks();
        getCompletedTasks();
        //  taskMenu();
        completedTaskMenu();
        addNewTask();
        deleteTasksOfGroupClickListener();


    }


    //display options menu bar.
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.main_view_groups, menu);
        return super.onCreateOptionsMenu(menu);
    }

    private void optionBar()
    {
        ActionBar ab = getSupportActionBar();
        ab.setLogo(R.drawable.logo_brown);
        ab.setDisplayUseLogoEnabled(true);
        ab.setDisplayHomeAsUpEnabled(false);
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
            case R.id.action_reAdd:
                ArrayList<String> tempArray = new ArrayList<String>();
                tempArray=completedTasksArray;
                //      fireDb.addNewTask("junk",nameOfGroup,nameOfGroupFullName, tasks2.this, input, true);
                fireDb.deleteAllCompletedTasksOfGroup(tasks2.this, nameOfGroup);


                for (int i=0; i<tempArray.size(); i++)
                {
                    if (!tempArray.get(i).equals("junk"))
                    {
                        fireDb.addNewTask(tempArray.get(i),nameOfGroup,nameOfGroupFullName, tasks2.this, input, true, false);
                        //fireDb.reAddTask(tempArray.get(i), nameOfGroup, nameOfGroupFullName, tasks2.this,  null);

                    }

                }


                //     deleteTask("junk",  nameOfGroup);
                //     deleteCompletedTask("", nameOfGroup);

                //    Toast.makeText(getApplicationContext(), "action_reAdd", Toast.LENGTH_SHORT).show();
                refreshActivity();
                return true;
            case R.id.action_deleteCompleted:
                fireDb.deleteAllCompletedTasksOfGroup(tasks2.this, nameOfGroup);
                refreshActivity();
                return true;
            case R.id.action_settings:
                Toast.makeText(getApplicationContext(), "setting icon is selected", Toast.LENGTH_SHORT).show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void enterFunc()
    {
        //create alert dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("הוסף מטלה חדשה:");

        builder.setMessage("");
        input= new EditText(this);

        //for pressed enter cases.
        input.addTextChangedListener(textWatcher);


        builder.setView(input);

        //set negative button.
        builder.setNegativeButton("ביטול", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        //set positive button.
        builder.setPositiveButton("אישור", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i)
            {
                //empty title.
                if (inputTemp.equals(""))
                {
                    emptyTaskInsertedAlertDialog();
                }
                else
                {
                    fireDb.addNewTask(inputTemp,nameOfGroup,nameOfGroupFullName, tasks2.this, input, true, true);
                    input.setText("");
                }

            }
        });


        final AlertDialog alertDialog= builder.create();

        enterButtonAddTask(alertDialog);
    }

    private void enterButtonAddTask(AlertDialog a)
    {
        AlertDialog alertDialog=a;

        a.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        a.show();
    }


    public void getTasks()
    {
        // String group=myPhoneNumber+"_"+nameOfGroup;
        System.out.println("nameOfGroupFullName: "+nameOfGroupFullName);
        String group=nameOfGroupFullName;

        mRootRef = FirebaseDatabase.getInstance()
                .getReferenceFromUrl("https://todo-ba2f4.firebaseio.com/groups/"+group);

        listView = (ListView)findViewById(R.id.tasksListView);



        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,R.layout.list_view_style, tasksArray );


        // listView.setAdapter(adapter);


        mRootRef.addChildEventListener(new ChildEventListener()
        {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s)
            {
                //         dataSnapshot=dataSnapshot.child("tasks");

                if(dataSnapshot.getKey().equals("tasks"))
                //   if (dataSnapshot!=null)
                {
                    for (DataSnapshot child: dataSnapshot.getChildren())
                    {
                        String value = child.getKey().toString();
                        if (!value.equals("junk"))
                        {
                            tasksArray.add(value);
                            adapter.notifyDataSetChanged();
                        }
                    }
                }

                //swipe code.//
                myAdapter=new MyAdapter(tasks2.this,tasksObj, nameOfGroup, myPhoneNumber, nameOfGroupFullName);

                rv= (RecyclerView) findViewById(R.id.rv);
                rv.setLayoutManager(new LinearLayoutManager(tasks2.this));

                //swipe area.
                tasksObj.clear();
                int size=tasksArray.size();
                int i=0;
                while (i<size)
                {
                    String data=tasksArray.get(i);
                    TaskObject p=new TaskObject();
                    p.setData(data);
                    p.setId(i);
                    tasksObj.add(p);

                    ++i;
                }
                if(tasksObj.size()>0)
                {
                    rv.setAdapter(myAdapter);
                }

//                String task = (String)listView.getItemAtPosition(p);
                //    task, nameOfGroup
                ItemTouchHelper.Callback callback=new SwipeHelper(myAdapter, nameOfGroup);
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

    /*
        public void onBackPressed()
        {
            startActivity(new Intent(this, viewGroups.class));
        }
    */
    public void getCompletedTasks()
    {
        //String group=myPhoneNumber+"_"+nameOfGroup;
        String group=nameOfGroupFullName;

        mRootRef = FirebaseDatabase.getInstance()
                .getReferenceFromUrl("https://todo-ba2f4.firebaseio.com/groups/"+group);

        listView = (ListView)findViewById(R.id.completedTasksListView);
        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,R.layout.list_adapter, completedTasksArray );
        ///


        ///



        listView.setAdapter(adapter);

        mRootRef.addChildEventListener(new ChildEventListener()
        {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s)
            {

                //         dataSnapshot=dataSnapshot.child("tasks");

                if(dataSnapshot.getKey().equals("completedTasks"))
                //   if (dataSnapshot!=null)
                {
                    for (DataSnapshot child: dataSnapshot.getChildren())
                    {
                        String value = child.getKey().toString();
                        if (!value.equals("junk"))
                        {
                            completedTasksArray.add(value);
                            adapter.notifyDataSetChanged();
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


    public void deleteTasksOfGroupClickListener()
    {
        delete_tasks_button = (Button)findViewById(R.id.delete_tasks_btn);
        delete_tasks_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                deleteAllTasksMessage();
            }
        });
    }

    public void deleteAllTasksMessage()
    {
        AlertDialog.Builder alert_builder = new AlertDialog.Builder(tasks2.this);
        alert_builder.setMessage("האם ברצונך למחוק את כל המטלות ברשימה?")
                .setCancelable(false)
                .setNegativeButton("ממש לא", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                })
                .setPositiveButton("בטח", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {


                        //delete all tasks in group.
                        fireDb.deleteAllTasksOfGroup(tasks2.this, nameOfGroup, nameOfGroupFullName);
                        /*
                        boolean isDeleted = myDb.deleteAllTasksOfGroup(nameOfGroup);
                        if (isDeleted == true)
                        {
                            Toast.makeText(tasks.this, "Tasks of " + nameOfGroup + " have been deleted successfully", Toast.LENGTH_LONG).show();
                            refreshActivity();
                        }
                        else
                        {
                            Toast.makeText(tasks.this, "didn't deleted", Toast.LENGTH_LONG).show();
                        }
                        */
                        //  finish();
                    }
                });
        AlertDialog alert = alert_builder.create();
        alert.setTitle("למחוק הכל?");
        alert.show();

    }


    public TextWatcher textWatcher = new TextWatcher()
    {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count)
        {
            inputTemp=s.toString();



            //detect if i pressed on enter.
            if (s.length()>0 && s.subSequence(s.length()-1, s.length()).toString().equalsIgnoreCase("\n"))
            {
                //empty title.
                if (!Character.isLetter(s.toString().charAt(0)))
                {
                    emptyFirstLetterInsertedAlertDialog();
                }
                else
                {
                    fireDb.addNewTask(s.subSequence(0, s.length()-1).toString(),nameOfGroup,nameOfGroupFullName, tasks2.this, input, false, true);
                    input.setText("");

                    Intent intent = getIntent();
                    intent.putExtra("enter", "true");
                    finish();
                    startActivity(intent);
                }


            }

            /*
            if (s.length()>0 && s.subSequence(s.length()-1, s.length()).toString().equalsIgnoreCase("\n"))
            {
                //empty title.
                if (input.getText().toString().equals(""))
                {
                    emptyTaskInsertedAlertDialog();
                }
                else
                {
                    fireDb.addNewTask(s.subSequence(0, s.length()-1).toString(),nameOfGroup, tasks2.this, input);
                    input.setText("");
                }
                System.out.println("enter pressed!");
            }
            */
        }

        @Override
        public void afterTextChanged(Editable s)
        {
        }
    };

    public void addNewTask()
    {
        //create alert dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("הוסף מטלה חדשה: ");

        builder.setMessage("");
        input= new EditText(this);

        //for pressed enter cases.
        input.addTextChangedListener(textWatcher);
        input.setText("");


        builder.setView(input);

        //set negative button.
        builder.setNegativeButton("ביטול", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                input.setText("");
                dialogInterface.dismiss();
            }
        });
        //set positive button.
        builder.setPositiveButton("אישור", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i)
            {
                //empty title.
                if (input.getText().toString().equals(""))
                {
                    emptyTaskInsertedAlertDialog();
                }
                else if((input.getText().toString().contains("/"))
                        || (input.getText().toString().contains("."))
                        || (input.getText().toString().contains("#"))
                        || (input.getText().toString().contains("$"))
                        || (input.getText().toString().contains("["))
                        || (input.getText().toString().contains("]")))

                {
                    badInputTaskInsertedAlertDialog();
                }
                else
                {
                    System.out.println("input.getText().toString(): "+input.getText().toString());
                    System.out.println("nameOfGroup: "+nameOfGroup);
                    System.out.println("nameOfGroupFullName: "+nameOfGroupFullName);
                    System.out.println("input: "+input );
                    System.out.println("context: "+tasks2.this );


                    fireDb.addNewTask(input.getText().toString(),nameOfGroup,nameOfGroupFullName, tasks2.this, input, true, true);
                    input.setText("");
                }

            }
        });


        final AlertDialog alertDialog= builder.create();

        buttonAddTask(alertDialog);

    }


    public void addNewTask(final Context context, final String funcNameOfGroup, final String funcNameOfGroupFullName, final String task)
    {
        //create alert dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("ערוך מטלה: ");

        builder.setMessage("");
        input= new EditText(context);

        //for pressed enter cases.
        input.addTextChangedListener(textWatcher);
        input.setText(task);


        builder.setView(input);

        //set negative button.
        builder.setNegativeButton("ביטול", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                input.setText("");
                dialogInterface.dismiss();
            }
        });
        //set positive button.
        builder.setPositiveButton("אישור", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i)
            {
                //empty title.
                if (input.getText().toString().equals(""))
                {
                    emptyTaskInsertedAlertDialog();
                }
                else if((input.getText().toString().contains("/"))
                        || (input.getText().toString().contains("."))
                        || (input.getText().toString().contains("#"))
                        || (input.getText().toString().contains("$"))
                        || (input.getText().toString().contains("["))
                        || (input.getText().toString().contains("]")))

                {
                    badInputTaskInsertedAlertDialog();
                }
                else if (input.getText().toString().equals(task))
                {
                    input.setText("");
                    dialogInterface.dismiss();
                }
                else
                {
                    fireDb= new FireBaseHelper();
                    fireDb.initialize(myPhoneNumber);
                    fireDb.addNewTask(input.getText().toString(),funcNameOfGroup,funcNameOfGroupFullName, context, input, true, true);
                    deleteTask(task, funcNameOfGroupFullName, false);
                    input.setText("");
                }

            }
        });


        final AlertDialog alertDialog= builder.create();

        buttonAddTask(alertDialog);
        alertDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        alertDialog.show();
    }


    public void emptyTaskInsertedAlertDialog()
    {
        AlertDialog.Builder alert_builder = new AlertDialog.Builder(tasks2.this);
        alert_builder.setMessage("בחייאת צור מטלה נורמלית")
                .setCancelable(false)
                .setNegativeButton("סבבה", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                });
        AlertDialog alert = alert_builder.create();
        alert.setTitle("אינך יכול ליצור מטלה ריקה!");
        alert.show();
    }

    public void badInputTaskInsertedAlertDialog()
    {
        AlertDialog.Builder alert_builder = new AlertDialog.Builder(tasks2.this);
        alert_builder.setMessage(". / # $ [ ]")
                .setCancelable(false)
                .setNegativeButton("סבבה", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                });
        AlertDialog alert = alert_builder.create();
        alert.setTitle("המטלה אינה יכולה להיות מורכבת מהסימנים הבאים:");
        alert.show();
    }

    public void emptyFirstLetterInsertedAlertDialog()
    {
        AlertDialog.Builder alert_builder = new AlertDialog.Builder(tasks2.this);
        alert_builder.setMessage("Please choose another task.")
                .setCancelable(false)
                .setNegativeButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                });
        AlertDialog alert = alert_builder.create();
        alert.setTitle("The task nust starts with a letter!");
        alert.show();
    }

    public void buttonAddTask(final AlertDialog a)
    {
        AlertDialog alertDialog=a;

        btnAddTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                //show keyboard.
                a.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
                a.show();
            }
        });

    }





    public void taskMenu()
    {
        listView = (ListView)findViewById(R.id.tasksListView);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,R.layout.list_view_style, tasksArray );
        listView.setAdapter(adapter);

        //go to tasks of group.
        listView.setOnItemClickListener
                (
                        new AdapterView.OnItemClickListener()
                        {
                            @Override
                            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l)
                            {
                                final int p=position;
                                AlertDialog.Builder builder = new AlertDialog.Builder(tasks2.this);

                                builder.setTitle("");
                                builder.setItems(new CharSequence[]
                                                {"Delete Task","Cancel"},
                                        new DialogInterface.OnClickListener()
                                        {
                                            public void onClick(DialogInterface dialogInterface, int which)
                                            {
                                                String task = (String)listView.getItemAtPosition(p);
                                                // The 'which' argument contains the index position
                                                // of the selected item
                                                switch (which)
                                                {

                                                    case 0:
                                                        deleteTask(task, nameOfGroup, true);
                                                        finish();
                                                        refreshActivity();
                                                        break;
                                                    case 1:
                                                        break;


                                                }
                                            }
                                        });
                                builder.create().show();


                                AlertDialog alert = builder.create();
                                alert.setTitle("Menu");
                                alert.show();
                                alert.dismiss();
                            }
                        }
                );
    }



    public void completedTaskMenu()
    {
        completedListView = (ListView)findViewById(R.id.completedTasksListView);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,R.layout.list_adapter, completedTasksArray );
        completedListView.setAdapter(adapter);

        //go to tasks of group.
        completedListView.setOnItemClickListener
                (
                        new AdapterView.OnItemClickListener()
                        {
                            @Override
                            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l)
                            {
                                final int p=position;
                                AlertDialog.Builder builder = new AlertDialog.Builder(tasks2.this,R.style.YourDialogStyle);

                                String theTask = (String)completedListView.getItemAtPosition(p);

                                builder.setTitle(theTask);
                                builder.setItems(new CharSequence[]
                                                {"מחק מטלה לצמיתות","החזר מטלה לרשימת מטלות לביצוע","מידע נוסף","ביטול"},
                                        new DialogInterface.OnClickListener()
                                        {
                                            public void onClick(DialogInterface dialogInterface, int which)
                                            {
                                                String task = (String)completedListView.getItemAtPosition(p);
                                                // The 'which' argument contains the index position
                                                // of the selected item
                                                switch (which)
                                                {

                                                    case 0:
                                                        deleteCompletedTask(task, nameOfGroup);
                                                        finish();
                                                        refreshActivity();
                                                        break;
                                                    case 1:
                                                        reAddTask(task, nameOfGroup);
                                                        finish();
                                                        refreshActivity();
                                                        break;
                                                    case 2:
                                                        Intent intent = new Intent("com.example2.nluria.missionList.ExtraInfo");
                                                        intent.putExtra("completedTask", task);
                                                        intent.putExtra("nameOfGroupFullName", nameOfGroupFullName);
                                                        intent.putExtra("myPhoneNumber", myPhoneNumber);
                                                        //finish();
                                                        startActivity(intent);
                                                    case 3:
                                                        break;

                                                }
                                            }
                                        });
                                builder.create().show();


                                AlertDialog alert = builder.create();
                                alert.setTitle("Menu");
                                alert.show();
                                alert.dismiss();
                            }
                        }
                );
    }



    public void deleteTask(String task, String nameOfGroupFullName, boolean comp)
    {

        mRootRef = FirebaseDatabase.getInstance()
                .getReferenceFromUrl("https://todo-ba2f4.firebaseio.com/groups/"+nameOfGroupFullName);
        Query query = mRootRef.child("tasks").orderByKey().equalTo(task);

        query.addListenerForSingleValueEvent(new ValueEventListener()
        {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot appleSnapshot: dataSnapshot.getChildren())
                {
                    appleSnapshot.getRef().removeValue();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                //  Log.e(TAG, "onCancelled", databaseError.toException());
            }
        });

        //add it to completed tasks.
        if (comp==true)
        {
            fireDb.addCompletedTask(task,nameOfGroup, tasks2.this, nameOfGroupFullName);
        }

    }


    public void deleteCompletedTask(String task, String nameOfGroup)
    {

        mRootRef = FirebaseDatabase.getInstance()
                .getReferenceFromUrl("https://todo-ba2f4.firebaseio.com/groups/"+nameOfGroupFullName);
        Query query = mRootRef.child("completedTasks").orderByKey().equalTo(task);

        query.addListenerForSingleValueEvent(new ValueEventListener()
        {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot appleSnapshot: dataSnapshot.getChildren())
                {
                    appleSnapshot.getRef().removeValue();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                //  Log.e(TAG, "onCancelled", databaseError.toException());
            }
        });

    }


    public void reAddTask(String task, String nameOfGroup)
    {

        mRootRef = FirebaseDatabase.getInstance()
                .getReferenceFromUrl("https://todo-ba2f4.firebaseio.com/groups/"+myPhoneNumber+"_"+nameOfGroup);
        Query query = mRootRef.child("completedTasks").orderByKey().equalTo(task);

        query.addListenerForSingleValueEvent(new ValueEventListener()
        {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot appleSnapshot: dataSnapshot.getChildren())
                {
                    appleSnapshot.getRef().removeValue();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                //  Log.e(TAG, "onCancelled", databaseError.toException());
            }
        });


        //add it to tasks.
        input.setText("");
        fireDb.reAddTask(task,nameOfGroup,nameOfGroupFullName,  tasks2.this, input);

    }



    public void deleteGroup(String group)
    {
        mRootRef = FirebaseDatabase.getInstance()
                .getReferenceFromUrl("https://todo-ba2f4.firebaseio.com");
        Query query = mRootRef.child("groups").orderByChild("nameOfGroup").equalTo(group);

        query.addListenerForSingleValueEvent(new ValueEventListener()
        {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
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

    }





    //show the data in an openned windows.
    //print to screen the groups.
    public void showMessage(String title, String message)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.show();

    }


    public void refreshActivity()
    {
        Intent intent = getIntent();
        finish();
        startActivity(intent);
    }


    public void onBackPressed()
    {
        Intent intent = new Intent(this, WelcomeScreen.class);
        intent.putExtra("firstLogin", "false");
        finish();
        startActivity(intent);
    }

}