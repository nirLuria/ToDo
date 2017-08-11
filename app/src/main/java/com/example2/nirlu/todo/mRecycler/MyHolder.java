package com.example2.nirlu.todo.mRecycler;

import android.view.View;
import android.widget.TextView;



import android.content.ClipData;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.example22.nluria.missionList.R;

/**
 * Created by nluria on 3/22/2017.
 */

public class MyHolder extends RecyclerView.ViewHolder
{
    TextView nametxt;

    public MyHolder(View itemView)
    {
        super(itemView);
        this.nametxt= (TextView) itemView.findViewById(R.id.nameTxt);
    }
}
