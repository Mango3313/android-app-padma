package com.example.padmapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.example.padmapp.Retrofit.IMyService;
import com.example.padmapp.Retrofit.RetrofitClient;
import com.example.padmapp.clases.TokenPreferences;
import com.github.javiersantos.materialstyleddialogs.MaterialStyledDialog;
import com.rengwuxian.materialedittext.MaterialEditText;

import org.json.JSONObject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;

public class login extends AppCompatActivity {

    MaterialEditText edtUser, edtPass, login_nombre, login_password;
    Button btn_login;
    TextView tvRegistro;

    CompositeDisposable compositeDisposable = new CompositeDisposable();

    IMyService iMyService;
    private TokenPreferences _tokPrefs;


    @Override
    protected void onStop() {
        compositeDisposable.clear();
        super.onStop();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        _tokPrefs = new TokenPreferences(getApplicationContext());
        Retrofit retrofitClient =  RetrofitClient.getInstance();
        iMyService = retrofitClient.create(IMyService.class);

        edtUser = findViewById(R.id.edt_nickname);
        edtPass = findViewById(R.id.edt_password);
        login_nombre = findViewById(R.id.login_nombre);
        login_password = findViewById(R.id.login_password);




        btn_login = findViewById(R.id.btn_login);
        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginUser(login_nombre.getText().toString(), login_password.getText().toString());
            }
        });

        tvRegistro = findViewById(R.id.tvRegistrar);
        tvRegistro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final View registro = LayoutInflater.from(login.this)
                        .inflate(R.layout.layout_registro,null);

                new MaterialStyledDialog.Builder(login.this)
                        .setIcon(R.drawable.ic_user_registro)
                        .setTitle("Registro")
                        .setDescription("Ingresa tus datos")
                        .setCustomView(registro)
                        .setNegativeText("Cancelar")
                        .onNegative(new MaterialDialog.SingleButtonCallback() {

                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                dialog.dismiss();
                            }
                        })
                        .setPositiveText("Registrar")
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                MaterialEditText edt_registro_user = (MaterialEditText) registro.findViewById(R.id.edt_nickname);
                                MaterialEditText edt_registro_password = (MaterialEditText) registro.findViewById(R.id.edt_password);

                                if(TextUtils.isEmpty(edt_registro_user.getText().toString())){
                                    Toast.makeText(login.this, "Ingrese su usuario", Toast.LENGTH_SHORT).show();
                                    return;
                                }

                                if(TextUtils.isEmpty(edt_registro_password.getText().toString())){
                                    Toast.makeText(login.this, "Ingrese su contraseña", Toast.LENGTH_SHORT).show();
                                    return;
                                }
                                
                                registerUser(edt_registro_user.getText().toString(),
                                            edt_registro_password.getText().toString());


                            }
                        }).show();

            }
        });
    }

    private void loginUser(String nombre, String password) {

        if (TextUtils.isEmpty(nombre)){
            Toast.makeText(this, "Ingresa tu usuario", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(password)){
            Toast.makeText(this, "Ingresa tu password", Toast.LENGTH_SHORT).show();
            return;
        }
        compositeDisposable.add(iMyService.loginUser(nombre, password)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(String response) throws Exception {
                        JSONObject json = new JSONObject(response);
                        Log.d("Respuesta",json.toString());
                        if(json.has("token")){
                            _tokPrefs.setToken(json.getString("token"));
                            Intent intent = new Intent (login.this, MainActivity.class);
                            startActivityForResult(intent, 0);
                        }else {
                            Toast.makeText(login.this, json.getString("message"), Toast.LENGTH_SHORT).show();
                        }
                    }
                }));

    }

    private void registerUser(String nombre, String password) {

        compositeDisposable.add(iMyService.registerUser(nombre, password)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(String response) throws Exception {
                        JSONObject json = new JSONObject(response);
                        Log.d("Respuesta",json.toString());
                        Toast.makeText(login.this, json.getString("message"), Toast.LENGTH_SHORT).show();
                    }
                }));
    }

}
