package com.navigator;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.navigator.model.Place;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

public class HeaderAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final String TAG = HeaderAdapter.class.getSimpleName();
    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM = 1;
    private ArrayList<Place> mData;

    private LayoutInflater mLayoutInflater;

    private boolean mIsSpaceVisible = true;

    private WeakReference<ItemClickListener> mCallbackRef;

    public interface ItemClickListener {
        void onItemClicked(int position);
    }

    public static OnItemClickListener listener;

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public interface OnItemClickListener {
        void onItemClick(View itemView, int position);
    }


    public HeaderAdapter(Context ctx, ArrayList<Place> data, ItemClickListener listener) {
        mLayoutInflater = LayoutInflater.from(ctx);
        mData = data;
        mCallbackRef = new WeakReference<>(listener);
    }

    public class PlaceViewHolder extends SpaceViewHolder {

        CardView cv;
        TextView mTitleView;
        Button mButtonView;
        ImageView mIcon;
        int mPlacePosition;


        PlaceViewHolder(final View itemView) {
            super(itemView);
            Log.i(TAG, "PlaceViewHolder Class: Start compose card view ");
            cv = (CardView) itemView.findViewById(R.id.cv);
            mTitleView = (TextView) itemView.findViewById(R.id.text_view);
            mIcon = (ImageView) itemView.findViewById(R.id.iconPoint);
            mButtonView = (Button) itemView.findViewById(R.id.btn_add);
            mButtonView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Log.i(TAG, "PlaceViewHolder Button Clicked! Item:  " + mPlacePosition);
                    deleteItem(mPlacePosition);

                }
            });

            //Как и прежде, мы привязываемся из ViewHolder, но вызываем метод адаптера.
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (HeaderAdapter.listener != null) {
                        HeaderAdapter.listener.onItemClick(itemView, getLayoutPosition());
                    }
                }
            });


            Log.i(TAG, "PlaceViewHolder Class: Finish compose card view ");


        }
    }

    class SpaceViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        View mSpaceView;
        int mSpacePosition;

        public SpaceViewHolder(View itemView) {
            super(itemView);
            mSpaceView = itemView.findViewById(R.id.space);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            ItemClickListener callback = mCallbackRef != null ? mCallbackRef.get() : null;
            if (callback != null) {
                Log.i(TAG, "SpaceViewHolder onClick: Possition view: " + mSpacePosition);
                callback.onItemClicked(mSpacePosition);
            }

        }
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {

        if (viewType == TYPE_ITEM) {
            //inflate your layout and pass it to view holder
            View v = mLayoutInflater.inflate(R.layout.cardview, viewGroup, false);
            return new PlaceViewHolder(v);
        } else if (viewType == TYPE_HEADER) {
            View v = mLayoutInflater.inflate(R.layout.transparent_header_view, viewGroup, false);
            return new SpaceViewHolder(v);
        }
        return null;


    }


    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        Log.i(TAG, "onBindViewHolder Holdr:  " + position);
        if (holder instanceof PlaceViewHolder) {
            Place dataItem = getItem(position);
            ((PlaceViewHolder) holder).mTitleView.setText(dataItem.getAddress());
            ((PlaceViewHolder) holder).mPlacePosition = position;
            switch (dataItem.getType()) {
                case MainFragment.TYPE_MAIN_POINT:
                    Log.i(TAG, "Type ImagePoint: MAIN POINT");
                    ((PlaceViewHolder) holder).mIcon.setImageResource(R.mipmap.a48);
                    break;
                case MainFragment.TYPE_LAST_POIN:
                    Log.i(TAG, "Type ImagePoint: LAST POINT");
                    ((PlaceViewHolder) holder).mIcon.setImageResource(R.mipmap.b48);
                    break;
                case MainFragment.TYPE_MEDIAT_POIN:
                    Log.i(TAG, "Type ImagePoint: MEDIAT POINT");
                    ((PlaceViewHolder) holder).mIcon.setImageResource(R.mipmap.c48);
                    break;

            }


        } else if (holder instanceof SpaceViewHolder) {
            ((SpaceViewHolder) holder).mSpaceView.setVisibility(mIsSpaceVisible ? View.VISIBLE : View.GONE);
            ((SpaceViewHolder) holder).mSpacePosition = position;
        }
    }


    public void addItem(Place data, int index) {
        mData.add(index, data);
        notifyItemInserted(index);
    }

    public void deleteItem(int index) {

        final int ind = index - 1;
        Log.i(TAG, "DELETE Item form List, index: " + ind);
        


        mData.remove(ind);

        notifyItemRemoved(index);
    }

    @Override
    public int getItemCount() {
        return mData.size() + 1;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return TYPE_HEADER;
        }
        return TYPE_ITEM;
    }

    private Place getItem(int position) {
//        return mData.get(position - 1);
        return mData.get(position - 1);
    }


    public void hideSpace() {
        mIsSpaceVisible = false;
        notifyItemChanged(0);
    }

    public void showSpace() {
        mIsSpaceVisible = true;
        notifyItemChanged(0);
    }
}