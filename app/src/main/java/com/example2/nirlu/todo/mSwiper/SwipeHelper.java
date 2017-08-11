package com.example2.nirlu.todo.mSwiper;

package com.example22.nluria.mSwiper;

        import android.support.v7.widget.RecyclerView;
        import android.support.v7.widget.helper.ItemTouchHelper;

        import com.example22.nluria.mRecycler.MyAdapter;
        import com.example22.nluria.missionList.FireBaseHelper;
        import com.google.firebase.database.DatabaseReference;

/**
 * Created by nluria on 3/22/2017.
 */

public class SwipeHelper extends ItemTouchHelper.SimpleCallback
{
    MyAdapter adapter;
    String nameOfGroup;
    private DatabaseReference mRootRef;
    FireBaseHelper fireDb;
    String myPhoneNumber;

    public SwipeHelper(int dragDirs, int swipeDirs)
    {
        super(dragDirs, swipeDirs);
    }

    public SwipeHelper( MyAdapter adapter)
    {
        super(ItemTouchHelper.UP | ItemTouchHelper.LEFT,ItemTouchHelper.RIGHT);
        this.adapter = adapter;
    }

    public SwipeHelper( MyAdapter adapter, String nameOfGroup)
    {
        //the direction of deletion tasks: right to left.
        super(ItemTouchHelper.UP | ItemTouchHelper.RIGHT,ItemTouchHelper.LEFT);
        this.adapter = adapter;
        this.nameOfGroup=nameOfGroup;
    }

    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
        return false;
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction)
    {

        adapter.deletePlanet(viewHolder.getAdapterPosition());

    }




}
