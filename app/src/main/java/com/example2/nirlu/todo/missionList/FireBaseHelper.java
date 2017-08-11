package com.example2.nirlu.todo.missionList;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Created by nluria on 2/9/2017.
 */





public class FireBaseHelper extends AppCompatActivity
{
    private DatabaseReference  mRootRef,mRootRef2, mRootRefUsers;
    private static ListView mListView;
    private List<String> groupsArray = new ArrayList<String>();
    private String myPhoneNumber;


    public void initialize(String num)
    {
        myPhoneNumber=num;
    }

    public String getMyPhoneNumber()
    {
        return myPhoneNumber;
    }


    //  final String myPhoneNumber="0546443430";

    public void addNewList(String title, final Context context, final EditText editText)
    {
        final boolean[] enteredToMethod = new boolean[1];
        enteredToMethod[0]=false;

        //for now - only me create new lists.
        final String group = myPhoneNumber+"_" + title;
        final String t=title;

        mRootRef = FirebaseDatabase.getInstance()
                .getReferenceFromUrl("https://todo-ba2f4.firebaseio.com/groups");
        mRootRef.addListenerForSingleValueEvent(new ValueEventListener()
        {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                //check if the title of the group is already exists.
                if (dataSnapshot.child(group).getValue() != null)
                {
                    if (enteredToMethod[0]==false)
                    {
                        System.out.println(group + " is already exist");
                        enteredToMethod[0]=true;
                        titleNameExistsAlertDialog(context);
                        editText.setText("");
                    }
                }
                //write the data.
                else
                {
                    //members.
                    mRootRef=mRootRef.child(group);
                    Map<String, String> userData = new HashMap<String, String>();


                    //userData.put(myPhoneNumber, "מנהל");
                    userData.put(convertPhoneToIsraelFormat(myPhoneNumber), "מנהל");

                    mRootRef = mRootRef.child("members");
                    mRootRef.setValue(userData);

                    //name
                    mRootRef=FirebaseDatabase.getInstance()
                            .getReferenceFromUrl("https://todo-ba2f4.firebaseio.com/groups/"+group+"");
                    mRootRef=mRootRef.child("nameOfGroup");
                    mRootRef.setValue(t);

                    //manager of group.
                    mRootRef=FirebaseDatabase.getInstance()
                            .getReferenceFromUrl("https://todo-ba2f4.firebaseio.com/groups/"+group+"");
                    mRootRef=mRootRef.child("manager");
                    mRootRef.setValue(myPhoneNumber);

                    //junk completed task. to avoid Null date bug.
                    addCompletedTask("junk",t, FireBaseHelper.this, myPhoneNumber+"_"+t);


                    enteredToMethod[0]=true;
                    newGroupEnteredSuccessfullyAlertDialog(context);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError)
            {

            }
        });

        addUserToUsersList(group, myPhoneNumber);
    }


    public void addUserToUsersList(final String group, final String userNumber)
    {
        final boolean[] enteredToMethod = new boolean[1];
        enteredToMethod[0]=false;


        mRootRefUsers = FirebaseDatabase.getInstance()
                .getReferenceFromUrl("https://todo-ba2f4.firebaseio.com/users");
        mRootRefUsers.addListenerForSingleValueEvent(new ValueEventListener()
        {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                //check if the user is already exists.
                if (dataSnapshot.child(userNumber).getValue() != null)
                {
                    mRootRefUsers = mRootRefUsers.child(userNumber);
                    mRootRefUsers = mRootRefUsers.child("groups");
                    mRootRefUsers=mRootRefUsers.child(group);
                    mRootRefUsers.setValue("junk");
                }
                else
                {
                    mRootRefUsers=mRootRefUsers.child(userNumber);
                    Map<String, String> userData = new HashMap<String, String>();
                    userData.put(group, "junk");
                    mRootRefUsers = mRootRefUsers.child("groups");
                    mRootRefUsers.setValue(userData);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError)
            {}
        });
    }





