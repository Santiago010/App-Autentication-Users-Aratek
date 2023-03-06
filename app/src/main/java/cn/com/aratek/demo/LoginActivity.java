package cn.com.aratek.demo;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import cn.com.aratek.demo.featuresrequest.DataForLogin;
import cn.com.aratek.demo.featuresrequest.FingerprintService;
import cn.com.aratek.demo.featuresrequest.Newuser;
import cn.com.aratek.demo.featuresrequest.User;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class LoginActivity extends AppCompatActivity {

    private EditText username;
    private  EditText pass;
    private Button btnSend;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        username = findViewById(R.id.name);
        pass =  findViewById(R.id.pass);
        btnSend = findViewById(R.id.btnSend);
        addOnClickBtnSend();
    }

    private void addOnClickBtnSend(){
        btnSend.setOnClickListener(v -> {
            if(username.length() == 0 || pass.length() == 0){
                Toast.makeText(this,"Te falta algun campo por llenar",Toast.LENGTH_SHORT).show();
                return;
            }else {
                sendData(String.valueOf(username.getText()) ,String.valueOf(pass.getText()));
            }
        });
    }

    private void navigateTo(){
        Intent intent = new Intent(this, hamburgermenuActivity.class);
        startActivity(intent);

    }

    private GsonConverterFactory makeConfGson(){
        Gson gsonC = new GsonBuilder().create();
        GsonConverterFactory gsonConverterFactoryC = GsonConverterFactory.create(gsonC);
        return gsonConverterFactoryC;
    }

    private Retrofit makeConfRequest(){
        Retrofit retrofit = new Retrofit.Builder().baseUrl("https://api.escuelajs.co/api/v1/")
                .addConverterFactory(makeConfGson()).build();
        return retrofit;
    }

    private void sendData(String name,String password){
        FingerprintService fps = makeConfRequest().create(FingerprintService.class);
        String email = name +"@gmail.com";
        DataForLogin dataForLogin = new DataForLogin(name,password,"admin","https://picsum.photos/200",email);
        Call<Newuser> call = fps.sendUser(dataForLogin);
        call.enqueue(new Callback<Newuser>() {
            @Override
            public void onResponse(Call<Newuser> call, Response<Newuser> response) {
                try {
                    if(response.isSuccessful()){
                        Newuser userRes = response.body();
                        Log.d("IDRES",String.valueOf(userRes.getId()));
                        navigateTo();
                    }
                }catch (Exception ex){
                    Log.d("APIER",ex.getMessage());
                }
            }

            @Override
            public void onFailure(Call<Newuser> call, Throwable t) {

            }
        });
    }
}