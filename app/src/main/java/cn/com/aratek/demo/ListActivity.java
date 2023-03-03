package cn.com.aratek.demo;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import java.util.ArrayList;
import java.util.List;


import cn.com.aratek.demo.featureslist.FingerprintAdapter;
import cn.com.aratek.demo.featuresrequest.FingerprintService;
import cn.com.aratek.demo.featuresrequest.User;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ListActivity extends AppCompatActivity {

    private RecyclerView rC;
    private FingerprintAdapter mFingerprintAdapter;

    private List<User> listTemp;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        rC = findViewById(R.id.recycler_view);

        listTemp = new ArrayList<>();


        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        rC.setLayoutManager(layoutManager);


        View.OnClickListener clickListener = v -> {

            User item = (User) v.getTag();
            navigateTo(item.getName());
        };
        mFingerprintAdapter = new FingerprintAdapter(listTemp, clickListener);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(rC.getContext(), LinearLayoutManager.VERTICAL);


        rC.addItemDecoration(dividerItemDecoration);
        rC.setAdapter(mFingerprintAdapter);
        getUsers();

    }

    private void navigateTo(String name) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("INFOFP", name);
        startActivity(intent);

    }

    private Retrofit makeConfRequest() {
        Retrofit retrofit = new Retrofit.Builder().baseUrl("https://api.escuelajs.co/api/v1/")
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