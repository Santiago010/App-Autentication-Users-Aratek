package cn.com.aratek.demo.featureslist;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import cn.com.aratek.demo.R;
import cn.com.aratek.demo.featuresrequest.User;


public class FingerprintAdapter extends RecyclerView.Adapter<FingerprintAdapter.ViewHolder> {

    public List<User> mData;
    public View.OnClickListener clickListenerFP;




    public FingerprintAdapter(List<User> data, View.OnClickListener clickListenerFP) {
        this.mData = data;
        this.clickListenerFP = clickListenerFP;

    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_list_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(view,clickListenerFP);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        User s = mData.get(position);

        holder.textView.setText(s.getName());
        holder.textView.setTag(s);


    }

    @Override
    public int getItemCount() {
       return mData.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView textView;

        public ViewHolder(View itemView,View.OnClickListener clickListenerFP) {
            super(itemView);
            textView = itemView.findViewById(R.id.nameFP);
            textView.setOnClickListener(clickListenerFP);

        }

    }

}
