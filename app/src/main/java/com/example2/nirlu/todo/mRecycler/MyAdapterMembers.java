package com.example2.nirlu.todo.mRecycler;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.ArrayList;package com.example22.nluria.mRecycler;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example22.nluria.mDataObject.TaskObject;
import com.example22.nluria.missionList.FireBaseHelper;
import com.example22.nluria.missionList.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

/**
 * Created by nluria on 3/22/2017.
 */

public class MyAdapterMembers extends RecyclerView.Adapter<MyHolder>
{
    Context context;
    ArrayList<TaskObject> tasksObj;
    ArrayList<TaskObject> phones;

    MyAdapter adapter;
    String nameOfGroup;
    private DatabaseReference mRootRef, mRootRef2;
    FireBaseHelper fireDb;
    String myPhoneNumber;
    String nameOfGroupFullName;


    public MyAdapterMembers(Context c, ArrayList<TaskObject> arr, ArrayList<TaskObject> phones, String nmGrp, String pNum, String fullNum)
    {
        this.context = c;
        this.tasksObj = arr;
        this.nameOfGroup=nmGrp;
        this.myPhoneNumber=pNum;
        this.nameOfGroupFullName=fullNum;
        this.phones=phones;

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
    public void onBindViewHolder(MyHolder holder, int position)
    {
        holder.nametxt.setText(tasksObj.get(position).getData());
        holder.nametxt.setTextSize(14);
        holder.itemView.setBackgroundColor(Color.parseColor("#ffff99"));
        //  holder.nametxt.setBackgroundColor(Color.parseColor("#ffff99"));
    }



    @Override
    public int getItemCount()
    {
        return tasksObj.size();
    }




    public void deleteTheUser(final String member, Context context)
    {
        mRootRef = FirebaseDatabase.getInstance()
                .getReferenceFromUrl("https://todo-ba2f4.firebaseio.com/groups/"+nameOfGroupFullName);
        Query query = mRootRef.child("members").orderByKey().equalTo(member);

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


        //remove also from users.
        mRootRef2= FirebaseDatabase.getInstance()
                .getReferenceFromUrl("https://todo-ba2f4.firebaseio.com/users/"+member+"/groups/"+nameOfGroupFullName);
        System.out.println("mRootRef2 users: "+mRootRef2);
        mRootRef2.setValue(null);
    }


    public void deleteMember(int pos, String manager)
    {
        //GET ID
        TaskObject p=phones.get(pos);
        System.out.println("data to delete is "+p.getData());

        //delete from firebase.
        if (manager.equals(p.getData()))
        {
            Toast.makeText(context, "אינך יכול להסיר את עצמך מהקבוצה. אתה המנהל. כדי למחוק את הקבוצה בחר 'נהל קבוצה' -> 'מחק קבוצה'. ", Toast.LENGTH_LONG).show();
        }
        else
        {
            deleteTheUser(p.getData(), context);
        }


        this.notifyItemRemoved(pos);


    }


}
