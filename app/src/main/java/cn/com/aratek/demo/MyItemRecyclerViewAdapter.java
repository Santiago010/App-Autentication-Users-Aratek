package cn.com.aratek.demo;

import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import cn.com.aratek.demo.featuresrequest.User;

import cn.com.aratek.demo.databinding.FragmentEnrollempleadoBinding;

import java.util.List;


public class MyItemRecyclerViewAdapter extends RecyclerView.Adapter<MyItemRecyclerViewAdapter.ViewHolder> {

    public List<User> mValues;
    public View.OnClickListener clickListenerFP;

    public MyItemRecyclerViewAdapter(List<User> items,View.OnClickListener clickListenerFP) {
        mValues = items;
        this.clickListenerFP = clickListenerFP;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        return new ViewHolder(FragmentEnrollempleadoBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));

    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        User s = mValues.get(position);
        holder.mItem = s;
//        holder.mIdView.setText(s.getId());
        holder.mContentView.setText(s.getEmail());
        holder.itemView.setTag(s);
        holder.itemView.setOnClickListener(clickListenerFP);
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final TextView mIdView;
        public final TextView mContentView;
        public User mItem;

        public ViewHolder(FragmentEnrollempleadoBinding binding) {
            super(binding.getRoot());
            mIdView = binding.itemNumber;
            mContentView = binding.content;
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mContentView.getText() + "'";
        }
    }
}