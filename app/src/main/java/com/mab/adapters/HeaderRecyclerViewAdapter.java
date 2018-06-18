package com.mab.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

/**
 * Created by Jaideep.Lakshminaray on 27-02-2017.
 */

public abstract class HeaderRecyclerViewAdapter<T extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<T> {
    public static final int TYPE_HEADER = Integer.MIN_VALUE;
    public static final int TYPE_FOOTER = Integer.MIN_VALUE + 1;
    public static final int TYPE_ADAPTEE_OFFSET = 2;

    @Override
    public T onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_HEADER) {
            return onCreateHeaderViewHolder(parent, viewType);
        } else if (viewType == TYPE_FOOTER) {
            return onCreateFooterViewHolder(parent, viewType);
        }
        return onCreateBasicItemViewHolder(parent, viewType - TYPE_ADAPTEE_OFFSET);
    }

    @Override
    public int getItemCount() {
        int itemCount = getBasicItemCount();
        if (useHeader()) {
            itemCount += 1;
        }
        if (useFooter()) {
            itemCount += 1;
        }
        return itemCount;
    }


    @Override
    public int getItemViewType(int position) {
        if (position == 0 && useHeader()) {
            return TYPE_HEADER;
        }

        if (useHeader() && position > getBasicItemCount() && useFooter())
            return TYPE_FOOTER;
        else if (!useHeader() && position >= getBasicItemCount() && useFooter())
            return TYPE_FOOTER;

        int basicItemType = getBasicItemType(position - (useHeader() ? 1 : 0));

        //This is not needed, the above condition satisfies that. If we will use this then when we will use both header and footer, then there will be problem of holder cast
        /*if (position >= getBasicItemCount() && useFooter()) {
            return TYPE_FOOTER;
        }*/

        if (basicItemType >= Integer.MAX_VALUE - TYPE_ADAPTEE_OFFSET) {
            new IllegalStateException(
                    "HeaderRecyclerViewAdapter offsets your BasicItemType by " + TYPE_ADAPTEE_OFFSET + ".");
        }

        return TYPE_ADAPTEE_OFFSET;
    }

    public void onBindViewHolder(T holder, int position) {
        if (position == 0 && holder.getItemViewType() == TYPE_HEADER) {
            onBindHeaderView(holder, position);
        } else if (useHeader() && position > getBasicItemCount() && holder.getItemViewType() == TYPE_FOOTER) {
            onBindFooterView(holder, position);
        } else if (!useHeader() && position >= getBasicItemCount() && holder.getItemViewType() == TYPE_FOOTER) {
            onBindFooterView(holder, position);
        } else {
            onBindBasicItemView(holder, position - (useHeader() ? 1 : 0));
        }
    }

    public abstract boolean useHeader();

    public abstract T onCreateHeaderViewHolder(ViewGroup parent, int viewType);

    public abstract void onBindHeaderView(T holder, int position);

    public abstract boolean useFooter();

    public abstract T onCreateFooterViewHolder(ViewGroup parent, int viewType);

    public abstract void onBindFooterView(T holder, int position);

    public abstract T onCreateBasicItemViewHolder(ViewGroup parent, int viewType);

    public abstract void onBindBasicItemView(T holder, int position);

    public abstract int getBasicItemCount();

    /**
     * make sure you don't use [Integer.MAX_VALUE-1, Integer.MAX_VALUE] as BasicItemViewType
     *
     * @param position
     * @return
     */
    public abstract int getBasicItemType(int position);

}
