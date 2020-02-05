package com.apps.jlee.boginder.Interfaces;

import com.apps.jlee.boginder.Adapters.PhotoAdapter;
import com.apps.jlee.boginder.R;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

public class ItemMoveCallback extends ItemTouchHelper.Callback
{
    private final ItemTouchHelperContract mAdapter;

    public ItemMoveCallback(ItemTouchHelperContract adapter)
    {
        mAdapter = adapter;
    }

    @Override
    public boolean isLongPressDragEnabled()
    {
        return true;
    }

    @Override
    public boolean isItemViewSwipeEnabled()
    {
        return false;
    }

    @Override
    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int i)
    {

    }

    @Override
    public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder)
    {
        if(viewHolder.itemView.findViewById(R.id.imageView).getTag().equals("Empty"))
            return makeMovementFlags(0, 0);

        int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.RIGHT | ItemTouchHelper.DOWN | ItemTouchHelper.LEFT;
        return makeMovementFlags(dragFlags, 0);
    }

    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target)
    {
        mAdapter.onRowMoved(viewHolder.getAdapterPosition(), target.getAdapterPosition());
        return true;
    }

    @Override
    public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int actionState)
    {
        if (actionState != ItemTouchHelper.ACTION_STATE_IDLE)
        {
            if (viewHolder instanceof PhotoAdapter.MyViewHolder)
            {
                PhotoAdapter.MyViewHolder myViewHolder = (PhotoAdapter.MyViewHolder) viewHolder;
                mAdapter.onRowSelected(myViewHolder);
            }
        }
        super.onSelectedChanged(viewHolder, actionState);
    }

    @Override
    public void clearView(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder)
    {
        super.clearView(recyclerView, viewHolder);

        if (viewHolder instanceof PhotoAdapter.MyViewHolder)
        {
            PhotoAdapter.MyViewHolder myViewHolder = (PhotoAdapter.MyViewHolder) viewHolder;
            mAdapter.onRowClear(myViewHolder);
        }
    }

    public interface ItemTouchHelperContract
    {
        void onRowMoved(int fromPosition, int toPosition);
        void onRowSelected(PhotoAdapter.MyViewHolder myViewHolder);
        void onRowClear(PhotoAdapter.MyViewHolder myViewHolder);
    }
}