    public void addNewTask(final String data, String g, final String nameOfGroupFullName, final Context context, final EditText editText, final boolean refreshAct, final boolean check)
    {
        System.out.println("data is: "+data+"!");


        final String nameOfGroup=g;

        //   final String group = myPhoneNumber+"_" + g;
        final String group = nameOfGroupFullName;

        final DatabaseReference mRootX   = FirebaseDatabase.getInstance()
                .getReferenceFromUrl("https://todo-ba2f4.firebaseio.com/groups/"+group);
        mRootX.addListenerForSingleValueEvent(new ValueEventListener()
        {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                System.out.println("dataSnapshot is:  "+dataSnapshot.child("tasks").child(data));
                //check if the task of the group is already exists.
                if (dataSnapshot.child("tasks").getValue() != null)
                {
                    if (dataSnapshot.child("tasks").child(data).getValue() != null)
                    {
                        System.out.println(data + " ,1 this data is already exist");
                        taskExistsAlertDialog(context);
                        editText.setText("");

                    }
                    else if ((dataSnapshot.child("completedTasks").getValue() != null) &&
                            ((dataSnapshot.child("completedTasks").child(data).getValue() != null)) &&
                            (check==true))
                    {

                        System.out.println(data + " ,2 this data is already exist");
                        taskExistsAlertDialog(context);
                        editText.setText("");
                    }

                    //write the data.
                    else
                    {
                        System.out.println("nor "+data);
                        //tasks.
                        Map<String, String> userData = new HashMap<String, String>();
                        userData.put(data, "1");
                        DatabaseReference mRootY = mRootX.child("tasks");
                        mRootY=mRootY.child(data);
                        mRootY.setValue("junk");

                        messageAlertDialog("מטלה חדשה התווספה בהצלחה!", context);
                        if (refreshAct==true)
                        {
                            //refreshTasksActivity(context, nameOfGroup);
                            refreshTasksActivityWithFullName(context, nameOfGroup, nameOfGroupFullName);
                        }
                    }
                }
                //completed list contains the data.
                else if ((dataSnapshot.child("completedTasks").getValue() != null) &&
                        ((dataSnapshot.child("completedTasks").child(data).getValue() != null)))
                {

                    System.out.println(data + " ,this data is already exist");
                    taskExistsAlertDialog(context);
                    editText.setText("");
                }
                //write the data.
                else
                {
                    //add junk task.
                    Map<String, String> userData = new HashMap<String, String>();
                    userData.put("junk", "1");
                    DatabaseReference mRootY = mRootX.child("tasks");
                    mRootY.setValue(userData);

                    //tasks.
                    userData.put(data, "1");
                    mRootY = mRootX.child("tasks");
                    mRootY.setValue(userData);



                    messageAlertDialog("מטלה חדשה התווספה בהצלחה!", context);

                    if (refreshAct==true)
                    {
                        //    refreshTasksActivity(context, nameOfGroup);
                        refreshTasksActivityWithFullName(context, nameOfGroup, nameOfGroupFullName);

                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError)
            {

            }
        });
    }


    public void reAddTask(final String data, String g, final String nameOfGroupFullName, final Context context, final EditText editText)
    {
        final String nameOfGroup=g;

        // final String group = myPhoneNumber+"_" + g;
        final String group = nameOfGroupFullName;


        System.out.println(":::::::::::::::::"+nameOfGroup+","+group+","+data);


        mRootRef = FirebaseDatabase.getInstance()
                .getReferenceFromUrl("https://todo-ba2f4.firebaseio.com/groups/"+group);
        mRootRef.addListenerForSingleValueEvent(new ValueEventListener()
        {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                //check if the task of the group is already exists.
                if (dataSnapshot.child("tasks").getValue() != null)
                {
                    if (dataSnapshot.child("tasks").child(data).getValue() != null)
                    {
                        System.out.println(data + " ,this data is already exist");
                        taskExistsAlertDialog(context);
                        editText.setText("");
                    }


                    //write the data.
                    else
                    {
                        //tasks.
                        Map<String, String> userData = new HashMap<String, String>();
                        userData.put(data, "1");
                        mRootRef = mRootRef.child("tasks");
                        mRootRef=mRootRef.child(data);
                        mRootRef.setValue("junk");

                        messageAlertDialog("מטלה חדשה התווספה בהצלחה!", context);
                        //    refreshTasksActivity(context, nameOfGroup);
                        refreshTasksActivityWithFullName(context, nameOfGroup, nameOfGroupFullName);

                    }
                }

                //write the data.
                else
                {
                    //tasks.
                    Map<String, String> userData = new HashMap<String, String>();
                    userData.put(data, "1");
                    mRootRef = mRootRef.child("tasks");
                    mRootRef.setValue(userData);

                    messageAlertDialog("מטלה חדשה התווספה בהצלחה!", context);
                    refreshTasksActivityWithFullName(context, nameOfGroup, nameOfGroupFullName);

                    //    refreshTasksActivity(context, nameOfGroup);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError)
            {

            }
        });
    }



    public void addUserToGroup(String friendNumber, String friendName, String g, final Context context)
    {
        final String nameOfGroup=g;

        final String group = myPhoneNumber+"_" + g;
        final String number = convertPhoneToIsraelFormat(friendNumber);
        final String nameOfFriend = friendName;

        mRootRef = FirebaseDatabase.getInstance()
                .getReferenceFromUrl("https://todo-ba2f4.firebaseio.com/groups/"+group);
        mRootRef.addListenerForSingleValueEvent(new ValueEventListener()
        {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                //check if the task of the group is already exists.
                if (dataSnapshot.child("members").child(number).getValue() != null)
                {
                    //   System.out.println(number + " ,this user is already on this group");
                    //   Toast.makeText(getApplicationContext(), number+", this user is already on this group", Toast.LENGTH_SHORT).show();
                    messageAlertDialog(nameOfFriend+" כבר נמצא בקבוצה ", context);
                }

                //add the user.
                else
                {
                    /*
                    Toast.makeText(getApplicationContext(), "New user was inserted to "+nameOfGroup+" successfully", Toast.LENGTH_SHORT).show();

                    mRootRef = FirebaseDatabase.getInstance()
                            .getReferenceFromUrl("https://todo-ba2f4.firebaseio.com/groups/"+group);
                    mRootRef = mRootRef.child("testtttttt");

                    mRootRef = FirebaseDatabase.getInstance()
                            .getReferenceFromUrl("https://todo-ba2f4.firebaseio.com/groups/"+group);
*/

                    /////
                    mRootRef = mRootRef.child("members");
                    mRootRef=mRootRef.child(number);
                    mRootRef.setValue(nameOfFriend);
                    messageAlertDialog(number+" הצטרף לקבוצה בהצלחה! " + group, context);

                    addUserToUsersList(group, number);

                    refreshMembersActivityWithFullName(context, nameOfGroup,  group);

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });


    }




    public void addCompletedTask(String d, String g,final Context context, final String nameOfGroupFullName)
    {
        final String nameOfGroup=g;

        //   final String group = myPhoneNumber+"_" + g;
        final String group = nameOfGroupFullName ;
        final String data = d;

        System.out.println(":::::::::::::::::"+nameOfGroup+","+group+","+data);


        mRootRef = FirebaseDatabase.getInstance()
                .getReferenceFromUrl("https://todo-ba2f4.firebaseio.com/groups/"+group);
        mRootRef.addListenerForSingleValueEvent(new ValueEventListener()
        {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                //check if the task of the group is already exists.
                if (dataSnapshot.child("completedTasks").getValue() != null)
                {
                    if (dataSnapshot.child("completedTasks").child(data).getValue() != null)
                    {
                        System.out.println(data + " ,this data is already exists");
                        taskExistsAlertDialog(context);
                    }
                    //write the data.
                    else
                    {
                        //tasks.
                        Map<String, String> userData = new HashMap<String, String>();
                        userData.put(data, "1");

                        mRootRef = mRootRef.child("completedTasks");
                        mRootRef=mRootRef.child(data);

                        DatabaseReference mRootRef1 = mRootRef.child("completed by");
                        mRootRef1.setValue(myPhoneNumber);

                        DatabaseReference mRootRef2 = mRootRef.child("date");
                        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                        Date date = new Date();
                        System.out.println(dateFormat.format(date)); //2016/11/16 12:08:43
                        mRootRef2.setValue(dateFormat.format(date));


                        if (!data.equals("junk"))
                        {
                            messageAlertDialog("המשימה בוצעה בהצלחה!", context);
                            //refreshTasksActivity(context, nameOfGroup);
                            refreshTasksActivityWithFullName(context, nameOfGroup, nameOfGroupFullName);
                        }

                    }
                }
                //write the data.
                else
                {
                    //tasks.
                    Map<String, String> userData = new HashMap<String, String>();
                    userData.put(data, "1");
                    mRootRef = mRootRef.child("completedTasks");
                    //    mRootRef.setValue(userData);
                    mRootRef=mRootRef.child(data);
                    mRootRef=mRootRef.child("completed by");
                    mRootRef.setValue(myPhoneNumber);


                    //messageAlertDialog("task was completed successfully", context);

                    if (!data.equals("junk"))
                    {
                        messageAlertDialog("המשימה בוצעה בהצלחה!", context);

                        //  refreshTasksActivity(context, nameOfGroup);
                        refreshTasksActivityWithFullName(context, nameOfGroup, nameOfGroupFullName);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError)
            {

            }
        });
    }


/*
    public void deleteTask(String task,String nameOfGroup, Context context)
    {
        mRootRef = FirebaseDatabase.getInstance()
                .getReferenceFromUrl("https://todo-ba2f4.firebaseio.com/groups/"+myPhoneNumber+"_"+nameOfGroup+"/tasks/");
        mRootRef.setValue(null);
        String str="The task was deleted successfully";
        messageAlertDialog( str,  context);

    }
*/

    public void deleteAllGroups(Context context)
    {
        //delete all
        mRootRef = FirebaseDatabase.getInstance()
                .getReferenceFromUrl("https://todo-ba2f4.firebaseio.com/groups");
        mRootRef.setValue(null);
        mRootRef = FirebaseDatabase.getInstance()
                .getReferenceFromUrl("https://todo-ba2f4.firebaseio.com/users");
        mRootRef.setValue(null);
        String str="All groups were deleted successfully";
        messageAlertDialog( str,  context);

    }

    public void deleteAllTasksOfGroup(Context context, String group, String nameOfGroupFullName)
    {
        /*
        mRootRef = FirebaseDatabase.getInstance()
                .getReferenceFromUrl("https://todo-ba2f4.firebaseio.com/groups/"+myPhoneNumber+"_"+group+"/tasks");
        mRootRef.setValue(null);

        mRootRef = FirebaseDatabase.getInstance()
                .getReferenceFromUrl("https://todo-ba2f4.firebaseio.com/groups/"+myPhoneNumber+"_"+group+"/completedTasks");
        mRootRef.setValue(null);
*/

        mRootRef = FirebaseDatabase.getInstance()
                .getReferenceFromUrl("https://todo-ba2f4.firebaseio.com/groups/"+nameOfGroupFullName);
        Query query = mRootRef.child("completedTasks").orderByKey();

        query.addListenerForSingleValueEvent(new ValueEventListener()
        {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot appleSnapshot: dataSnapshot.getChildren())
                {
                    System.out.println("appleSnapshot.getRef(): "+appleSnapshot.getRef().getKey());
                    if (!appleSnapshot.getRef().getKey().equals("junk"))
                    {
                        appleSnapshot.getRef().removeValue();
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                //  Log.e(TAG, "onCancelled", databaseError.toException());
            }
        });

        mRootRef2 = FirebaseDatabase.getInstance()
                .getReferenceFromUrl("https://todo-ba2f4.firebaseio.com/groups/"+nameOfGroupFullName);
        Query query2 = mRootRef2.child("tasks").orderByKey();

        query2.addListenerForSingleValueEvent(new ValueEventListener()
        {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot appleSnapshot: dataSnapshot.getChildren())
                {
                    System.out.println("appleSnapshot.getRef(): "+appleSnapshot.getRef().getKey());
                    if (!appleSnapshot.getRef().getKey().equals("junk"))
                    {
                        appleSnapshot.getRef().removeValue();
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                //  Log.e(TAG, "onCancelled", databaseError.toException());
            }
        });


        //refreshTasksActivity(context, group);
        refreshTasksActivityWithFullName(context, group,  nameOfGroupFullName);
    }





    public void deleteAllCompletedTasksOfGroup(Context context, String group)
    {
        mRootRef = FirebaseDatabase.getInstance()
                .getReferenceFromUrl("https://todo-ba2f4.firebaseio.com/groups/"+myPhoneNumber+"_"+group+"/completedTasks");


       /*
        mRootRef.setValue(null);
*/
        mRootRef.addListenerForSingleValueEvent(new ValueEventListener()
        {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                for (DataSnapshot child: dataSnapshot.getChildren())
                {
                    String value = child.getKey().toString();
                    if (!value.equals("junk"))
                    {
                        System.out.println("``````````````");
                        System.out.println("child: "+child);
                        child.getRef().removeValue();
                    }

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });

        refreshTasksActivity(context, group);
    }




    public void titleNameExistsAlertDialog(Context context)
    {
        //print error message to screen.
        AlertDialog.Builder alert_builder = new AlertDialog.Builder(context);
        alert_builder.setMessage("Please choose another name.")
                .setCancelable(false)
                .setNegativeButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                });
        AlertDialog alert = alert_builder.create();
        alert.setTitle("This title name is already exists!");
        alert.show();
    }


    public void taskExistsAlertDialog(Context context)
    {
        //print error message to screen.
        AlertDialog.Builder alert_builder = new AlertDialog.Builder(context);
        alert_builder.setMessage("אנא הוסף מטלה אחרת.")
                .setCancelable(false)
                .setNegativeButton("סבבה", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                });
        AlertDialog alert = alert_builder.create();
        alert.setTitle("קיימת כבר מטלה כזו!");
        alert.show();
    }


    public void newGroupEnteredSuccessfullyAlertDialog(Context context)
    {
        // Toast.makeText(context, "New list was inserted successfully", Toast.LENGTH_LONG).show();
        Toast.makeText(context, "רשימת מטלות חדשה נוצרה בהצלחה!", Toast.LENGTH_LONG).show();
        ((Activity)context).finish();
    }


    public void messageAlertDialog(String str, Context context)
    {
        System.out.println("context is: "+context);
        System.out.println("str is: "+str);

        Toast.makeText(context, str, Toast.LENGTH_LONG).show();
    }


    public void refreshTasksActivity(Context context, String nameOfGroup)
    {
        Intent intent = new Intent(context,tasks2.class);

        //pass the name of the group to the next activity.
        intent.putExtra("nameOfGroup", nameOfGroup);
        intent.putExtra("myPhoneNumber", myPhoneNumber);
        intent.putExtra("enter", "false");
        ((Activity)context).finish();
        context.startActivity(intent);
    }

    public void refreshTasksActivityWithFullName(Context context, String nameOfGroup, String nameOfGroupFullName)
    {
        Intent intent = new Intent(context,tasks2.class);

        //pass the name of the group to the next activity.
        intent.putExtra("nameOfGroup", nameOfGroup);
        intent.putExtra("nameOfGroupFullName", nameOfGroupFullName);
        intent.putExtra("myPhoneNumber", myPhoneNumber);
        intent.putExtra("enter", "false");
        ((Activity)context).finish();
        context.startActivity(intent);
    }

    public void refreshMembersActivityWithFullName(Context context, String nameOfGroup, String nameOfGroupFullName)
    {
        Intent intent = new Intent(context,members.class);

        //pass the name of the group to the next activity.
        intent.putExtra("nameOfGroup", nameOfGroup);
        intent.putExtra("nameOfGroupFullName", nameOfGroupFullName);
        intent.putExtra("myPhoneNumber", myPhoneNumber);
        intent.putExtra("enter", "false");
        ((Activity)context).finish();
        context.startActivity(intent);
    }





    public String convertPhoneToIsraelFormat(String myPhoneNumber)
    {
        String ret="";
        if((myPhoneNumber.charAt(0)=='+')
                &&(myPhoneNumber.charAt(1)=='9')
                &&(myPhoneNumber.charAt(2)=='7')
                &&(myPhoneNumber.charAt(3)=='2'))
        {
            ret="0"+myPhoneNumber.substring(4);
            System.out.println("ret is: "+ret);
            return ret;
        }
        else
        {
            System.out.println("ret is usual");
            return myPhoneNumber;
        }
    }

}







