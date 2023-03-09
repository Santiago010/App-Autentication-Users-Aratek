package cn.com.aratek.demo;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import cn.com.aratek.demo.featureslist.FingerprintAdapter;
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
public class EmpleadoFragment extends Fragment {

    // TODO: Customize parameter argument names
    private static final String ARG_COLUMN_COUNT = "column-count";
    // TODO: Customize parameters
    private int mColumnCount = 1;
    private List<User> listTemp;
    private FingerprintAdapter mFingerprintAdapter;

    private Prefs prefs;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public EmpleadoFragment() {
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static EmpleadoFragment newInstance(int columnCount) {
        EmpleadoFragment fragment = new EmpleadoFragment();
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
        View view = inflater.inflate(R.layout.fragment_p_list, container, false);

        prefs = new Prefs(getContext());


        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            RecyclerView recyclerView = (RecyclerView) view;

            listTemp = new ArrayList<>();

            View.OnClickListener clickListener = v -> {

                User item = (User) v.getTag();
                navigateTo(item,context);
            };

            mFingerprintAdapter = new FingerprintAdapter(listTemp,clickListener);

            DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(), LinearLayoutManager.VERTICAL);


            recyclerView.addItemDecoration(dividerItemDecoration);
            recyclerView.setAdapter(mFingerprintAdapter);
            getUsers();

        }
        return view;
    }

    private void navigateTo(User item,Context context) {
        Intent intent = new Intent(context, MainActivity.class);
        intent.putExtra("INFOFP", item);
        startActivity(intent);

    }

    private Retrofit makeConfRequest() {
        Retrofit retrofit = new Retrofit.Builder().baseUrl(prefs.getUrl())
                .addConverterFactory(GsonConverterFactory.create()).build();
        return retrofit;
    }

    private void getUsers(){
        FingerprintService fps = makeConfRequest().create(FingerprintService.class);
        Call<List<User>> call = fps.getUsers(100);
        call.enqueue(new Callback<List<User>>() {
            @Override
            public void onResponse(Call<List<User>> call, Response<List<User>> response) {
                try {
                    if(response.isSuccessful()){
                        List<User> userRes = (List<User>) response.body();
                        mFingerprintAdapter.mData = userRes;
                        mFingerprintAdapter.notifyDataSetChanged();
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
