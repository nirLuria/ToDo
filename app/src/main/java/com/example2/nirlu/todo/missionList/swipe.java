package com.example2.nirlu.todo.missionList;


import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.EditText;

import java.util.ArrayList;




import com.example22.nluria.mDataBase.DBAdapter;
import com.example22.nluria.mDataObject.TaskObject;
import com.example22.nluria.mRecycler.MyAdapter;
import com.example22.nluria.mSwiper.SwipeHelper;

import java.util.ArrayList;

public class swipe extends AppCompatActivity {

    RecyclerView rv;
    MyAdapter adapter;
    EditText nameEditText;
    Button saveBtn,retrieveBtn;
    ArrayList<TaskObject> tasks=new ArrayList<>();
    String nameOfGroup;
    String nameOfGroupFullName;
    String phoneNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.swipe);


        rv= (RecyclerView) findViewById(R.id.rv);
        rv.setLayoutManager(new LinearLayoutManager(this));

        adapter=new MyAdapter(this,tasks, nameOfGroup, phoneNumber, nameOfGroupFullName);



        getTasks();

        ItemTouchHelper.Callback callback=new SwipeHelper(adapter);
        ItemTouchHelper helper=new ItemTouchHelper(callback);
        helper.attachToRecyclerView(rv);

    }


    //RETRIEVE
    private void getTasks()
    {
        tasks.clear();

        DBAdapter db=new DBAdapter(this);
        db.openDB();
        Cursor c=db.retrieve();

        while (c.moveToNext())
        {
            int id=c.getInt(0);
            String data=c.getString(1);

            TaskObject p=new TaskObject();
            p.setData(data);

            tasks.add(p);
        }
        db.closeDB();

        if(tasks.size()>0)
        {
            rv.setAdapter(adapter);
        }
    }

}










