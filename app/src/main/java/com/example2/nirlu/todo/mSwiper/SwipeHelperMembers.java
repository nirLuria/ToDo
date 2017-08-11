package com.example2.nirlu.todo.mSwiper;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;package com.example22.nluria.mSwiper;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.widget.Toast;

import com.example22.nluria.mRecycler.MyAdapterMembers;
import com.example22.nluria.missionList.FireBaseHelper;
import com.example22.nluria.missionList.members;
import com.google.firebase.database.DatabaseReference;

/**
 * Created by nluria on 3/22/2017.
 */

public class SwipeHelperMembers extends ItemTouchHelper.SimpleCallback
{
    MyAdapterMembers adapter;
    String nameOfGroup;
    private DatabaseReference mRootRef;
    FireBaseHelper fireDb;
    String myPhoneNumber, nameOfGroupFullName;
    String manager;
    Context context;

    public SwipeHelperMembers(int dragDirs, int swipeDirs)
    {
        super(dragDirs, swipeDirs);
    }

    public SwipeHelperMembers( MyAdapterMembers adapter)
    {
        super(ItemTouchHelper.UP | ItemTouchHelper.LEFT,ItemTouchHelper.RIGHT);
        this.adapter = adapter;
    }

    public SwipeHelperMembers( MyAdapterMembers adapter, String nameOfGroup, String manager,
                               String myPhoneNumber, Context context, String nameOfGroupFullName)
    {
        //the direction of deletion tasks: right to left.
        super(ItemTouchHelper.UP | ItemTouchHelper.RIGHT,ItemTouchHelper.LEFT);
        this.adapter = adapter;
        this.nameOfGroup=nameOfGroup;
        this.nameOfGroupFullName=nameOfGroupFullName;
        this.manager = manager;
        this.myPhoneNumber=myPhoneNumber;
        this.context=context;
    }

    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target)
    {
        return false;
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction)
    {
        if (manager.equals(myPhoneNumber))
        {
            adapter.deleteMember(viewHolder.getAdapterPosition(),manager );
            refreshActivity(context);

        }

        //i'm not the manager- no permissions to delete users.
        else
        {
            Toast.makeText(context, "לא להיות חוצפן! רק מנהל יכול להעיף אנשים מהקבוצה.", Toast.LENGTH_LONG).show();
            refreshActivity(context);
        }

    }

    public void refreshActivity(Context context)
    {
        Intent intent = new Intent(context,members.class);
        ((Activity)context).finish();
        intent.putExtra("nameOfGroup", nameOfGroup);
        intent.putExtra("myPhoneNumber", myPhoneNumber);
        intent.putExtra("nameOfGroupFullName", nameOfGroupFullName);
        context.startActivity(intent);



    }


}
