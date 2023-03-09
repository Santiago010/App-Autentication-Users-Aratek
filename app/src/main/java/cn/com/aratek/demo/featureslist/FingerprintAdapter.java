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
        holder.textId.setText(String.valueOf(s.getId()) );
        holder.textUsername.setText(s.getEmail());
        holder.textUsername.setTag(s);

    }

    @Override
    public int getItemCount() {
       return mData.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView textUsername;
        public TextView textId;

        public ViewHolder(View itemView,View.OnClickListener clickListenerFP) {
            super(itemView);
            textId = itemView.findViewById(R.id.idFP);
            textUsername = itemView.findViewById(R.id.nameFP);
            textUsername.setOnClickListener(clickListenerFP);

        }

    }

}
