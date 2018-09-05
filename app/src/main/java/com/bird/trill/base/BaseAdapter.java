package com.bird.trill.base;

import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.support.annotation.IdRes;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class BaseAdapter<T, B extends ViewDataBinding> extends RecyclerView.Adapter<BaseAdapter.SimpleViewHolder> {
    private static final String TAG = "BaseAdapter";

    protected List<T> data;
    protected OnItemClickListener listener;

    protected abstract int layoutResId(int viewType);

    protected abstract void convert(SimpleViewHolder holder, int position, T item);

    public SimpleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        B binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()),
                layoutResId(viewType),
                parent,
                false);
        return new SimpleViewHolder(binding);
    }

    public void onBindViewHolder(BaseAdapter.SimpleViewHolder holder, int position) {
        holder.itemView.setOnClickListener(v -> {
            if (null != listener) {
                listener.onItemClick(v, position);
            }
        });
        convert(holder, position, getItem(position));
    }

    public T getItem(int position) {
        return data.get(position);
    }

    @Override
    public int getItemCount() {
        return null == data ? 0 : data.size();
    }

    public void add(T t) {
        if (null == data) {
            data = new ArrayList<>();
        }
        data.add(t);
        notifyItemInserted(data.size() - 1);
    }

    public void addAll(List<T> list) {
        if (null == data) {
            refresh(list);
        } else {
            data.addAll(list);
            notifyDataSetChanged();
        }
    }

    public void clear() {
        if (null != data) {
            data.clear();
            notifyDataSetChanged();
        }
    }

    public void refresh(T[] array) {
        refresh(Arrays.asList(array));
    }

    public void refresh(List<T> list) {
        if (null == list) {
            Log.d(TAG, "List is null");
            data = new ArrayList<>();
        } else {
            this.data = list;
        }
        Log.d(TAG, "refreshEvent data.");
        notifyDataSetChanged();
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

    public class SimpleViewHolder extends RecyclerView.ViewHolder {
        public B binding;

        public SimpleViewHolder(B binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public B getBinding() {
            return binding;
        }

        public <T extends View> T findViewById(@IdRes int id) {
            return itemView.findViewById(id);
        }
    }
}
