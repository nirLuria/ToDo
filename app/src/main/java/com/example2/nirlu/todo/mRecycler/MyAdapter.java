package com.example2.nirlu.todo.mRecycler;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;



import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.example22.nluria.mDataObject.TaskObject;
import com.example22.nluria.missionList.FireBaseHelper;
import com.example22.nluria.missionList.R;
import com.example22.nluria.missionList.WelcomeScreen;
import com.example22.nluria.missionList.tasks2;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import static android.R.id.input;

/**
 * Created by nluria on 3/22/2017.
 */

public class MyAdapter extends RecyclerView.Adapter<MyHolder>
{
    Context context;
    ArrayList<TaskObject> tasksObj;
    MyAdapter adapter;
    String nameOfGroup;
    private DatabaseReference mRootRef;
    FireBaseHelper fireDb;
    String myPhoneNumber;
    String nameOfGroupFullName;
    AdapterView.OnItemLongClickListener itemLongClickListener;
    private static ListView listView;
    tasks2 tasks2;

    public MyAdapter(Context c, ArrayList<TaskObject> arr, String nmGrp, String pNum, String fullNum)
    {
        this.context = c;
        this.tasksObj = arr;
        this.nameOfGroup=nmGrp;
        this.myPhoneNumber=pNum;
        this.nameOfGroupFullName=fullNum;

    }

    @Override
    public MyHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View v= LayoutInflater.from(parent.getContext()).inflate(R.layout.model,parent,false);
        MyHolder holder=new MyHolder(v);
        return holder;
    }

    //configurations of tasks view, like color and size.
    @Override
    public void onBindViewHolder(MyHolder holder, final int position)
    {
        final String task =tasksObj.get(position).getData();
        holder.nametxt.setText(task);
        holder.nametxt.setTextSize(14);
        holder.itemView.setBackgroundColor(Color.parseColor("#ffff99"));
        //  holder.nametxt.setBackgroundColor(Color.parseColor("#ffff99"));


        holder.nametxt.setOnLongClickListener(new View.OnLongClickListener()
        {
            public boolean onLongClick(View view)
            {
                tasks2=new tasks2();
                tasks2.addNewTask(context,nameOfGroup, nameOfGroupFullName, task);
                /*
                AlertDialog.Builder builder = new AlertDialog.Builder(context,R.style.YourDialogStyle);
                builder.setItems(new CharSequence[]
                                //   {"Watch tasks", "Watch members", "Add a friend to list", "Delete Group","Cancel"},
                                {"ערוך "+task, "ביטול"},
                        new DialogInterface.OnClickListener()
                        {
                            public void onClick(DialogInterface dialogInterface, int which)
                            {
                                switch (which)
                                {
                                    case 0:


                                        tasks2=new tasks2();
                                        tasks2.addNewTask(context,nameOfGroup, nameOfGroupFullName, task);
                                        break;
                                    case 1:
                                        Toast.makeText(context,"ביטול",Toast.LENGTH_LONG).show();
                                        break;
                                }
                            }
                        });
                builder.create().show();


                AlertDialog alert = builder.create();
                alert.setTitle("Menu");
                alert.show();
                alert.dismiss();
                //
*/
                return true;
            }
        });

        holder.nametxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                //for now do nothing.

                //Toast.makeText(context,"short",Toast.LENGTH_LONG).show();

            }
        });


    }




    @Override
    public int getItemCount()
    {
        return tasksObj.size();
    }

    public void deletePlanet(int pos)
    {
        //GET ID
        TaskObject p=tasksObj.get(pos);
        System.out.println("data to delete is "+p.getData());

        //delete from firebase.
        deleteTask(p.getData(), nameOfGroup, context);
        //  Toast.makeText(context,"Deleted "+ p.getData(),Toast.LENGTH_SHORT).show();


        /*
        //DELETE FROM DB
        DBAdapter db=new DBAdapter(context);
        db.openDB();
        if(db.delete(p.getId()))
        {
            tasksObj.remove(pos);
        }else
        {
            Toast.makeText(context,"Unable To Delete",Toast.LENGTH_SHORT).show();
        }

        db.closeDB();

        */

        this.notifyItemRemoved(pos);


    }


    public void deleteTask(String task, String nameOfGroup, Context context)
    {
        //  mRootRef = FirebaseDatabase.getInstance()
        //           .getReferenceFromUrl("https://todo-ba2f4.firebaseio.com/groups/"+myPhoneNumber+"_"+nameOfGroup);
        mRootRef = FirebaseDatabase.getInstance()
                .getReferenceFromUrl("https://todo-ba2f4.firebaseio.com/groups/"+nameOfGroupFullName);
        System.out.println("mRootRef is  "+mRootRef);
        Query query = mRootRef.child("tasks").orderByKey().equalTo(task);

        query.addListenerForSingleValueEvent(new ValueEventListener()
        {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
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
        fireDb= new FireBaseHelper();
        fireDb.initialize(myPhoneNumber);
        fireDb.addCompletedTask(task,nameOfGroup, context, nameOfGroupFullName);

    }



}
