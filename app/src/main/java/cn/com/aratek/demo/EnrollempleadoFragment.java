package cn.com.aratek.demo;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import cn.com.aratek.demo.featuresrequest.FingerprintService;
import cn.com.aratek.demo.featuresrequest.User;

import cn.com.aratek.demo.utils.Prefs;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * A fragment representing a list of Items.
 */
public class EnrollempleadoFragment extends Fragment {

    // TODO: Customize parameter argument names
    private static final String ARG_COLUMN_COUNT = "column-count";
    // TODO: Customize parameters
    private int mColumnCount = 1;
    private List<User> listTemp;

    private MyItemRecyclerViewAdapter mMyItemRVA;

    private Prefs prefs;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public EnrollempleadoFragment() {
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static EnrollempleadoFragment newInstance(int columnCount) {
        EnrollempleadoFragment fragment = new EnrollempleadoFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_enrollempleado_list, container, false);

        prefs = new Prefs(getContext());

        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            RecyclerView recyclerView = (RecyclerView) view;

            listTemp = new ArrayList<>();

            View.OnClickListener clickListener = v -> {
                User item = (User) v.getTag();
                Log.d("USERENROLL",item.getName());
            };

            mMyItemRVA = new MyItemRecyclerViewAdapter(listTemp,clickListener);


//            if (mColumnCount <= 1) {
//                recyclerView.setLayoutManager(new LinearLayoutManager(context));
//            } else {
//                recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
//            }

            DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(), LinearLayoutManager.VERTICAL);


            recyclerView.addItemDecoration(dividerItemDecoration);
            recyclerView.setAdapter(mMyItemRVA);
            getUsers();
        }
        return view;
    }

    private Retrofit makeConfRequest() {
        Retrofit retrofit = new Retrofit.Builder().baseUrl(prefs.getUrl())
                .addConverterFactory(GsonConverterFactory.create()).build();
        return retrofit;
    }

    private void getUsers(){
        FingerprintService fps = makeConfRequest().create(FingerprintService.class);
        Call<List<User>> call = fps.getUsers(20);
        call.enqueue(new Callback<List<User>>() {
            @Override
            public void onResponse(Call<List<User>> call, Response<List<User>> response) {
                try {
                    if(response.isSuccessful()){
                        List<User> userRes = (List<User>) response.body();
                        mMyItemRVA.mValues = userRes;
                        mMyItemRVA.notifyDataSetChanged();
                        Log.d("USERRES",String.valueOf(userRes.size()) );
                    }
                }catch (Exception ex){
                    Log.d("APIER",ex.getMessage());
                }
            }

            @Override
            public void onFailure(Call<List<User>> call, Throwable t) {

            }
        });
    }
}